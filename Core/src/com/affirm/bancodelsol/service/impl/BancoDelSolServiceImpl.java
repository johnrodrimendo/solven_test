package com.affirm.bancodelsol.service.impl;

import com.affirm.acceso.model.Direccion;
import com.affirm.bancodelsol.model.CampaniaBds;
import com.affirm.bancodelsol.service.BancoDelSolService;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.CSVutils;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.SftpUtil;
import com.affirm.nosis.NosisResult;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.jcabi.ssh.Shell;
import com.jcabi.ssh.Ssh;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("bancoDelSolService")
public class BancoDelSolServiceImpl implements BancoDelSolService {

    public static final String CLIENTE_SANCOR_CLUSTER_NAME = "cliente sancor";
    public static final String CLIENTE_NO_SANCOR_CLUSTER_NAME = "no cliente sancor";

    public static final List<String> CLIENT_TYPES = Arrays.asList("Actual", "No Cliente");

    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private LoanApplicationDAO loanApplicationDAO;
    @Autowired
    private UserService userService;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private TranslatorDAO translatorDAO;
    @Autowired
    private UtilService utilService;
    @Autowired
    private RccDAO rccDao;
    @Autowired
    private ErrorService errorService;

    private final String FTP_HOST_LOCAL = "xxxxxxxxxxxxxxxxxxx";
    private final String FTP_USER_LOCAL = "xxxxxxxxxxxxxxxxxxx";
    private final String FTP_PWD_LOCAL = "xxxxxxxxxxxxxxxxxxx";
    private final String FTP_TARGET_PATH_LOCAL = "/bases/test/";
    private final String FTP_HOST = "xxxxxxxxxxxxxxxxxxx";
    private final String FTP_USER = "xxxxxxxxxxxxxxxxxxx";
    private final String FTP_PWD = "xxxxxxxxxxxxxxxxxxx";
    private final String FTP_TARGET_PATH = "/creditos/";
    private final String FTP_TARGET_PATH_BACKUP = "/bases/backup/";

    private final String BIND_HOST_QA = "xxxxxxxxxxxxxxxxxxx";
    private final String BIND_COMMAND_TOKEN_QA = "xxxxxxxxxxxxxxxxxxx";
    private final String BIND_COMMAND_CBU_QA = "xxxxxxxxxxxxxxxxxxx";
    private final String BIND_PK_QA = "";

    private final String BIND_HOST_PRD = "xxxxxxxxxxxxxxxxxxx";
    private final String BIND_COMMAND_TOKEN_PRD = "xxxxxxxxxxxxxxxxxxx";
    private final String BIND_COMMAND_CBU_PRD = "xxxxxxxxxxxxxxxxxxx";
    private final String BIND_PK_PRD = "";


    @Override
    public String generarCSVCliente(int personId, int loanApplicationId, JSONObject jsonRisk) throws Exception {

        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), personId, false);
        User user = userService.getUser(person.getUserId());
        Direccion direccion = personDAO.getDisggregatedAddress(personId, "H");
        District generalDistrict = catalogService.getGeneralDistrictById(direccion.getLocalityId());
        BancoDelSolCsvInfo bancoDelSolCsvInfo = personDAO.getBancoDelSolEmploymentDetailsByPersonId(loanApplicationId);
        List<PersonOcupationalInformation> personOcupationalInformationList = personDAO.getPersonOcupationalInformation(Configuration.getDefaultLocale(), personId);
        PersonOcupationalInformation personOcupationalInformation = personOcupationalInformationList != null ? personOcupationalInformationList.get(0) : null;
        List<PersonDisqualifier> personDisqualifierList = personDAO.getPersonDisqualifierByPersonId(loanApplication.getPersonId());
        ApplicationBureau applicationBureauNosis = loanApplicationDAO.getBureauResults(loanApplication.getId()).stream().filter(b -> b.getBureauId() != null && b.getBureauId() == Bureau.NOSIS).findFirst().orElse(null);

        StringBuilder sb = new StringBuilder();
        String personDateOfBirth = "";

        if (null != person.getBirthday()) {
            StringTokenizer st = new StringTokenizer(utilService.datetimeShortFormatByCountry(person.getBirthday(), CountryParam.COUNTRY_ARGENTINA), " ");
            personDateOfBirth = st.nextToken();
        }


//        CABECERA
        sb.append(CSVutils.writeLine(Arrays.asList("Cuit", "Apellidos", "Nombres", "Sexo", "Tipo_Doc", "Nro_Doc", "Calle", "Numero", "Piso", "Depto", "Localidad",
                "Cod_Pcia", "Cod_Posx", "Telefono_Prefijo", "Telefono", "Telefono_Laboral_Prefijo", "Telefono_Lab", "Celular_Prefijo", "Celular_Numero", "Cod_Pais",
                "Fecha_Nacimiento", "Fecha_Ingreso_Laboral", "Actividad_Economica", "Ingresos", "Estado_Civil", "Email", "Es_Cliente_Pep", "Categoria_IVA", "Tipo_cliente", "Es_Cliente_Fatca")));

