package com.affirm.client.model.messengerbot.contract;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

public class Message {

	@SerializedName("mid")
	@Expose
	private String mid;
	@SerializedName("seq")
	@Expose
	private Long seq;
	@SerializedName("text")
	@Expose
	private String text;
	@SerializedName("sticker_id")
	@Expose
	private Long stickerId;
	@SerializedName("attachments")
	@Expose
	private List<Attachment> attachments;
    @SerializedName("attachment")
    @Expose
    private Attachment attachment;
    @SerializedName("quick_replies")
    @Expose
    private List<QuickReply> quickReplies;

	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Long getStickerId() {
		return stickerId;
	}

	public void setStickerId(Long stickerId) {
		this.stickerId = stickerId;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

    public List<QuickReply> getQuickReplies() {
        return quickReplies;
    }

    public void setQuickReplies(List<QuickReply> quickReplies) {
        this.quickReplies = quickReplies;
    }

    @Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(mid).append(seq).append(stickerId).append(attachments).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Message) == false) {
			return false;
		}
		Message rhs = ((Message) other);
		return new EqualsBuilder().append(mid, rhs.mid).append(seq, rhs.seq).append(stickerId, rhs.stickerId)
				.append(attachments, rhs.attachments).isEquals();
	}

	/** Used a lot by FbHelper*/
	public String toJson(String senderId, Gson gson) {
		Recipient recipient = new Recipient();
		recipient.setId(senderId);
		Messaging reply = new Messaging();
		reply.setRecipient(recipient);
		reply.setMessage(this);
        String jsonReply = gson.toJson(reply);
        return jsonReply;
	}

}
