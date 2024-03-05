package com.affirm.nosis.client;

import com.affirm.nosis.NosisResult;
import com.affirm.system.configuration.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class NosisClient {
    /**
     * Get Info with the document number of the person, return xml string
     * @param docNumber Numero de documento de la persona
     * @param nroConsulta Se usara el loanApplicationId
     * @return
     */
    public static NosisResult getInfo(String docNumber, Integer nroConsulta) {

        String url = Configuration.hostEnvIsProduction() ? "xxxxxxxxxxxxxxxxxxxx"
                : "xxxxxxxxxxxxxxxxxxxx";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("Usuario", "xxxxxxxxxxxxxxxxxxxx")
                .queryParam("Password", "xxxxxxxxxxxxxxxxxxxx")
                .queryParam("NroConsulta", nroConsulta)
                .queryParam("Cons_CDA", 0)
                .queryParam("Cons_SoloPorDoc", "Si")
                .queryParam("ConsXML_Doc", docNumber)
                .queryParam("ConsHTML_Doc", docNumber)
                .queryParam("ConsXML_Setup", "CO|HIST.36,CI|HIST.24");

        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                entity,
                String.class);

        String responseXML = response.getBody();

        try {
            DocumentBuilder builderXml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(responseXML));

            Document document = builderXml.parse(src);
            String responseUrl = document.getElementsByTagName("URL").item(0).getTextContent();

            builder = UriComponentsBuilder.fromHttpUrl(responseUrl);
            restTemplate = new RestTemplate();
            return restTemplate.getForObject(builder.build().encode().toUri(), NosisResult.class);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
