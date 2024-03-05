package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.NewDAO;
import com.affirm.common.model.New;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository("newDao")
public class NewDAOImpl extends JsonResolverDAO implements NewDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public List<New> getNews() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_news()", JSONArray.class);

        if (dbArray == null) {
            return null;
        }

        List<New> news = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            New _new = new New();
            _new.fillFromDb(dbArray.getJSONObject(i), catalogService);
            news.add(_new);
        }

        return news;
    }

    @Override
    public List<New> getNews(Integer countryId) {
        return getNews().stream().filter(n -> n.getCountry().getId().equals(countryId))
                .collect(Collectors.toList());
    }

    @Override
    public void registerNew(Integer countryId, String title, String summary, String link, String pressMedium, Date date, String imgUrl, String altImgUrl) {
        queryForObjectTrx("select * from support.register_news(?, ?, ?, ?, ?, ?, ?, ?)", String.class, false,
                new SqlParameterValue(Types.VARCHAR, title), // titular de la noticia
                new SqlParameterValue(Types.DATE, date),
                new SqlParameterValue(Types.VARCHAR, link),
                new SqlParameterValue(Types.VARCHAR, imgUrl),
                new SqlParameterValue(Types.VARCHAR, pressMedium),
                new SqlParameterValue(Types.INTEGER, countryId),
                new SqlParameterValue(Types.VARCHAR, summary),
                new SqlParameterValue(Types.VARCHAR, altImgUrl));
    }


}
