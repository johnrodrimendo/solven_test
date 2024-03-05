package com.affirm.client.model.messengerbot.contract;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class Delivery {

	@SerializedName("mids")
	@Expose
	private List<String> mids = new ArrayList<String>();
	@SerializedName("watermark")
	@Expose
	private Long watermark;
	@SerializedName("seq")
	@Expose
	private Long seq;

	public List<String> getMids() {
		return mids;
	}

	public void setMids(List<String> mids) {
		this.mids = mids;
	}

	public Long getWatermark() {
		return watermark;
	}

	public void setWatermark(Long watermark) {
		this.watermark = watermark;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(mids).append(watermark).append(seq).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Delivery) == false) {
			return false;
		}
		Delivery rhs = ((Delivery) other);
		return new EqualsBuilder().append(mids, rhs.mids).append(watermark, rhs.watermark).append(seq, rhs.seq)
				.isEquals();
	}

}