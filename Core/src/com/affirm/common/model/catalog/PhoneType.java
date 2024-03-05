package com.affirm.common.model.catalog;

/**
 * Created by dev5 on 02/02/18.
 */
public class PhoneType {

    public static final String LANDLINE = "L";
    public static final String CELLPHONE = "M";

    private String id;
    private String name;

    public PhoneType(String id, String name){
        this.id = id;
        this.name = name;
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
}
