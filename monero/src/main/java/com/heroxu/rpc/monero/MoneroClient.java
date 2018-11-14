package com.heroxu.rpc.monero;

import com.alibaba.fastjson.JSONObject;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MoneroClient {

    private static MoneroClient instance;

    private JsonRpcHttpClient client;

    private static void init(String ip, String port, String user, String password) throws Throwable {
        if (null == instance) {
            instance = new MoneroClient(ip, port, user, password);
        }
    }

    private MoneroClient(String ip, String port, String user, String password) throws Throwable {
        // 身份认证
        String authStr = user + ":" + password;
        final Base64.Encoder encoder = Base64.getEncoder();
        String cred = String.valueOf(encoder.encodeToString(authStr.getBytes(Charset.forName("UTF-8"))));
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "Basic " + cred);
        client = new JsonRpcHttpClient(new URL("http://" + ip + ":" + port + "/json_rpc"), headers);
    }

    public static MoneroClient getInstance(String ip, String port, String user, String password) throws Throwable {
        init(ip, port, user, password);
        return instance;
    }


    /**
     * 获取 Block 信息
     *
     * @param height height of block
     * @return block
     */
    public JSONObject getBlockJson(Long height) throws Throwable {
        JSONObject data = client.invoke("getblock", new Object[]{height}, JSONObject.class);
        JSONObject json = data.getJSONObject("json");
        data.put("json", json);
        return data;
    }

}