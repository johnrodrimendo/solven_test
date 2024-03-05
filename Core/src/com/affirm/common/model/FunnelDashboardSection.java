package com.affirm.common.model;


import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class FunnelDashboardSection {
    private String name;
    private Integer values;
    private Set<Pair<String, Integer>> rows;

    private List<Integer> users;

    private HashMap<String, HashMap<String, List<String>>> mapDetail;

    private UtilService utilService;

    private HashMap<String, Double> calculatedTotal;
    private HashMap<String, Double> calculatedAvg;

    public FunnelDashboardSection() {
    }

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        String customName = JsonUtil.getStringFromJson(json, "js_id", null);

        switch (customName) {
            case "consumer_credits":
                customName = "Crédito de Consumo";
                break;
            case "salary_advance":
                customName = "Adelanto de sueldo";
                break;
            case "bots":
                customName = "Consumos variables";
                break;
        }

        setName(customName);
        JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(json, "js_detail", null);

        mapDetail = new HashMap<>();
        rows = new HashSet<>();

        calculatedTotal = new HashMap<>();
        calculatedAvg = new HashMap<>();


        setValues(2);
        for(int i = 0 ; i < jsonArray.length() ; ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String period = JsonUtil.getStringFromJson(jsonObject, "period", null);
            JSONArray detailArray = JsonUtil.getJsonArrayFromJson(jsonObject, "detail", null);

            HashMap<String, List<String>> rowMap = mapDetail.get(period);

            if(rowMap == null) {
                rowMap = new HashMap<>();
                mapDetail.put(period, rowMap);
            }

            for(int j = 0 ; j < detailArray.length() ; ++j) {
                JSONObject detailObject = detailArray.getJSONObject(j);

                String rowText = JsonUtil.getStringFromJson(detailObject, "row_text", null);
                Integer order = JsonUtil.getIntFromJson(detailObject, "application_status_id", null);

                if(order == null) {
                    order = JsonUtil.getIntFromJson(detailObject, "row_id", null);
                }

                if(order == null) {
                    order = 0;
                }

                List<String> cell = rowMap.get(rowText);
                rows.add(Pair.of(rowText, order));

                if(cell == null) {
                    cell = new ArrayList<>();
                    rowMap.put(rowText, cell);
                }
                if(getName().equals("Consumos variables")) {
                    cell.add(String.valueOf(JsonUtil.getDoubleFromJson(detailObject, "bot_cost", 0.0)));
                    cell.add(String.valueOf(JsonUtil.getIntFromJson(detailObject, "quantity", 0)));
                }
                else {
                    cell.add(String.valueOf(JsonUtil.getIntFromJson(detailObject, "num_events", 0)));
                    cell.add(String.valueOf(JsonUtil.getIntFromJson(detailObject, "num_events", 0)));
                }
                Double total = JsonUtil.getDoubleFromJson(detailObject, "total", null);
                if(total != null) {
                    calculatedTotal.put(period, total);
                }
                Double avg = JsonUtil.getDoubleFromJson(detailObject, "avg", null);
                if(avg != null) {
                    calculatedAvg.put(period, avg);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValues() {
        return values;
    }

    public void setValues(Integer values) {
        this.values = values;
    }

    public HashMap<String, HashMap<String, List<String>>> getMapDetail() {
        return mapDetail;
    }

    public void setMapDetail(HashMap<String, HashMap<String, List<String>>> mapDetail) {
        this.mapDetail = mapDetail;
    }

    public List<String> getRows() {
        List<Pair<String, Integer>> calculatedRows = new ArrayList<>(rows);

        calculatedRows.sort(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                if(o1.getValue() < o2.getValue()){
                    return - 1;
                }
                else if(o1.getValue().equals(o2.getValue())) {
                    return 0;
                }

                return 1;
            }
        });

        List<String> response = new ArrayList<>();

        for(Pair<String, Integer> calculatedRow : calculatedRows) {
            response.add(calculatedRow.getKey());
        }

        return response;
    }

    public void setRows(Set<Pair<String, Integer>> rows) {
        this.rows = rows;
    }

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }

    public UtilService getUtilService() {
        return utilService;
    }

    public void setUtilService(UtilService utilService) {
        this.utilService = utilService;
    }

    public HashMap<String, Double> getCalculatedTotal() {
        return calculatedTotal;
    }

    public void setCalculatedTotal(HashMap<String, Double> calculatedTotal) {
        this.calculatedTotal = calculatedTotal;
    }

    public HashMap<String, Double> getCalculatedAvg() {
        return calculatedAvg;
    }

    public void setCalculatedAvg(HashMap<String, Double> calculatedAvg) {
        this.calculatedAvg = calculatedAvg;
    }
}
