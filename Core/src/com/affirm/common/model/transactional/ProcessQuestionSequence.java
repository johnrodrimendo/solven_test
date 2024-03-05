package com.affirm.common.model.transactional;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by john on 08/11/16.
 */
public class ProcessQuestionSequence implements Serializable {

    public static final int TYPE_FORWARD = 1;
    public static final int TYPE_BACKWARD = 2;
    public static final int TYPE_SKIPPED = 3;

    private Integer id;
    private Integer type;
    private Date date;
    private Date finishDate;
    private String createdUserAgent;

    public ProcessQuestionSequence() {
    }

    public ProcessQuestionSequence(Integer id, Integer type, Date date, Date finishDate, String createdUserAgent) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.finishDate = finishDate;
        this.createdUserAgent = createdUserAgent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public String getCreatedUserAgent() {
        return createdUserAgent;
    }

    public void setCreatedUserAgent(String createdUserAgent) {
        this.createdUserAgent = createdUserAgent;
    }
}
