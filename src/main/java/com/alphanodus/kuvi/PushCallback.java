package com.alphanodus.kuvi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PushCallback implements MqttCallback {

    private Context context;
    private String Message = "";

    public PushCallback(Context context) {
        this.context = context;
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

        Message = new String(mqttMessage.getPayload());
        Log.d(Utils.LOG_TAG, "MESSAGE TO DISPLAY " + Message);
        Intent intent = new Intent("publish");
        intent.putExtra("topic",topic);
        intent.putExtra("message",Message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
