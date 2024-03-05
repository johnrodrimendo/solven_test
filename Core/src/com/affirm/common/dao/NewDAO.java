package com.affirm.common.dao;

import com.affirm.common.model.New;

import java.util.Date;
import java.util.List;

public interface NewDAO {
    List<New> getNews();

    List<New> getNews(Integer countryId);

    void registerNew(Integer countryId, String title, String summary, String link, String pressMedium, Date date, String imgUrl, String altImgUrl);
}
