package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class RccDate {

    private Integer codMes;
    private Date fecRep;

    public void fillFromDb(JSONObject json) throws Exception {
        setCodMes(JsonUtil.getIntFromJson(json, "cod_mes", null));
        setFecRep(JsonUtil.getPostgresDateFromJson(json, "fec_rep", null));
    }

    public Integer getCodMes() {
        return codMes;
    }

    public void setCodMes(Integer codMes) {
        this.codMes = codMes;
    }

    public Date getFecRep() {
        return fecRep;
    }

    public void setFecRep(Date fecRep) {
        this.fecRep = fecRep;
    }
}
