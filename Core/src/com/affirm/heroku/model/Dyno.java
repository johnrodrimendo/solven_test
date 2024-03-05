package com.affirm.heroku.model;

public class Dyno {

    private String id;
    private String name;
    private String state;
    private String type;
    private App app;
    private String customAppName;

    public static class App {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public void fillCustomName() {
        if (app != null) {
            String[] appNameSplit = app.getName().split("-");
            if(appNameSplit.length < 2) return;
            switch (appNameSplit[1]) {
                case "ac":
                    customAppName = "Acquisition";
                    break;
                case "la":
                    customAppName = "Landing";
                    break;
                case "worker":
                    if (type.equalsIgnoreCase("worker"))
                        customAppName = "Worker";
                    else if (type.equalsIgnoreCase("schedule"))
                        customAppName = "Scheduler";
                    else
                        customAppName = "";
                    break;
                case "entity":
                    customAppName = "Extranet Entity";
                    break;
                case "company":
                    customAppName = "Extranet Company";
                    break;
                default:
                    customAppName = "";
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public String getCustomAppName() {
        return customAppName;
    }

    public void setCustomAppName(String customAppName) {
        this.customAppName = customAppName;
    }
}
