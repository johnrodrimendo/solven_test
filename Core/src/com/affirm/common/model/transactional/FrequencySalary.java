package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FrequencySalary implements Serializable {
    private List<Integer> days;
    private Character frequencyType;

    public static final Character MONTHLY = 'M';
    public static final Character BIWEEKLY = 'Q';
    public static final Character WEEKLY = 'S';

    public FrequencySalary() {};

    public FrequencySalary(JSONObject json) {
        JSONArray daysArray = JsonUtil.getJsonArrayFromJson(json, "days", null);
        days = new ArrayList<>();
        for (int i = 0; i < daysArray.length(); i++) {
            days.add(daysArray.getInt(i));
        }
        setFrequencyType(JsonUtil.getCharacterFromJson(json, "frequencyType", null));
    }

    public List<Integer> getDays() { return days; }

    public void setDays(List<Integer> days) { this.days = days; }

    public Character getFrequencyType() { return frequencyType; }

    public void setFrequencyType(Character frequencyType) { this.frequencyType = frequencyType; }

    public String getFrequency() {
        switch (frequencyType) {
            case 'M': return "Mensual";
            case 'Q': return "Quincenal";
            case 'S': return "Semanal";
        }
        return null;
    }

    public String getFrequencyDays() {
        switch (frequencyType) {
            case 'M': return String.format("%d de cada mes", days.get(0));
            case 'Q': return String.format("%d y %d de cada mes", days.get(0), days.get(1));
            case 'S':
                String day = "";
                switch (days.get(0)){
                    case 1: day = "Lunes"; break;
                    case 2: day = "Martes"; break;
                    case 3: day = "Miercoles"; break;
                    case 4: day = "Jueves"; break;
                    case 5: day = "Viernes"; break;
                    case 6: day = "Sabado"; break;
                    case 7: day = "Domingo"; break;
                }
                return String.format("%s de cada semana", day);
        }
        return null;
    }
}
