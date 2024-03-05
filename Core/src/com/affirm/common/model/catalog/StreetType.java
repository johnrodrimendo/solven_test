/**
 * 
 */
package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * @author jrodriguez
 *
 */
public class StreetType implements Serializable {

	public static final int AVENIDA = 1;
	public static final int JIRON = 2;
	public static final int CALLE = 3;
	public static final int PASAJE = 4;

	private Integer id;
	private String type;

	public StreetType() {
	}

	public StreetType(Integer id, String type) {
		super();
		this.id = id;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
