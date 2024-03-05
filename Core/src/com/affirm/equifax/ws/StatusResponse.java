
package com.affirm.equifax.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para statusResponse.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="statusResponse">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RSP_SUCCESS"/>
 *     &lt;enumeration value="RSP_ERROR"/>
 *     &lt;enumeration value="RSP_EC"/>
 *     &lt;enumeration value="RSP_EC_SERV_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_SERV_CA_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_SERV_PM_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_SERV_OP2_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_SERV_EMAIL_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_SERV_IDV_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_SERV_EXPERT_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_SERV_DAS_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_SERV_RIO_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_SERV_DF_NOT_AVAILABLE"/>
 *     &lt;enumeration value="RSP_EC_REQ_INCORRECT"/>
 *     &lt;enumeration value="RSP_EC_REQ_HEADER_INCORRECT"/>
 *     &lt;enumeration value="RSP_EC_REQ_NOT_AUTHENTICATED"/>
 *     &lt;enumeration value="RSP_EC_REQ_NOT_AUTHORIZED"/>
 *     &lt;enumeration value="RSP_EC_REQ_TIMEOUT"/>
 *     &lt;enumeration value="RSP_EC_REQ_VERY_BIG"/>
 *     &lt;enumeration value="RSP_EC_REQ_URL_VERY_LONG"/>
 *     &lt;enumeration value="RSP_EC_REQ_MEDIA_TYPE_INCORRECT"/>
 *     &lt;enumeration value="RSP_EI"/>
 *     &lt;enumeration value="RSP_EI_VALUES_NULL"/>
 *     &lt;enumeration value="RSP_EI_CA_TOKEN_NULL"/>
 *     &lt;enumeration value="RSP_EI_OP2_COD_ENTI_NULL"/>
 *     &lt;enumeration value="RSP_EI_VALUES_EMPTY"/>
 *     &lt;enumeration value="RSP_EI_CA_TOKEN_EMPTY"/>
 *     &lt;enumeration value="RSP_EI_VALUES_INCORRECT"/>
 *     &lt;enumeration value="RSP_EI_CA_TOKEN_INCORRECT"/>
 *     &lt;enumeration value="RSP_EI_CA_USER_INCORRECT"/>
 *     &lt;enumeration value="RSP_EI_CA_PWD_INCORRECT"/>
 *     &lt;enumeration value="RSP_EI_SESSION_INVALID"/>
 *     &lt;enumeration value="RSP_EI_COOKIES_INVALID"/>
 *     &lt;enumeration value="RSP_EI_SESSION_EXPIRED"/>
 *     &lt;enumeration value="RSP_EI_IP_NO_AUTHORIZED"/>
 *     &lt;enumeration value="RSP_EI_PARAMS_INCORRECT"/>
 *     &lt;enumeration value="RSP_EB"/>
 *     &lt;enumeration value="RSP_EB_BUSINESS_RULE_INCORRECT"/>
 *     &lt;enumeration value="RSP_EB_OPERATION_INCORRECT"/>
 *     &lt;enumeration value="RSP_EB_FILE_NOT_GENERATE"/>
 *     &lt;enumeration value="RSP_ED"/>
 *     &lt;enumeration value="RSP_ED_DATASOURCE_NOT_FOUND"/>
 *     &lt;enumeration value="RSP_ED_DATASOURCE_NOT_GET"/>
 *     &lt;enumeration value="RSP_ED_DATASOURCE_NOT_INSERT"/>
 *     &lt;enumeration value="RSP_ED_DATASOURCE_NOT_DELETE"/>
 *     &lt;enumeration value="RSP_ED_DATASOURCE_NOT_UPDATE"/>
 *     &lt;enumeration value="RSP_ED_DATASOURCE_TIMEOUT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "statusResponse")
@XmlEnum
public enum StatusResponse {