//        CUERPO
        List<String> fields = new ArrayList<>();
        fields.add(nullSafeParser(person.getDocumentNumber())); //Cuit
        fields.add(nullSafeParser(person.getFullSurnames())); //Apellidos
        fields.add(nullSafeParser(person.getName())); // Nombres
        fields.add(nullSafeParser(person.getGender())); //Sexo
        fields.add("DNI");
        fields.add(nullSafeParser(person.getDNIFromCUIT())); //Nro_Doc

        fields.add(nullSafeParser(direccion.getNombreVia())); //Calle
        fields.add(nullSafeParser(direccion.getNumeroVia())); //Numero
        fields.add(nullSafeParser(direccion.getFloor())); // Piso
        fields.add(nullSafeParser(direccion.getNumeroInterior())); // Depto
        fields.add(nullSafeParser(generalDistrict.getName())); //Localidad
        fields.add(nullSafeParser(translatorDAO.translate(Entity.BANCO_DEL_SOL, 2, generalDistrict.getProvince().getProvinceId().toString(), null))); //Codigo Prov
        fields.add(nullSafeParser(generalDistrict.getPostalCode())); // Cod Posx

        if (person.getLandline() != null) {
            String landline = person.getLandline().substring(person.getLandline().indexOf(')') + 1).trim();
            String landlineAreaCode = person.getLandline().substring(1, person.getLandline().indexOf(')')); // Co Aread

            fields.add(nullSafeParser(landlineAreaCode));// Co Aread
            fields.add(nullSafeParser(landline.replace("-", "")));// Telef Fijo

        } else {
            fields.add(nullSafeParser(null));
            fields.add(nullSafeParser(null));
        }

        if (personOcupationalInformation != null) {
            fields.add(nullSafeParser(personOcupationalInformation.getPhoneCode())); //Telefono_Laboral_Prefijo
            fields.add(nullSafeParser(personOcupationalInformation.getPhoneNumber())); //Telefono_Lab
        } else {
            fields.add(nullSafeParser(null));
            fields.add(nullSafeParser(null));
        }

        fields.add(nullSafeParser(user.getPhoneCode())); //Celular_Prefijo
        fields.add(nullSafeParser(user.getPhoneNumberWithoutCode())); //Celular_Numero

        fields.add(nullSafeParser(translatorDAO.translate(Entity.BANCO_DEL_SOL, 4, person.getCountry().getId().toString(), null))); //Cod_Pais
        fields.add(nullSafeParser(personDateOfBirth)); //Fecha Nac

        fields.add(nullSafeParser(bancoDelSolCsvInfo != null && bancoDelSolCsvInfo.getEmploymentStartDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(bancoDelSolCsvInfo.getEmploymentStartDate()) : null)); //Fecha_Ingreso_Laboral
        fields.add(nullSafeParser(loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey()) ? loanApplication.getEntityCustomData().getString(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey()) : null)); // Actividad Economica
        fields.add(nullSafeParser(jsonRisk.optString("vai_ingresos_calculado", null))); // Ingresos

        fields.add(nullSafeParser(translatorDAO.translate(Entity.BANCO_DEL_SOL, 3, person.getMaritalStatus() != null ? person.getMaritalStatus().getId().toString() : null, null))); //Estado_Civil
        fields.add(nullSafeParser(user.getEmail())); //Email
        fields.add(nullSafeParser((personDisqualifierList != null ?
                personDisqualifierList.stream().filter(p -> PersonDisqualifier.PEP.equals(p.getType())).findFirst().orElse(null).isDisqualified() : false) ? "Si" : "No")); //Pep
        fields.add(nullSafeParser(applicationBureauNosis != null ? applicationBureauNosis.getNosisResult().getCategoriaIva() : "")); //Categoria IVA
        fields.add(nullSafeParser(loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey()) ? loanApplication.getEntityCustomData().getString(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey()) : null)); // Tipo cliente
        fields.add(nullSafeParser((personDisqualifierList != null ?
                personDisqualifierList.stream().filter(p -> PersonDisqualifier.FACTA.equals(p.getType())).findFirst().orElse(null).isDisqualified() : false) ? "Si" : "No")); //Cliente Fatca


        sb.append(CSVutils.writeLine(fields));

        return sb.toString();
    }


    @Override
    public String generarCSVCredito(int creditId, int personId, int loanApplicationId) throws Exception {
        StringBuilder sb = new StringBuilder();
        Credit credit = creditDAO.getCreditByID(creditId, Configuration.getDefaultLocale(), true, Credit.class);
        LoanApplication loanApplication = loanApplicationService.getLoanApplicationById(credit.getLoanApplicationId());
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), personId, false);
        PersonContactInformation contactInfo = personDAO.getPersonContactInformation(Configuration.getDefaultLocale(), personId);
        PersonBankAccountInformation personBankAccountInformation = personDAO.getPersonBankAccountInformationByCredit(Configuration.getDefaultLocale(), personId, creditId);
        BancoDelSolCsvInfo bancoDelSolCsvInfo = personDAO.getBancoDelSolEmploymentDetailsByPersonId(loanApplicationId);

        List<Province> provinces = catalogService.getGeneralProvince(CountryParam.COUNTRY_ARGENTINA);
        ApplicationBureau applicationBureauNosis = loanApplicationDAO.getBureauResults(loanApplication.getId()).stream().filter(b -> b.getBureauId() != null && b.getBureauId() == Bureau.NOSIS).findFirst().orElse(null);
        NosisResult nosis = applicationBureauNosis != null ? applicationBureauNosis.getNosisResult() : null;
        Province province = provinces.stream().filter(p -> p.getProvinceId() == contactInfo.getProvince().getProvinceId()).findFirst().orElse(null);
        String idZonaDePago = translatorDAO.translate(Entity.BANCO_DEL_SOL, 1, province.getProvinceId().toString(), null);
        String uploadDate = "";
        String uploadTime = "";
        String registerDate = "";

        StringTokenizer stUploadDateAndTime = new StringTokenizer(utilService.datetimeShortFormatByCountry(new Date(), CountryParam.COUNTRY_ARGENTINA), " ");
        uploadDate = stUploadDateAndTime.nextToken();
        uploadTime = stUploadDateAndTime.nextToken();


        if (null != credit.getRegisterDate()) {
            StringTokenizer st = new StringTokenizer(utilService.datetimeShortFormatByCountry(credit.getRegisterDate(), CountryParam.COUNTRY_ARGENTINA), " ");
            registerDate = st.nextToken();
        }

        sb.append(CSVutils.writeLine(Arrays.asList(
                "Cuit",
                "ID_Producto",
                "Identificador_de_Credito ",
                "Fecha_de_Alta",
                "Cuotas",
                "Capital",
                "Interes",
                "IVA",
                "Alicuota_Sellado",
                "Importe_Sellado",
                "Valor_Cuota ",
                "Id_Zona_de_Pago",
                "Tasa_Efectiva_Mensual",
                "Tasa_Nominal_Anual",
                "Tasa_Efectiva_Anual",
                "Costo_Financiero_Total",
                "CBU",
                "Id_Organizador",
                "Id_Productor",
                "Scoring",
                "Id_Usuario_de_carga",
                "Dia_de_carga",
                "Hora_de_Carga"

        )));

        Integer productIdToPaint = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PRODUCT_ID.getKey(), null);

        sb.append(CSVutils.writeLine(Arrays.asList(
                nullSafeParser(person.getDocumentNumber()), // Cuit
                nullSafeParser(productIdToPaint), // IDProducto
                nullSafeParser(credit.getId()), //Identificador de Crédito
                registerDate, // Fecha de Alta
                nullSafeParser(credit.getInstallments()), //Cuotas
                utilService.customDoubleFormat(credit.getAmount() + credit.getStampTax(), 2), // Capital
                String.valueOf(utilService.customDoubleFormat(credit.getTotalScheduleField("interest", 'O'), 2)), //Interés
                nullSafeParser(utilService.customDoubleFormat(credit.getTotalScheduleField("interestTax", 'O'), 2)), //IVA
                nullSafeParser(utilService.customDoubleFormat(credit.getStampTaxRate(), 2)),  // <-- alicouta sellado :
                nullSafeParser(utilService.customDoubleFormat(credit.getStampTax(), 2)),  // Importe Sellado (definido por la zona de pago)
                nullSafeParser(utilService.customDoubleFormat(credit.getInstallmentAmount(), 2)), // valor Cuota
                idZonaDePago, // Id Zona de Pago (Según Tabla Provincias)
                nullSafeParser(utilService.customDoubleFormat(credit.getEffectiveMonthlyRate(), 4)), //Tasa Efectiva Mensual
                nullSafeParser(utilService.customDoubleFormat(credit.getNominalAnualRate(), 4)),// Tasa Nominal Anual
                nullSafeParser(utilService.customDoubleFormat(credit.getEffectiveAnnualRate(), 4)),//Tasa Efectiva Anual
                nullSafeParser(utilService.customDoubleFormat(credit.getEffectiveAnnualCostRate(), 2)), //  costo financiero
                nullSafeParser(personBankAccountInformation.getCciCode()),//CBU
                bancoDelSolCsvInfo.getLevel3() != null ? bancoDelSolCsvInfo.getLevel3() + "" : "", // ID Organizador
                bancoDelSolCsvInfo.getLevel2() != null ? bancoDelSolCsvInfo.getLevel2() + "" : "", // ID Productor
                nosis == null ? "" : nullSafeParser(nosis.getScore()),
                bancoDelSolCsvInfo.getLevel1() != null ? bancoDelSolCsvInfo.getLevel1() + "" : "", // ID usuario de carga
                uploadDate, //DIA de carga
                uploadTime) // hora de carga
        ));

        return sb.toString();
    }

    @Override
    public String generarCSVCuotas(int creditId) throws Exception {
        StringBuilder sb = new StringBuilder();
        Credit credit = creditDAO.getCreditByID(creditId, Configuration.getDefaultLocale(), false, Credit.class);

        sb.append(CSVutils.writeLine(Arrays.asList(
                "Identificador_de_Credito",
                "Numero_de_Cuota",
                "Fecha_de_Vencimiento",
                "Capital",
                "Interes",
                "IVA",
                "Valor_Cuota"
        )));

        List<OriginalSchedule> msList = creditDAO.getOriginalSchedule(credit.getId());
        if (msList != null && msList.size() > 0) {

            for (int i = 0; i < msList.size(); i++) {
                OriginalSchedule ms = msList.get(i);

                String dueDate = "";
                if (null != ms.getDueDate()) {
                    StringTokenizer st = new StringTokenizer(utilService.datetimeShortFormatByCountry(ms.getDueDate(), CountryParam.COUNTRY_ARGENTINA), " ");
                    dueDate = st.nextToken();
                }

                Double installmentCapitalRounded = utilService.round(ms.getInstallmentCapital(), 2);
                Double interestRounded = utilService.round(ms.getInterest(), 2);
                Double interestTaxRounded = utilService.round(ms.getInterestTax(), 2);
                Double instlmentAmountRounded = utilService.round(ms.getInstallmentAmount(), 2);

                Double diff = instlmentAmountRounded - (installmentCapitalRounded + interestRounded + interestTaxRounded);
                if (diff != 0.0) {
                    interestTaxRounded += diff;
                }

                sb.append(CSVutils.writeLine(Arrays.asList(
                        nullSafeParser(credit.getId()),
                        nullSafeParser(ms.getInstallmentId()),
                        dueDate,
                        nullSafeParser(utilService.customDoubleFormat(installmentCapitalRounded, 2)),
                        nullSafeParser(utilService.customDoubleFormat(interestRounded, 2)),
                        nullSafeParser(utilService.customDoubleFormat(interestTaxRounded, 2)),
                        nullSafeParser(utilService.customDoubleFormat(instlmentAmountRounded, 2))
                )));
            }

        }

        return sb.toString();
    }

    @Override
    public String generarCSVRisk(int loanApplicationId, JSONObject jsonRisk) throws Exception {

        if (jsonRisk != null) {
            JSONArray array = new JSONArray();
            array.put(jsonRisk);
            return generarReportCSVRisk(array, false);
        }

        return null;
    }

    @Override
    public String generarReportCSVRisk(JSONArray arrayRisk, boolean showReportFields) throws Exception {
        List<String> headers = new ArrayList<>();

        headers.add("sol_nro_solicitud");

        if (showReportFields) {
            headers.add("sol_canal");
        }

        headers.add("sol_credit_id");
        headers.add("sol_id_zona_de_pago");
        headers.add("sol_id_organizador");
        headers.add("sol_id_productor");
        headers.add("sol_id_usuario_de_carga");
        headers.add("sol_fecha_registro");
        headers.add("sol_fecha_ultima_ejecucion");
        headers.add("sol_documento");
        headers.add("sol_apellidos");
        headers.add("sol_nombres");
        headers.add("sol_sexo");
        headers.add("sol_tipo_doc");
        headers.add("sol_nro_doc");
        headers.add("sol_fecha_nacimiento_declarada");
        headers.add("sol_calle");
        headers.add("sol_numero");
        headers.add("sol_piso");
        headers.add("sol_depto");
        headers.add("sol_localidad");
        headers.add("sol_cod_pcia");
        headers.add("sol_cod_posx");
        headers.add("sol_telefono_personal_prefijo");
        headers.add("sol_telefono_personal");
        headers.add("sol_celular_prefijo");
        headers.add("sol_celular_numero");
        headers.add("sol_cod_pais");
        headers.add("sol_email");
        headers.add("sol_estado_civil");
        headers.add("sol_id_producto");
        headers.add("sol_monto_solicitado");
        headers.add("sol_destino_prestamo");
        headers.add("sol_motivo_contacto");
        headers.add("sol_tipo_cliente");
        headers.add("sol_banco");
        headers.add("sol_tipo_cuenta");
        headers.add("sol_cbu");
        headers.add("sol_tipo_campania");
        headers.add("res_estado_solicitud");
        headers.add("res_motivo_rechazo_preevaluacion");
        headers.add("res_motivo_rechazo_evaluacion");
        headers.add("ofe_monto_maximo");
        headers.add("ofe_monto_simulado");
        headers.add("ofe_monto_seleccionado");
        headers.add("ofe_cuota_1_oferta_solicitada");
        headers.add("ofe_cuota_2_oferta_solicitada");
        headers.add("ofe_cuota_1_oferta_maxima");
        headers.add("ofe_cuota_2_oferta_maxima");
        headers.add("ofe_cuota_1_oferta_simulada");
        headers.add("ofe_cuota_2_oferta_simulada");
        headers.add("ofe_cuota_1_oferta_seleccionada");
        headers.add("ofe_cuota_2_oferta_seleccionada");
        headers.add("ofe_plazo_oferta_solicitada");
        headers.add("ofe_plazo_oferta_maxima");
        headers.add("ofe_plazo_oferta_simulada");
        headers.add("ofe_plazo_oferta_seleccionada");
        headers.add("ofe_fecha_de_vencimiento");
        headers.add("ofe_tasa_nominal_anual");
        headers.add("vai_ingresos_calculado");
        headers.add("anses_certificacion_negativa");
        headers.add("afip_nombre_completo");
        headers.add("afip_categoria");
        headers.add("afip_actividad");
        headers.add("afip_extra_information");
        headers.add("bcra_cheques_rechazados");
        headers.add("bcra_peor_situacion");
        headers.add("bcra_deuda_sit_2");
        headers.add("bcra_deuda_total");
        headers.add("bcra_cheques_sin_fondo");
        headers.add("bcra_peor_sit_12m");
        headers.add("cendeu_situacion");
        headers.add("cendeu_prestamos");
        headers.add("pyp_ef_ape_nom");
        headers.add("pyp_ef_nume_docu");
        headers.add("pyp_ef_cdi");
        headers.add("pyp_ef_fecha_nacimiento");
        headers.add("pyp_ef_direc_calle");
        headers.add("pyp_ef_localidad");
        headers.add("pyp_ef_codigo_postal");
        headers.add("pyp_ef_provincia");
        headers.add("pyp_ef_apellido_materno");
        headers.add("pyp_ef_t_docu");
        headers.add("pyp_ef_clase");
        headers.add("pyp_ef_edad");
        headers.add("pyp_ef_fallecido");
        headers.add("pyp_pi_predictor_ingresos");
        headers.add("pyp_dsf_act_entidad");
        headers.add("pyp_dsf_act_situacion");
        headers.add("pyp_dsf_act_monto_maximo");
        headers.add("pyp_dsf_act_deuda_actual");
        headers.add("pyp_dsf_act_fecha");
        headers.add("pyp_dsf_6m_entidad");
        headers.add("pyp_dsf_6m_situacion");
        headers.add("pyp_dsf_6m_monto_maximo");
        headers.add("pyp_dsf_6m_deuda_actual");
        headers.add("pyp_dsf_6m_fecha");
        headers.add("pyp_dsf_12m_entidad");
        headers.add("pyp_dsf_12m_situacion");
        headers.add("pyp_dsf_12m_monto_maximo");
        headers.add("pyp_dsf_12m_deuda_actual");
        headers.add("pyp_dsf_12m_fecha");
        headers.add("pyp_dsf_24m_entidad");
        headers.add("pyp_dsf_24m_situacion");
        headers.add("pyp_dsf_24m_monto_maximo");
        headers.add("pyp_dsf_24m_deuda_actual");
        headers.add("pyp_dsf_24m_fecha");
        headers.add("pyp_ch_12m_cant_ch_rec_sin_fondos");
        headers.add("pyp_ch_6m_cant_ch_rec_sin_fondos");
        headers.add("pyp_mt2_cant");
        headers.add("pyp_mt2_deuda_tot");
        headers.add("pyp_mt1_cant_moras");
        headers.add("pyp_rd_ult_periodo");
        headers.add("pyp_rd_alta_ult_trabajo");
        headers.add("pyp_rd_rango_dependiente");
        headers.add("pyp_rd_cuit_empleador");
        headers.add("pyp_rd_situacion_laboral_actual");
        headers.add("pyp_rd_razon");
        headers.add("pyp_lab_inf_lab_cuit_ultimo");
        headers.add("pyp_lab_inf_lab_razon_ultimo");
        headers.add("pyp_lab_relacion_desde_ultimo");
        headers.add("pyp_lab_relacion_hasta_ultimo");
        headers.add("pyp_lab_inf_lab_cuit_penultimo");
        headers.add("pyp_lab_inf_lab_razon_penultimo");
        headers.add("pyp_lab_relacion_desde_penultimo");
        headers.add("pyp_lab_relacion_hasta_penultimo");
        headers.add("pyp_ac_tipo_actividad");
        headers.add("pyp_as_cant_auh_cobra");
        headers.add("pyp_au_posee_autos");
        headers.add("pyp_au_cantidad_autos");
        headers.add("pyp_au_dominio");
        headers.add("pyp_au_modelo_auto");
        headers.add("pyp_tel_telefono");
        headers.add("pyp_cel_cuit");
        headers.add("pyp_cel_documento");
        headers.add("pyp_cel_empresa");
        headers.add("pyp_cel_celular");
        headers.add("pyp_cel_fecha_activacion");
        headers.add("pyp_jud_tiene_juicio");
        headers.add("pyp_ci_imp_ganancias");
        headers.add("pyp_ci_imp_iva");
        headers.add("pyp_ci_monotributo");
        headers.add("pyp_ci_integrante_soc");
        headers.add("pyp_ci_empleador");
        headers.add("pyp_ci_periodo");
        headers.add("pyp_ci_afip_cuit");
        headers.add("pyp_ci_afip_denominacion");
        headers.add("pyp_ci_afip_fecha_contrato_social");
        headers.add("pyp_ci_afip_mes_cierre");
        headers.add("pyp_ci_afip_categoria");
        headers.add("pyp_ci_afip_fecha_inicio_actividades");
        headers.add("pyp_ci_afip_descripcion");
        headers.add("pyp_ci_afip_direccion");
        headers.add("pyp_ci_afip_localidad");
        headers.add("pyp_ci_afip_provincia");
        headers.add("pyp_ci_afip_cp");
        headers.add("pyp_jui_apellido_nombre");
        headers.add("pyp_jui_num_doc");
        headers.add("pyp_jui_cant_juicios");
        headers.add("pyp_cons_consultas");
        headers.add("pyp_cons_consultas_6");
        headers.add("pyp_emb_juicios_posee_tipo");
        headers.add("pyp_inh_juicios_posee_tipo");
        headers.add("nos_vi_identificacion");
        headers.add("nos_vi_razonsocial");
        headers.add("nos_vi_tipopersona");
        headers.add("nos_vi_tiposocietario");
        headers.add("nos_vi_razonsocial_porccoinc");
        headers.add("nos_vi_nombre");
        headers.add("nos_vi_apellido");
        headers.add("nos_vi_fallecido_es");
        headers.add("nos_vi_dni");
        headers.add("nos_vi_dni_version");
        headers.add("nos_vi_fecnacimiento");
        headers.add("nos_vi_sexo");
        headers.add("nos_vi_domaf_calle");
        headers.add("nos_vi_domaf_nro");
        headers.add("nos_vi_domaf_piso");
        headers.add("nos_vi_domaf_dto");
        headers.add("nos_vi_domaf_loc");
        headers.add("nos_vi_domaf_cp");
        headers.add("nos_vi_domaf_prov");
        headers.add("nos_vi_act01_cod");
        headers.add("nos_vi_act01_sector");
        headers.add("nos_vi_act01_fecinicio");
        headers.add("nos_vi_clanae_nivel3_cod");
        headers.add("nos_vi_clanae_nivel5_cod");
        headers.add("nos_vi_act02_cod");
        headers.add("nos_vi_act02_sector");
        headers.add("nos_vi_inscrip_afip_antiguedad");
        headers.add("nos_vi_jubilado_es");
        headers.add("nos_vi_empleado_es");
        headers.add("nos_vi_integrantesociedad_es");
        headers.add("nos_vi_empleador_es");
        headers.add("nos_vi_edad_empresas");
        headers.add("nos_vi_contratosocial_fec");
        headers.add("nos_vi_empleados_cant");
        headers.add("nos_vi_inscrip_monotributo_es");
        headers.add("nos_vi_inscrip_monotributo_fec");
        headers.add("nos_vi_inscrip_monotributo");
        headers.add("nos_vi_inscrip_monotributo_tipo");
        headers.add("nos_vi_inscrip_monotributo_antiguedad");
        headers.add("nos_vi_autonomo_id");
        headers.add("nos_vi_inscrip_autonomo_es");
        headers.add("nos_vi_inscrip_autonomo_fec");
        headers.add("nos_vi_inscrip_autonomo");
        headers.add("nos_vi_inscrip_autonomo_antiguedad");
        headers.add("nos_vi_antiguedadlaboral");
        headers.add("nos_vi_empleadodomestico_es");
        headers.add("nos_vi_antiguedadlaboral_empleadoractual");
        headers.add("nos_vi_empleado_es_ultfecha");
        headers.add("nos_vi_inscrip_iva");
        headers.add("nos_vi_inscrip_iva_fec");
        headers.add("nos_vi_inscrip_gcia");
        headers.add("nos_vi_inscrip_gcia_fec");
        headers.add("nos_vi_inscrip_afip_fec");
        headers.add("nos_vi_inscrip_iva_condicion");
        headers.add("nos_vi_inscrip_gcia_condicion");
        headers.add("nos_vi_empleador_cuit");
        headers.add("nos_vi_empleador_rz");
        headers.add("nos_vi_empleador_act01_cod");
        headers.add("nos_ap_12m_empleado_pagos_cant");
        headers.add("nos_ap_12m_empleado_pagoparcial_cant");
        headers.add("nos_ap_12m_empleado_impagos_cant");
        headers.add("nos_ap_12m_empleadores_cant");
        headers.add("nos_ap_vig_empleado_declaracionjurada");
        headers.add("nos_ap_12m_empleador_pagoparcial_cant");
        headers.add("nos_ap_12m_empleador_impagos_cant");
        headers.add("nos_ap_vig_empleador_estado_cod");
        headers.add("nos_ci_vig_peorsit");
        headers.add("nos_ci_vig_peorsit_porc");
        headers.add("nos_ci_vig_total_cantbcos");
        headers.add("nos_ci_vig_total_monto");
        headers.add("nos_ci_vig_monto_sit3omas");
        headers.add("nos_ci_bancarizado");
        headers.add("nos_ci_antiguedad");
        headers.add("nos_ci_vig_detalle");
        headers.add("nos_ci_3m_peorsit_porc");
        headers.add("nos_ci_6m_peorsit");
        headers.add("nos_ci_6m_peorsit_porc");
        headers.add("nos_ci_12m_peorsit");
        headers.add("nos_ci_12m_peorsit_porc");
        headers.add("nos_ci_24m_peorsit");
        headers.add("nos_ci_24m_peorsit_porc");
        headers.add("nos_ci_24m_bancarizado");
        headers.add("nos_ci_3m_peorsit");
        headers.add("nos_ci_vig_tc_cant");
        headers.add("nos_ci_vig_tc_monto");
        headers.add("nos_ci_vig_li_cc_cant");
        headers.add("nos_ci_vig_li_cc_monto");
        headers.add("nos_ci_12m_li_cc_minmonto_consol");
        headers.add("nos_ci_12m_li_cc_maxmonto_consol");
        headers.add("nos_ci_12m_li_cc_maxsuma_sit1_consol");
        headers.add("nos_ci_vig_li_hi_monto");
        headers.add("nos_ci_vig_li_hi_cuotasrestantes");
        headers.add("nos_ci_li_hi_antiguedad");
        headers.add("nos_ci_vig_li_pr_cant");
        headers.add("nos_ci_vig_li_pr_monto");
        headers.add("nos_ci_vig_li_pr_cuotasrestantes");
        headers.add("nos_ci_li_pr_antiguedad");
        headers.add("nos_ci_vig_li_opr_cant");
        headers.add("nos_ci_vig_li_opr_monto");
        headers.add("nos_ci_vig_li_opr_cuotasrestantes");
        headers.add("nos_ci_vig_li_opr_sitmay2_tiene");
        headers.add("nos_ci_12m_li_opr_maxsuma_sit1_consol");
        headers.add("nos_ci_li_opr_antiguedad");
        headers.add("nos_ci_vig_li_pp_cant");
        headers.add("nos_ci_vig_li_pp_monto");
        headers.add("nos_ci_vig_li_pp_cuotasrestantes");
        headers.add("nos_ci_12m_li_pp_suma_maxmonto");
        headers.add("nos_ci_li_pp_antiguedad");
        headers.add("nos_ci_vig_li_ot_cant");
        headers.add("nos_ci_vig_li_ot_monto");
        headers.add("nos_ci_vig_compmensual");
        headers.add("nos_ci_vig_ratio_compmensual");
        headers.add("nos_ci_vig_ratio_compmensual_a6m");
        headers.add("nos_ci_vig_ratio_compmensual_lp");
        headers.add("nos_ci_vig_compmensual_sinbcopropio");
        headers.add("nos_ci_vig_ratio_deudatotal");
        headers.add("nos_ci_vig_ratio_deudatotal_a3m");
        headers.add("nos_ci_vig_ratio_deudatotal_a6m");
        headers.add("nos_vr_vig_capacidadendeu_deuda");
        headers.add("nos_dx_es");
        headers.add("nos_dx_cant");
        headers.add("nos_hc_12m_sf_nopag_cant");
        headers.add("nos_hc_12m_sf_nopag_monto");
        headers.add("nos_hc_12m_sf_pag_cant");
        headers.add("nos_hc_12m_sf_pag_monto");
        headers.add("nos_hc_12m_ot_nopag_cant");
        headers.add("nos_hc_12m_ot_nopag_monto");
        headers.add("nos_hc_12m_ot_pag_cant");
        headers.add("nos_hc_12m_ot_pag_monto");
        headers.add("nos_hc_12m_sf_sinpagmulta_cant");
        headers.add("nos_hc_12m_sf_sinpagmulta_monto");
        headers.add("nos_ju_12m_cant");
        headers.add("nos_cq_12m_cant");
        headers.add("nos_pq_12m_cant");
        headers.add("nos_ju_60m_cant");
        headers.add("nos_pq_60m_cant");
        headers.add("nos_cq_60m_cant");
        headers.add("nos_rc_vig_cant");
        headers.add("nos_rc_vig_fuente");
        headers.add("nos_hg_6m_cant_positivos");
        headers.add("nos_hg_6m_monto_positivos");
        headers.add("nos_hg_7a12m_cant_positivos");
        headers.add("nos_hg_7a12m_monto_positivos");
        headers.add("nos_co_1m_finan_cant");
        headers.add("nos_co_1m_banca_cant");
        headers.add("nos_co_1m_otros_cant");
        headers.add("nos_co_1m_excesocons");
        headers.add("nos_sco_vig");
        headers.add("nos_sco_3m");
        headers.add("nos_sco_6m");
        headers.add("nos_sco_12m");
        headers.add("nos_sco_3m_tendencia");
        headers.add("nos_nse_percentil");
        headers.add("nos_fe_maxapertura");
        headers.add("nos_cda");
        headers.add("nos_bl_fechanov");
        headers.add("nos_bl_monto_activocorr");
        headers.add("nos_bl_monto_activonocorr");
        headers.add("nos_bl_monto_pasivocorr");
        headers.add("nos_bl_monto_pasivonocorr");
        headers.add("nos_bl_monto_patrimneto");
        headers.add("nos_bl_monto_resultejerc");
        headers.add("nos_bl_monto_ventasnetas");
        headers.add("nos_rp_fecalta");
        headers.add("nos_rp_estado");
        headers.add("nos_ce_importador_es");
        headers.add("nos_ce_exportador_es");
        headers.add("nos_pe_proveedor_es");
        headers.add("nos_dc_tiene");
        headers.add("nos_dc_version");
        headers.add("nos_dc_motivo");
        headers.add("nos_og_estado");
        headers.add("nos_fa_tiene");
        headers.add("nos_df_tiene");
        headers.add("nos_ch_cant");
        headers.add("nos_bc_dem_cant");
        headers.add("nos_fex_bcra_fecact");
        headers.add("nos_fex_bcra_fecvenc");
        headers.add("nos_fex_rd_fecact");
        headers.add("nos_fex_rd_fecvenc");
        headers.add("nos_fex_afinscrip_fecact");
        headers.add("nos_fex_afinscrip_fecvenc");
        headers.add("nos_fex_afap_fecact");
        headers.add("nos_fex_afap_fecvenc");
        headers.add("nos_fex_afoc_fecact");
        headers.add("nos_fex_afoc_fecvenc");
        headers.add("nos_fex_mnp_fecact");
        headers.add("nos_fex_mnp_fecvenc");
        headers.add("nos_fex_mn_fecact");
        headers.add("nos_fex_mn_fecvenc");
        headers.add("nos_fex_cn_fecact");
        headers.add("nos_fex_cn_fecvenc");
        headers.add("nos_as_beneficios_detalle_txt");
        headers.add("nos_cn_estado");
        headers.add("nos_cn_periododesde");
        headers.add("nos_cn_periodohasta");
        headers.add("nos_mn_3m_cant");
        headers.add("nos_mn_6m_cant");
        headers.add("nos_mn_12m_cant");
        headers.add("nos_vr_relaciones_detalle");
        headers.add("nos_ci_vig_li_hi_uva_monto");
        headers.add("nos_ci_vig_li_hi_uva_cuotasrestantes");
        headers.add("nos_ci_li_hi_uva_antiguedad");
        headers.add("nos_ci_vig_li_pr_uva_monto");
        headers.add("nos_ci_vig_li_pr_uva_cuotasrestantes");
        headers.add("nos_ci_li_pr_uva_antiguedad");
        headers.add("nos_ci_vig_li_opr_uva_monto");
        headers.add("nos_ci_vig_li_opr_uva_cuotasrestantes");
        headers.add("nos_ci_vig_li_opr_uva_sitmay2_tiene");
        headers.add("nos_ci_li_opr_uva_antiguedad");
        headers.add("nos_ci_vig_li_pp_uva_monto");
        headers.add("nos_ci_vig_li_pp_uva_cuotasrestantes");
        headers.add("nos_ci_li_pp_uva_antiguedad");
        headers.add("nos_ci_vig_li_ot_uva_monto");
        headers.add("sol_estado_interno");
        headers.add("nos_nse_ranking");
        // INICO VERAZ
        headers.add("ve_score_veraz");
        headers.add("ve_score_poblacion");
        headers.add("ve_peor_bureau_actual");
        headers.add("ve_peor_situacion_bureau_3m");
        headers.add("ve_peor_situacion_bureau_4_12m");
        headers.add("ve_vcb_deuda_vencida");
        headers.add("ve_observaciones_12m");
        headers.add("ve_observaciones_basecerrada_monto_1m");
        headers.add("ve_consultas_financieras_1m");
        headers.add("ve_compromiso_total");
        headers.add("ve_vcb_tar_gral_alta");
        headers.add("ve_vcb_tar_gral_total");
        headers.add("ve_val_doc_status");
        headers.add("ve_vcb_pre_gral_total");
        headers.add("ve_prom_lim_tarj_6_meses");
        headers.add("ve_max_lim_tar_act_bancarias");
        headers.add("ve_max_lim_tar_act_no_bancarias");
        headers.add("ve_sum_pago_minimo");
        headers.add("ve_sum_cuotas_pp");
        headers.add("ve_cant_cheques_no_pagados_12m");
        headers.add("ve_job_category");
        headers.add("ve_income_predictor");
        headers.add("ve_consultas_fintech");
        headers.add("ve_consultas_financieras_6m");
        headers.add("ve_consultas_no_financieras_6m");
        headers.add("ve_peor_situacion_bcra_6m");
        headers.add("ve_peor_situacion_bcra_7_12m");
        headers.add("ve_categoria");
        headers.add("ve_documento");
        headers.add("ve_sexo");
        // FIN VERAZ
        headers.add("vcapitales");
        headers.add("vcuotas");
        headers.add("inhabcc");
        headers.add("embajud");
        headers.add("entliq");
        headers.add("mora");


        StringBuilder sb = new StringBuilder();
        sb.append(CSVutils.writeLine(headers));

        headers.add("bds_base_info");

        if (arrayRisk != null) {
            for (int i = 0; i < arrayRisk.length(); i++) {
                JSONObject json = arrayRisk.getJSONObject(i);
                List<String> values = new ArrayList<>();

                for (String header : headers) {

                    if (json.opt(header) != null) {
                        if (header.equals("bds_base_info")) {
                            String params = json.opt(header).toString();
                            String[] array = params.split("\\|");

                            if (array.length > 1) {
                                Collections.addAll(values, array);
                            }
                        } else {
                            switch (header) {
                                case "sol_canal": {
                                    try {
                                        values.add(json.opt(header).getClass().equals(JSONObject.NULL.getClass()) ? "" : catalogService.getEntityAcquisitionChannelById(Integer.parseInt(json.opt(header).toString())).getEntityAcquisitionChannel());
                                    } catch (NumberFormatException e) {
                                        values.add("");
                                    }
                                    break;
                                }
                                default: {
                                    values.add(json.opt(header).getClass().equals(JSONObject.NULL.getClass()) ? "" : json.opt(header).toString());
                                    break;
                                }
                            }
                        }
                    }
                }
                sb.append(CSVutils.writeLine(values));
            }
        }
        return sb.toString();
    }

    private static String nullSafeParser(Object o) {
        return o != null ? String.valueOf(o) : "";
    }

    @Override
    @Async
    public void uploadCSVdocsToFTP(int creditId, int personId, String documentNumber, int loanApplicationId) throws Exception {
        try{
            JSONObject jsonRisk = creditDAO.getBancoDelSolRiskReport(loanApplicationId);

            String cliente = generarCSVCliente(personId, loanApplicationId, jsonRisk);
            String credito = generarCSVCredito(creditId, personId, loanApplicationId);
            String cuotas = generarCSVCuotas(creditId);
            String risk = generarCSVRisk(loanApplicationId, jsonRisk);
            int zeroes = 6 - String.valueOf(creditId).length();
            String targetFileNamePrefix = documentNumber + "_" + StringUtils.repeat("0", zeroes) + creditId + "_";
            String backupFolderPath = FTP_TARGET_PATH_BACKUP + (Configuration.hostEnvIsProduction() ? Configuration.HOSTENV_VAL_PRD : Configuration.hostEnvIsStage() ? Configuration.HOSTENV_VAL_STG : Configuration.hostEnvIsDev() ? Configuration.HOSTENV_VAL_DEV : Configuration.HOSTENV_VAL_LOC) + "/";

            {
                Map<String, InputStream> map = new HashMap<>();
                map.put("cliente.csv", new ByteArrayInputStream(cliente.getBytes()));
                map.put("credito.csv", new ByteArrayInputStream(credito.getBytes()));
                map.put("cuotas.csv", new ByteArrayInputStream(cuotas.getBytes()));
                map.put("riesgo.csv", new ByteArrayInputStream(risk.getBytes()));

                if (Configuration.hostEnvIsProduction()) {
                    SftpUtil.upload(FTP_HOST, FTP_USER, FTP_PWD, map, FTP_TARGET_PATH, backupFolderPath, targetFileNamePrefix);
                } else {
                    SftpUtil.upload(FTP_HOST_LOCAL, FTP_USER_LOCAL, FTP_PWD_LOCAL, map, FTP_TARGET_PATH_LOCAL, backupFolderPath, targetFileNamePrefix);
                }
            }
            // The flag.csv should be uploaded in the end
            {
                Map<String, InputStream> secondMap = new HashMap<>();
                secondMap.put("flag.csv", new ByteArrayInputStream(new byte[]{}));

                if (Configuration.hostEnvIsProduction()) {
                    SftpUtil.upload(FTP_HOST, FTP_USER, FTP_PWD, secondMap, FTP_TARGET_PATH, targetFileNamePrefix);
                } else {
                    SftpUtil.upload(FTP_HOST_LOCAL, FTP_USER_LOCAL, FTP_PWD_LOCAL, secondMap, FTP_TARGET_PATH_LOCAL, targetFileNamePrefix);
                }
            }
        }catch (Exception exception){
            errorService.onError(exception);
        }
    }


    @Override
    public boolean isValidCUITByCBU(String cbu, String cuit) throws Exception {
        if (Configuration.hostEnvIsLocal() || Configuration.hostEnvIsDev())
            return true;
        String key = Configuration.hostEnvIsProduction() || Configuration.hostEnvIsStage() ? BIND_PK_PRD : BIND_PK_QA;
        String hostEc2 = Configuration.hostEnvIsProduction() || Configuration.hostEnvIsStage() ? BIND_HOST_PRD : BIND_HOST_QA;
        String tokenCommand = Configuration.hostEnvIsProduction() || Configuration.hostEnvIsStage() ? BIND_COMMAND_TOKEN_PRD : BIND_COMMAND_TOKEN_QA;
        String cbuCommand = Configuration.hostEnvIsProduction() || Configuration.hostEnvIsStage() ? BIND_COMMAND_CBU_PRD : BIND_COMMAND_CBU_QA;

        Shell shell = new Ssh(hostEc2, 22, "ubuntu", key);
        Shell.Plain shellPlain = new Shell.Plain(shell);
        String tokenResult = shellPlain.exec(tokenCommand);
        JSONObject tokenJson = new JSONObject(tokenResult);
        String token = tokenJson.getString("token");

        String cbuResult = shellPlain.exec(cbuCommand.replaceAll("%CBU%", cbu).replaceAll("%TOKEN%", token));
        JSONObject cbuJson = new JSONObject(cbuResult);
        JSONArray cbuOwners = cbuJson.getJSONArray("owners");
        List<String> cuits = new ArrayList<>();
        for (int i = 0; i < cbuOwners.length(); i++) {
            cuits.add(cbuOwners.getJSONObject(i).getString("id"));
        }
        return cuits.stream().anyMatch(c -> c.equalsIgnoreCase(cuit));
    }

    @Override
    public JSONObject commissionClusterByClientType() throws Exception {
        JSONObject clusterJson = new JSONObject();
        List<RateCommissionProduct> productClusters = catalogService.getRateCommissionProductByEntity(Entity.BANCO_DEL_SOL);

        List<RateCommissionCluster> rateCommissionClusterList = productClusters.stream()
                .flatMap(rcproduct -> rcproduct.getPrices().stream())
                .flatMap(rcprice -> rcprice.getClusters().stream())
                .collect(Collectors.toList());

        clusterJson.put(CLIENTE_SANCOR_CLUSTER_NAME, buildJsonByRateCommissionClusterStream(CLIENTE_SANCOR_CLUSTER_NAME, rateCommissionClusterList));
        clusterJson.put(CLIENTE_NO_SANCOR_CLUSTER_NAME, buildJsonByRateCommissionClusterStream(CLIENTE_NO_SANCOR_CLUSTER_NAME, rateCommissionClusterList));

        return clusterJson;
    }

    private JSONObject buildJsonByRateCommissionClusterStream(String clusterFilterName, List<RateCommissionCluster> rateCommissionClusterStream) {
        JSONObject json = new JSONObject();

        Supplier<Stream<RateCommission>> stream = () -> rateCommissionClusterStream
                .stream()
                .filter(rcc -> rcc.getCluster().toLowerCase().startsWith(clusterFilterName))
                .flatMap(rcc -> rcc.getRateCommissions().stream());

        RateCommission clusterMinInstallments = stream.get()
                .min(Comparator.comparingInt(RateCommission::getMinInstallments)).orElse(null);
        json.put("minInstallments", clusterMinInstallments != null ? clusterMinInstallments.getMinInstallments() : 0);

        RateCommission clusterMaxInstallments = stream.get()
                .max(Comparator.comparingInt(RateCommission::getInstallments)).orElse(null);
        json.put("maxInstallments", clusterMaxInstallments != null ? clusterMaxInstallments.getInstallments() : 0);

        RateCommission clusterMinAmount = stream.get()
                .min(Comparator.comparingInt(RateCommission::getMinAmount)).orElse(null);
        json.put("minAmount", clusterMinAmount != null ? clusterMinAmount.getMinAmount() : 0.0);

        RateCommission clusterMaxAmount = stream.get()
                .max(Comparator.comparingInt(RateCommission::getMaxAmountCommission)).orElse(null);
        json.put("maxAmount", clusterMaxAmount != null ? clusterMaxAmount.getMaxAmountCommission() : 0.0);

        RateCommission clusterMinRateCommission = stream.get()
                .min(Comparator.comparingDouble(RateCommission::getEffectiveAnualRate)).orElse(null);
        json.put("minRateCommission", clusterMinRateCommission != null ? clusterMinRateCommission.getEffectiveAnualRate() : null);

        RateCommission clusterMaxRateCommission = stream.get()
                .max(Comparator.comparingDouble(RateCommission::getEffectiveAnualRate)).orElse(null);
        json.put("maxRateCommission", clusterMaxRateCommission != null ? clusterMaxRateCommission.getEffectiveAnualRate() : 0.0);

        return json;
    }

    @Override
    public void updateBaseValuesInLoan(LoanApplication loanApplication, String documentNumber) throws Exception {
        List<BDSBase> baseResults = rccDao.getBancoDelSolBase(documentNumber);
        JSONObject documentBase = null;
        if (baseResults != null && !baseResults.isEmpty()) {
            documentBase = new JSONObject(new Gson().toJson(baseResults.get(0)));
        }
        BDSBaseProcess process = rccDao.getCurrentBancoDelSolBaseProcess();
        JSONObject procesJson = new JSONObject();
        procesJson.put("id", process.getId());
        procesJson.put("date", process.getProcessDate());

        if (loanApplication.getEntityCustomData() == null)
            loanApplication.setEntityCustomData(new JSONObject());
        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_BASE_DATA.getKey(), documentBase);
        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_BASE_PROCESS_DATE.getKey(), procesJson.toString());

        // Bcra Data
        loanApplication.getEntityCustomData().remove(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_BCRA_DATA.getKey());
