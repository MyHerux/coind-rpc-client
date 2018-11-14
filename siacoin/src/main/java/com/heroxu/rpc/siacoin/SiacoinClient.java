package com.heroxu.rpc.siacoin;

import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

public class SiacoinClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final static String userAgent = "Sia-Agent";

    private static SiacoinClient instance;

    private OkHttpClient client;

    private String host;

    private static void init(String ip, String port, String user, String password) {
        if (null == instance) {
            instance = new SiacoinClient(ip, port, user, password);
        }
    }

    /**
     * Siacoin 的 method 直接在 url 中，使用 OkHttp 调用 RPC 接口
     *
     * @param ip       ip
     * @param port     port
     * @param user     user
     * @param password password
     */
    private SiacoinClient(String ip, String port, String user, String password) {
        host = "http://" + ip + ":" + port;

        String auth = ":" + password;
        final Base64.Encoder encoder = Base64.getEncoder();
        String cred = String.valueOf(encoder.encodeToString(auth.getBytes(Charset.forName("UTF-8"))));
        String authHeader = "Basic " + cred;

        client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("User-Agent", userAgent)
                    .addHeader("Authorization", authHeader)
                    .build();

            return chain.proceed(request);
        }).build();
    }

    public static SiacoinClient getInstance(String ip, String port, String user, String password) {
        init(ip, port, user, password);
        return instance;
    }


    /**
     * returns information about the consensus set, such as the current block height.
     *
     * @return Consensus
     */
    public JSONObject consensusJson() throws IOException {
        Request request = new Request.Builder()
                .url(host + "/consensus")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return JSONObject.parseObject(response.body().string());
        }
        return null;
    }

}