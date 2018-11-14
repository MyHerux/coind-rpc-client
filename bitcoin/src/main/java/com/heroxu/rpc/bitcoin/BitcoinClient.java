package com.heroxu.rpc.bitcoin;

import com.alibaba.fastjson.JSONObject;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import sun.misc.BASE64Encoder;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BitcoinClient {

    private static BitcoinClient instance;

    private JsonRpcHttpClient client;

    private static void init(String ip, String port, String user, String password) throws Throwable {
        if (null == instance) {
            instance = new BitcoinClient(ip, port, user, password);
        }
    }

    private BitcoinClient(String ip, String port, String user, String password) throws Throwable {
        // 身份认证
        String authStr = user + ":" + password;
        final Base64.Encoder encoder = Base64.getEncoder();
        String cred = String.valueOf(encoder.encodeToString(authStr.getBytes("UTF-8")));
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "Basic " + cred);
        client = new JsonRpcHttpClient(new URL("http://" + ip + ":" + port), headers);
    }


    public static BitcoinClient getInstance(String ip, String port, String user, String password) throws Throwable {
        init(ip, port, user, password);
        return instance;
    }


    /**
     * 获取钱包信息
     *
     * @return 钱包信息
     */
    public JSONObject getWalletInfoJson() throws Throwable {
        return client.invoke("getwalletinfo", new Object[]{}, JSONObject.class);
    }
}