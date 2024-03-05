package com.affirm.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author jrodriguez
 */
public class JsonUtil {

    public static final SimpleDateFormat POSTGRES_TIME_FORMATTER = new SimpleDateFormat("HH:mm:ssX");
    public static final SimpleDateFormat POSTGRES_DATETIME_FORMATTER_1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat POSTGRES_DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX");
    public static final SimpleDateFormat POSTGRES_DATETIME_FORMATTER_2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    public static final SimpleDateFormat POSTGRES_ONLY_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public static Integer getIntFromJson(JSONObject j, String name, Integer defaultValue) {
        try {
            if (j.has(name) && !j.isNull(name)) {
                return j.getInt(name);
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public static Double getDoubleFromJson(JSONObject j, String name, Double defaultValue) {
        try {
            if (j.has(name) && !j.isNull(name)) {
                return j.getDouble(name);
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public static String getStringFromJson(JSONObject j, String name, String defaultValue) {
        try {
            if (j.has(name) && !j.isNull(name)) {
                return j.getString(name);
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public static Character getCharacterFromJson(JSONObject j, String name, Character defaultValue) {
        try {
            if (j.has(name) && !j.isNull(name)) {
                return j.getString(name).charAt(0);
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public static Boolean getBooleanFromJson(JSONObject j, String name, Boolean defaultValue) {
        try {
            if (j.has(name) && !j.isNull(name)) {
                return j.getBoolean(name);
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public static Long getLongFromJson(JSONObject j, String name, Long defaultValue) {
        try {
            if (j.has(name) && !j.isNull(name)) {
                return j.getLong(name);
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }

//    public static Date getDateFromJson(JSONObject j, String name, Date defaultValue, SimpleDateFormat formatter) {
//        try {
//            if (j.has(name) && !j.isNull(name)) {
//                return formatter.parse(j.getString(name));
//            }
//        } catch (Exception ex) {
//        }
//        return defaultValue;
//    }

    public static Date getPostgresDateFromJson(JSONObject j, String name, Date defaultValue) {
        if (j.has(name) && !j.isNull(name)) {
            try {
                return POSTGRES_DATETIME_FORMATTER_1.parse(j.getString(name));
            } catch (Exception ex) {
            }
            try {
                return POSTGRES_DATETIME_FORMATTER.parse(j.getString(name));
            } catch (Exception ex) {
            }
            try {
                return POSTGRES_DATETIME_FORMATTER_2.parse(j.getString(name));
            } catch (Exception ex) {
            }
            try {
                return POSTGRES_ONLY_DATE_FORMATTER.parse(j.getString(name));
            } catch (Exception ex) {
            }
            try {
                return POSTGRES_TIME_FORMATTER.parse(j.getString(name));
            } catch (Exception ex) {
            }
        }
        return defaultValue;
    }

    public static JSONObject getJsonObjectFromJson(JSONObject j, String name, JSONObject defaultValue) {
        try {
            if (j.has(name) && !j.isNull(name)) {
                return j.getJSONObject(name);
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    public static JSONArray getJsonArrayFromJson(JSONObject j, String name, JSONArray defaultValue) {
        try {
            if (j.has(name) && !j.isNull(name)) {
                return j.getJSONArray(name);
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }

    /**
     * Transforms array to List. You need to provide a funtion from array and index to T object
     */
    public static <T> List<T> getListFromJsonArray(JSONArray array, BiFunction<JSONArray, Integer, T> fun) {
        List<T> list = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                list.add(fun.apply(array, i));
            }
        }
        return list;
    }

    /**
     * Transforms array to List. You need to provide a funtion from array and index to T object
     */
    public static <T> List<T> getListFromJsonArray(String jsonArrayString, BiFunction<JSONArray, Integer, T> fun) {
        List<T> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonArrayString);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    list.add(fun.apply(array, i));
                }
            }
            return list;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static JSONArray sanitizedJSONArray(String jsonStringInput) {
        return new JSONArray(sanitizeJsonString(jsonStringInput));
    }

    public static JSONObject sanitizedJSON(String jsonStringInput) {
        return new JSONObject(sanitizeJsonString(jsonStringInput));
    }

    public static String sanitizeJsonString(String jsonStringInput) {
        JsonObject js = (new JsonParser()).parse(jsonStringInput).getAsJsonObject();
        return js.toString();
    }

    public static JSONObject getJSONObjectFromUrl(String link) {
        URL url;
        String jsonString = "";
        JSONObject json = null;
        try {
            url = new URL(link);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString = jsonString + inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!StringUtils.isEmpty(jsonString)) {
            json = sanitizedJSON(jsonString);
        }
        return json;
    }

    public static JSONObject postJSONObjectFromUrl(String link, String body, Optional<String> contentType) {
        try {//text/plain
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", contentType.orElse("application/json"));//text/plain

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED && conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                return new JSONObject(output);
            }
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String... vargs) {
        JSONObject response = postJSONObjectFromUrl("https://www.virginmobile.pe/getMobileNumbers", "{\"dni\": \"70679894\"}", Optional.of("text/plain"));
        System.out.println("code: " + response.getInt("code"));
        JSONArray arr = response.getJSONArray("description");
        for (int i = 0; i < arr.length(); i++) {
            System.out.println("description " + i + ": " + arr.getString(i));
        }
    }

    public static Integer[] getIntegerArrayFromJson(String json, Integer[] defaultValue) {
        try {
            String[] jsonArray = json.split("\\[");
            String[] jsonArray2 = jsonArray[1].split("\\]");
            if (jsonArray2.length > 0) {
                String[] stringValues = jsonArray2[0].split(",");
                List<Integer> integerList = new ArrayList<>();

                for (int i = 0; i < stringValues.length; i++) {
                    integerList.add(Integer.parseInt(stringValues[i]));
                }

                Integer[] integerArray = new Integer[integerList.size()];
                integerArray = integerList.toArray(integerArray);

                return integerArray;
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static String[] getStringArrayFromJson(String json, String[] defaultValue) {
        try {
            String[] jsonArray = json.split("\\[");
            String[] jsonArray2 = jsonArray[1].split("\\]");
            if (jsonArray2.length > 0) {
                String[] stringValues = jsonArray2[0].split(",");

                return stringValues;
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }
}
