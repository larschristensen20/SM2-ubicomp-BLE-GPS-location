package dk.sdu.mmmi.ubicomp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class BLEInformationHandler {
    private Context c;

    public BLEInformationHandler(Context c) {
        this.c = c;
    }

    private String loadJSONFromAsset() {
        String json;
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

    public BLEDevice getBLEInfo(String alias) {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray beaconsArray = obj.getJSONArray("beacons");
            BLEDevice device;
            for (int i = 0; i < beaconsArray.length(); i++) {
                JSONObject beacon = beaconsArray.getJSONObject(i);
                String alias_value = beacon.getString("alias");
                if (alias.equals(alias_value)) {
                    device = new BLEDevice(alias, beacon.getString("roomName"), beacon.getString("level"));
                    return device;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
