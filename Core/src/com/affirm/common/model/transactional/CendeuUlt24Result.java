package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class CendeuUlt24Result {

    private String entityCode;
    private Integer tipoDocumento;
    private String numDocumento;
    private Integer situacion1;
    private Double monto1;
    private Integer revision1;
    private Integer situacion2;
    private Double monto2;
    private Integer revision2;
    private Integer situacion3;
    private Double monto3;
    private Integer revision3;
    private Integer situacion4;
    private Double monto4;
    private Integer revision4;
    private Integer situacion5;
    private Double monto5;
    private Integer revision5;
    private Integer situacion6;
    private Double monto6;
    private Integer revision6;
    private Integer situacion7;
    private Double monto7;
    private Integer revision7;
    private Integer situacion8;
    private Double monto8;
    private Integer revision8;
    private Integer situacion9;
    private Double monto9;
    private Integer revision9;
    private Integer situacion10;
    private Double monto10;
    private Integer revision10;
    private Integer situacion11;
    private Double monto11;
    private Integer revision11;
    private Integer situacion12;
    private Double monto12;
    private Integer revision12;
    private Integer situacion13;
    private Double monto13;
    private Integer revision13;
    private Integer situacion14;
    private Double monto14;
    private Integer revision14;
    private Integer situacion15;
    private Double monto15;
    private Integer revision15;
    private Integer situacion16;
    private Double monto16;
    private Integer revision16;
    private Integer situacion17;
    private Double monto17;
    private Integer revision17;
    private Integer situacion18;
    private Double monto18;
    private Integer revision18;
    private Integer situacion19;
    private Double monto19;
    private Integer revision19;
    private Integer situacion20;
    private Double monto20;
    private Integer revision20;
    private Integer situacion21;
    private Double monto21;
    private Integer revision21;
    private Integer situacion22;
    private Double monto22;
    private Integer revision22;
    private Integer situacion23;
    private Double monto23;
    private Integer revision23;
    private Integer situacion24;
    private Double monto24;
    private Integer revision24;

    public void fillFromDb(JSONObject json) {
        setEntityCode(JsonUtil.getStringFromJson(json, "entity_code", null));
        setTipoDocumento(JsonUtil.getIntFromJson(json, "tipo_documento", null));
        setNumDocumento(JsonUtil.getStringFromJson(json, "num_documento", null));
        setSituacion1(JsonUtil.getIntFromJson(json, "situacion_1", null));
        setMonto1(JsonUtil.getDoubleFromJson(json, "monto_1", null));
        setRevision1(JsonUtil.getIntFromJson(json, "revision_1", null));
        setSituacion2(JsonUtil.getIntFromJson(json, "situacion_2", null));
        setMonto2(JsonUtil.getDoubleFromJson(json, "monto_2", null));
        setRevision2(JsonUtil.getIntFromJson(json, "revision_2", null));
        setSituacion3(JsonUtil.getIntFromJson(json, "situacion_3", null));
        setMonto3(JsonUtil.getDoubleFromJson(json, "monto_3", null));
        setRevision3(JsonUtil.getIntFromJson(json, "revision_3", null));
        setSituacion4(JsonUtil.getIntFromJson(json, "situacion_4", null));
        setMonto4(JsonUtil.getDoubleFromJson(json, "monto_4", null));
        setRevision4(JsonUtil.getIntFromJson(json, "revision_4", null));
        setSituacion5(JsonUtil.getIntFromJson(json, "situacion_5", null));
        setMonto5(JsonUtil.getDoubleFromJson(json, "monto_5", null));
        setRevision5(JsonUtil.getIntFromJson(json, "revision_5", null));
        setSituacion6(JsonUtil.getIntFromJson(json, "situacion_6", null));
        setMonto6(JsonUtil.getDoubleFromJson(json, "monto_6", null));
        setRevision6(JsonUtil.getIntFromJson(json, "revision_6", null));
        setSituacion7(JsonUtil.getIntFromJson(json, "situacion_7", null));
        setMonto7(JsonUtil.getDoubleFromJson(json, "monto_7", null));
        setRevision7(JsonUtil.getIntFromJson(json, "revision_7", null));
        setSituacion8(JsonUtil.getIntFromJson(json, "situacion_8", null));
        setMonto8(JsonUtil.getDoubleFromJson(json, "monto_8", null));
        setRevision8(JsonUtil.getIntFromJson(json, "revision_8", null));
        setSituacion9(JsonUtil.getIntFromJson(json, "situacion_9", null));
        setMonto9(JsonUtil.getDoubleFromJson(json, "monto_9", null));
        setRevision9(JsonUtil.getIntFromJson(json, "revision_9", null));
        setSituacion10(JsonUtil.getIntFromJson(json, "situacion_10", null));
        setMonto10(JsonUtil.getDoubleFromJson(json, "monto_10", null));
        setRevision10(JsonUtil.getIntFromJson(json, "revision_10", null));
        setSituacion11(JsonUtil.getIntFromJson(json, "situacion_11", null));
        setMonto11(JsonUtil.getDoubleFromJson(json, "monto_11", null));
        setRevision11(JsonUtil.getIntFromJson(json, "revision_11", null));
        setSituacion12(JsonUtil.getIntFromJson(json, "situacion_12", null));
        setMonto12(JsonUtil.getDoubleFromJson(json, "monto_12", null));
        setRevision12(JsonUtil.getIntFromJson(json, "revision_12", null));
        setSituacion13(JsonUtil.getIntFromJson(json, "situacion_13", null));
        setMonto13(JsonUtil.getDoubleFromJson(json, "monto_13", null));
        setRevision13(JsonUtil.getIntFromJson(json, "revision_13", null));
        setSituacion14(JsonUtil.getIntFromJson(json, "situacion_14", null));
        setMonto14(JsonUtil.getDoubleFromJson(json, "monto_14", null));
        setRevision14(JsonUtil.getIntFromJson(json, "revision_14", null));
        setSituacion15(JsonUtil.getIntFromJson(json, "situacion_15", null));
        setMonto15(JsonUtil.getDoubleFromJson(json, "monto_15", null));
        setRevision15(JsonUtil.getIntFromJson(json, "revision_15", null));
        setSituacion16(JsonUtil.getIntFromJson(json, "situacion_16", null));
        setMonto16(JsonUtil.getDoubleFromJson(json, "monto_16", null));
        setRevision16(JsonUtil.getIntFromJson(json, "revision_16", null));
        setSituacion17(JsonUtil.getIntFromJson(json, "situacion_17", null));
        setMonto17(JsonUtil.getDoubleFromJson(json, "monto_17", null));
        setRevision17(JsonUtil.getIntFromJson(json, "revision_17", null));
        setSituacion18(JsonUtil.getIntFromJson(json, "situacion_18", null));
        setMonto18(JsonUtil.getDoubleFromJson(json, "monto_18", null));
        setRevision18(JsonUtil.getIntFromJson(json, "revision_18", null));
        setSituacion19(JsonUtil.getIntFromJson(json, "situacion_19", null));
        setMonto19(JsonUtil.getDoubleFromJson(json, "monto_19", null));
        setRevision19(JsonUtil.getIntFromJson(json, "revision_19", null));
        setSituacion20(JsonUtil.getIntFromJson(json, "situacion_20", null));
        setMonto20(JsonUtil.getDoubleFromJson(json, "monto_20", null));
        setRevision20(JsonUtil.getIntFromJson(json, "revision_20", null));
        setSituacion21(JsonUtil.getIntFromJson(json, "situacion_21", null));
        setMonto21(JsonUtil.getDoubleFromJson(json, "monto_21", null));
        setRevision21(JsonUtil.getIntFromJson(json, "revision_21", null));
        setSituacion22(JsonUtil.getIntFromJson(json, "situacion_22", null));
        setMonto22(JsonUtil.getDoubleFromJson(json, "monto_22", null));
        setRevision22(JsonUtil.getIntFromJson(json, "revision_22", null));
        setSituacion23(JsonUtil.getIntFromJson(json, "situacion_23", null));
        setMonto23(JsonUtil.getDoubleFromJson(json, "monto_23", null));
        setRevision23(JsonUtil.getIntFromJson(json, "revision_23", null));
        setSituacion24(JsonUtil.getIntFromJson(json, "situacion_24", null));
        setMonto24(JsonUtil.getDoubleFromJson(json, "monto_24", null));
        setRevision24(JsonUtil.getIntFromJson(json, "revision_24", null));
    }

    public Integer getSituacionOfMonth(int month){
        try {
            Class<?> c = Class.forName("com.affirm.common.model.transactional.CendeuUlt24Result");
            Method method = c.getMethod("getSituacion" + month);
            return (Integer) method.invoke(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public Integer getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(Integer tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public Integer getSituacion1() {
        return situacion1;
    }

    public void setSituacion1(Integer situacion1) {
        this.situacion1 = situacion1;
    }

    public Double getMonto1() {
        return monto1;
    }

    public void setMonto1(Double monto1) {
        this.monto1 = monto1;
    }

    public Integer getRevision1() {
        return revision1;
    }

    public void setRevision1(Integer revision1) {
        this.revision1 = revision1;
    }

    public Integer getSituacion2() {
        return situacion2;
    }

    public void setSituacion2(Integer situacion2) {
        this.situacion2 = situacion2;
    }

    public Double getMonto2() {
        return monto2;
    }

    public void setMonto2(Double monto2) {
        this.monto2 = monto2;
    }

    public Integer getRevision2() {
        return revision2;
    }

    public void setRevision2(Integer revision2) {
        this.revision2 = revision2;
    }

    public Integer getSituacion3() {
        return situacion3;
    }

    public void setSituacion3(Integer situacion3) {
        this.situacion3 = situacion3;
    }

    public Double getMonto3() {
        return monto3;
    }

    public void setMonto3(Double monto3) {
        this.monto3 = monto3;
    }

    public Integer getRevision3() {
        return revision3;
    }

    public void setRevision3(Integer revision3) {
        this.revision3 = revision3;
    }

    public Integer getSituacion4() {
        return situacion4;
    }

    public void setSituacion4(Integer situacion4) {
        this.situacion4 = situacion4;
    }

    public Double getMonto4() {
        return monto4;
    }

    public void setMonto4(Double monto4) {
        this.monto4 = monto4;
    }

    public Integer getRevision4() {
        return revision4;
    }

    public void setRevision4(Integer revision4) {
        this.revision4 = revision4;
    }

    public Integer getSituacion5() {
        return situacion5;
    }

    public void setSituacion5(Integer situacion5) {
        this.situacion5 = situacion5;
    }

    public Double getMonto5() {
        return monto5;
    }

    public void setMonto5(Double monto5) {
        this.monto5 = monto5;
    }

    public Integer getRevision5() {
        return revision5;
    }

    public void setRevision5(Integer revision5) {
        this.revision5 = revision5;
    }

    public Integer getSituacion6() {
        return situacion6;
    }

    public void setSituacion6(Integer situacion6) {
        this.situacion6 = situacion6;
    }

    public Double getMonto6() {
        return monto6;
    }

    public void setMonto6(Double monto6) {
        this.monto6 = monto6;
    }

    public Integer getRevision6() {
        return revision6;
    }

    public void setRevision6(Integer revision6) {
        this.revision6 = revision6;
    }

    public Integer getSituacion7() {
        return situacion7;
    }

    public void setSituacion7(Integer situacion7) {
        this.situacion7 = situacion7;
    }

    public Double getMonto7() {
        return monto7;
    }

    public void setMonto7(Double monto7) {
        this.monto7 = monto7;
    }

    public Integer getRevision7() {
        return revision7;
    }

    public void setRevision7(Integer revision7) {
        this.revision7 = revision7;
    }

    public Integer getSituacion8() {
        return situacion8;
    }

    public void setSituacion8(Integer situacion8) {
        this.situacion8 = situacion8;
    }

    public Double getMonto8() {
        return monto8;
    }

    public void setMonto8(Double monto8) {
        this.monto8 = monto8;
    }

    public Integer getRevision8() {
        return revision8;
    }

    public void setRevision8(Integer revision8) {
        this.revision8 = revision8;
    }

    public Integer getSituacion9() {
        return situacion9;
    }

    public void setSituacion9(Integer situacion9) {
        this.situacion9 = situacion9;
    }

    public Double getMonto9() {
        return monto9;
    }

    public void setMonto9(Double monto9) {
        this.monto9 = monto9;
    }

    public Integer getRevision9() {
        return revision9;
    }

    public void setRevision9(Integer revision9) {
        this.revision9 = revision9;
    }

    public Integer getSituacion10() {
        return situacion10;
    }

    public void setSituacion10(Integer situacion10) {
        this.situacion10 = situacion10;
    }

    public Double getMonto10() {
        return monto10;
    }

    public void setMonto10(Double monto10) {
        this.monto10 = monto10;
    }

    public Integer getRevision10() {
        return revision10;
    }

    public void setRevision10(Integer revision10) {
        this.revision10 = revision10;
    }

    public Integer getSituacion11() {
        return situacion11;
    }

    public void setSituacion11(Integer situacion11) {
        this.situacion11 = situacion11;
    }

    public Double getMonto11() {
        return monto11;
    }

    public void setMonto11(Double monto11) {
        this.monto11 = monto11;
    }

    public Integer getRevision11() {
        return revision11;
    }

    public void setRevision11(Integer revision11) {
        this.revision11 = revision11;
    }

    public Integer getSituacion12() {
        return situacion12;
    }

    public void setSituacion12(Integer situacion12) {
        this.situacion12 = situacion12;
    }

    public Double getMonto12() {
        return monto12;
    }

    public void setMonto12(Double monto12) {
        this.monto12 = monto12;
    }

    public Integer getRevision12() {
        return revision12;
    }

    public void setRevision12(Integer revision12) {
        this.revision12 = revision12;
    }

    public Integer getSituacion13() {
        return situacion13;
    }

    public void setSituacion13(Integer situacion13) {
        this.situacion13 = situacion13;
    }

    public Double getMonto13() {
        return monto13;
    }

    public void setMonto13(Double monto13) {
        this.monto13 = monto13;
    }

    public Integer getRevision13() {
        return revision13;
    }

    public void setRevision13(Integer revision13) {
        this.revision13 = revision13;
    }

    public Integer getSituacion14() {
        return situacion14;
    }

    public void setSituacion14(Integer situacion14) {
        this.situacion14 = situacion14;
    }

    public Double getMonto14() {
        return monto14;
    }

    public void setMonto14(Double monto14) {
        this.monto14 = monto14;
    }

    public Integer getRevision14() {
        return revision14;
    }

    public void setRevision14(Integer revision14) {
        this.revision14 = revision14;
    }

    public Integer getSituacion15() {
        return situacion15;
    }

    public void setSituacion15(Integer situacion15) {
        this.situacion15 = situacion15;
    }

    public Double getMonto15() {
        return monto15;
    }

    public void setMonto15(Double monto15) {
        this.monto15 = monto15;
    }

    public Integer getRevision15() {
        return revision15;
    }

    public void setRevision15(Integer revision15) {
        this.revision15 = revision15;
    }

    public Integer getSituacion16() {
        return situacion16;
    }

    public void setSituacion16(Integer situacion16) {
        this.situacion16 = situacion16;
    }

    public Double getMonto16() {
        return monto16;
    }

    public void setMonto16(Double monto16) {
        this.monto16 = monto16;
    }

    public Integer getRevision16() {
        return revision16;
    }

    public void setRevision16(Integer revision16) {
        this.revision16 = revision16;
    }

    public Integer getSituacion17() {
        return situacion17;
    }

    public void setSituacion17(Integer situacion17) {
        this.situacion17 = situacion17;
    }

    public Double getMonto17() {
        return monto17;
    }

    public void setMonto17(Double monto17) {
        this.monto17 = monto17;
    }

    public Integer getRevision17() {
        return revision17;
    }

    public void setRevision17(Integer revision17) {
        this.revision17 = revision17;
    }

    public Integer getSituacion18() {
        return situacion18;
    }

    public void setSituacion18(Integer situacion18) {
        this.situacion18 = situacion18;
    }

    public Double getMonto18() {
        return monto18;
    }

    public void setMonto18(Double monto18) {
        this.monto18 = monto18;
    }

    public Integer getRevision18() {
        return revision18;
    }

    public void setRevision18(Integer revision18) {
        this.revision18 = revision18;
    }

    public Integer getSituacion19() {
        return situacion19;
    }

    public void setSituacion19(Integer situacion19) {
        this.situacion19 = situacion19;
    }

    public Double getMonto19() {
        return monto19;
    }

    public void setMonto19(Double monto19) {
        this.monto19 = monto19;
    }

    public Integer getRevision19() {
        return revision19;
    }

    public void setRevision19(Integer revision19) {
        this.revision19 = revision19;
    }

    public Integer getSituacion20() {
        return situacion20;
    }

    public void setSituacion20(Integer situacion20) {
        this.situacion20 = situacion20;
    }

    public Double getMonto20() {
        return monto20;
    }

    public void setMonto20(Double monto20) {
        this.monto20 = monto20;
    }

    public Integer getRevision20() {
        return revision20;
    }

    public void setRevision20(Integer revision20) {
        this.revision20 = revision20;
    }

    public Integer getSituacion21() {
        return situacion21;
    }

    public void setSituacion21(Integer situacion21) {
        this.situacion21 = situacion21;
    }

    public Double getMonto21() {
        return monto21;
    }

    public void setMonto21(Double monto21) {
        this.monto21 = monto21;
    }

    public Integer getRevision21() {
        return revision21;
    }

    public void setRevision21(Integer revision21) {
        this.revision21 = revision21;
    }

    public Integer getSituacion22() {
        return situacion22;
    }

    public void setSituacion22(Integer situacion22) {
        this.situacion22 = situacion22;
    }

    public Double getMonto22() {
        return monto22;
    }

    public void setMonto22(Double monto22) {
        this.monto22 = monto22;
    }

    public Integer getRevision22() {
        return revision22;
    }

    public void setRevision22(Integer revision22) {
        this.revision22 = revision22;
    }

    public Integer getSituacion23() {
        return situacion23;
    }

    public void setSituacion23(Integer situacion23) {
        this.situacion23 = situacion23;
    }

    public Double getMonto23() {
        return monto23;
    }

    public void setMonto23(Double monto23) {
        this.monto23 = monto23;
    }

    public Integer getRevision23() {
        return revision23;
    }

    public void setRevision23(Integer revision23) {
        this.revision23 = revision23;
    }

    public Integer getSituacion24() {
        return situacion24;
    }

    public void setSituacion24(Integer situacion24) {
        this.situacion24 = situacion24;
    }

    public Double getMonto24() {
        return monto24;
    }

    public void setMonto24(Double monto24) {
        this.monto24 = monto24;
    }

    public Integer getRevision24() {
        return revision24;
    }

    public void setRevision24(Integer revision24) {
        this.revision24 = revision24;
    }
}
