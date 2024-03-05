package com.affirm.common.util;

import com.affirm.common.model.catalog.ProcessQuestion;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TreeProcessQuestionWrapper {

    private ProcessQuestion processQuestion;
    private boolean skipped = false;
    private List<TreeProcessQuestionWrapper> childs;
    private JSONObject params;

    public void addChild(TreeProcessQuestionWrapper child) {
        if (childs == null)
            childs = new ArrayList<>();
        childs.add(child);
    }

    public TreeProcessQuestionWrapper getLastChildOfTree() {
        if (childs == null || childs.isEmpty() || childs.get(0) == null)
            return null;

        TreeProcessQuestionWrapper childrenOfChild = childs.get(0).getLastChildOfTree();
        if (childrenOfChild == null)
            return childs.get(0);
        else
            return childrenOfChild;
    }

    public void addParam(String key, Object value) {
        if (params == null)
            params = new JSONObject();
        params.put(key, value);
    }

    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    public ProcessQuestion getProcessQuestion() {
        return processQuestion;
    }

    public void setProcessQuestion(ProcessQuestion processQuestion) {
        this.processQuestion = processQuestion;
    }

    public List<TreeProcessQuestionWrapper> getChilds() {
        return childs;
    }

    public void setChilds(List<TreeProcessQuestionWrapper> childs) {
        this.childs = childs;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }
}
