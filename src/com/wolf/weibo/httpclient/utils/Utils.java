
package com.wolf.weibo.httpclient.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Utils {
    public static HashMap<?, ?> JsonHashMap(JSONArray joArr) {
        List<Map<Object, Object>> list = new ArrayList<>();
        JsonArray2HashMap(joArr, list);
        HashMap<Object, Object> result = new HashMap<>();
        for (Map<Object, Object> map : list) {
            Set<?> keys = map.keySet();
            for (Object key : keys) {
                result.put(key, map.get(key));
            }
        }

        return result;
    }

    private static void JsonArray2HashMap(JSONArray joArr, List<Map<Object, Object>> list) {
        for (int i = 0; i < joArr.length(); i++) {
            try {
                if (joArr.get(i) instanceof JSONObject) {
                    JsonObject2HashMap((JSONObject) (joArr.get(i)), list);
                    continue;
                }
                if (joArr.get(i) instanceof JSONArray) {
                    JsonArray2HashMap((JSONArray) joArr.get(i), list);
                    continue;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private static void json2HashMap(String key, Object value,
            List<Map<Object, Object>> list) {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(key, value);
        list.add(map);
    }

    private static void JsonObject2HashMap(JSONObject jo, List<Map<Object, Object>> list) {
        for (Iterator<String> keys = jo.keys(); keys.hasNext();) {
            try {
                String key1 = keys.next();
                System.out.println("key1---" + key1 + "------" + jo.get(key1)
                        + (jo.get(key1) instanceof JSONObject) + jo.get(key1)
                        + (jo.get(key1) instanceof JSONArray));
                if (jo.get(key1) instanceof JSONObject) {

                    JsonObject2HashMap((JSONObject) jo.get(key1), list);
                    continue;
                }
                if (jo.get(key1) instanceof JSONArray) {
                    JsonArray2HashMap((JSONArray) jo.get(key1), list);
                    continue;
                }
                System.out.println("key1:" + key1 + "----------jo.get(key1):"
                        + jo.get(key1));
                json2HashMap(key1, jo.get(key1), list);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
