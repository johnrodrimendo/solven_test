package com.affirm.aws.lambda.model;

public class CBUOwnerLambdaResponse {
    private String id;
    private String display_name;
    private String id_type;
    private boolean is_physical_person;

    public CBUOwnerLambdaResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getId_type() {
        return id_type;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }

    public boolean isIs_physical_person() {
        return is_physical_person;
    }

    public void setIs_physical_person(boolean is_physical_person) {
        this.is_physical_person = is_physical_person;
    }
}
