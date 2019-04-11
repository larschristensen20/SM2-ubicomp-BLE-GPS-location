package com.kontakt.sample;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BLELocationHandler {
    private Context c;

    public BLELocationHandler(Context c) {
        this.c = c;
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = c.getAssets().open("beacons.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public Map<String, String> getBLEInfo(String alias) {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray beaconsArray = obj.getJSONArray("beacons");
            HashMap<String, String> beaconsMap;

            for (int i = 0; i < beaconsArray.length(); i++) {
                JSONObject beacon = beaconsArray.getJSONObject(i);
                String alias_value = beacon.getString("alias");
                if (alias.equals(alias_value)) {
                    beaconsMap = new HashMap<>();

                    beaconsMap.put("roomNameValue", beacon.getString("roomName"));
                    beaconsMap.put("levelValue", beacon.getString("level"));
                    return beaconsMap;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
