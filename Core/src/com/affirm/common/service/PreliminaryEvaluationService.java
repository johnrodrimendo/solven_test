package com.affirm.common.service;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.PreliminaryEvaluationDAO;
import com.affirm.common.model.PreApprovedInfo;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("preliminaryEvaluationService")
public class PreliminaryEvaluationService {

    @Autowired
    private HardFilterService hardFilterService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PreliminaryEvaluationDAO preliminaryEvaluationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private EvaluationCacheService evaluationCacheService;
    @Autowired
    private LoanApplicationService loanApplicationService;

    public void runPreliminaryEvaluation(LoanApplicationPreliminaryEvaluation preEvaluation, LoanApplication loanApplication, Map<String, Object> cachedSources) throws Exception {

        boolean hardFiltersApproved = true;
        boolean runDefaultPreEvaluation = true;

        // If the preevaluation has failed status, update the is approved and run default evaluation
        if(preEvaluation.getStatus() == 'F' || (preEvaluation.getEntityId() == Entity.AFFIRM && !preEvaluation.getApproved())){
            preEvaluation.setApproved(false);
            preEvaluation.setRunDefaultEvaluation(true);
            preliminaryEvaluationDao.updateIsApproved(preEvaluation.getId(), preEvaluation.getApproved());
            preliminaryEvaluationDao.updateRunDefaultEvaluation(preEvaluation.getId(), preEvaluation.getRunDefaultEvaluation());

            runDefaultPreliminaryEvaluation(loanApplication, preEvaluation, cachedSources);
            return;
        }

        preEvaluation.setApproved(true);
        preliminaryEvaluationDao.updateIsApproved(preEvaluation.getId(), preEvaluation.getApproved());

        int minAmount = catalogService.getProductsEntity().stream()
                .filter(p -> p.getId() == preEvaluation.getProduct().getId().intValue())
                .map(p -> p.getProductMaxMinParameters())
                .flatMap(Collection::stream)
                .filter(p -> (p.getEntityId() == null || p.getEntityId().equals(preEvaluation.getEntityId()))
                        && p.getCountryId() != null && p.getCountryId() == loanApplication.getCountryId().intValue())
                .mapToInt(p -> p.getMinAmount()).min().orElse(0);
        EntityProductParams preEvaEntityProdParams = catalogService.getEntityProductParamById(preEvaluation.getEntityProductParameterId());
        switch (preEvaEntityProdParams.getFlowType()){
            case EntityProductParams.FLOW_TYPE_PRE_APPROVED_BASE: {
                Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);

                List<PreApprovedInfo> preApproved = personDao.getPreApprovedData(person.getDocumentType().getId(), person.getDocumentNumber());
                boolean existsPreApproved = preApproved.stream()
                        .anyMatch(p -> p.getProduct() != null && p.getProduct().getId() == preEvaluation.getProduct().getId().intValue()
                                && p.getEntity() != null && p.getEntity().getId() == preEvaluation.getEntityId().intValue()
                                && p.getMaxAmount() != null && p.getMaxAmount() >= minAmount
                                && (p.getEntityProductParams() == null || p.getEntityProductParams().isEmpty() || p.getEntityProductParams().contains(preEvaluation.getEntityProductParameterId())));

                // if exists pre approved, migrate the data and run the hard filters.
                // If not, register a default hardfilter rejection and run default preevaluation
                if (existsPreApproved) {
                    personDao.migrateFromApprovedData(loanApplication.getId(), preEvaluation.getEntityId(),
                            preEvaluation.getProduct().getId(), person.getDocumentType().getId(), person.getDocumentNumber());
                    for (PreliminaryHardFilter preHardFilter : preEvaluation.getPreliminaryHardFiltersOrdered()) {
                        boolean hardFilterResult = hardFilterService.runHardFilter(
                                preHardFilter.getHardFilter().getId(), preHardFilter.getParam1(),
                                preHardFilter.getParam2(), preHardFilter.getParam3(),
                                preEvaluation, loanApplication, cachedSources);

                        // If the hard filter fails, update the step and break the loop
                        if (!hardFilterResult) {
                            Calendar exppirationDate = Calendar.getInstance();
                            exppirationDate.add(Calendar.DATE, preHardFilter.getExpirationDays());
                            loanApplicationDao.updatePreliminaryEvaluationStep(
                                    preEvaluation.getId(),
                                    preHardFilter.getHardFilter().getId(),
                                    preHardFilter.getHardFilter().getMessage(),
                                    preHardFilter.getHardFilter().getHelpMessage(),
                                    exppirationDate.getTime());

                            // If it's not a vehiculo garantizado hardfilter or doesnt have vehicle, run default evaluation,
                            // else update with the data of the hardfilter rejected
//                            if (preHardFilter.getHardFilter().getId() != HardFilter.VEHICULO_NO_AUTORIZADO_PARA_GARANTIA || loanApplication.getGuaranteedVehicleBrandId() == null) {
                            if (shouldRunDefaultPreEvaluation(loanApplication)) {
                                preEvaluation.setRunDefaultEvaluation(true);
                                preliminaryEvaluationDao.updateRunDefaultEvaluation(preEvaluation.getId(), preEvaluation.getRunDefaultEvaluation());
                                runDefaultPreliminaryEvaluation(loanApplication, preEvaluation, cachedSources);
                            } else {
                                exppirationDate = Calendar.getInstance();
                                exppirationDate.add(Calendar.DATE, 30);
                                loanApplicationDao.updatePreliminaryEvaluationStep(
                                        preEvaluation.getId(),
                                        preHardFilter.getHardFilter().getId(),
                                        preHardFilter.getHardFilter().getMessage(),
                                        preHardFilter.getHardFilter().getHelpMessage(),
                                        exppirationDate.getTime());

                                runDefaultPreEvaluation = false;
                            }

                            // Break the loop and stop the proces of the others hard filters
                            hardFiltersApproved = false;
                            break;
                        }
                    }
                } else {
                    HardFilter defaultHardFilter = catalogService.getHardFilterById(HardFilter.CLOSED_PLATFORM);
                    Calendar exppirationDate = Calendar.getInstance();
                    exppirationDate.add(Calendar.DATE, 15);
                    loanApplicationDao.updatePreliminaryEvaluationStep(
                            preEvaluation.getId(),
                            defaultHardFilter.getId(),
                            defaultHardFilter.getMessage(),
                            defaultHardFilter.getHelpMessage(),
                            exppirationDate.getTime());

                    if(shouldRunDefaultPreEvaluation(loanApplication)){
                        preEvaluation.setRunDefaultEvaluation(true);
                        preliminaryEvaluationDao.updateRunDefaultEvaluation(preEvaluation.getId(), preEvaluation.getRunDefaultEvaluation());
                        runDefaultPreliminaryEvaluation(loanApplication, preEvaluation, cachedSources);
                    }

                    hardFiltersApproved = false;
                }
                break;
            }
            case EntityProductParams.FLOW_TYPE_NORMAL:
            case EntityProductParams.FLOW_TYPE_MIXTO: {

                // If is flow MIXTO and exists pre approved, migrate the pre approved data.
                if (preEvaEntityProdParams.getFlowType() == EntityProductParams.FLOW_TYPE_MIXTO) {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<PreApprovedInfo> preApproved = personDao.getPreApprovedData(person.getDocumentType().getId(), person.getDocumentNumber());
                    boolean existsPreApproved = preApproved.stream()
                            .anyMatch(p -> p.getProduct() != null && p.getProduct().getId() == preEvaluation.getProduct().getId().intValue()
                                    && p.getEntity() != null && p.getEntity().getId() == preEvaluation.getEntityId().intValue()
                                    && p.getMaxAmount() != null && p.getMaxAmount() >= minAmount
                                    && (p.getEntityProductParams() == null || p.getEntityProductParams().isEmpty() || p.getEntityProductParams().contains(preEvaluation.getEntityProductParameterId())));
                    if (existsPreApproved) {
                        personDao.migrateFromApprovedData(loanApplication.getId(), preEvaluation.getEntityId(),
                                preEvaluation.getProduct().getId(), person.getDocumentType().getId(), person.getDocumentNumber());
                    }
                }

                for (PreliminaryHardFilter preHardFilter : preEvaluation.getPreliminaryHardFiltersOrdered()) {
                    boolean hardFilterResult = hardFilterService.runHardFilter(
                            preHardFilter.getHardFilter().getId(), preHardFilter.getParam1(),
                            preHardFilter.getParam2(), preHardFilter.getParam3(),
                            preEvaluation, loanApplication, cachedSources);

                    // If the hard filter fails, update the step and break the loop
                    if (!hardFilterResult) {
                        Calendar exppirationDate = Calendar.getInstance();
                        exppirationDate.add(Calendar.DATE, preHardFilter.getExpirationDays());
                        loanApplicationDao.updatePreliminaryEvaluationStep(
                                preEvaluation.getId(),
                                preHardFilter.getHardFilter().getId(),
                                preHardFilter.getHardFilter().getMessage(),
                                preHardFilter.getHardFilter().getHelpMessage(),
                                exppirationDate.getTime());

                        // If it's not a vehiculo garantizado hardfilter or doesnt have vehicle, run default evaluation,
                        // else update with the data of the hardfilter rejected
                        if (preHardFilter.getHardFilter().getId() != HardFilter.VEHICULO_NO_AUTORIZADO_PARA_GARANTIA || loanApplication.getGuaranteedVehicleBrandId() == null) {
                            if (shouldRunDefaultPreEvaluation(loanApplication)) {
                                preEvaluation.setRunDefaultEvaluation(true);
                                preliminaryEvaluationDao.updateRunDefaultEvaluation(preEvaluation.getId(), preEvaluation.getRunDefaultEvaluation());
                                runDefaultPreliminaryEvaluation(loanApplication, preEvaluation, cachedSources);
                            }
                        } else {
                            exppirationDate = Calendar.getInstance();
                            exppirationDate.add(Calendar.DATE, 30);
                            loanApplicationDao.updatePreliminaryEvaluationStep(
                                    preEvaluation.getId(),
                                    preHardFilter.getHardFilter().getId(),
                                    preHardFilter.getHardFilter().getMessage(),
                                    preHardFilter.getHardFilter().getHelpMessage(),
                                    exppirationDate.getTime());

                            runDefaultPreEvaluation = false;
                        }

                        // Break the loop and stop the proces of the others hard filters
                        hardFiltersApproved = false;
                        break;
                    }
                }
                break;
            }
        }

