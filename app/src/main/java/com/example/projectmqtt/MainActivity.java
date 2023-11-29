package com.example.projectmqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    //variables conexion mqtt
    private static String mqttHost="tcp://mqttproject20.cloud.shiftr.io:1883";
    private static String IdUsuario="AppAndroid";
    private static String Topico="Mensaje";
    private static String User="mqttproject20";
    private static String Pass="zU4eWgvUxd9wR2LY";

    //variable para imprimir los datos

    private TextView textView;
    private Button buttonSendMessage;
    private EditText editTextMessage;

    //librerias MQTT
    private MqttClient mqttClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        editTextMessage = findViewById(R.id.ediTextView);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);

        try {
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());

            // conexion al servidor
            mqttClient.connect(options);

            // si se conecta
            Toast.makeText(this, "Aplicación Conectada al Servidor", Toast.LENGTH_SHORT).show();

            // mensaje de entrega de datos y perdida de conexion
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("Mqtt", "conexion Perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    runOnUiThread(() -> textView.setText(payload));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("Mqtt", "Entega completa");

                }
            });

            mqttClient.subscribe(Topico);
            // envio de mensaje al presionar el button

            buttonSendMessage.setOnClickListener(v -> {
                String message = editTextMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    editTextMessage.getText().toString();
                } else {
                    Toast.makeText(this, "Ingrese un mensaje", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

        // metodo enviar mensaaje
        private void sendMessage(String message){
            try{
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttClient.publish(Topico, mqttMessage);
            }catch(MqttException e){
                e.printStackTrace();
            }
        }
        @Override
        protected void onDestroy(){
            super.onDestroy();
            try{
                mqttClient.disconnect();
            }catch (MqttException e){
                e.printStackTrace();
            }
        }

    }
