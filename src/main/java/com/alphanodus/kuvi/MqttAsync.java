package com.alphanodus.kuvi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class MqttAsync extends AsyncTask<Void, Void, String> {

    Context thisContext;


    public MqttAsync(Context context) {
        thisContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {

        String clientId = Utils.getClientId(thisContext) + System.currentTimeMillis();
        MqttService mqttService = MqttService.getMqttService(clientId);

        if (mqttService.connect(thisContext)) {
            Log.d(Utils.LOG_TAG, "Connected to broker.");




        } else {
            Log.d(Utils.LOG_TAG, "Connection failed.");
        }

        return "";
    }
}

