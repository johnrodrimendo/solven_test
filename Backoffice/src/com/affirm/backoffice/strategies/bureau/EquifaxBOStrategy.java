package com.affirm.backoffice.strategies.bureau;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.ApplicationBureau;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.equifax.ws.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class EquifaxBOStrategy implements BureauStrategy {

    @Override
    public void updateModelMap(Locale locale, ApplicationBureau applicationBureau, ModelMap model, LoanApplicationDAO loanApplicationDAO, PersonDAO personDAO) throws Exception {
        ReporteCrediticio equifaxResult = applicationBureau.getEquifaxReportCreditcio();

        //valores para ver si muestro o no el boton de reintentar

        if (equifaxResult != null) {
            RiskPredictor riskPredictor = null;
            DirectorioPersona directorioPersona = null;
            DirectorioSUNAT directorioSUNAT = null;
            RepresentantesLegales representantesLegales = null;
            List<IndicadorDeConsulta.Entidades.Entidad> entidades = null;
            DeudasImpagas deudasImpagas = null;
            IncomePredictor incomePredictor = null;
            SistemaFinanciero sistemaFinanciero = null;

            for (ReporteCrediticio.Modulos.Modulo mod : equifaxResult.getModulos().getModulo()) {

                if (mod.getCodigo().equals("805") && mod.getData().getAny() instanceof RiskPredictor) {
                    if (mod.getData().getAny() != null) {
                        riskPredictor = (RiskPredictor) mod.getData().getAny();
                    }
                } else if (mod.getCodigo().equals("602") && mod.getData().getAny() instanceof DirectorioPersona) {
                    if (mod.getData().getAny() != null) {
                        directorioPersona = (DirectorioPersona) mod.getData().getAny();
                    }
                } else if (mod.getCodigo().equals("604") && mod.getData().getAny() instanceof DirectorioSUNAT) {
                    if (mod.getData().getAny() != null) {
                        directorioSUNAT = (DirectorioSUNAT) mod.getData().getAny();
                    }
                } else if (mod.getCodigo().equals("605") && mod.getData().getAny() instanceof RepresentantesLegales) {
                    if (mod.getData().getAny() != null) {
                        representantesLegales = (RepresentantesLegales) mod.getData().getAny();
                    }
                } else if (mod.getCodigo().equals("606") && mod.getData().getAny() instanceof IncomePredictor) {
                    if (mod.getData().getAny() != null) {
                        incomePredictor = ((IncomePredictor) mod.getData().getAny());
                    }
                } else if (mod.getCodigo().equals("607") && mod.getData().getAny() instanceof SistemaFinanciero) {
                    if (mod.getData().getAny() != null) {
                        sistemaFinanciero = ((SistemaFinanciero) mod.getData().getAny());
                    }
                } else if (mod.getCodigo().equals("608") && mod.getData().getAny() instanceof DeudasImpagas) {
                    if (mod.getData().getAny() != null) {
                        deudasImpagas = ((DeudasImpagas) mod.getData().getAny());
                    }
                } else if (mod.getCodigo().equals("610") && mod.getData().getAny() instanceof IndicadorDeConsulta) {
                    if (mod.getData().getAny() != null) {
                        entidades = ((IndicadorDeConsulta) mod.getData().getAny()).getEntidades().getEntidad();
                    }
                }
            }

            model.addAttribute("riskPredictor", riskPredictor);
            model.addAttribute("directorioPersona", directorioPersona);
            model.addAttribute("directorioSUNAT", directorioSUNAT);
            model.addAttribute("representantesLegales", representantesLegales);
            model.addAttribute("entidades", entidades);
            model.addAttribute("deudasImpagas", deudasImpagas);
            model.addAttribute("incomePredictor", incomePredictor);
            model.addAttribute("sistemaFinanciero", sistemaFinanciero);
        }

        model.addAttribute("allowEFX", true);
        model.addAttribute("equifax", equifaxResult);
    }
}
