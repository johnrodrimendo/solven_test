package com.affirm.entityExt.models;

public class TrayReportForm extends PaginatorTableFilterForm{

    private Integer trayReport;
    private Integer tray;

    public Integer getTrayReport() {
        return trayReport;
    }

    public void setTrayReport(Integer trayReport) {
        this.trayReport = trayReport;
    }

    public Integer getTray() {
        return tray;
    }

    public void setTray(Integer tray) {
        this.tray = tray;
    }
}