//        BcraResult bcraResult = personDAO.getBcraResult(loanApplication.getPersonId());

        JSONObject jsonBcraData = new JSONObject();
        Person person = personDAO.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());
        List<CendeuUlt24Result> cendeuResults = personDAO.getCendeu24Result(person.getDocumentNumber());
        int maxSituacion = cendeuResults.stream()
                .filter(c -> c.getSituacion1() != null)
                .mapToInt(c -> c.getSituacion1())
                .max().orElse(0);
        int minSituacion = cendeuResults.stream()
                .filter(c -> c.getSituacion1() != null)
                .mapToInt(c -> c.getSituacion1())
                .min().orElse(0);
        double sumaDeudaSituacion2 = cendeuResults.stream()
                .filter(c -> c.getSituacion1() != null && c.getSituacion1() >= 2)
                .mapToDouble(c -> c.getMonto1())
                .sum();
        double sumaDeudaTotal = cendeuResults.stream()
                .filter(c -> c.getSituacion1() != null)
                .mapToDouble(c -> c.getMonto1())
                .sum();
        jsonBcraData.put("maxSituacion", maxSituacion);
        jsonBcraData.put("minSituacion", minSituacion);
        jsonBcraData.put("sumaDeudaSituacion2", sumaDeudaSituacion2);
        jsonBcraData.put("sumaDeudaTotal", sumaDeudaTotal);
        // Set the max situation in the last 12 months
        long maxSituacionUlt12m = 0;
        for (int i = 1; i <= 12; i++) {
            int index = i;
            int max = cendeuResults.stream().mapToInt(c -> c.getSituacionOfMonth(index)).max().orElse(0);
            if(max > maxSituacionUlt12m)
                maxSituacionUlt12m = max;
        }
        jsonBcraData.put("maxSituacionUlt12m", maxSituacionUlt12m);
        // Set the count of cheuqes impagos
        List<CendeuRejectedCheck> rejectedChecks = rccDao.getCendeuRejectedChecksByCuit(person.getDocumentNumber());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -12);
        Date date12monthsbefore = cal.getTime();
        long chequesImpagosUlt12M = rejectedChecks.stream()
                .filter(c -> c.getFechaLevantamiento() == null || c.getFechaLevantamiento().before(date12monthsbefore))
                .filter(c -> c.getFechaRechazo().after(date12monthsbefore))
                .count();
        jsonBcraData.put("chequesImpagosUlt12M", chequesImpagosUlt12M);
        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_BCRA_DATA.getKey(), jsonBcraData);

        // BDS Campaign data
        personDAO.deletePreApprovedBase(Entity.BANCO_DEL_SOL, Product.TRADITIONAL, person.getDocumentType().getId(), person.getDocumentNumber());
        personDAO.deletePreApprovedLoanApplicationBase(loanApplication.getId());
        CampaniaBds campaign = rccDao.getLastCampaniaBds(documentNumber);
        if (campaign != null) {
            // Update the entity custom data
            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_CAMPAIGN_ID.getKey(), campaign.getId());
            campaign.setDatosPypNosis(null);
            campaign.setTna(utilService.teaToTna(campaign.getTasa()));
            JSONObject campaignData = new JSONObject(new Gson().toJson(campaign));
            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_CAMPAIGN_DATA.getKey(), campaignData);
            // Register the pre approved data
            personDAO.registerPreApprovedBaseByEntityProductParameter(Entity.BANCO_DEL_SOL, Product.TRADITIONAL, person.getDocumentType().getId(), person.getDocumentNumber(),
                    campaign.getMontoMaximo(), campaign.getPlazoMaximo(), campaign.getTasa() * 100.0, null, null, null, null, null, "{" + EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_CAMPANA + "}");
            // Update the application expiratino date with the vigencia of the base + 1 (this because the vigencia day should be inclusive)
            cal = Calendar.getInstance();
            cal.setTime(campaign.getVigencia());
            cal.add(Calendar.DATE, 1);
            loanApplicationDAO.updateExpirationDate(loanApplication.getId(), cal.getTime());
        }

        // BDS Product id
        Integer bdsProductiId = null;
        if (campaign != null && campaign.getTipoCampania() != null) {
            switch (campaign.getTipoCampania().substring(3, 5)) {
                case "02":
                    bdsProductiId = 3;
                    break;
                case "03":
                case "04":
                    bdsProductiId = 4;
                    break;
                case "08":
                    bdsProductiId = 5;
                    break;
                case "12":
                    bdsProductiId = 6;
                    break;
            }
        }
        if (bdsProductiId == null) {
            String clientType = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey());
            if (clientType != null) {
                if (clientType.equalsIgnoreCase("Actual") || clientType.equalsIgnoreCase("Vinculado"))
                    bdsProductiId = 1;
                else
                    bdsProductiId = 2;
            }
        }
        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PRODUCT_ID.getKey(), bdsProductiId);

        loanApplicationDAO.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
    }

    @Override
    public boolean isValidCBUforBDS(String cbu) {
        return cbu.startsWith("310810");
    }
}
