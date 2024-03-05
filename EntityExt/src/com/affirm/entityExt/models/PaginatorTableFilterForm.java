package com.affirm.entityExt.models;

public class PaginatorTableFilterForm {

    public final static Integer DEFAULT_LIMIT_PAGINATOR = 100;

    private Integer page;
    private Integer limit;
    private String creationFrom;
    private String creationTo;
    private String search;
    private String product;
    private String status;
    private String disbursementType;
    private Integer minProgress;
    private Integer maxProgress;

    private String rejectedReason;
    private String entityProductParam;

    public String getCreationFrom() {
        return creationFrom;
    }

    public String getCreationTo() {
        return creationTo;
    }

    public void setCreationFrom(String creationFrom) {
        this.creationFrom = creationFrom;
    }

    public void setCreationTo(String creationTo) {
        this.creationTo = creationTo;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(String disbursementType) {
        this.disbursementType = disbursementType;
    }

    public Integer getMinProgress() {
        return minProgress;
    }

    public void setMinProgress(Integer minProgress) {
        this.minProgress = minProgress;
    }

    public Integer getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(Integer maxProgress) {
        this.maxProgress = maxProgress;
    }
    
    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public String getEntityProductParam() {
        return entityProductParam;
    }

    public void setEntityProductParam(String entityProductParam) {
        this.entityProductParam = entityProductParam;
    }
}
