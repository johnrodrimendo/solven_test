package com.affirm.common.model;

import org.springframework.http.HttpHeaders;

public class BinaryOutputWrapper {

    private byte[] data;
    private HttpHeaders headers;

    public BinaryOutputWrapper(byte[] data, HttpHeaders headers) {
        this.data = data;
        this.headers = headers;
    }

    public BinaryOutputWrapper() {

    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }
}
