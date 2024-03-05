package com.affirm.abaco.service;

import com.affirm.abaco.service.impl.CreditoServiceImpl;
import com.affirm.api.endpoint.soap.impl.ConfirmOperationEndpointImpl;

import javax.xml.ws.Endpoint;

public class WebServicePublisher {

    public static void main(String[] args) {

        Endpoint.publish("http://localhost:8080/servicio/WSCredito", new CreditoServiceImpl());
        Endpoint.publish("http://localhost:8080/v2/servicio/WSCredito", new ConfirmOperationEndpointImpl());
    }

}