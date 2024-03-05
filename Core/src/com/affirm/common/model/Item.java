package com.affirm.common.model;

public class Item {
    private String id;
    private String name;
    private double price;
    private String category;
    private String code;
    private int quantity;
    private double tax;
    private double revenue;

    private String campaign;
    private String source;
    private String medium;

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public Item() {

        this.quantity = 1;
    }

    public Item(String name, double price, double tax) {
        this.id = null;
        this.name = name;
        this.price = price;
        this.category = null;
        this.code = null;
        this.quantity = 1;
        this.tax = tax;
    }

    public Item(String name, double price, double tax, String category) {
        this.id = null;
        this.name = name;
        this.price = price;
        this.category = category;
        this.code = null;
        this.quantity = 1;
        this.tax = tax;
    }

    public Item(String name, double price, double tax, String category, String code) {
        this.id = null;
        this.name = name;
        this.price = price;
        this.category = category;
        this.code = code;
        this.quantity = 1;
        this.tax = tax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }
}
