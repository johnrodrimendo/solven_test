package com.affirm.backoffice.model;

import java.util.ArrayList;
import java.util.List;

public class OriginationReportDashboardPainter {

    private List<OriginationReportPeriod> periods;
    private List<OriginationReportOriginationTypeGroup> originations;
    private OriginationReportOriginationTypeGroup totalOriginations;

    public OriginationReportDashboardPainter(List<OriginationReportPeriod> periods) {
        this.periods = periods;
        originations = new ArrayList<>();
    }

    public void processReport() {

        for (OriginationReportPeriod period : periods) {
            if(period.getDetails() != null)
                for(OriginationReportPeriodDetail detail : period.getDetails()){

                    // Get or create the origination type filter
                    OriginationReportOriginationTypeGroup originationTypeGroup =  originations.stream()
                            .filter(p -> p.getOriginationType().intValue() == detail.getOrigination())
                            .findFirst()
                            .orElse(null);
                    if (originationTypeGroup == null) {
                        originationTypeGroup = new OriginationReportOriginationTypeGroup(detail.getOrigination());
                        originations.add(originationTypeGroup);
                    }

                    // Get or create the Product filter
                    OriginationReportProductGroup productGroup =  originationTypeGroup.getProducts().stream()
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

    public OriginationReportOriginationTypeGroup getTotalOriginations(){
        return totalOriginations;
    }

    public List<OriginationReportPeriod> getPeriods() {
        return periods;
    }

    public void setPeriods(List<OriginationReportPeriod> periods) {
        this.periods = periods;
    }

    public List<OriginationReportOriginationTypeGroup> getOriginations() {
        return originations;
    }

    public void setOriginations(List<OriginationReportOriginationTypeGroup> originations) {
        this.originations = originations;
    }
}
