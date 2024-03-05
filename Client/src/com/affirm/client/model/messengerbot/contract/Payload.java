package com.affirm.client.model.messengerbot.contract;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

public class Payload {

	@SerializedName("template_type")
	@Expose
	private String templateType;
	@SerializedName("text")
	@Expose
	private String text;
	@SerializedName("elements")
	@Expose
	private List<Element> elements;
	@SerializedName("buttons")
	@Expose
	private List<Button> buttons;
	@SerializedName("top_element_style")
	@Expose
	private String topElementStyle;

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(templateType).append(elements).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Payload) == false) {
			return false;
		}
		Payload rhs = ((Payload) other);
		return new EqualsBuilder().append(templateType, rhs.templateType).append(elements, rhs.elements).isEquals();
	}

	public String getTopElementStyle() {
		return topElementStyle;
	}

	public void setTopElementStyle(String topElementStyle) {
        this.topElementStyle = topElementStyle;
    }
}
