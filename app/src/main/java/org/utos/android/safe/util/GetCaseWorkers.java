package org.utos.android.safe.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zachariah.davis on 1/24/17.
 */

public class GetCaseWorkers {

    private final Context ctx;

    public GetCaseWorkers(Context _ctx) {
        ctx = _ctx;
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = ctx.getAssets().open("caseworkers.json");
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

    public ArrayList<HashMap<String, String>> getCaseWorkers() {

        //
        ArrayList<HashMap<String, String>> caseWorkersList = new ArrayList<>();

        try {
            //            JsonObjectRequest jsonObj = new JsonObjectRequest(loadJSONFromAsset());
            JSONObject jsonObj = new JSONObject(loadJSONFromAsset());

            // Getting JSON Array node
            JSONArray caseWorkers = jsonObj.getJSONArray("CaseWorkers");

            // looping through All bases
            for (int i = 0; i < caseWorkers.length(); i++) {
                JSONObject c = caseWorkers.getJSONObject(i);

                String name = c.getString("name");
                String phone = c.getString("phone");

                // tmp hashmap for single contact
                HashMap<String, String> contactTemp = new HashMap<>();

                // adding each child node to HashMap key => value
                contactTemp.put("name", name);
                contactTemp.put("phone", phone);

                // adding contact to contact list
                caseWorkersList.add(contactTemp);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return caseWorkersList;
    }

}
