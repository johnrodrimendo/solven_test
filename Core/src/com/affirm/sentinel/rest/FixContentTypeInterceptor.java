package com.affirm.sentinel.rest;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class FixContentTypeInterceptor implements Interceptor {
    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request fixedRequest = originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .build();
        return chain.proceed(fixedRequest);
    }
}
