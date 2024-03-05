/**
 *
 */
package com.affirm.common.util;

/**
 * @author jrodriguez
 * <p>
 */
public class GoToNextQuestionException extends RuntimeException {

    private String result;
    private Integer sequenceType;

    public GoToNextQuestionException(String result, Integer sequenceType) {
        super();
        this.result = result;
        this.sequenceType = sequenceType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(Integer sequenceType) {
        this.sequenceType = sequenceType;
    }
}
