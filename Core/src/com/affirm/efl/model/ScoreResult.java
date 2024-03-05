package com.affirm.efl.model;



import java.security.Timestamp;
import java.util.List;


/**
 * Created by dev5 on 06/07/17.
 */
public class ScoreResult {

    private Timestamp birthday;
    private String externalKey;
    private List<String> subjectData;
    private List<Identification> identification;
    private List<Score> scores;
    private String answerReliabilityFlag;
    private String eflId;
    private String fullname;
    private String statusMessage;
    private String status;

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
    }

    public List<String> getSubjectData() {
        return subjectData;
    }

    public void setSubjectData(List<String> subjectData) {
        this.subjectData = subjectData;
    }

    public List<Identification> getIdentification() {
        return identification;
    }

    public void setIdentification(List<Identification> identification) {
        this.identification = identification;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public String getAnswerReliabilityFlag() {
        return answerReliabilityFlag;
    }

    public void setAnswerReliabilityFlag(String answerReliabilityFlag) {
        this.answerReliabilityFlag = answerReliabilityFlag;
    }

    public String getEflId() {
        return eflId;
    }

    public void setEflId(String eflId) {
        this.eflId = eflId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