    RSP_SUCCESS("RSP_SUCCESS"),
    RSP_ERROR("RSP_ERROR"),
    RSP_EC("RSP_EC"),
    RSP_EC_SERV_NOT_AVAILABLE("RSP_EC_SERV_NOT_AVAILABLE"),
    RSP_EC_SERV_CA_NOT_AVAILABLE("RSP_EC_SERV_CA_NOT_AVAILABLE"),
    RSP_EC_SERV_PM_NOT_AVAILABLE("RSP_EC_SERV_PM_NOT_AVAILABLE"),
    @XmlEnumValue("RSP_EC_SERV_OP2_NOT_AVAILABLE")
    RSP_EC_SERV_OP_2_NOT_AVAILABLE("RSP_EC_SERV_OP2_NOT_AVAILABLE"),
    RSP_EC_SERV_EMAIL_NOT_AVAILABLE("RSP_EC_SERV_EMAIL_NOT_AVAILABLE"),
    RSP_EC_SERV_IDV_NOT_AVAILABLE("RSP_EC_SERV_IDV_NOT_AVAILABLE"),
    RSP_EC_SERV_EXPERT_NOT_AVAILABLE("RSP_EC_SERV_EXPERT_NOT_AVAILABLE"),
    RSP_EC_SERV_DAS_NOT_AVAILABLE("RSP_EC_SERV_DAS_NOT_AVAILABLE"),
    RSP_EC_SERV_RIO_NOT_AVAILABLE("RSP_EC_SERV_RIO_NOT_AVAILABLE"),
    RSP_EC_SERV_DF_NOT_AVAILABLE("RSP_EC_SERV_DF_NOT_AVAILABLE"),
    RSP_EC_REQ_INCORRECT("RSP_EC_REQ_INCORRECT"),
    RSP_EC_REQ_HEADER_INCORRECT("RSP_EC_REQ_HEADER_INCORRECT"),
    RSP_EC_REQ_NOT_AUTHENTICATED("RSP_EC_REQ_NOT_AUTHENTICATED"),
    RSP_EC_REQ_NOT_AUTHORIZED("RSP_EC_REQ_NOT_AUTHORIZED"),
    RSP_EC_REQ_TIMEOUT("RSP_EC_REQ_TIMEOUT"),
    RSP_EC_REQ_VERY_BIG("RSP_EC_REQ_VERY_BIG"),
    RSP_EC_REQ_URL_VERY_LONG("RSP_EC_REQ_URL_VERY_LONG"),
    RSP_EC_REQ_MEDIA_TYPE_INCORRECT("RSP_EC_REQ_MEDIA_TYPE_INCORRECT"),
    RSP_EI("RSP_EI"),
    RSP_EI_VALUES_NULL("RSP_EI_VALUES_NULL"),
    RSP_EI_CA_TOKEN_NULL("RSP_EI_CA_TOKEN_NULL"),
    @XmlEnumValue("RSP_EI_OP2_COD_ENTI_NULL")
    RSP_EI_OP_2_COD_ENTI_NULL("RSP_EI_OP2_COD_ENTI_NULL"),
    RSP_EI_VALUES_EMPTY("RSP_EI_VALUES_EMPTY"),
    RSP_EI_CA_TOKEN_EMPTY("RSP_EI_CA_TOKEN_EMPTY"),
    RSP_EI_VALUES_INCORRECT("RSP_EI_VALUES_INCORRECT"),
    RSP_EI_CA_TOKEN_INCORRECT("RSP_EI_CA_TOKEN_INCORRECT"),
    RSP_EI_CA_USER_INCORRECT("RSP_EI_CA_USER_INCORRECT"),
    RSP_EI_CA_PWD_INCORRECT("RSP_EI_CA_PWD_INCORRECT"),
    RSP_EI_SESSION_INVALID("RSP_EI_SESSION_INVALID"),
    RSP_EI_COOKIES_INVALID("RSP_EI_COOKIES_INVALID"),
    RSP_EI_SESSION_EXPIRED("RSP_EI_SESSION_EXPIRED"),
    RSP_EI_IP_NO_AUTHORIZED("RSP_EI_IP_NO_AUTHORIZED"),
    RSP_EI_PARAMS_INCORRECT("RSP_EI_PARAMS_INCORRECT"),
    RSP_EB("RSP_EB"),
    RSP_EB_BUSINESS_RULE_INCORRECT("RSP_EB_BUSINESS_RULE_INCORRECT"),
    RSP_EB_OPERATION_INCORRECT("RSP_EB_OPERATION_INCORRECT"),
    RSP_EB_FILE_NOT_GENERATE("RSP_EB_FILE_NOT_GENERATE"),
    RSP_ED("RSP_ED"),
    RSP_ED_DATASOURCE_NOT_FOUND("RSP_ED_DATASOURCE_NOT_FOUND"),
    RSP_ED_DATASOURCE_NOT_GET("RSP_ED_DATASOURCE_NOT_GET"),
    RSP_ED_DATASOURCE_NOT_INSERT("RSP_ED_DATASOURCE_NOT_INSERT"),
    RSP_ED_DATASOURCE_NOT_DELETE("RSP_ED_DATASOURCE_NOT_DELETE"),
    RSP_ED_DATASOURCE_NOT_UPDATE("RSP_ED_DATASOURCE_NOT_UPDATE"),
    RSP_ED_DATASOURCE_TIMEOUT("RSP_ED_DATASOURCE_TIMEOUT");
    private final String value;

    StatusResponse(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatusResponse fromValue(String v) {
        for (StatusResponse c: StatusResponse.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
