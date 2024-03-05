package com.affirm.common.model.rcc;

import com.affirm.common.model.catalog.RccEntity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;


public class Synthesized {

    private Integer codMes;
    private Date fecRep;
    private Character tipTri;
    private Character tipIde;
    private String numIde;
    private String numTri;
    private String nomDeu;
    private String apeMat;
    private String apeCas;
    private String priNom;
    private String segNom;
    private String codEmp;
    private Double microSaldoTc;
    private Double microPrestamosRev;
    private Double microSobregiros;
    private Double microPrestamosNoRev;
    private Double microArrendamiento;
    private Double microLeaseBack;
    private Double microLineaCredito;
    private Double pequenaSaldoTc;
    private Double pequenaPrestamosRev;
    private Double pequenaSobregiros;
    private Double pequenaPrestamosNoRev;
    private Double pequenaArrendamiento;
    private Double pequenaLeaseBack;
    private Double pequenaLineaCredito;
    private Double medianaSaldoTc;
    private Double medianaPrestamosRev;
    private Double medianaSobregiros;
    private Double medianaPrestamosNoRev;
    private Double medianaArrendamiento;
    private Double medianaLeaseBack;
    private Double medianaLineaCredito;
    private Double granSaldoTc;
    private Double granPrestamosRev;
    private Double granSobregiros;
    private Double granPrestamosNoRev;
    private Double granArrendamiento;
    private Double granLeaseBack;
    private Double granLineaCredito;
    private Double hipotecario;
    private Double consumoSaldoTc;
    private Double consumoPrestamosRev;
    private Double consumoSobregiros;
    private Double consumoPrestamosNoRev;
    private Double consumoLineaCredito;
    private Double lineaTc;
    private Double saldoCastigo;
    private Double saldoVigente;
    private Double saldoReestructurado;
    private Double saldoRefinanciado;
    private Double saldoVencido;
    private Double saldoJudicial;
    private Double diasAtraso;
    private Integer calificacion;
    private Integer experiencia;
    private Double saldo;
    private Double consumoSaldoCv;
    private String entityShortName;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setCodMes(JsonUtil.getIntFromJson(json, "cod_mes", null));
        setFecRep(JsonUtil.getPostgresDateFromJson(json, "fec_rep", null));
        setTipTri(JsonUtil.getCharacterFromJson(json, "tip_tri", null));
        setTipIde(JsonUtil.getCharacterFromJson(json, "tip_ide", null));
        setNumTri(JsonUtil.getStringFromJson(json, "num_tri", null));
        setNomDeu(JsonUtil.getStringFromJson(json, "nom_deu", null));
        setApeMat(JsonUtil.getStringFromJson(json, "ape_mat", null));
        setApeCas(JsonUtil.getStringFromJson(json, "ape_cas", null));
        setPriNom(JsonUtil.getStringFromJson(json, "pri_nom", null));
        setSegNom(JsonUtil.getStringFromJson(json, "seg_nom", null));
        setCodEmp(JsonUtil.getStringFromJson(json, "cod_emp", null));
        setMicroSaldoTc(JsonUtil.getDoubleFromJson(json, "micro_saldo_tc", null));
        setMicroPrestamosRev(JsonUtil.getDoubleFromJson(json, "micro_prestamos_rev", null));
        setMicroSobregiros(JsonUtil.getDoubleFromJson(json, "micro_sobregiros", null));
        setMicroPrestamosNoRev(JsonUtil.getDoubleFromJson(json, "micro_prestamos_no_rev", null));
        setMicroArrendamiento(JsonUtil.getDoubleFromJson(json, "micro_arrendamiento", null));
        setMicroLeaseBack(JsonUtil.getDoubleFromJson(json, "micro_lease_back", null));
        setMicroLineaCredito(JsonUtil.getDoubleFromJson(json, "micro_linea_credito", null));
        setPequenaSaldoTc(JsonUtil.getDoubleFromJson(json, "pequena_saldo_tc", null));
        setPequenaPrestamosRev(JsonUtil.getDoubleFromJson(json, "pequena_prestamos_rev", null));
        setPequenaSobregiros(JsonUtil.getDoubleFromJson(json, "pequena_sobregiros", null));
        setPequenaPrestamosNoRev(JsonUtil.getDoubleFromJson(json, "pequena_prestamos_no_rev", null));
        setPequenaArrendamiento(JsonUtil.getDoubleFromJson(json, "pequena_arrendamiento", null));
        setPequenaLeaseBack(JsonUtil.getDoubleFromJson(json, "pequena_lease_back", null));
        setPequenaLineaCredito(JsonUtil.getDoubleFromJson(json, "pequena_linea_credito", null));
        setMedianaSaldoTc(JsonUtil.getDoubleFromJson(json, "mediana_saldo_tc", null));
        setMedianaPrestamosRev(JsonUtil.getDoubleFromJson(json, "mediana_prestamos_rev", null));
        setMedianaSobregiros(JsonUtil.getDoubleFromJson(json, "mediana_sobregiros", null));
        setMedianaPrestamosNoRev(JsonUtil.getDoubleFromJson(json, "mediana_prestamos_no_rev", null));
        setMedianaArrendamiento(JsonUtil.getDoubleFromJson(json, "mediana_arrendamiento", null));
        setMedianaLeaseBack(JsonUtil.getDoubleFromJson(json, "mediana_lease_back", null));
        setMedianaLineaCredito(JsonUtil.getDoubleFromJson(json, "mediana_linea_credito", null));
        setGranSaldoTc(JsonUtil.getDoubleFromJson(json, "gran_saldo_tc", null));
        setGranPrestamosRev(JsonUtil.getDoubleFromJson(json, "gran_prestamos_rev", null));
        setGranSobregiros(JsonUtil.getDoubleFromJson(json, "gran_sobregiros", null));
        setGranPrestamosNoRev(JsonUtil.getDoubleFromJson(json, "gran_prestamos_no_rev", null));
        setGranArrendamiento(JsonUtil.getDoubleFromJson(json, "gran_arrendamiento", null));
        setGranLeaseBack(JsonUtil.getDoubleFromJson(json, "gran_lease_back", null));
        setGranLineaCredito(JsonUtil.getDoubleFromJson(json, "gran_linea_credito", null));
        setHipotecario(JsonUtil.getDoubleFromJson(json, "hipotecario", null));
        setConsumoSaldoTc(JsonUtil.getDoubleFromJson(json, "consumo_saldo_tc", null));
        setConsumoPrestamosRev(JsonUtil.getDoubleFromJson(json, "consumo_prestamos_rev", null));
        setConsumoSobregiros(JsonUtil.getDoubleFromJson(json, "consumo_sobregiros", null));
        setConsumoPrestamosNoRev(JsonUtil.getDoubleFromJson(json, "consumo_prestamos_no_rev", null));
        setConsumoLineaCredito(JsonUtil.getDoubleFromJson(json, "consumo_linea_credito", null));
        setLineaTc(JsonUtil.getDoubleFromJson(json, "linea_tc", null));
        setSaldoCastigo(JsonUtil.getDoubleFromJson(json, "saldo_castigo", null));
        setSaldoVigente(JsonUtil.getDoubleFromJson(json, "saldo_vigente", null));
        setSaldoReestructurado(JsonUtil.getDoubleFromJson(json, "saldo_reestructurado", null));
        setSaldoRefinanciado(JsonUtil.getDoubleFromJson(json, "saldo_refinanciado", null));
        setSaldoVencido(JsonUtil.getDoubleFromJson(json, "saldo_vencido", null));
        setSaldoJudicial(JsonUtil.getDoubleFromJson(json, "saldo_judicial", null));
        setDiasAtraso(JsonUtil.getDoubleFromJson(json, "dias_atraso", null));
        setCalificacion(JsonUtil.getIntFromJson(json, "calificacion", null));
        setExperiencia(JsonUtil.getIntFromJson(json, "experiencia", null));
        setSaldo(JsonUtil.getDoubleFromJson(json, "saldo", null));
        setConsumoSaldoCv(JsonUtil.getDoubleFromJson(json, "consumo_saldo_cv", null));
        if(codEmp != null){
            try{
                RccEntity rccEntity = catalogService.getRccEntity(codEmp);
                if(rccEntity != null) setEntityShortName(rccEntity.getShortName());
            }
            catch (Exception e){

            }
        }
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

