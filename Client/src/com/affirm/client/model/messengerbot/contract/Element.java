package com.affirm.client.model.messengerbot.contract;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

public class Element {

	@SerializedName("title")
	@Expose
	private String title;
	@SerializedName("subtitle")
	@Expose
	private String subtitle;
	@SerializedName("image_url")
	@Expose
	private String imageUrl;
	@SerializedName("buttons")
	@Expose
	private List<Button> buttons;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(title).append(subtitle).append(imageUrl).append(buttons).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Element) == false) {
			return false;
		}
		Element rhs = ((Element) other);
		return new EqualsBuilder().append(title, rhs.title).append(subtitle, rhs.subtitle)
				.append(imageUrl, rhs.imageUrl).append(buttons, rhs.buttons).isEquals();
	}

}
