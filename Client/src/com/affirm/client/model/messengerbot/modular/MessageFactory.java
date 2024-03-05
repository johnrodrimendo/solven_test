package com.affirm.client.model.messengerbot.modular;

import com.affirm.client.model.messengerbot.contract.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarmando on 18/01/17.
 */
public class MessageFactory {

    private MessageFactory() {
        throw new RuntimeException("Non instantiable class");
    }


    public static Message newSimpleMessage(String msg) {
        Message message = new Message();
        message.setText(msg);
        return message;
    }

    public static Message newQuickReplyMessage(String questionStr, Button... buttons) {
        return newQuickReplyMessage(questionStr, Arrays.asList(buttons));
    }

    public static Message newQuickReplyMessage(String questionStr, List<Button> buttons) {
        if (buttons.size() == 0) throw new RuntimeException("No buttons defined");
        Message message = new Message();
        message.setText(questionStr);
        message.setQuickReplies(buttons.stream().map(btn -> {
            QuickReply quickReply = new QuickReply();
            quickReply.setTitle(btn.getTitle());
            quickReply.setContentType("text");
            quickReply.setImageUrl(btn.getUrl());
            quickReply.setPayload(btn.getPayload());
            return quickReply;
        }).collect(Collectors.toList()));
        return message;// newCTAMessage(questionStr, buttons);//WISHLIST IMPLEMENT QUICK REPLIES
    }

    public static Message newCTAMessage(String questionStr, Button... buttons) {
        return newCTAMessage(questionStr, Arrays.asList(buttons));
    }

    public static Message newCTAMessage(String questionStr, List<Button> buttons) {
        if (buttons.size() == 0) throw new RuntimeException("No buttons defined");
        Payload payload = new Payload();
        payload.setText(questionStr);
        payload.setButtons(buttons);
        payload.setTemplateType("button");

        Attachment attachment = new Attachment();
        attachment.setPayload(payload);
        attachment.setType("template");
        Message message = new Message();
        message.setAttachment(attachment);

        return message;
    }

    //button makers
    private static Button newButton(String title, String type) {
        Button button = new Button();
        button.setType(type);
        button.setTitle(title);
        return button;
    }

    public static Button newPostBackButton(String title, String payload) {
        Button button = newButton(title, "postback");
        button.setPayload(payload);
        return button;
    }

    public static Button newWebUrlButton(String title, String url) {
        Button button = newButton(title, "web_url");
        button.setUrl(url);
        return button;
    }

    public static Button newShareButton(String title) {
        return newButton(title, "element_share");
    }

    //Element Message
    public static Message newElementsMessage(Element... elements) {
        Payload payload = new Payload();
        payload.setElements(Arrays.asList(elements));
        payload.setTemplateType("generic");
        Attachment attachment = new Attachment();
        attachment.setPayload(payload);
        attachment.setType("template");
        Message message = new Message();
        message.setAttachment(attachment);
        return message;//the templatetype doesnt allow for pictures to be shown
    }

    //element maker
    public static Element newImageElement(String title, String subtitle, String urlImage, Button... buttons) {
        Element e = new Element();
        e.setTitle(title);
        e.setSubtitle(subtitle);
        e.setImageUrl(urlImage);
        e.setButtons(Arrays.asList(buttons));
        return e;
    }

    public static Message newListMessage(Element... elements) {
        Payload payload = new Payload();
        payload.setElements(Arrays.asList(elements));
        payload.setTemplateType("list");
        payload.setTopElementStyle("compact");
        Attachment attachment = new Attachment();
        attachment.setPayload(payload);
        attachment.setType("template");
        Message message = new Message();
        message.setAttachment(attachment);
        return message;//the templatetype doesnt allow for pictures to be shown
    }
}