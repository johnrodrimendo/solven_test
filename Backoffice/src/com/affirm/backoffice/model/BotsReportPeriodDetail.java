package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.Bot;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class BotsReportPeriodDetail {
    private String period;
    private Integer quantity;
    private Double cost;
    private Bot bot;

    private Integer botId;

    CatalogService catalog;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        this.catalog = catalog;
        setBotId(JsonUtil.getIntFromJson(json, "bot_id", null));
        setQuantity(JsonUtil.getIntFromJson(json, "quantity", null));
        setCost(JsonUtil.getDoubleFromJson(json, "bot_cost", null));
        setPeriod(JsonUtil.getStringFromJson(json, "period", null));
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Bot getBot() throws Exception {
        if(bot == null && this.botId != null) {
            if(botId.equals(0)){
                bot = new Bot();
                bot.setName("Robots");
            }
            else{
                bot = catalog.getBot(this.botId);
            }
        }
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public Integer getBotId() {
        return botId;
    }

    public void setBotId(Integer botId) {
        this.botId = botId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
