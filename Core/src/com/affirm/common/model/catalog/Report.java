/**
 *
 */
package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class Report implements Serializable {

    public static final int REPORTE_SOLICITUDES_BO = 1;
    public static final int REPORTE_ORIGINACIONES_BO = 2;
    public static final int REPORTE_FUNNEL = 3;
    public static final int REPORTE_GESTION_OPERADORES = 4;
    public static final int REPORTE_CONSOLIDADO_DEUDORES = 5;
    public static final int REPORTE_DE_PANTALLAS = 6;
    public static final int REPORTE_DE_PANTALLAS_RECORRIDAS = 7;
    public static final int REPORTE_FUNNEL_DASHBOARD_BO = 8;
    public static final int REPORTE_SOLICITUDES_EN_PROCESO_EXT_BDS = 9;
    public static final int REPORTE_CREDITOS_A_DESEMBOLSAR_EXT_BDS = 10;
    public static final int REPORTE_CREDITOS_DESEMBOLSADOS_EXT_BDS = 11;
    public static final int REPORTE_RIESGOS_EXT_BDS = 12;
    public static final int REPORTE_SOLICITUDES_EN_PROCESO_EXT_FDLM = 13;
    public static final int REPORTE_SOLICITUDES_LIGHT = 14;
    public static final int REPORTE_FUNNELV3 = 15;
    public static final int REPORTE_BANDEJAS_EXTRANET = 16;
    public static final int REPORTE_FUNNEL_COLLECTION = 17;

    private Integer id;
    private Integer webAppId;

    public Report() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWebAppId() {
        return webAppId;
    }

    public void setWebAppId(Integer webAppId) {
        this.webAppId = webAppId;
    }
}
