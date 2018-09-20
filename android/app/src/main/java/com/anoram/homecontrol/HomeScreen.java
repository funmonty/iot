package com.anoram.homecontrol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class HomeScreen extends AppCompatActivity implements MqttCallback {
    ImageView bulb_image,lock_image,sec_image,garage_image,pet_image,socket_image,car1_image,car2_image;
    CardView bulb_card,lock_card,sec_card,garage_card,pet_card,socket_card,car1,car2;

    MqttAndroidClient client ;
    MediaPlayer mp;
    int counter =0;

    TextView temp_label,hum_label,gas_label,smoke_label,gas_status,smoke_status,car1_text,car2_text,slots;

    private Context mContext;
    private Activity mActivity;

    private RelativeLayout mRelativeLayout;
    private Button mButton;

    private PopupWindow mPopupWindow;
    private Thread thread = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        getSupportActionBar().hide();


        String clientId = MqttClient.generateClientId();

        client=  new MqttAndroidClient(HomeScreen.this, "tcp://139.59.116.150:1883",
                clientId);
        mp = MediaPlayer.create(this, R.raw.alarm);

        try {
            IMqttToken token = client.connect();
            client.setCallback(this);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    String topic = "outTopic";
                    int qos = 1;
                    try {
                        client.subscribe(topic, qos);
                        sendmsg("inTopic","z");

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("log", String.valueOf(exception));

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        bulb_card = (CardView) findViewById(R.id.bulb_card);
        lock_card = (CardView) findViewById(R.id.lock_card);
        sec_card  = (CardView) findViewById(R.id.sec_card);
        garage_card = (CardView) findViewById(R.id.garage_card);
        socket_card = (CardView) findViewById(R.id.socket_card);
        pet_card = (CardView) findViewById(R.id.pet_card);
        car1 = (CardView) findViewById(R.id.park_1);
        car2 = (CardView) findViewById(R.id.park_2);

        bulb_image = (ImageView) findViewById(R.id.bulb_image);
        lock_image = (ImageView) findViewById(R.id.lock_image);
        sec_image  = (ImageView) findViewById(R.id.sec_image);
        garage_image = (ImageView) findViewById(R.id.garage_image);
        pet_image = (ImageView) findViewById(R.id.pet_image);
        socket_image = (ImageView) findViewById(R.id.socket_image);
        car1_image = (ImageView) findViewById(R.id.park1_image);
        car2_image = (ImageView) findViewById(R.id.park2_image);
        slots = (TextView) findViewById(R.id.slots);

        bulb_image.setTag("on");
        lock_image.setTag("locked");
        sec_image.setTag("armed");
        garage_image.setTag("open");
        pet_image.setTag("on");
        socket_image.setTag("on");

        temp_label = (TextView) findViewById(R.id.Temperature_value);
        hum_label = (TextView) findViewById(R.id.Humidity_value);
        gas_label = (TextView) findViewById(R.id.gas_value);
        smoke_label = (TextView) findViewById(R.id.smoke_value);
        smoke_status = (TextView) findViewById(R.id.smoke_status);
        gas_status = (TextView) findViewById(R.id.gas_status);
        car1_text = (TextView) findViewById(R.id.park1_text);
        car2_text = (TextView) findViewById(R.id.park2_text);


//        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotate.setRepeatCount(Animation.INFINITE);
//
//        rotate.setDuration(500);
//        rotate.setInterpolator(new LinearInterpolator());



//        exhaust_image.startAnimation(rotate);



        thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        counter++;
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("debug ", String.valueOf(counter));
                                if(counter%2 == 0){
                                    car2_image.setImageDrawable(getDrawable(R.drawable.car2));
                                    car2_text.setText("Occupied");
                                    slots.setText("Parking Slots : 1");
                                }
                                else{
                                    car2_image.setImageDrawable(getDrawable(R.drawable.car4));
                                    car2_text.setText("Free to park");
                                    slots.setText("Parking Slots : 2");
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();



        bulb_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bulb_image.getTag().toString().equals("on")) {
//                    bulb_image.setImageResource(R.drawable.light_bulb_dim);
                    bulb_image.setTag("off");
                    sendmsg("inTopic","7");
                }
                else
                {
//                    bulb_image.setImageResource(R.drawable.light_bulb);
                    bulb_image.setTag("on");
                    sendmsg("inTopic","6");
                }
            }
        });

        lock_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lock_image.getTag().toString().equals("locked")) {
//                    lock_image.setImageResource(R.drawable.unlocked);
                    lock_image.setTag("unlocked");
                    sendmsg("inTopic","5");

                }
                else
                {
//                    lock_image.setImageResource(R.drawable.locked);
                    lock_image.setTag("locked");
                    sendmsg("inTopic","4");
                }
            }
        });
        sec_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sec_image.getTag().toString().equals("armed")) {
//                    sec_image.setImageResource(R.drawable.sec_off);
                    sec_image.setTag("disarmed");
                    sendmsg("inTopic","9");
                }
                else
                {
//                    sec_image.setImageResource(R.drawable.sec_on);
                    sec_image.setTag("armed");
                    sendmsg("inTopic","8");
                }
            }
        });
        garage_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(garage_image.getTag().toString().equals("open")) {
//                    garage_image.setImageResource(R.drawable.garage_close);
                    garage_image.setTag("close");
                    sendmsg("inTopic","b");
                }
                else
                {
//                    garage_image.setImageResource(R.drawable.garage);
                    garage_image.setTag("open");
                    sendmsg("inTopic","a");
                }
            }
        });

        socket_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(socket_image.getTag().toString().equals("on")) {
//                    socket_image.setImageResource(R.drawable.plugoff);
                    socket_image.setTag("off");
                    sendmsg("inTopic","3");

                }
                else
                {
//                    socket_image.setImageResource(R.drawable.plugon);
                    socket_image.setTag("on");
                    sendmsg("inTopic","2");

                }
            }
        });

        pet_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             pet_image.setImageResource(R.drawable.bowl_full);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        pet_image.setImageResource(R.drawable.bowl_empty);
                        sendmsg("inTopic","1");
                    }
                }, 5000);
            }
        });



    }
    Runnable updater;
    void updateTime() throws InterruptedException {
        counter++;

        final Handler timerHandler = new Handler();

        if(counter % 2 == 0) {

            updater = new Runnable() {
                @Override
                public void run() {
                    car2_image.setImageDrawable(getDrawable(R.drawable.car2));
                    Toast.makeText(getApplicationContext(),counter,Toast.LENGTH_SHORT).show();
                    car2_text.setText("Occupied");
                    slots.setText("Parking Slots : 1");
                    timerHandler.postDelayed(updater, 5000);

                }
            };
            timerHandler.post(updater);

        }

    }
    private void updateDisplay() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                 car2_image.setImageDrawable(getDrawable(R.drawable.car2));
                 car2_text.setText("Occupied");
                 slots.setText("Parking Slots : 1");
            }

        },0,5000);//Update text every second
    }
    public void sendmsg(final String topic, final String payload){
        Log.d(topic, payload);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "onSuccess");
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("mqtt", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        Log.d(topic,message.toString());
        JSONObject response = new JSONObject(message.toString());
        String from = response.getString("from");

        if(from.equalsIgnoreCase("dh11"))
        {
            String temp = response.getString("temp");
            String hum = response.getString("hum");

            temp_label.setText(temp+" °C");
            hum_label.setText(hum+" %");



        }

        if(from.equalsIgnoreCase("pet"))
        {
            int mq5s = Integer.valueOf(response.getString("mq5s"));
            gas_label.setText(response.getString("mq5s"));

            if(mq5s> 450)
            {
                gas_status.setText("Unsafe");
                gas_status.setTextColor(Color.parseColor("#ed143d"));
//                if(mp.isPlaying())
//                {
//
//                }
//                else
//                {
//                    mp.start();
//                }
            }
            else
            {
                gas_status.setText("Safe");
                gas_status.setTextColor(Color.parseColor("#8bc34a"));
            }


        }

        if(from.equalsIgnoreCase("garage"))
        {
            int garagestatus = Integer.valueOf(response.getString("gds"));
            if(garagestatus == 1)
            {
                garage_image.setImageResource(R.drawable.garage_open);
                garage_image.setTag("open");
            }
            else
            {
                garage_image.setImageResource(R.drawable.garage_close);
                garage_image.setTag("close");
            }


        }

        if(from.equalsIgnoreCase("lock"))
        {
            int lockstatus = Integer.valueOf(response.getString("lks"));
            if(lockstatus == 1)
            {
                lock_image.setImageResource(R.drawable.locked);
                lock_image.setTag("locked");
            }
            else
            {
                lock_image.setImageResource(R.drawable.unlocked);
                lock_image.setTag("unlocked");
            }


        }

        if(from.equalsIgnoreCase("plug"))
        {
            int plugstatus = Integer.valueOf(response.getString("pls"));
            if(plugstatus == 1)
            {
                socket_image.setImageResource(R.drawable.plugon);
                socket_image.setTag("on");
            }
            else
            {
                socket_image.setImageResource(R.drawable.plugoff);
                socket_image.setTag("off");
            }



        }

        if(from.equalsIgnoreCase("hall"))
        {
            int mq2s = Integer.valueOf(response.getString("mq2s"));
            int lightstatus = Integer.valueOf(response.getString("hls"));
            int secstatus = Integer.valueOf(response.getString("secs"));

            smoke_label.setText(response.getString("mq2s"));

            if(mq2s> 450)
            {
                smoke_status.setText("Unsafe");
                smoke_status.setTextColor(Color.parseColor("#ed143d"));
//                if(mp.isPlaying())
//                {
//
//                }
//                else
//                {
//                    mp.start();
//                }
            }
            else
            {
                smoke_status.setText("Safe");
                smoke_status.setTextColor(Color.parseColor("#8bc34a"));
            }

            if(secstatus == 1)
            {
                sec_image.setImageResource(R.drawable.sec_on);
                sec_image.setTag("armed");
            }
            else
            {
                sec_image.setImageResource(R.drawable.sec_off);
                sec_image.setTag("disarmed");
            }

            if(lightstatus == 1)
            {
                bulb_image.setImageResource(R.drawable.light_bulb);
                bulb_image.setTag("on");
            }
            else
            {
                bulb_image.setImageResource(R.drawable.light_bulb_dim);
                bulb_image.setTag("off");
            }


        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
    private class MyAsyncTask extends AsyncTask<String, Void, String> {



        public MyAsyncTask(){

        }

        @Override
        protected String doInBackground(String... params) {
            //Background operation in a separate thread
            //Write here your code to run in the background thread

            //calculate here whatever you like

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //Called on Main UI Thread. Executed after the Background operation, allows you to have access to the UI
            car2_image.setImageDrawable(getDrawable(R.drawable.car2));
            car2_text.setText("Occupied");
            slots.setText("Parking Slots : 1");


        }

        @Override
        protected void onPreExecute() {
            //Called on Main UI Thread. Executed before the Background operation, allows you to have access to the UI
        }
    }


}

