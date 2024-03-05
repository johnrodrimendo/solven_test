package com.affirm.common.model.transactional;

/**
 * Created by jarmando on 15/02/17.
 */
public class Attachment {
    private String filename;
    private String content;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
