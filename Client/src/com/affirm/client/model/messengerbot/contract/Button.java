package com.affirm.client.model.messengerbot.contract;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Button {

	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("title")
	@Expose
	private String title;
	@SerializedName("payload")
	@Expose
	private String payload;
	@SerializedName("url")
	@Expose
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(type).append(title).append(payload).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Button) == false) {
			return false;
		}
		Button rhs = ((Button) other);
		return new EqualsBuilder().append(type, rhs.type).append(title, rhs.title).append(payload, rhs.payload)
				.isEquals();
	}

}
