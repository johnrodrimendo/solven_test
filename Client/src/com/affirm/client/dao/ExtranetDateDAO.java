package com.affirm.client.dao;

import com.affirm.client.model.ExtranetDate;

import java.util.Date;
import java.util.List;

public interface ExtranetDateDAO {
    List<ExtranetDate> getExtranetDates(Date startDate, Date endDate);
}
