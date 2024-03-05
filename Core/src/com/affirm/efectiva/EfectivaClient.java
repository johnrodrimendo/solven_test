package com.affirm.efectiva;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.service.impl.EntityWebServiceUtilImpl;
import com.affirm.efectiva.client.*;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;

/**
 * Created by miberico on 25/09/17.
 */
@Component
public class EfectivaClient {

    private static Logger logger = Logger.getLogger(EfectivaClient.class);

    @Autowired
    PersonDAO personDAO;
    @Autowired
    EntityWebServiceUtil entityWebServiceUtil;
    @Autowired
    CatalogService catalogService;

    public EntityWebServiceLog<ArrayOfMovilCMERpt> callEfectiva(String documentNumber, Integer loanApplicationId) throws Exception {
        ServiceMovilCME serviceMovilCME = new ServiceMovilCME();
        ServiceMovilCMESoap callService = serviceMovilCME.getServiceMovilCMESoap12();
        MovilCMESlc datos = populateDatos(documentNumber);

        EntityWebServiceLog<ArrayOfMovilCMERpt> response = entityWebServiceUtil.callSoapWs(
                catalogService.getEntityWebService(EntityWebService.EFECTIVA_CONSULTAR_BASE),
                (BindingProvider) callService,
                loanApplicationId,
                new EntityWebServiceUtilImpl.ISOAPProcess() {
                    @Override
                    public ArrayOfMovilCMERpt process() throws Exception {
                        return callService.wmInterfaceCmeMovil(datos);
                    }
                });

        return response;
    }

    public void processEfectiva(String documentNumber, Integer loanApplicationId) throws Exception{

        EntityWebServiceLog<ArrayOfMovilCMERpt> resultWebService = callEfectiva(documentNumber, loanApplicationId);
        MovilCMERpt resultEfectiva = resultWebService.getSoapResponse().getMovilCMERpt().size() > 0 ? resultWebService.getSoapResponse().getMovilCMERpt().get(0) : null;

        if(resultEfectiva != null && resultEfectiva.getCodigoError() == 0){
            if(!resultEfectiva.getCodigoRechazo().isEmpty()){
                System.out.println("[EFECTIVA] : RECHAZO DNI " + documentNumber);
                System.out.println("[EFECTIVA] : " + resultEfectiva.getDescripcionRechazo());
                personDAO.deletePreApprovedBase(Entity.EFECTIVA, Product.TRADITIONAL, IdentityDocumentType.DNI, documentNumber);
                if(Configuration.hostEnvIsProduction())
                    personDAO.registerRejection(Entity.EFECTIVA, IdentityDocumentType.DNI, documentNumber, resultEfectiva.getResultadoEvaluacion(), resultEfectiva.getCodigoRechazo(), resultEfectiva.getDescripcionRechazo());
            }else{
                System.out.println("[EFECTIVA] : DNI " + documentNumber);
                System.out.println("[EFECTIVA] : " + resultEfectiva.getResultadoEvaluacion());
                System.out.println(resultEfectiva.toString());
                personDAO.registerPreApprovedBaseByEntityProductParameter(Entity.EFECTIVA, Product.TRADITIONAL, IdentityDocumentType.DNI, documentNumber,
                        resultEfectiva.getLineaDisponible(), Double.valueOf(resultEfectiva.getPlazoMaximo()).intValue(), Double.valueOf(resultEfectiva.getFile1()), null, null, null, null, resultEfectiva.getFile2(), "{12101}");
            }
        }else if(resultEfectiva == null){
            System.out.println("[EFECTIVA] : ERROR SERVICIO - DNI " + documentNumber);
            System.out.println("[EFECTIVA] : NULL RESPONSE");
            entityWebServiceUtil.updateLogStatusToFailed(resultWebService.getId(), true);
        }else{
            System.out.println("[EFECTIVA] : ERROR SERVICIO - DNI " + documentNumber);
            System.out.println("[EFECTIVA] : " + resultEfectiva.getDescripcionError());
            entityWebServiceUtil.updateLogStatusToFailed(resultWebService.getId(), true);
        }
    }

    public static MovilCMESlc populateDatos(String documentNumber) throws DatatypeConfigurationException {
        MovilCMESlc datos = new MovilCMESlc();
        /*************************************************/
        Autenticacion autenticacion = new Autenticacion();
        autenticacion.setUsuario(System.getenv("EFECTIVA_SERVICE_USER"));
        autenticacion.setClave(System.getenv("EFECTIVA_SERVICE_PASSWORD"));
        /*************************************************/

        /*************************************************/
        Cliente cliente = new Cliente();
        cliente.setDniVendedor("0");
        //ToDo put this 3 variables in a Config class
        cliente.setPlaza(10);
        cliente.setProducto(1);
        cliente.setCanal(5);
        /***********************/
        cliente.setDniCliente(documentNumber);
        cliente.setMontoFinanciar(0);
        cliente.setTipoAfiliacion(0);
        cliente.setTipoCliente(0);
        cliente.setOficioActividad(0);
        cliente.setPerfil(0);
        cliente.setSituacionLaboral(0);
        cliente.setCondicionLaboral(0);
        cliente.setActividadComercial(0);
        cliente.setNegocioPropio("0");
        cliente.setPuestoFijo("");
        cliente.setAntiguedadLaboral(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar()));
        cliente.setTipoIngresos(0);
        cliente.setOtrosIngresos(0);
        cliente.setIngresosConyuge(0);
        cliente.setIngresosComplementoRenta(0);
        cliente.setTipoVivienda(0);
        cliente.setTipoAfiliacion(0);
        cliente.setAntiguedadDomiciliaria(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar()));
        cliente.setIngresos(0);
        cliente.setEstadoCivil(0);
        cliente.setNumeroHijos(0);
        /*************************************************/

        datos.setObjAutenticacion(autenticacion);
        datos.setObjCliente(cliente);

        return datos;
    }

}
