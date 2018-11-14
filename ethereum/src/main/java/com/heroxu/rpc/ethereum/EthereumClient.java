package com.heroxu.rpc.ethereum;

import com.alibaba.fastjson.JSONObject;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class EthereumClient {

    private static final String PREFIX = "0x";

    private static EthereumClient instance;

    private JsonRpcHttpClient client;

    private static void init(String ip, String port, String user, String password) throws Throwable {
        if (null == instance) {
            instance = new EthereumClient(ip, port, user, password);
        }
    }

    private EthereumClient(String ip, String port, String user, String password) throws Throwable {
        // 身份认证
        String authStr = user + ":" + password;
        final Base64.Encoder encoder = Base64.getEncoder();
        String cred = String.valueOf(encoder.encodeToString(authStr.getBytes(Charset.forName("UTF-8"))));
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "Basic " + cred);
        headers.put("Content-type", "application/json");
        client = new JsonRpcHttpClient(new URL("http://" + ip + ":" + port), headers);
    }


    public static EthereumClient getInstance(String ip, String port, String user, String password) throws Throwable {
        init(ip, port, user, password);
        return instance;
    }


    /**
     * Returns information about a block by block number.
     *
     * @param blockNumber integer of a block number, or the string "earliest", "latest" or "pending"
     * @param ifMore      Boolean - If true it returns the full transaction objects, if false only the hashes of the transactions.
     * @return block info
     */
    public JSONObject ethGetBlockByNumberJson(Long blockNumber, Boolean ifMore) throws Throwable {
        return client.invoke("eth_getBlockByNumber",
                new Object[]{PREFIX + Long.toHexString(blockNumber), ifMore}, JSONObject.class);
    }


    /**
     * Returns information about a block by hash.
     *
     * @param blockHash 32 Bytes - Hash of a block.
     * @param ifMore    If true it returns the full transaction objects, if false only the hashes of the transactions.
     * @return A block object, or null when no block was found
     */
    public JSONObject ethGetBlockByHashJson(String blockHash, Boolean ifMore) throws Throwable {
        if (!blockHash.startsWith(PREFIX)) {
            blockHash = PREFIX + blockHash;
        }
        return client.invoke("eth_getBlockByHash", new Object[]{blockHash, ifMore}, JSONObject.class);
    }

}