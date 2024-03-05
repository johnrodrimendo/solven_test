package com.affirm.common.service;

import com.affirm.heroku.model.Dyno;

import java.util.List;

public interface HerokuService {

    List<Dyno> getDynos();

}