    public Character getTipTri() {
        return tipTri;
    }

    public void setTipTri(Character tipTri) {
        this.tipTri = tipTri;
    }

    public Character getTipIde() {
        return tipIde;
    }

    public void setTipIde(Character tipIde) {
        this.tipIde = tipIde;
    }

    public String getNumIde() {
        return numIde;
    }

    public void setNumIde(String numIde) {
        this.numIde = numIde;
    }

    public String getNumTri() {
        return numTri;
    }

    public void setNumTri(String numTri) {
        this.numTri = numTri;
    }

    public String getNomDeu() {
        return nomDeu;
    }

    public void setNomDeu(String nomDeu) {
        this.nomDeu = nomDeu;
    }

    public String getApeMat() {
        return apeMat;
    }

    public void setApeMat(String apeMat) {
        this.apeMat = apeMat;
    }

    public String getApeCas() {
        return apeCas;
    }

    public void setApeCas(String apeCas) {
        this.apeCas = apeCas;
    }

    public String getPriNom() {
        return priNom;
    }

    public void setPriNom(String priNom) {
        this.priNom = priNom;
    }

    public String getSegNom() {
        return segNom;
    }

    public void setSegNom(String segNom) {
        this.segNom = segNom;
    }

    public String getCodEmp() {
        return codEmp;
    }

