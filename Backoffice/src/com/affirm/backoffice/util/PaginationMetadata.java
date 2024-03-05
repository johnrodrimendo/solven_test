package com.affirm.backoffice.util;

import java.util.List;

/**
 * Created by john on 29/12/16.
 */
public class PaginationMetadata {

    private int total;
    private Integer offset;
    private Integer limit;

    public PaginationMetadata(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
