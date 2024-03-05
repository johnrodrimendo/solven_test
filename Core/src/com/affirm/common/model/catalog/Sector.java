/**
 *
 */
package com.affirm.common.model.catalog;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class Sector implements Serializable {

    private String id;
    private String name;

    public Sector(String id, String name){
        setId(id);
        setName(name);
    }

    public void fillFromDb(JSONObject json) {

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
