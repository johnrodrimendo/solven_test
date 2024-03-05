package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class FunnelReport {

    private Integer step1;
    private Integer step2;
    private Integer step3;
    private Integer step4;
    private Integer step5;
    private Integer step6;
    private Integer step7;
    private Integer step8;
    private Integer step9;
    private Integer step10;
    private Integer step11;
    private Integer step12;
    private Integer step13;
    private Integer step14;
    private Integer step15;
    private Integer step16;
    private Integer step17;
    private Integer step18;
    private Integer step21;
    private Integer step22;


    public void fillFromDb(JSONObject json) {
        setStep1(JsonUtil.getIntFromJson(json, "step_1", null));
        setStep2(JsonUtil.getIntFromJson(json, "step_2", null));
        setStep3(JsonUtil.getIntFromJson(json, "step_3", null));
        setStep4(JsonUtil.getIntFromJson(json, "step_4", null));
        setStep5(JsonUtil.getIntFromJson(json, "step_5", null));
        setStep6(JsonUtil.getIntFromJson(json, "step_6", null));
        setStep7(JsonUtil.getIntFromJson(json, "step_7", null));
        setStep8(JsonUtil.getIntFromJson(json, "step_8", null));
        setStep9(JsonUtil.getIntFromJson(json, "step_9", null));
        setStep10(JsonUtil.getIntFromJson(json, "step_10", null));
        setStep11(JsonUtil.getIntFromJson(json, "step_11", null));
        setStep12(JsonUtil.getIntFromJson(json, "step_12", null));
        setStep13(JsonUtil.getIntFromJson(json, "step_13", null));
        setStep14(JsonUtil.getIntFromJson(json, "step_14", null));
        setStep15(JsonUtil.getIntFromJson(json, "step_15", null));
        setStep16(JsonUtil.getIntFromJson(json, "step_16", null));
        setStep17(JsonUtil.getIntFromJson(json, "step_17", null));
        setStep18(JsonUtil.getIntFromJson(json, "step_18", null));
        setStep21(JsonUtil.getIntFromJson(json, "step_21", null));
        setStep22(JsonUtil.getIntFromJson(json, "step_22", null));
    }

    public double getTotal(){
        return step1 + step2 + step3 + step4 + step5 + step6 + step7 + step8;
    }

    public Integer getStep1() {
        return step1;
    }

    public void setStep1(Integer step1) {
        this.step1 = step1;
    }

    public Integer getStep2() {
        return step2;
    }

    public void setStep2(Integer step2) {
        this.step2 = step2;
    }

    public Integer getStep3() {
        return step3;
    }

    public void setStep3(Integer step3) {
        this.step3 = step3;
    }

    public Integer getStep4() {
        return step4;
    }

    public void setStep4(Integer step4) {
        this.step4 = step4;
    }

    public Integer getStep5() {
        return step5;
    }

    public void setStep5(Integer step5) {
        this.step5 = step5;
    }

    public Integer getStep6() {
        return step6;
    }

    public void setStep6(Integer step6) {
        this.step6 = step6;
    }

    public Integer getStep7() {
        return step7;
    }

    public void setStep7(Integer step7) {
        this.step7 = step7;
    }

    public Integer getStep8() {
        return step8;
    }

    public void setStep8(Integer step8) {
        this.step8 = step8;
    }

    public Integer getStep9() {
        return step9;
    }

    public void setStep9(Integer step9) {
        this.step9 = step9;
    }

    public Integer getStep10() {
        return step10;
    }

    public void setStep10(Integer step10) {
        this.step10 = step10;
    }

    public Integer getStep11() {
        return step11;
    }

    public void setStep11(Integer step11) {
        this.step11 = step11;
    }

    public Integer getStep12() {
        return step12;
    }

    public void setStep12(Integer step12) {
        this.step12 = step12;
    }

    public Integer getStep13() {
        return step13;
    }

    public void setStep13(Integer step13) {
        this.step13 = step13;
    }

    public Integer getStep14() {
        return step14;
    }

    public void setStep14(Integer step14) {
        this.step14 = step14;
    }

    public Integer getStep15() {
        return step15;
    }

    public void setStep15(Integer step15) {
        this.step15 = step15;
    }

    public Integer getStep16() {
        return step16;
    }

    public void setStep16(Integer step16) {
        this.step16 = step16;
    }

    public Integer getStep17() {
        return step17;
    }

    public void setStep17(Integer step17) {
        this.step17 = step17;
    }

    public Integer getStep18() {
        return step18;
    }

    public void setStep18(Integer step18) {
        this.step18 = step18;
    }

    public Integer getStep21() {
        return step21;
    }

    public void setStep21(Integer step21) {
        this.step21 = step21;
    }

    public Integer getStep22() {
        return step22;
    }

    public void setStep22(Integer step22) {
        this.step22 = step22;
    }
}
