package com.affirm.common.dao.impl;

import com.affirm.common.dao.ExtranetNoteDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.OriginatorDAO;
import com.affirm.common.model.ExtranetNote;
import com.affirm.common.model.FunnelReport;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Repository
public class ExtranetNoteDAOImpl extends JsonResolverDAO implements ExtranetNoteDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public List<ExtranetNote> getExtranetMenuNotes(Integer entityId, Integer menuId,Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        JSONArray dbArray = queryForObject("SELECT * FROM support.get_notes_by_menu_and_entity(?, ?,?,?);",JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, menuId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit)
                );
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<ExtranetNote> data = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ExtranetNote extranetNote = new ExtranetNote();
            extranetNote.fillFromDb(dbArray.getJSONObject(i), catalogService);
            data.add(extranetNote);
        }
        return data;
    }


    @Override
    public ExtranetNote getExtranetMenuNote(Integer extranetNoteId) {
        JSONObject dbJson = queryForObject("SELECT * FROM support.get_notes_by_id(?);",JSONObject.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, extranetNoteId)
        );
        if (dbJson == null) return null;

        ExtranetNote data = new ExtranetNote();
        data.fillFromDb(dbJson, catalogService);
        return data;
    }


    @Override
    public void insExtranetMenuNote(ExtranetNote data) {
        updateTrx("INSERT INTO support.tb_extranet_note(entity_user_id,entity_id,extranet_menu_id,type,note) VALUES (?,?,?,?,?)",
                new SqlParameterValue(Types.INTEGER, data.getEntityUserId()),
                new SqlParameterValue(Types.INTEGER, data.getEntityId()),
                new SqlParameterValue(Types.INTEGER, data.getExtranetMenuId()),
                new SqlParameterValue(Types.VARCHAR, data.getType()),
                new SqlParameterValue(Types.VARCHAR, data.getNote())
                );
    }

    @Override
    public void editExtranetMenuNote(ExtranetNote data) {
        updateTrx("UPDATE support.tb_extranet_note SET note = ?, type = ? WHERE entity_id = ? AND extranet_note_id = ?",
                new SqlParameterValue(Types.VARCHAR, data.getNote()),
                new SqlParameterValue(Types.VARCHAR, data.getType()),
                new SqlParameterValue(Types.INTEGER, data.getEntityId()),
                new SqlParameterValue(Types.INTEGER, data.getId())

        );
    }

    @Override
    public Pair<Integer, Double> getNotesCount(Integer entityId, Integer menuId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from support.get_notes_by_menu_and_entity_count(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, menuId),
                new SqlParameterValue(Types.INTEGER, entityId));

        if (dbJson == null)
            return Pair.of(0, 0.0);

        return Pair.of(JsonUtil.getIntFromJson(dbJson, "count", 0), null);
    }



}