    public void setCodEmp(String codEmp) {
        this.codEmp = codEmp;
    }

    public Double getMicroSaldoTc() {
        return microSaldoTc;
    }

    public void setMicroSaldoTc(Double microSaldoTc) {
        this.microSaldoTc = microSaldoTc;
    }

    public Double getMicroPrestamosRev() {
        return microPrestamosRev;
    }

    public void setMicroPrestamosRev(Double microPrestamosRev) {
        this.microPrestamosRev = microPrestamosRev;
    }

    public Double getMicroSobregiros() {
        return microSobregiros;
    }

    public void setMicroSobregiros(Double microSobregiros) {
        this.microSobregiros = microSobregiros;
    }

    public Double getMicroPrestamosNoRev() {
        return microPrestamosNoRev;
    }

    public void setMicroPrestamosNoRev(Double microPrestamosNoRev) {
        this.microPrestamosNoRev = microPrestamosNoRev;
    }

    public Double getMicroArrendamiento() {
        return microArrendamiento;
    }

    public void setMicroArrendamiento(Double microArrendamiento) {
        this.microArrendamiento = microArrendamiento;
    }

    public Double getMicroLeaseBack() {
        return microLeaseBack;
    }

    public void setMicroLeaseBack(Double microLeaseBack) {
        this.microLeaseBack = microLeaseBack;
    }

    public Double getMicroLineaCredito() {
        return microLineaCredito;
    }

    public void setMicroLineaCredito(Double microLineaCredito) {
        this.microLineaCredito = microLineaCredito;
    }

    public Double getPequenaSaldoTc() {
        return pequenaSaldoTc;
    }

    public void setPequenaSaldoTc(Double pequenaSaldoTc) {
        this.pequenaSaldoTc = pequenaSaldoTc;
    }

    public Double getPequenaPrestamosRev() {
        return pequenaPrestamosRev;
    }

    public void setPequenaPrestamosRev(Double pequenaPrestamosRev) {
        this.pequenaPrestamosRev = pequenaPrestamosRev;
    }

    public Double getPequenaSobregiros() {
        return pequenaSobregiros;
    }

    public void setPequenaSobregiros(Double pequenaSobregiros) {
        this.pequenaSobregiros = pequenaSobregiros;
    }

    public Double getPequenaPrestamosNoRev() {
        return pequenaPrestamosNoRev;
    }

    public void setPequenaPrestamosNoRev(Double pequenaPrestamosNoRev) {
        this.pequenaPrestamosNoRev = pequenaPrestamosNoRev;
    }

    public Double getPequenaArrendamiento() {
        return pequenaArrendamiento;
    }

    public void setPequenaArrendamiento(Double pequenaArrendamiento) {
        this.pequenaArrendamiento = pequenaArrendamiento;
    }

    public Double getPequenaLeaseBack() {
        return pequenaLeaseBack;
    }

    public void setPequenaLeaseBack(Double pequenaLeaseBack) {
        this.pequenaLeaseBack = pequenaLeaseBack;
    }

    public Double getPequenaLineaCredito() {
        return pequenaLineaCredito;
    }

    public void setPequenaLineaCredito(Double pequenaLineaCredito) {
        this.pequenaLineaCredito = pequenaLineaCredito;
    }

    public Double getMedianaSaldoTc() {
        return medianaSaldoTc;
    }

    public void setMedianaSaldoTc(Double medianaSaldoTc) {
        this.medianaSaldoTc = medianaSaldoTc;
    }

    public Double getMedianaPrestamosRev() {
        return medianaPrestamosRev;
    }

    public void setMedianaPrestamosRev(Double medianaPrestamosRev) {
        this.medianaPrestamosRev = medianaPrestamosRev;
    }

    public Double getMedianaSobregiros() {
        return medianaSobregiros;
    }

    public void setMedianaSobregiros(Double medianaSobregiros) {
        this.medianaSobregiros = medianaSobregiros;
    }

    public Double getMedianaPrestamosNoRev() {
        return medianaPrestamosNoRev;
    }

    public void setMedianaPrestamosNoRev(Double medianaPrestamosNoRev) {
        this.medianaPrestamosNoRev = medianaPrestamosNoRev;
    }

    public Double getMedianaArrendamiento() {
        return medianaArrendamiento;
    }

    public void setMedianaArrendamiento(Double medianaArrendamiento) {
        this.medianaArrendamiento = medianaArrendamiento;
    }

    public Double getMedianaLeaseBack() {
        return medianaLeaseBack;
    }

    public void setMedianaLeaseBack(Double medianaLeaseBack) {
        this.medianaLeaseBack = medianaLeaseBack;
    }

    public Double getMedianaLineaCredito() {
        return medianaLineaCredito;
    }

