package com.alphanodus.kuvi;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by M1032185 on 3/10/2016.
 */
public class MqttService {
   // private static final String BROKER_URL = "tcp://digitalpumpkin.cloudapp.net:1883";
    private static final String BROKER_URL = "tcp://192.168.1.3:1883";

    private static final String CLIENT_ID = "Emergency-Evac";

    //public static final String TOPIC = "FireAlert";
    public static final String TOPIC = "AN/Org/1/DevID/100";

    public static final String SUBSCRIBE_ROUTES = "Emergency/GetRoute";

    private static MqttClient mqttClient;

    private final static String LOG_TAG = "CONNECTION STATUS";

    public final static String PUBLISH_START_LOCATION_TOPIC = "Emergency/UserStartLocation";

    public final static String PUBLISH_USER_NAVIGATION_TOPIC = "Emergency/UserNavigateLocation";

    public static MqttService mqttService = new MqttService();

    private MqttService() {

    }

    public static MqttService getMqttService(String clientId) {
        try {
            mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return mqttService;
    }

    public boolean connect(Context context) {

        Log.d(LOG_TAG, "BROKER URL " + BROKER_URL);
        Log.d(LOG_TAG, "TOPIC " + TOPIC);
        Log.d(LOG_TAG, "CLIENT ID " + CLIENT_ID);

        final MqttConnectOptions options = new MqttConnectOptions();
//        options.setCleanSession(false);

        options.setKeepAliveInterval(90);
        options.setConnectionTimeout(1000);
        mqttClient.setCallback(new PushCallback(context));

        if (!mqttClient.isConnected()) {
            try {
                mqttClient.connect();
                Log.d(LOG_TAG, "CONNECTED TO " + BROKER_URL);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        return mqttClient.isConnected();
    }

    public void subscribe() {
        if (mqttClient.isConnected()) {
            try {
                mqttClient.subscribe(TOPIC);


                Log.d(LOG_TAG, "Subscribed to " + TOPIC);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void subscribeForRoute() {
        if (mqttClient.isConnected()) {
            try {
                mqttClient.subscribe(SUBSCRIBE_ROUTES);
                Log.d(LOG_TAG, "Subscribed to " + SUBSCRIBE_ROUTES);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                Log.d(LOG_TAG, "Disconnected..");

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public static void publishStartLocation(String message) {
        try {
//            mqttClient.publish(PUBLISH_START_LOCATION_TOPIC, new MqttMessage(message.getBytes()));
            mqttClient.publish(PUBLISH_START_LOCATION_TOPIC, message.getBytes(), 2, false);
            Log.d(Utils.LOG_TAG, "USER LOCATION" + message + "Published to " + PUBLISH_START_LOCATION_TOPIC);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void publishNavigation(String message) {
        try {
            mqttClient.publish(PUBLISH_USER_NAVIGATION_TOPIC, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
