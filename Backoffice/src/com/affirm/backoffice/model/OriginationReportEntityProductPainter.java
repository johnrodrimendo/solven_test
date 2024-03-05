package com.affirm.backoffice.model;

import java.util.ArrayList;
import java.util.List;

public class OriginationReportEntityProductPainter {

    private List<OriginationReportPeriod> periods;
    private List<OriginationReportEntityGroup> entities;
    private OriginationReportEntityGroup totalEntities;
    private OriginationReportOriginationTypeGroup totalOriginations;

    public OriginationReportEntityProductPainter(List<OriginationReportPeriod> periods) {
        this.periods = periods;
        entities = new ArrayList<>();
    }

    public void processReport() {

        for (OriginationReportPeriod period : periods) {
            if(period.getDetails() != null)
                for (OriginationReportPeriodDetail detail : period.getDetails()) {

                    // Get or create the entty filter
                    OriginationReportEntityGroup entityGroup = entities.stream()
                            .filter(e -> e.getEntity().getId().intValue() == detail.getEntity().getId())
                            .findFirst()
                            .orElse(null);
                    if (entityGroup == null) {
                        entityGroup = new OriginationReportEntityGroup(detail.getEntity());
                        entities.add(entityGroup);
                    }

                    // Get or create the origination type filter
                    OriginationReportOriginationTypeGroup originationTypeGroup = entityGroup.getOriginationTypes().stream()
                            .filter(p -> p.getOriginationType().intValue() == detail.getOrigination())
                            .findFirst()
                            .orElse(null);
                    if (originationTypeGroup == null) {
                        originationTypeGroup = new OriginationReportOriginationTypeGroup(detail.getOrigination());
                        entityGroup.getOriginationTypes().add(originationTypeGroup);
                    }

                    // Get or create the Product filter
                    OriginationReportProductGroup productGroup = originationTypeGroup.getProducts().stream()
                            .filter(p -> p.getProduct().getId().intValue() == detail.getProduct().getId())
                            .findFirst()
                            .orElse(null);
                    if (productGroup == null) {
                        productGroup = new OriginationReportProductGroup(detail.getProduct());
                        originationTypeGroup.getProducts().add(productGroup);
                    }

                    // Add the detail
                    productGroup.getDetails().add(detail);
                }
        }

        totalEntities = new OriginationReportEntityGroup(null);
        totalEntities.setTotalReport(true);
        for (OriginationReportPeriod period : periods) {
            if(period.getDetails() != null)
                for (OriginationReportPeriodDetail detail : period.getDetails()) {

                    // Get or create the origination type filter
                    OriginationReportOriginationTypeGroup originationTypeGroup = totalEntities.getOriginationTypes().stream()
                            .filter(p -> p.getOriginationType().intValue() == detail.getOrigination())
                            .findFirst()
                            .orElse(null);
                    if (originationTypeGroup == null) {
                        originationTypeGroup = new OriginationReportOriginationTypeGroup(detail.getOrigination());
                        totalEntities.getOriginationTypes().add(originationTypeGroup);
                    }

                    // Get or create the Product filter
                    OriginationReportProductGroup productGroup = originationTypeGroup.getProducts().stream()
                            .filter(p -> p.getProduct().getId().intValue() == detail.getProduct().getId())
                            .findFirst()
                            .orElse(null);
                    if (productGroup == null) {
                        productGroup = new OriginationReportProductGroup(detail.getProduct());
                        originationTypeGroup.getProducts().add(productGroup);
                    }

                    // Get the detail of the period to sum the data, if doesnt exist, add to the list
                    OriginationReportPeriodDetail existingDetail = productGroup.getDetails().stream().filter(d -> d.getPeriod().equalsIgnoreCase(period.getPeriod())).findFirst().orElse(null);
                    if (existingDetail != null) {
                        existingDetail.sumDetailData(detail);
                    } else {
                        productGroup.getDetails().add(new OriginationReportPeriodDetail(detail));
                    }

                }
        }

        totalOriginations = new OriginationReportOriginationTypeGroup(null);
        totalOriginations.setTotalReport(true);
        for (OriginationReportPeriod period : periods) {
            if(period.getDetails() != null)
                for(OriginationReportPeriodDetail detail : period.getDetails()){

                    // Get or create the Product filter
                    OriginationReportProductGroup productGroup =  totalOriginations.getProducts().stream()
                            .filter(p -> p.getProduct().getId().intValue() == detail.getProduct().getId())
                            .findFirst()
                            .orElse(null);
                    if (productGroup == null) {
                        productGroup = new OriginationReportProductGroup(detail.getProduct());
                        totalOriginations.getProducts().add(productGroup);
                    }

                    // Get the detail of the period to sum the data, if doesnt exist, add to the list
                    OriginationReportPeriodDetail existingDetail =  productGroup.getDetails().stream().filter(d -> d.getPeriod().equalsIgnoreCase(period.getPeriod())).findFirst().orElse(null);
                    if (existingDetail != null) {
                        existingDetail.sumDetailData(detail);
                    }else{
                        productGroup.getDetails().add(new OriginationReportPeriodDetail(detail));
                    }

                }
        }
    }

    public OriginationReportEntityGroup getTotalEntities() {
        return totalEntities;
    }

    public OriginationReportOriginationTypeGroup getTotalOriginations() {
        return totalOriginations;
    }

    public List<OriginationReportPeriod> getPeriods() {
        return periods;
    }

    public void setPeriods(List<OriginationReportPeriod> periods) {
        this.periods = periods;
    }

    public List<OriginationReportEntityGroup> getEntities() {
        return entities;
    }

    public void setEntities(List<OriginationReportEntityGroup> entities) {
        this.entities = entities;
    }

}
