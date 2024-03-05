package com.affirm.common.service.impl;

import com.affirm.common.service.PipedriveService;
import com.affirm.pipedrive.client.PipedriveClient;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service("pipedriveService")
public class PipedriveServiceImpl implements PipedriveService {



    @Autowired
    PipedriveClient pipedriveClient;

    @Override
    @Async
    public Boolean submitLandingForm(String name, String company, String email, String phone, String position, String size, String source) throws IOException,InterruptedException {
        Integer personId, companyId;
        boolean dealCreated = false;

        companyId = pipedriveClient.findCompanyIdByNameClient(company);
        Thread.sleep(1500);

        if (companyId == null) {
            pipedriveClient.registerCompanyClient(company, source, size);
            Thread.sleep(1500);
            companyId = pipedriveClient.findCompanyIdByNameClient(company);
        }


        personId = pipedriveClient.findPersonIdByNameAndEmailClient(name, email);
        Thread.sleep(1500);
        if (personId == null) {
            pipedriveClient.registerPersonClient(name, email, phone, position);
            Thread.sleep(1500);
            personId = pipedriveClient.findPersonIdByNameAndEmailClient(name, email);
        }
        Thread.sleep(1500);
        dealCreated = pipedriveClient.insertDeal(personId, companyId);
        return dealCreated;
    }

    @Override
    public List<String> sources() {
        String[] sources = {"Redes sociales", "Linkedin", "Búsqueda en internet", "Eventos/ferias", "Recomendaciones", "Folletos"};
        return Arrays.asList(sources);
    }

    @Override
    public List<String> size() {
        String[] sizes = {"-25", "26-50", "51-100", "101-250", "+251", "+500", "+1000", "+5000"};
        return Arrays.asList(sizes);
    }
}
