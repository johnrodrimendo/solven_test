package com.affirm.common.model;

public class SlackMessage {

    private String text;

    public static final String STG_WEBHOOK_URL = "https://hooks.slack.com/services/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";
    public static final String PRD_WEBHOOK_URL = "https://hooks.slack.com/services/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";
    public static final String PRD_WEBHOOK_ERROR_CRITIC_URL = "https://hooks.slack.com/services/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";
    public static final String STG_WEBHOOK_ERROR_CRITIC_URL = "https://hooks.slack.com/services/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";
    public static final String PRD_WEBHOOK_ERROR_GENERAL_URL = "https://hooks.slack.com/services/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";
    public static final String STG_WEBHOOK_ERROR_GENERAL_URL = "https://hooks.slack.com/services/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";

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
