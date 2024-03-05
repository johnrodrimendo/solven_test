package com.affirm.common.model.rcc;

import java.util.Date;
import java.util.List;

public class CalificationDetailRcc {

    private Date rccDate;
    private List<RccDetail> rccDetail;

    public Date getRccDate() {
        return rccDate;
    }

    public void setRccDate(Date rccDate) {
        this.rccDate = rccDate;
    }

    public List<RccDetail> getRccDetail() {
        return rccDetail;
    }

    public void setRccDetail(List<RccDetail> rccDetail) {
        this.rccDetail = rccDetail;
    }
}


