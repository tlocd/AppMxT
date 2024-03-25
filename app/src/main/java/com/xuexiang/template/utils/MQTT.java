package com.xuexiang.template.utils;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.xuexiang.template.R;
import com.xuexiang.template.activity.MainActivity;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;

public class MQTT extends Service {

    public static final String TAG = MQTT.class.getSimpleName();

    private MyBinder mBinder = new MyBinder();

    /**
     * 1、BROKER_URL：MQ的URL
     * 2、CLIENT_ID：客户端ID
     * 3、TOPIC：订阅的主题
     * 4、USER_NAME：用户名
     * 5、PASSWORD：密码
     */
    private String BROKER_URL;
    private String CLIENT_ID;
    private String USER_NAME;
    private String PASSWORD;

    //订阅的主题名 --建议用final String的方式定义，谨慎使用通配符
    private static final String TOPIC_TEST1 = "mx/topic";
    private static final String TOPIC_TEST2 = "xxxxx";

    /**
     * mqtt客户端类 mqttClient
     * mqtt连接配置类 options
     */
    private MqttClient mqttClient;
    private MqttConnectOptions options;


    @Override
    public void onCreate() {
        super.onCreate();
        Random rd = new Random(System.currentTimeMillis());//以生成随机数为例

        CLIENT_ID = "ClientId_" + rd.nextInt(); //注意 --需要防止CLIENT_ID重复
        BROKER_URL = "ws://47.236.111.201:8083";
        USER_NAME = "tjj";
        PASSWORD = "tjj200209";
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "mqtt start command");

        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                //设置通知栏大图标
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                //设置服务标题
                .setContentTitle("MQTT服务")
                //设置状态栏小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置服务内容
                .setContentText("服务正在运行")
                //设置通知时间
                .setWhen(System.currentTimeMillis());
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        }

        startForeground(50, notification);
        //注意：mqttclient实现的是阻塞的接口IMqttClient，为了防止ANR，这里必须开启线程/线程池
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //在服务开始时new一个mqttClient实例，客户端ID为clientId，第三个参数说明是持久化客户端，如果是null则是非持久化
                    mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
                    // MQTT的连接设置
                    options = new MqttConnectOptions();
                    // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
                    options.setCleanSession(true);
                    // 设置连接的用户名
                    options.setUserName(USER_NAME);
                    // 设置连接的密码
                    options.setPassword(PASSWORD.toCharArray());
                    // 设置超时时间 单位为秒
                    options.setConnectionTimeout(10);
                    options.setKeepAliveInterval(20);
                    // 设置回调  回调类的说明在后面
                    mqttClient.setCallback((MqttCallback) new PushCallback());
                    //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
                    //options.setWill(topic, "close".getBytes(), 2, true);
                    //mqtt客户端连接服务器
                    mqttClient.connect(options);
                    //mqtt客户端订阅主题
                    //在mqtt中用QoS来标识服务质量
                    //QoS=0时，报文最多发送一次，有可能丢失
                    //QoS=1时，报文至少发送一次，有可能重复
                    //QoS=2时，报文只发送一次，并且确保消息只到达一次。
                    int[] Qos = {2, 2};
                    String[] topicArray = {TOPIC_TEST1, TOPIC_TEST2};
                    mqttClient.subscribe(topicArray, Qos);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "start IBinder");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "start onUnbind");
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        public MQTT getService() {
            return MQTT.this;
        }
    }

    /**
     * 用于MQ重连
     *
     * @throws Exception
     */
    public void reConnect() throws Exception {
        if (null != mqttClient) {
            mqttClient.connect(options);
            Log.e(TAG, "MQ重连");
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "start onDestroy");
        if (mqttClient != null) {
            try {
                mqttClient.disconnect(0);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }



    class PushCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable cause) {
            Log.e(TAG, "MQ连接丢失:" + cause);
            try {
                reConnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }

        @Override
        public void messageArrived(String topic, final MqttMessage message) throws Exception {
            String msg = new String(message.getPayload());
            Log.d(TAG, "消息到达,topic:" + topic + ",msg:" + msg);
        }
    }
}