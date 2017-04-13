package com.pixlee.pixleesdk;


import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class JsonUtilTests {
    @Test
    public void optDecodeStringTests() throws Exception {
        JSONObject json = new JSONObject();
        String field = "testField";
        String result = JsonUtils.optDecodeString(field, json);
        Assert.assertEquals("", result);

        json.put(field, null);
        result = JsonUtils.optDecodeString(field, json);
        Assert.assertEquals("", result);

        json.put(field, "");
        result = JsonUtils.optDecodeString(field, json);
        Assert.assertEquals("", result);

        json.put(field, "hello world");
        result = JsonUtils.optDecodeString(field, json);
        Assert.assertEquals("hello world", result);
    }
}