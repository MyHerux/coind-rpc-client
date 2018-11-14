package com.heroxu.rpc.bytom;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.nio.charset.Charset;
import java.util.Base64;

public class BytomClient {

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static BytomClient instance;

    private OkHttpClient client;

    private String host;

    private static void init(String ip, String port, String user, String password) {
        if (null == instance) {
            instance = new BytomClient(ip, port, user, password);
        }
    }

    /**
     * Bytom 的 method 直接在 url 中，使用 OkHttp 调用 RPC 接口
     *
     * @param ip       ip
     * @param port     port
     * @param user     user
     * @param password password
     */
    private BytomClient(String ip, String port, String user, String password) {
        host = "http://" + ip + ":" + port;

        String authStr = user + ":" + password;
        final Base64.Encoder encoder = Base64.getEncoder();
        String cred = String.valueOf(encoder.encodeToString(authStr.getBytes(Charset.forName("UTF-8"))));
        String authHeader = "Basic " + cred;

        client = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("Authorization", authHeader)
                    .build();
            return chain.proceed(request);
        }).build();
    }

    public static BytomClient getInstance(String ip, String port, String user, String password) {
        init(ip, port, user, password);
        return instance;
    }


    /**
     * Returns the detail block by block height or block hash.
     *
     * @param blockHash   hash of block.
     * @param blockHeight height of block.
     * @return get specified block information by block_hash or block_height, if both exists, the block result is querying by hash.
     */
    public JSONObject getBlockJson(String blockHash, Integer blockHeight) throws Throwable {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("block_height", blockHeight);
        jsonObject.put("block_hash", blockHash);
        RequestBody body = RequestBody.create(JSON, jsonObject.toJSONString());

        Request request = new Request.Builder()
                .url(host + "/get-block")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return JSONObject.parseObject(response.body().string()).getJSONObject("data");
        }
        return null;
    }

}