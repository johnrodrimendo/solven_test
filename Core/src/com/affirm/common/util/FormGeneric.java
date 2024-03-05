/**
 * 
 */
package com.affirm.common.util;

/**
 * @author jrodriguez
 *
 *         Contains the validation of all the forms
 *
 */
public abstract class FormGeneric {

	private FormValidator validator;

	public FormValidator getValidator() {
		return validator;
	}

	public void setValidator(FormValidator validator) {
		this.validator = validator;
	}

}
