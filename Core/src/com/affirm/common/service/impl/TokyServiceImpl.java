package com.affirm.common.service.impl;

import com.affirm.common.dao.SysUserDAO;
import com.affirm.common.service.TokyService;
import com.affirm.common.util.JsonUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service("tokyService")
public class TokyServiceImpl implements TokyService {

    private static final int TOKY_CALLS_LIMIT = 500;
    private static final int TOKY_REQUEST_THREADS = 5;
    private static final String TOKY_CALLS_URL = "https://api.toky.co/v1/cdrs?filter=$filter&limit=$limit&offset=$offset";

    @Autowired
    private SysUserDAO sysUserDao;

    private final OkHttpClient client = new OkHttpClient.Builder().build();

    @Override
    public Triple<Integer, Integer, Integer> getCallsAndMinutes(Date from, Date to, String sysUserEmail) throws Exception {


        MutableInt offset = new MutableInt(0);
        MutableInt sumCallsOk = new MutableInt(0);
        MutableInt sumCallsError = new MutableInt(0);
        MutableLong sumSeconds = new MutableLong(0);
        MutableBoolean hasMoreResult = new MutableBoolean(true);

        ExecutorService executor = Executors.newFixedThreadPool(TOKY_REQUEST_THREADS);
        runThreads(from, to, sysUserEmail, offset, sumCallsOk, sumCallsError, sumSeconds, hasMoreResult, executor);
        executor.shutdownNow();

        return Triple.of(sumCallsOk.intValue(), sumCallsError.intValue(), Math.toIntExact(sumSeconds.toLong() / 60L));
    }

    private void runThreads(Date from, Date to, String agentId, MutableInt offset, MutableInt sumCallsOk, MutableInt sumCallsError, MutableLong sumSeconds, MutableBoolean hasMoreResult, ExecutorService executor){
        List<CompletableFuture> completables = new ArrayList<>();
        for (int i = 1; i <= TOKY_REQUEST_THREADS; i++) {
            if(offset.getValue() != 0 || i != 1)
                offset.add(TOKY_CALLS_LIMIT);

            int newOffset = offset.intValue();
            completables.add(
                    CompletableFuture.supplyAsync(() -> {
                        callTokyAndSumResult(from, to, agentId, newOffset, sumCallsOk, sumCallsError, sumSeconds, hasMoreResult);
                        return null;
                    }, executor)
            );
        }

        CompletableFuture.allOf(completables.toArray(new CompletableFuture[completables.size()])).join();

        if(hasMoreResult.getValue())
            runThreads(from, to, agentId, offset, sumCallsOk, sumCallsError, sumSeconds, hasMoreResult, executor);
    }

    private void callTokyAndSumResult(Date from, Date to, String agentId, int offset, MutableInt sumCallsOk, MutableInt sumCallsError, MutableLong sumSeconds, MutableBoolean hasMoreResult){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String url = TOKY_CALLS_URL
                    .replace("$filter", URLEncoder.encode("(created_at >= " + sdf.format(from) + ") AND (created_at <= " + sdf.format(to) + ")", "UTF-8"))
                    .replace("$offset", offset + "")
                    .replace("$limit", TOKY_CALLS_LIMIT + "");

            OkHttpClient clientCall = client.newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
            Request.Builder serviceRequestBuilder = new Request.Builder()
                    .url(url)
                    .get();
            serviceRequestBuilder.addHeader("X-Toky-Key", System.getenv("TOKY_API_KEY"));

            Request serviceRequest = serviceRequestBuilder.build();
            Date startDate = new Date();
            Response response = clientCall.newCall(serviceRequest).execute();

            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONArray calls = JsonUtil.getJsonArrayFromJson(jsonResponse, "results", new JSONArray());
            for (int i = 0; i < calls.length(); i++) {
                JSONObject call = calls.getJSONObject(i);
                if (agentId == null || JsonUtil.getStringFromJson(call, "agent_id", "").equals(agentId)) {
                    if (JsonUtil.getLongFromJson(call, "duration", null) != null) {
                        sumSeconds.add(JsonUtil.getLongFromJson(call, "duration", null));
                        sumCallsOk.add(1);
                    } else {
                        sumCallsError.add(1);
                    }
                }
            }
            if (JsonUtil.getStringFromJson(jsonResponse, "next", null) == null){
                hasMoreResult.setValue(false);
            }
        } catch (Exception ex) {
            hasMoreResult.setValue(false);
        }
    }

//    private void callTokyAndSumResult(Date from, Date to, String agentId, int offset, MutableInt sumCallsOk, MutableInt sumCallsError, MutableLong sumSeconds) throws Exception {
//        System.out.println("Calling toky - offset: "+offset);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String url = TOKY_CALLS_URL
//                .replace("$filter", URLEncoder.encode("(created_at >= " + sdf.format(from) + ") AND (created_at <= " + sdf.format(to) + ")", "UTF-8"))
//                .replace("$offset", offset + "")
//                .replace("$limit", TOKY_CALLS_LIMIT + "");
//
//        OkHttpClient clientCall = client.newBuilder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build();
//        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
//        Request.Builder serviceRequestBuilder = new Request.Builder()
//                .url(url)
//                .get();
//        serviceRequestBuilder.addHeader("X-Toky-Key", System.getenv("TOKY_API_KEY"));
//
//        Request serviceRequest = serviceRequestBuilder.build();
//        Response response = clientCall.newCall(serviceRequest).execute();
//
//        JSONObject jsonResponse = new JSONObject(response.body().string());
//        JSONArray calls = JsonUtil.getJsonArrayFromJson(jsonResponse, "results", new JSONArray());
//
//        for (int i = 0; i < calls.length(); i++) {
//            JSONObject call = calls.getJSONObject(i);
//            if (agentId != null && JsonUtil.getStringFromJson(call, "agent_id", "").equals(agentId)) {
//                if (JsonUtil.getLongFromJson(call, "duration", null) != null) {
//                    sumSeconds.add(JsonUtil.getLongFromJson(call, "duration", null));
//                    sumCallsOk.add(1);
//                } else {
//                    sumCallsError.add(1);
//                }
//            }
//        }
//
//        if (JsonUtil.getStringFromJson(jsonResponse, "next", null) != null)
//            callTokyAndSumResult(from, to, agentId, offset + TOKY_CALLS_LIMIT, sumCallsOk, sumCallsError, sumSeconds);
//    }

    public static void main(String[] args) throws Exception {

        Date start = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        TokyServiceImpl service = new TokyServiceImpl();
        Triple<Integer, Integer, Integer> result = service.getCallsAndMinutes(sdf.parse("2018-06-01"), sdf.parse("2018-06-28"), "kromero@solven.pe");

        System.out.println("sumCallsOk: " + result.getLeft());
        System.out.println("sumCallsError: " + result.getMiddle());
        System.out.println("sumSeconds: " + result.getRight());


        System.out.println("Time: " + (new Date().getTime() - start.getTime()));
    }

}
