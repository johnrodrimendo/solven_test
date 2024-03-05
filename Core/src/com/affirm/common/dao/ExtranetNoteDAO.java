package com.affirm.common.dao;

import com.affirm.common.model.ExtranetNote;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ExtranetNoteDAO {

    List<ExtranetNote> getExtranetMenuNotes(Integer entityId, Integer menuId,Integer offset, Integer limit);

    ExtranetNote getExtranetMenuNote(Integer extranetNoteId);

    void insExtranetMenuNote(ExtranetNote data);

    void editExtranetMenuNote(ExtranetNote data);

    Pair<Integer, Double> getNotesCount(Integer entityId, Integer menuId) throws Exception;

}
