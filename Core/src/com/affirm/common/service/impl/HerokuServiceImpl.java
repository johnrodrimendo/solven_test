package com.affirm.common.service.impl;

import com.affirm.common.service.HerokuService;
import com.affirm.heroku.HerokuServiceCall;
import com.affirm.heroku.model.Dyno;
import com.affirm.system.configuration.Configuration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HerokuServiceImpl implements HerokuService {

    private static final String ACTIVE_PROFILE = Configuration.hostEnvIsLocal() ? "stg" : System.getenv(Configuration.HOSTENV_KEY);

    private static final List<String> URLS = Arrays.asList(
            String.format("https://api.heroku.com/apps/%s-%s/dynos", "solven-ac", ACTIVE_PROFILE),
            String.format("https://api.heroku.com/apps/%s-%s/dynos", "solven-la", ACTIVE_PROFILE),
            String.format("https://api.heroku.com/apps/%s-%s/dynos", "solven-worker", ACTIVE_PROFILE),
            String.format("https://api.heroku.com/apps/%s-%s/dynos", "solven-entity", ACTIVE_PROFILE)
    );

    private final HerokuServiceCall herokuServiceCall;

    public HerokuServiceImpl(HerokuServiceCall herokuServiceCall) {
        this.herokuServiceCall = herokuServiceCall;
    }

    @Override
    public List<Dyno> getDynos() {
        List<Dyno> allDynos = new ArrayList<>();

        URLS.stream().map(herokuServiceCall::getDynos).forEach(dynos -> {
            for (Dyno dyno : dynos) {
                if (dyno != null) {
                    dyno.fillCustomName();
                    allDynos.add(dyno);
                }
            }
        });

        return allDynos;
    }
}
