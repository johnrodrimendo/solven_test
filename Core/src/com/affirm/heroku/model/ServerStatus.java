package com.affirm.heroku.model;

import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerStatus implements Serializable {

    public static final String ENTITY_APP_NAME = "Extranet Entity";
    public static final String ACQUISITION_APP_NAME = "Acquisition";
    public static final String LANDING_APP_NAME = "Landing";
    public static final String SCHEDULER_APP_NAME = "Scheduler";
    public static final String JOB_APP_NAME = "Worker";


    private String app;
    private String state;
    private Date registerDate;

    public void fillFromDb(JSONObject json) {
        setApp(JsonUtil.getStringFromJson(json, "app", null));
        setState(JsonUtil.getStringFromJson(json, "state", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public void setState(List<Dyno> dynos) {
        if (dynos.stream().anyMatch(e -> e.getState().equalsIgnoreCase("crashed"))) {
            setState("crashed");
        } else if (dynos.stream().anyMatch(e -> e.getState().equalsIgnoreCase("down"))) {
            setState("down");
        } else if (dynos.stream().anyMatch(e -> e.getState().equalsIgnoreCase("idle"))) {
            setState("idle");
        } else if (dynos.stream().anyMatch(e -> e.getState().equalsIgnoreCase("starting"))) {
            setState("starting");
        } else {
            setState("up");
        }
    }

    public void setCustomState(String state) {
        this.state = state;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getState() {
        return state;
    }

    private void setState(String state) {
        this.state = state;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String generateClusterName (String appName){
        String base = "{{env}}-{{app}}-ecs-cluster";
        base = base.replace("{{env}}", Configuration.hostEnvIsProduction() ? "prd" : "stg");
        switch (appName){
            case ENTITY_APP_NAME:
                return base.replace("{{app}}","entity");
            case ACQUISITION_APP_NAME:
                return base.replace("{{app}}","acquisition");
            case JOB_APP_NAME:
                return base.replace("{{app}}","job");
            case SCHEDULER_APP_NAME:
                return base.replace("{{app}}","schedule");
            case LANDING_APP_NAME:
                return base.replace("{{app}}","landing");
        }
        return null;
    }

    public String generateServiceName(String appName){
        String base = "{{env}}-{{app}}-ecs-service";
        base = base.replace("{{env}}", Configuration.hostEnvIsProduction() ? "prd" : "stg");
        switch (appName){
            case ENTITY_APP_NAME:
                return base.replace("{{app}}","entity");
            case ACQUISITION_APP_NAME:
                return base.replace("{{app}}","acquisition");
            case JOB_APP_NAME:
                return base.replace("{{app}}","job");
            case SCHEDULER_APP_NAME:
                return base.replace("{{app}}","scheduler");
            case LANDING_APP_NAME:
                return base.replace("{{app}}","landing");
        }
        return null;
    }

    public String convertECSStatusToServerStatus(com.amazonaws.services.ecs.model.Service service){
        if(service == null) return "down";
        if(service.getRunningCount() > 0) return "up";
        if(service.getPendingCount() > 0) return "starting";
        return "down";
    }


    public static class Grouped {
        private String date;
        private List<Status> statuses;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public void setStatuses(List<Status> statuses) {
            this.statuses = statuses;
        }

        public void addStatus(Status status) {
            if (statuses == null)
                statuses = new ArrayList<>();
            statuses.add(status);
        }
    }

    public static class Status {
        private String app;
        private long crashed = 0;
        private long down = 0;
        private long crashedDown = 0;
        private long idle = 0;
        private long starting = 0;
        private long idleStarting = 0;
        private long up = 0;
        private double total = 0.0;

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getCrashedPerc() {
            return crashedPerc;
        }

        public void setCrashedPerc(double crashedPerc) {
            this.crashedPerc = crashedPerc;
        }

        public double getDownPerc() {
            return downPerc;
        }

        public void setDownPerc(double downPerc) {
            this.downPerc = downPerc;
        }

        public double getIdlePerc() {
            return idlePerc;
        }

        public void setIdlePerc(double idlePerc) {
            this.idlePerc = idlePerc;
        }

        public double getStartingPerc() {
            return startingPerc;
        }

        public void setStartingPerc(double startingPerc) {
            this.startingPerc = startingPerc;
        }

        public double getUpPerc() {
            return upPerc;
        }

        public void setUpPerc(double upPerc) {
            this.upPerc = upPerc;
        }

        private double crashedPerc = 0.0;
        private double downPerc = 0.0;
        private double idlePerc = 0.0;
        private double startingPerc = 0.0;
        private double upPerc = 0.0;

        public long getCrashedDown() {
            return crashedDown;
        }

        public void setCrashedDown(long crashedDown) {
            this.crashedDown = crashedDown;
        }

        public long getIdleStarting() {
            return idleStarting;
        }

        public void setIdleStarting(long idleStarting) {
            this.idleStarting = idleStarting;
        }

        public double getCrashedDownPerc() {
            return crashedDownPerc;
        }

        public void setCrashedDownPerc(double crashedDownPerc) {
            this.crashedDownPerc = crashedDownPerc;
        }

        public double getIdleStartingPerc() {
            return idleStartingPerc;
        }

        public void setIdleStartingPerc(double idleStartingPerc) {
            this.idleStartingPerc = idleStartingPerc;
        }

        private double crashedDownPerc = 0.0;
        private double idleStartingPerc = 0.0;

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public long getCrashed() {
            return crashed;
        }

        public void setCrashed(long crashed) {
            this.crashed = crashed;
        }

        public long getDown() {
            return down;
        }

        public void setDown(long down) {
            this.down = down;
        }

        public long getIdle() {
            return idle;
        }

        public void setIdle(long idle) {
            this.idle = idle;
        }

        public long getStarting() {
            return starting;
        }

        public void setStarting(long starting) {
            this.starting = starting;
        }

        public long getUp() {
            return up;
        }

        public void setUp(long up) {
            this.up = up;
        }
    }
}