        // If there is a hard
        if (hardFiltersApproved) {
            preEvaluation.setApproved(true);
            preEvaluation.setRunDefaultEvaluation(false);
            preliminaryEvaluationDao.updateIsApproved(preEvaluation.getId(), preEvaluation.getApproved());
            preliminaryEvaluationDao.updateRunDefaultEvaluation(preEvaluation.getId(), preEvaluation.getRunDefaultEvaluation());
        } else {

            if (!shouldRunDefaultPreEvaluation(loanApplication)) {
                runDefaultPreEvaluation = false;
            }

            preEvaluation.setApproved(false);
            preEvaluation.setRunDefaultEvaluation(runDefaultPreEvaluation);
            preliminaryEvaluationDao.updateIsApproved(preEvaluation.getId(), preEvaluation.getApproved());
            preliminaryEvaluationDao.updateRunDefaultEvaluation(preEvaluation.getId(), preEvaluation.getRunDefaultEvaluation());

            if (runDefaultPreEvaluation) {
                runDefaultPreliminaryEvaluation(loanApplication, preEvaluation, cachedSources);
            }
        }

        // If this pre evaluation is approves, update the status to EVAL_APPROVED
        if(preEvaluation.getApproved()){
            if(preEvaluation.getEvaluationUpdatesStatus()){
                loanApplication.setStatus(catalogService.getLoanApplicationStatus(Configuration.getDefaultLocale(), LoanApplicationStatus.PRE_EVAL_APPROVED));
                loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.PRE_EVAL_APPROVED, null);
            }
        }else{

            // If all the preliminary evaluation are dissaproved, update the status and hard filter message with the worst preliminary evaluation
            List<LoanApplicationPreliminaryEvaluation> preliminaryEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale());
            if(preliminaryEvaluations.stream().allMatch(p -> p.getApproved() != null && !p.getApproved())){

                preliminaryEvaluations = preliminaryEvaluationDao.getPreliminaryEvaluationsWithHardFilters(loanApplication.getId(), Configuration.getDefaultLocale());
                int firstEvaluationOrder = preliminaryEvaluations.stream()
                        .map(h -> h.getPreliminaryHardFilters())
                        .flatMap(Collection::stream)
                        .mapToInt(h -> h.getEvaluationOrder()).min().orElse(0);
                LoanApplicationPreliminaryEvaluation worstPreEvaluation = preliminaryEvaluations.stream()
                        .filter(p -> p.getPreliminaryHardFilters().stream().anyMatch(h -> h.getEvaluationOrder() == firstEvaluationOrder))
                        .findFirst().orElse(null);

                loanApplication.setStatus(catalogService.getLoanApplicationStatus(Configuration.getDefaultLocale(), LoanApplicationStatus.REJECTED_AUTOMATIC));
                loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.REJECTED_AUTOMATIC, null);
                loanApplication.setRejectionHardFilterKey(worstPreEvaluation.getHardFilterMessageKey());
                loanApplicationDao.updateHardFilterMessage(loanApplication.getId(), loanApplication.getRejectionHardFilterKey());
                loanApplication.setExpirationDate(worstPreEvaluation.getEvaluationExpirationDate());
                Date newExpirationDateByProduct = null;
                if(loanApplication.getEntityId() != null && Arrays.asList(Entity.AZTECA).contains(loanApplication.getEntityId()) && loanApplication.getProductCategoryId() != null && Arrays.asList(ProductCategory.CONSUMO).contains(loanApplication.getProductCategoryId())){
                    newExpirationDateByProduct = loanApplicationService.generateExpirationDateByLoan(loanApplication.getEntityId(), loanApplication.getProductCategoryId());
                    if(newExpirationDateByProduct != null){
                        loanApplicationDao.updateExpirationDate(loanApplication.getId(), newExpirationDateByProduct);
                        return;
                    }
                }
                if(loanApplication.getEntityId() != null && Arrays.asList(Entity.AZTECA).contains(loanApplication.getEntityId()) && loanApplication.getProductCategoryId() != null && Arrays.asList(ProductCategory.GATEWAY).contains(loanApplication.getProductCategoryId())){
                    newExpirationDateByProduct = loanApplicationService.generateExpirationDateByLoan(loanApplication.getEntityId(), loanApplication.getProductCategoryId());
                    if(newExpirationDateByProduct != null){
                        loanApplicationDao.updateExpirationDate(loanApplication.getId(), newExpirationDateByProduct);
                        return;
                    }
                }
                if(worstPreEvaluation.getHardFilter() != null && preEvaluation.getPreliminaryHardFilters() != null && worstPreEvaluation.getHardFilter().getId() == HardFilter.BDS_EXISTS_IN_CNE){
                    PreliminaryHardFilter preliminaryHardFilterData = preEvaluation.getPreliminaryHardFiltersOrdered().stream().filter(e -> e.getHardFilter() != null && e.getHardFilter().getId() == HardFilter.BDS_EXISTS_IN_CNE).findFirst().orElse(null);
                    if(preliminaryHardFilterData == null) loanApplicationDao.updateExpirationDate(loanApplication.getId(), loanApplication.getExpirationDate());
                    else {
                        Calendar newExpirationDate = Calendar.getInstance();
                        newExpirationDate.add(Calendar.DATE, preliminaryHardFilterData.getExpirationDays());
                        loanApplicationDao.updateExpirationDate(loanApplication.getId(), newExpirationDate.getTime());
                    }
                }
                else loanApplicationDao.updateExpirationDate(loanApplication.getId(), loanApplication.getExpirationDate());
            }
        }



    }

    public boolean shouldRunDefaultPreEvaluation(LoanApplication loanApplication){
        if(loanApplication.getEntityId() != null && Arrays.asList(Entity.FUNDACION_DE_LA_MUJER, Entity.BANBIF).contains(loanApplication.getEntityId()))
            return false;
        return true;
    }

    private void runDefaultPreliminaryEvaluation(LoanApplication loanApplication, LoanApplicationPreliminaryEvaluation preliminaryEvaluation, Map<String, Object> cachedSources) throws Exception {

        // If the default pre evaluation has alredy ran, update this one with the same values
        List<LoanApplicationPreliminaryEvaluation> preEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(),Configuration.getDefaultLocale());
        if(preEvaluations.stream().anyMatch(p -> p.getDefaultEvaluationHardFilterId() != null)){
            LoanApplicationPreliminaryEvaluation preEvaWithDefaultEva = preEvaluations.stream().filter(p -> p.getDefaultEvaluationHardFilterId() != null).findFirst().orElse(null);
            loanApplicationDao.updatePreliminaryEvaluationStep(
                    preliminaryEvaluation.getId(),
                    preEvaWithDefaultEva.getDefaultEvaluationHardFilterId(),
                    preEvaWithDefaultEva.getHardFilterMessageKey(),
                    preEvaWithDefaultEva.getHelpMessage() != null ? preEvaWithDefaultEva.getHelpMessage().getId() : null,
                    preEvaWithDefaultEva.getEvaluationExpirationDate());
            return;
        }

        // Get the hard filters for the default evaluation
        List<HardFilter> hardFiltersToRun = catalogService.getHardFilters().stream()
                .filter(h -> h.getDefaultOrder() != null && (h.getCountryId() == null || h.getCountryId() == loanApplication.getCountryId().intValue()))
                .sorted(Comparator.comparingInt(HardFilter::getDefaultOrder))
                .collect(Collectors.toList());

        // Run the hardfilters for the default evaluation.
        // If any fails, update the pre evaluation with expiration date +30 days and return.
        for(HardFilter hardFilter : hardFiltersToRun){
            boolean hardFilterResult = hardFilterService.runHardFilter(
                    hardFilter.getId(), hardFilter.getDefaultParameter1(),
                    hardFilter.getDefaultParameter2(), hardFilter. getDefaultParameter3(),
                    preliminaryEvaluation, loanApplication, cachedSources);
            if(!hardFilterResult){
                Calendar exppirationDate = Calendar.getInstance();
                exppirationDate.add(Calendar.DATE, 30);
                loanApplicationDao.updatePreliminaryEvaluationStep(
                        preliminaryEvaluation.getId(),
                        hardFilter.getId(),
                        hardFilter.getMessage(),
                        hardFilter.getHelpMessage(),
                        exppirationDate.getTime());
                return;
            }
        }

        // If all the hardfilters are approved, set a default hardfilter for the reject
        HardFilter defaultHardFilter = catalogService.getHardFilterById(loanApplication.getEntityId() != null ? HardFilter.CLOSED_PLATFORM_BRANDED : HardFilter.CLOSED_PLATFORM);
        Calendar exppirationDate = Calendar.getInstance();
        exppirationDate.add(Calendar.DATE, 30);
        loanApplicationDao.updatePreliminaryEvaluationStep(
                preliminaryEvaluation.getId(),
                defaultHardFilter.getId(),
                defaultHardFilter.getMessage(),
                defaultHardFilter.getHelpMessage(),
                exppirationDate.getTime());
    }

    private boolean isBrandingFDLM(LoanApplication loanApplication) {
        return loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.FUNDACION_DE_LA_MUJER;
    }
}
