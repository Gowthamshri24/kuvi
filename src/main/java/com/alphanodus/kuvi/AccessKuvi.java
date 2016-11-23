package com.alphanodus.kuvi;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class AccessKuvi{

    private Context kuviContext;
    private static MqttClient mqttClient;
    private static IMqttToken token;
    private static final String clientId = "demo_client";
    private static final String BROKER_URL = "tcp://35.163.147.24:1883";
    private final MqttConnectOptions options = new MqttConnectOptions();
    private static final String username = "demo";
    private static final String password = "emqtt";

    public AccessKuvi(Context context){
        kuviContext = context;
        try {
            mqttClient = new MqttClient(BROKER_URL,clientId,new MemoryPersistence());
            mqttClient.setCallback(new PushCallback(kuviContext));
            options.setCleanSession(true);
            options.setKeepAliveInterval(120);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public IMqttToken connect(String UID,String KEY){

        options.setCleanSession(true);
        options.setKeepAliveInterval(120);
        options.setUserName(UID);
        options.setPassword(KEY.toCharArray());
        mqttClient.setCallback(new PushCallback(kuviContext));

        if (!mqttClient.isConnected()) {
            try {
                token = mqttClient.connectWithResult(options);
                Log.d("connected","connected to" + BROKER_URL);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        return token;
    }

    public void subscribe(String topic){

        try {
            if (mqttClient.connectWithResult(options).isComplete()) {
                mqttClient.subscribe(topic);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public static void publish(String topic, String msg) {
        try {

            mqttClient.publish(topic, msg.getBytes(), 2, false);
            Log.d(Utils.LOG_TAG, "USER LOCATION" + msg + "Published to " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
