package com.anoram.homecontrol;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements MqttCallback {
    Switch garage_door_switch,garage_light_switch,kitchen_exhaust_switch,hall_light_switch,security_sytem_switch;
    ImageView garage_icon,garage_light_icon,kitchen_mq5_icon,kitchen_exhaust_icon,hall_lamp_icon,hall_mq2_icon,security_system_icon;
    MqttAndroidClient client ;
    TextView temp_label,hum_label;
    MediaPlayer mp;
    TextView SubText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        String clientId = MqttClient.generateClientId();

        client=  new MqttAndroidClient(MainActivity.this, "tcp://139.59.116.150:1883",
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



        //SubText = (TextView)findViewById(R.id.HallMQ2_label);


        garage_door_switch = (Switch) findViewById(R.id.garage_door_switch);
        garage_light_switch=(Switch) findViewById(R.id.garage_light_switch);
        kitchen_exhaust_switch=(Switch) findViewById(R.id.kitchen_exhaust_switch);
        hall_light_switch=(Switch) findViewById(R.id.hall_light_switch);
        security_sytem_switch=(Switch) findViewById(R.id.securitysystem_switch);
        garage_icon = (ImageView) findViewById(R.id.garage_door_icon);
        garage_light_icon=(ImageView) findViewById(R.id.garage_light_icon);
        kitchen_exhaust_icon = (ImageView) findViewById(R.id.kitchen_exhaust_icon);
        kitchen_mq5_icon = (ImageView) findViewById(R.id.kitchen_mq5_icon);
        hall_lamp_icon = (ImageView)  findViewById(R.id.hall_light_icon);
        hall_mq2_icon =(ImageView) findViewById(R.id.hall_mq2_icon);
        security_system_icon = (ImageView) findViewById(R.id.securitysystem_icon);
        temp_label = (TextView) findViewById(R.id.Temperature_value);
        hum_label = (TextView) findViewById(R.id.Humidity_value);

        security_sytem_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    sendmsg("inTopic","8");
                    Log.i("Security System","On");
                    security_system_icon.setImageResource(R.drawable.shieldon);


                }
                else
                {
                    sendmsg("inTopic","9");
                    Log.i("Security System","Off");
                    security_system_icon.setImageResource(R.drawable.shield_off);

                }
            }
        });
        hall_light_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    sendmsg("inTopic","6");
                    Log.i("Hall light","On");
                    hall_lamp_icon.setImageResource(R.drawable.lampon);

                }
                else
                {
                    sendmsg("inTopic","7");
                    Log.i("Hall light","Off");
                    hall_lamp_icon.setImageResource(R.drawable.lampoff);
                }
            }
        });

        kitchen_exhaust_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    sendmsg("inTopic","4");
                    Log.i("Exhaust","On");
                    kitchen_exhaust_icon.setImageResource(R.drawable.exh_on);

                }
                else
                {
                    sendmsg("inTopic","5");
                    Log.i("Exhaust","Off");
                    kitchen_exhaust_icon.setImageResource(R.drawable.exh_off);

                }
            }
        });
        garage_light_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    sendmsg("inTopic","2");
                    Log.i("Garage light","On");
                    garage_light_icon.setImageResource(R.drawable.lampon);

                }
                else
                {
                    sendmsg("inTopic","3");
                    Log.i("Garage light","Off");
                    garage_light_icon.setImageResource(R.drawable.lampoff);
                }
            }
        });
        garage_door_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    sendmsg("inTopic","1");

                    Log.i("Door","Open");
                    garage_icon.setImageResource(R.drawable.garage_door);

                }
                else
                {
                    sendmsg("inTopic","0");

                    Log.i("Door","Close");
                    garage_icon.setImageResource(R.drawable.gargedoor_close);
                }
            }
        });

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

            temp_label.setText(temp);
            hum_label.setText(hum);


        }
        if(from.equalsIgnoreCase("hall"))
        {
            String hls = response.getString("hls");

            String secs= response.getString("secs");
            int mq2s = Integer.valueOf(response.getString("mq2s"));

            if(hls.equalsIgnoreCase("0"))
            {
                hall_light_switch.setChecked(false);
            }
            else
            {
                hall_light_switch.setChecked(true);
            }

            if(secs.equalsIgnoreCase("0"))
            {
                security_sytem_switch.setChecked(false);
            }
            else
            {
                security_sytem_switch.setChecked(true);
            }

            if(mq2s>350)
            {


                hall_mq2_icon.setImageResource(R.drawable.mq2on);
                if(mp.isPlaying())
                {

                }
                else
                {
                    mp.start();
                }

            }
            else
            {
                hall_mq2_icon.setImageResource(R.drawable.mq2);

            }




        }
        if(from.equalsIgnoreCase("kg"))
        {
            String gds = response.getString("gds");
            String gls = response.getString("gls");
            String exk = response.getString("exk");
            int mq5s = Integer.valueOf(response.getString("mq5s"));

            if(gds.equalsIgnoreCase("0"))
            {
                garage_door_switch.setChecked(false);
            }
            else
            {
                garage_door_switch.setChecked(true);
            }

            if(gls.equalsIgnoreCase("0"))
            {
                garage_light_switch.setChecked(false);
            }
            else
            {
                garage_light_switch.setChecked(true);
            }

            if(exk.equalsIgnoreCase("0"))
            {
                kitchen_exhaust_switch.setChecked(false);
            }
            else
            {
                kitchen_exhaust_switch.setChecked(true);
            }

            if(mq5s>450)
            {


               kitchen_mq5_icon.setImageResource(R.drawable.mq5);
                if(mp.isPlaying())
                {

                }
                else
                {
                    mp.start();
                }

            }
            else
            {
                kitchen_mq5_icon.setImageResource(R.drawable.mq5_off);

            }


        }


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
