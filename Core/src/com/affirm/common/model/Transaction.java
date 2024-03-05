package com.affirm.common.model;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private String id;
    private String affiliation;
    private double revenue;
    private double tax;
    private List<Item> items;

    private String campaign;
    private String source;
    private String medium;

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public Transaction() {
        this.revenue = 0.0;
        this.tax = 0.0;
    }

    public Transaction(String id) {
        this.id = id;
        this.revenue = 0.0;
        this.tax = 0.0;

        this.affiliation = null;
        this.items = null;
    }

    public Transaction(String id, String affiliation, Item item) {
        this.id = id;
        this.affiliation = affiliation;
        this.items = new ArrayList<>();

        this.revenue = item.getRevenue() * (item.getQuantity() * 1.0);
        this.tax = item.getTax() * (item.getQuantity() * 1.0);

        item.setId(id);
        items.add(item);
    }

    public Transaction(String id, String affiliation, List<Item> items) {
        this.id = id;
        this.affiliation = affiliation;
        this.revenue = 0.0;
        this.tax = 0.0;

        for(Item item : items)
        {
            item.setId(id);
            this.revenue += item.getPrice() * (item.getQuantity() * 1.0);
            this.tax += item.getTax() * (item.getQuantity() * 1.0);
        }

        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public List<Item> getItems() {
        return items;
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

    public void setItem(Item item) {
        this.revenue = item.getRevenue();

        item.setId(id);

        this.items = new ArrayList<>();
        items.add(item);
    }

    public void setItems(List<Item> items) {
        this.revenue = 0.0;
        this.tax = 0.0;

        for(Item item : items)
        {
            item.setId(id);
            this.revenue += item.getRevenue() * (item.getQuantity() * 1.0);
            this.tax += item.getTax() * (item.getQuantity() * 1.0);
        }

        this.items = items;
    }
}
