package com.affirm.client.model.messengerbot.contract;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Messaging {

	@SerializedName("sender")
	@Expose
	private Sender sender;
	@SerializedName("recipient")
	@Expose
	private Recipient recipient;
	@SerializedName("timestamp")
	@Expose
	private long timestamp;
	@SerializedName("message")
	@Expose
	private Message message;

	@SerializedName("postback")
	@Expose
	private Postback postback;

	@SerializedName("delivery")
	@Expose
	private Delivery delivery;

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Postback getPostback() {
		return postback;
	}

	public void setPostback(Postback postback) {
		this.postback = postback;
	}

	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}

	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(sender).append(recipient).append(timestamp).append(message).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Messaging) == false) {
			return false;
		}
		Messaging rhs = ((Messaging) other);
		return new EqualsBuilder().append(sender, rhs.sender).append(recipient, rhs.recipient)
				.append(timestamp, rhs.timestamp).append(message, rhs.message).isEquals();
	}

}
