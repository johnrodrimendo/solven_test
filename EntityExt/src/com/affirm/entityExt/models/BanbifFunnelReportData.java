package com.affirm.entityExt.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BanbifFunnelReportData {

    private SideReport leftReport;
    private SideReport rightReport;

    public static class SideReport {

        private Date fromDate;
        private Date toDate;
        private String name;
        private List<Step> steps;

        public SideReport() {
        }

        public SideReport(Date fromDate, Date toDate, String name, List<Step> steps) {
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.name = name;
            this.steps = steps;
        }

        public String getFromToDateFormatted() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            return sdf.format(fromDate) + " - " + sdf.format(toDate);
        }

        public int getStepUsers(double number) {
            return steps.stream().filter(s -> s.getNumber() == number).findFirst().orElse(new Step()).getUsers();
        }

        public Step getStep(double number) {
            return steps.stream().filter(s -> s.getNumber() == number).findFirst().orElse(null);
        }

        public Double getStepPercentage(double number) {
            return steps.stream().filter(s -> s.getNumber() == number).findFirst().orElse(new Step()).getAccumulatedPerc();
        }

        public Boolean getStepStatus(double number) {
            return steps.stream().filter(s -> s.getNumber() == number).findFirst().orElse(new Step()).getActive();
        }

        public Date getFromDate() {
            return fromDate;
        }

        public void setFromDate(Date fromDate) {
            this.fromDate = fromDate;
        }

        public Date getToDate() {
            return toDate;
        }

        public void setToDate(Date toDate) {
            this.toDate = toDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }
    }

    public static class Step {
        private Double number;
        private String step;
        private Integer users;
        private Double stepPerc;
        private Double accumulatedPerc;
        private Boolean active;

        public Step() {
        }

        public Step(Double number, String step, Integer users, Double stepPerc, Double accumulatedPerc, Boolean active) {
            this.number = number;
            this.step = step;
            this.users = users;
            this.stepPerc = stepPerc;
            this.accumulatedPerc = accumulatedPerc;
            this.active = active;
        }

        public Double getNumber() {
            return number;
        }

        public void setNumber(Double number) {
            this.number = number;
        }

        public String getStep() {
            return step;
        }

        public void setStep(String step) {
            this.step = step;
        }

        public Integer getUsers() {
            return users;
        }

        public void setUsers(Integer users) {
            this.users = users;
        }

        public Double getStepPerc() {
            return stepPerc;
        }

        public void setStepPerc(Double stepPerc) {
            this.stepPerc = stepPerc;
        }

        public Double getAccumulatedPerc() {
            return accumulatedPerc;
        }

        public void setAccumulatedPerc(Double accumulatedPerc) {
            this.accumulatedPerc = accumulatedPerc;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }

    public SideReport getLeftReport() {
        return leftReport;
    }

    public void setLeftReport(SideReport leftReport) {
        this.leftReport = leftReport;
    }

    public SideReport getRightReport() {
        return rightReport;
    }

    public void setRightReport(SideReport rightReport) {
        this.rightReport = rightReport;
    }
}
