package com.affirm.common.service;

import java.io.IOException;
import java.util.List;

public interface PipedriveService {

    Boolean submitLandingForm(String name, String company, String email, String phone, String position, String size, String source) throws IOException,InterruptedException;

    List<String> sources();

    List<String> size();

}
