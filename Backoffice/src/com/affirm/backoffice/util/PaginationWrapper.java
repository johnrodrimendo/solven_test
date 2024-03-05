package com.affirm.backoffice.util;

import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by john on 29/12/16.
 */
public class PaginationWrapper<T extends IPaginationWrapperElement> {

    private PaginationMetadata meta;
    private Class<T> returnType;
    private List<T> results;

    public PaginationWrapper(Class returnType, PaginationMetadata meta) {
        this.returnType = returnType;
        this.meta = meta;
    }

    public void fillFromDb(JSONArray array, CatalogService catalog, MessageSource messageSource, Locale locale) throws Exception {
        if (array != null) {
            results = new ArrayList<T>();
            for (int i = 0; i < array.length(); i++) {
                if (i == 0) {
                    meta.setTotal(array.getJSONObject(i).getInt("pagination_total_count"));
                }
                T object = returnType.getConstructor().newInstance();
                object.fillFromDb(array.getJSONObject(i), catalog, messageSource,locale);
                results.add(object);
            }
        }
    }

    public PaginationMetadata getMeta() {
        return meta;
    }

    public void setMeta(PaginationMetadata meta) {
        this.meta = meta;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
