package com.affirm.tests.efectiva


import com.affirm.common.model.Offer
import com.affirm.common.model.transactional.EntityWebServiceLog
import com.affirm.efectiva.EfectivaClient
import com.affirm.efectiva.client.ArrayOfMovilCMERpt
import com.affirm.efectiva.client.MovilCMERpt
import com.affirm.tests.BaseConfig
import com.opencsv.CSVWriter
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class EfectivaClientTest extends BaseConfig {

    @Autowired
    private EfectivaClient efectivaClient

    @Test
    void shouldGetOffer() {
        String input = "/home/omar/Downloads/base.csv"
        String output = "/home/omar/Downloads/outputEfectiva.csv"

        List<Offer> offers = new ArrayList<>()

        new File(input).splitEachLine(",") { fields ->
            Offer offer = new Offer()
            offer.setDocumentNumber(fields[0])
            offer.setEmail(fields[1])
            offer.setPhoneNumber(fields[2])
            offer.setPersonName(fields[3])

            offers.add(offer)
        }

        offers.remove(0) // remove header

        for (Offer offer : offers) {

            EntityWebServiceLog<ArrayOfMovilCMERpt> resultWebService = efectivaClient.callEfectiva(offer.getDocumentNumber(), null)

            MovilCMERpt resultEfectiva = resultWebService.getSoapResponse().getMovilCMERpt().size() > 0 ? resultWebService.getSoapResponse().getMovilCMERpt().get(0) : null

            if (resultEfectiva != null && resultEfectiva.getCodigoError() == 0) {
                if (!resultEfectiva.getCodigoRechazo().isEmpty()) {
                    System.out.println("[EFECTIVA] : RECHAZO DNI " + offer.getDocumentNumber())
                    System.out.println("[EFECTIVA] : " + resultEfectiva.getDescripcionRechazo())
                } else {
                    System.out.println("[EFECTIVA] : DNI " + offer.getDocumentNumber())
                    System.out.println("[EFECTIVA] : " + resultEfectiva.getResultadoEvaluacion())
                    System.out.println(resultEfectiva.toString())

                    offer.setAmount(resultEfectiva.getLineaDisponible())
                    offer.setInstallment(Double.valueOf(resultEfectiva.getPlazoMaximo()).intValue())

                    if (resultEfectiva.getFile1() != null && !resultEfectiva.getFile1().isEmpty())
                        offer.setRate(Double.valueOf(resultEfectiva.getFile1()))
                }
            } else if (resultEfectiva == null) {
                System.out.println("[EFECTIVA] : ERROR SERVICIO - DNI " + offer.getDocumentNumber())
                System.out.println("[EFECTIVA] : NULL RESPONSE")
            } else {
                System.out.println("[EFECTIVA] : ERROR SERVICIO - DNI " + offer.getDocumentNumber())
                System.out.println("[EFECTIVA] : " + resultEfectiva.getDescripcionError())
            }
        }

        File file = new File(output)
        CSVWriter csvWriter = new CSVWriter(new FileWriter(file))
        List<String[]> data = new ArrayList<String[]>()

        String[] headers = [
                "document_number",
                "email",
                "phone_number",
                "split_part",
                "max_amount",
                "max_installments",
                "effective_annual_rate",
        ]

        data.add(headers)

        for (Offer offer : offers) {
            String[] array = [
                    offer.getDocumentNumber(),
                    offer.getEmail(),
                    offer.getPhoneNumber(),
                    offer.getPersonName(),
                    offer.getAmount(),
                    offer.getInstallment(),
                    offer.getRate()
            ]
            data.add(array)
        }

        csvWriter.writeAll(data)
        csvWriter.close()
    }

}
