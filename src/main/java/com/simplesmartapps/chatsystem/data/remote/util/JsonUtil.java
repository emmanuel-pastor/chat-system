package com.simplesmartapps.chatsystem.data.remote.util;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class JsonUtil {

    public static JSONObject fromByteToJson(byte[] message) {
        return new JSONObject(new String(message, StandardCharsets.UTF_8));
    }
}
