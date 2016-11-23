package com.alphanodus.kuvi;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class AccessKuvi {

    private Context kuviContext;
    private static MqttClient mqttClient;
    private static IMqttToken token;
    private static final String clientId = "embedded";
    private static final String BROKER_URL = "tcp://35.164.64.228:1883";
    private final MqttConnectOptions options = new MqttConnectOptions();
    public static final String DEVICE_LIST = "AN/Org/1/DevID/deviceList";
    private static final String username = "embedded";
    private static final String password = "password";
    public final static String URL = "AN/org/1/dev/";


    public AccessKuvi(Context context) {
        kuviContext = context;
        try {
            mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());
            mqttClient.setCallback(new PushCallback(kuviContext));
            options.setCleanSession(true);
            options.setKeepAliveInterval(120);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public IMqttToken connect(String UID, String KEY) {

        options.setCleanSession(true);
        options.setKeepAliveInterval(120);
        options.setUserName(UID);
        options.setPassword(KEY.toCharArray());
        mqttClient.setCallback(new PushCallback(kuviContext));

        if (!mqttClient.isConnected()) {
            try {
                token = mqttClient.connectWithResult(options);
                Log.d("connected", "connected to" + BROKER_URL);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        return token;
    }



    public void changeDeviceValue(String deviceType, int deviceId, String valueType, String msg) {
        try {
            if (mqttClient.isConnected()) {
                String id = URL+deviceType+"/"+valueType;
                mqttClient.publish(URL+deviceType+"/"+deviceId+"/"+valueType,msg.getBytes(), 2, false);
                Log.d(Utils.LOG_TAG, "CHANGE DEVICE VALUE" + msg + "Published");
            } else {
                if (mqttClient.connectWithResult(options).isComplete()) {
                    String id = URL+deviceType+"/"+valueType;
                    mqttClient.publish(URL+deviceType+"/"+deviceId+"/"+valueType, msg.getBytes(), 2, false);

                }
            }

        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {

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
            Log.d(Utils.LOG_TAG, "PUBLISHING STATUS" + msg + "Published to " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void modifySetTemperature(String device, JSONObject val){
        try {
            if (mqttClient.isConnected()) {
                String msg= String.valueOf(val);
                mqttClient.publish(URL + device + "/" + "sptemp", msg.getBytes(), 2, false);
                String url = URL+device+"/"+"sptemp";
                int i =1;
                Log.d(Utils.LOG_TAG, "CHANGE DEVICE VALUE" + val + "Published");
            } else {
                if (mqttClient.connectWithResult(options).isComplete()) {
                    String msg= String.valueOf(val);
                    mqttClient.publish(URL + device + "/" + "sptemp" , msg.getBytes(), 2, false);
                }
            }

        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }


    public void subscribeToDevice(String deviceId) {
        try {
            if(deviceId.charAt(0)=='T') {
                if (mqttClient.isConnected()) {

                        mqttClient.subscribe(URL + deviceId + "/" + "ambtemp");
                    mqttClient.subscribe(URL + deviceId + "/" + "sptemp");
                      mqttClient.subscribe(URL + deviceId + "/" + "spmode");
                     mqttClient.subscribe(URL + deviceId + "/" + "hvacmode");

                } else {
                    if (mqttClient.connectWithResult(options).isComplete()) {
                        //mqttClient.subscribe(URL + deviceId + "/" + "ambtemp");
                        String temp = URL+deviceId+"/"+"sptemp";
                        mqttClient.subscribe(URL + deviceId + "/" + "sptemp");
                         mqttClient.subscribe(URL + deviceId + "/" + "spmode");
                         mqttClient.subscribe(URL + deviceId + "/" + "hvacmode");
                    }
                }
            }
            else if(deviceId.charAt(0)=='A') {
                if (mqttClient.isConnected()) {
                    mqttClient.subscribe(URL + deviceId + "/" + "ambtemp");
                     mqttClient.subscribe(URL + deviceId + "/" + "ambhum");
                    mqttClient.subscribe(URL + deviceId + "/" + "motion");
                } else {
                    if (mqttClient.connectWithResult(options).isComplete()) {
                        mqttClient.subscribe(URL + deviceId + "/" + "ambtemp");
                          mqttClient.subscribe(URL + deviceId + "/" + "ambhum");
                         mqttClient.subscribe(URL + deviceId + "/" + "motion");
                    }

                }
            }
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();

        }
    }
}
