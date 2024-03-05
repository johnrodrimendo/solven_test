package com.affirm.bantotalrest.model.common;

import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DEPE6S1 {

    private Integer NumCuo;
    private String FecVec;
    private String Tipo;
    private Double ImpCuota;
    private Double TotPago;
    private Double ImpMora;

    public Integer getNumCuo() {
        return NumCuo;
    }

    public void setNumCuo(Integer numCuo) {
        NumCuo = numCuo;
    }

    public String getFecVec() {
        return FecVec;
    }

    public void setFecVec(String fecVec) {
        FecVec = fecVec;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public Double getImpCuota() {
        return ImpCuota;
    }

    public void setImpCuota(Double impCuota) {
        ImpCuota = impCuota;
    }

    public Double getTotPago() {
        return TotPago;
    }

    public void setTotPago(Double totPago) {
        TotPago = totPago;
    }

    public Double getImpMora() {
        return ImpMora;
    }

    public void setImpMora(Double impMora) {
        ImpMora = impMora;
    }

    public Date getFechaPagoAsDate() throws Exception{
        if(FecVec != null)
            return new SimpleDateFormat("yyyy-MM-dd").parse(FecVec);
        return null;
    }

    public int getDaysWithDebt() throws Exception{
        Date paymentDate = new SimpleDateFormat("yyyy-MM-dd").parse(FecVec);
        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        Calendar paymentCal = Calendar.getInstance();
        paymentCal.setTime(paymentDate);
        Calendar currendCal = Calendar.getInstance();
        long daysBetween = ChronoUnit.DAYS.between(paymentCal.toInstant(), currendCal.toInstant());
        if(daysBetween > 0){
            return Math.toIntExact(daysBetween);
        }
        return 0;
    }

    public boolean canBePayed() throws Exception{
        Date paymentDate = new SimpleDateFormat("yyyy-MM-dd").parse(FecVec);
        Calendar m_calendar=Calendar.getInstance();
        m_calendar.setTime(paymentDate);
        int nMonth1=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
        m_calendar.setTime(new Date());
        int nMonth2=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
        int monthsBeetwen = nMonth1-nMonth2;
        return monthsBeetwen <= 2;
    }
}
