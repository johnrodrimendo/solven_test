package com.affirm.common.util;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class Marshall {

    static {
        try {
            System.out.println("SETEANDO EL PROPERTY Y EL CONTEXT");
            System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
        } catch (Exception ex) {
            System.out.println("EXCEPCIÓN DURANTE EL SETEO");
            System.out.println(System.getProperty("javax.xml.bind.context.factory"));
            ex.printStackTrace();
        }
    }

    public <T> String toJson(T instance) throws Exception {
        JAXBContext sjc = JAXBContext.newInstance(instance.getClass());
        Marshaller marshaller = sjc.createMarshaller();

        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
        marshaller.marshal(instance, System.out);

        StringWriter sw = new StringWriter();
        marshaller.marshal(instance, sw);

        return sw.toString();
    }

    public <T> String toXml(T instance) throws Exception {
        JAXBContext sjc = JAXBContext.newInstance(instance.getClass());
        Marshaller marshaller = sjc.createMarshaller();
        StringWriter sw = new StringWriter();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(instance, sw);
        return sw.toString();
    }

    public <T> T unmarshall(String json, Class<T> klass) throws Exception {
        JAXBContext sjc = JAXBContext.newInstance(klass);
        Unmarshaller unmarshaller = sjc.createUnmarshaller();

        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        StreamSource jsonSS = new StreamSource(new StringReader(json));
        return unmarshaller.unmarshal(jsonSS, klass).getValue();
    }
}