    public void setMedianaLineaCredito(Double medianaLineaCredito) {
        this.medianaLineaCredito = medianaLineaCredito;
    }

    public Double getGranSaldoTc() {
        return granSaldoTc;
    }

    public void setGranSaldoTc(Double granSaldoTc) {
        this.granSaldoTc = granSaldoTc;
    }

    public Double getGranPrestamosRev() {
        return granPrestamosRev;
    }

    public void setGranPrestamosRev(Double granPrestamosRev) {
        this.granPrestamosRev = granPrestamosRev;
    }

    public Double getGranSobregiros() {
        return granSobregiros;
    }

    public void setGranSobregiros(Double granSobregiros) {
        this.granSobregiros = granSobregiros;
    }

    public Double getGranPrestamosNoRev() {
        return granPrestamosNoRev;
    }

    public void setGranPrestamosNoRev(Double granPrestamosNoRev) {
        this.granPrestamosNoRev = granPrestamosNoRev;
    }

    public Double getGranArrendamiento() {
        return granArrendamiento;
    }

    public void setGranArrendamiento(Double granArrendamiento) {
        this.granArrendamiento = granArrendamiento;
    }

    public Double getGranLeaseBack() {
        return granLeaseBack;
    }

    public void setGranLeaseBack(Double granLeaseBack) {
        this.granLeaseBack = granLeaseBack;
    }

    public Double getGranLineaCredito() {
        return granLineaCredito;
    }

    public void setGranLineaCredito(Double granLineaCredito) {
        this.granLineaCredito = granLineaCredito;
    }

    public Double getHipotecario() {
        return hipotecario;
    }

    public void setHipotecario(Double hipotecario) {
        this.hipotecario = hipotecario;
    }

    public Double getConsumoSaldoTc() {
        return consumoSaldoTc;
    }

    public void setConsumoSaldoTc(Double consumoSaldoTc) {
        this.consumoSaldoTc = consumoSaldoTc;
    }

    public Double getConsumoPrestamosRev() {
        return consumoPrestamosRev;
    }

    public void setConsumoPrestamosRev(Double consumoPrestamosRev) {
        this.consumoPrestamosRev = consumoPrestamosRev;
    }

    public Double getConsumoSobregiros() {
        return consumoSobregiros;
    }

    public void setConsumoSobregiros(Double consumoSobregiros) {
        this.consumoSobregiros = consumoSobregiros;
    }

    public Double getConsumoPrestamosNoRev() {
        return consumoPrestamosNoRev;
    }

    public void setConsumoPrestamosNoRev(Double consumoPrestamosNoRev) {
        this.consumoPrestamosNoRev = consumoPrestamosNoRev;
    }

    public Double getConsumoLineaCredito() {
        return consumoLineaCredito;
    }

    public void setConsumoLineaCredito(Double consumoLineaCredito) {
        this.consumoLineaCredito = consumoLineaCredito;
    }

    public Double getLineaTc() {
        return lineaTc;
    }

    public void setLineaTc(Double lineaTc) {
        this.lineaTc = lineaTc;
    }

    public Double getSaldoCastigo() {
        return saldoCastigo;
    }

    public void setSaldoCastigo(Double saldoCastigo) {
        this.saldoCastigo = saldoCastigo;
    }

    public Double getSaldoVigente() {
        return saldoVigente;
    }

    public void setSaldoVigente(Double saldoVigente) {
        this.saldoVigente = saldoVigente;
    }

    public Double getSaldoReestructurado() {
        return saldoReestructurado;
    }

    public void setSaldoReestructurado(Double saldoReestructurado) {
        this.saldoReestructurado = saldoReestructurado;
    }

    public Double getSaldoRefinanciado() {
        return saldoRefinanciado;
    }

    public void setSaldoRefinanciado(Double saldoRefinanciado) {
        this.saldoRefinanciado = saldoRefinanciado;
    }

    public Double getSaldoVencido() {
        return saldoVencido;
    }

    public void setSaldoVencido(Double saldoVencido) {
        this.saldoVencido = saldoVencido;
    }

    public Double getSaldoJudicial() {
        return saldoJudicial;
    }

    public void setSaldoJudicial(Double saldoJudicial) {
        this.saldoJudicial = saldoJudicial;
    }

    public Double getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(Double diasAtraso) {
        this.diasAtraso = diasAtraso;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public Integer getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(Integer experiencia) {
        this.experiencia = experiencia;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Double getConsumoSaldoCv() {
        return consumoSaldoCv;
    }

    public void setConsumoSaldoCv(Double consumoSaldoCv) {
        this.consumoSaldoCv = consumoSaldoCv;
    }

    public String getEntityShortName() {
        return entityShortName;
    }

    public void setEntityShortName(String entityShortName) {
        this.entityShortName = entityShortName;
    }
}
