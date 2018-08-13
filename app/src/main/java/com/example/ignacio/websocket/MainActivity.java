/*
NO MUESTRA MENSAJE AL DESCONECTARSE EL SOCKET!!! - CORREGIR -
LUEGO LAS VUELTAS....
_hacer funcionar el progressbar
Resolviendo mejor la variable posicion que manda el esp y se recibe mediante output... por eso no avanza y no puede cerrar
la cortina, porque siempre esta en 0
_chequear las distintas lecturas del outputTEXT
_testear que recibe el ESP mediante Serial.print en arduino
_pedir posicion y V_ por serial desde arduino

LUEGO QUE LA BARRA(SEEKBAR) MUEVA HASTA DETERMINADA POSICION Y QUE NO SEA PARA EL TERCER LED

LUEGO EL TIEMPO
*/

package com.example.ignacio.websocket;

import android.content.Context;
import android.content.ReceiverCallNotAllowedException;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button start;
    private Button abrir;
    private Button cerrar;
    int led1State = 0, state1;
    String LED1, estado1;
    String LED2, estado2;
    int led2State = 0, state2;
    private TextView output;
    private OkHttpClient client;
    private ProgressBar progressBar;
    int V_ = 05, T_, posicion = 1;
    String vueltas, vuel;
    private SeekBar seekBar1;
    int r = 0;
    String rgb;
    String l;
    private EchoWebSocketListener MyEchoWebSocketListener = new EchoWebSocketListener();
    private WebSocket webS;

    @Override
    public boolean moveDatabaseFrom(Context sourceContext, String name) {
        return super.moveDatabaseFrom(sourceContext, name);
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;


        @Override// DESDE ACA LA MANDO AL ESP
        public void onOpen(WebSocket conection, Response response) {
            conection.send("Connect " + new Date());
            conection.send("aa ce perro..?");

            //conection.send(rgb);
            // webSocket.send(ByteString.decodeHex(rgb));
            //  webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye");
        }

        @Override// DESDE ACA RECIBO DESDE EL ESP
        public void onMessage(WebSocket conection, String text) {
            //sendRGB(r,g,b);
        if (text.equals("1.1")){

            output("estado: CERRANDO" );
            }else if(text.equals("0.1")){

            output("estado: ABRIENDO" );
        }else if(text.equals("0.0")){

            output("estado: DETENIDO" );
            //led1State = 1;
           // led2State = 1;
        }else if(text.equals("CORTINA ABIERTA") || text.equals("CORTINA CERRADA") || text.equals("LA CORTINA SE ESTA MOVIENDO SOLA")){
            output("estado: " + text );
        }else if(text.startsWith("p")){
            posicion = Integer.parseInt(text.substring(2,3));

        }
           // output("estado: " + text );
        }

        @Override // DESDE ACA LE MANDO BYTES AL ESP
        public void onMessage(WebSocket conection, ByteString bytes) {
            //    conection.send(rgb);

           // output("Receiving bytes: " + bytes);
        }

        /* @Override // DESDE ACA LE MANDO BYTES AL ESP
         public void onMessage(WebSocket conection, String payload) {
             conection.send(rgb);
             output("Receiving bytes: " + bytes.hex());
         }*/
        @Override// CIERRO EL VINCULO
        public void onClosing(WebSocket conection, int code, String reason) {
            conection.close(NORMAL_CLOSURE_STATUS, null);
            output("closing: " + code + "/" + reason);
        }

        @Override// SACO MENSAJE DE ERROR
        public void onFailure(WebSocket conection, Throwable t, Response response) {
            output("Error: " + t.getMessage());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar1.setMax(99);
        seekBar1.setProgress(V_);
        seekBar1.setKeyProgressIncrement (1);

        progressBar =(ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(V_);
        progressBar.setProgress(posicion);


        start = (Button) findViewById(R.id.start);
        abrir = (Button) findViewById(R.id.abrir);
        cerrar = (Button) findViewById(R.id.cerrar);
        output = (TextView) findViewById(R.id.output);
        client = new OkHttpClient();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
                vuel = Integer.toString(V_);
                vueltas = ("V" + vuel);
                try {
                    sendMessage(vueltas);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        abrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (led1State == 0){
                    abrir.setText("PARAR...");
                    if (led2State == 1){
                        cerrar.setText("CERRAR");
                        led2State =0;
                    }
                }else {abrir.setText("ABRIR");}
                try {
                    Send_State_led1();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (led2State == 0){
                    cerrar.setText("PARAR...");
                    if (led1State == 1){
                        abrir.setText("ABRIR");
                        led1State =0;
                    }

                }else {cerrar.setText("CERRAR");}
                try {
                    Send_State_led2();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                @Override
                                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                    V_ = progress;

                                                    try {
                                                        sendMessage(vueltas);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onStartTrackingTouch(SeekBar seekBar) {
                                                }
                                                @Override
                                                public void onStopTrackingTouch(SeekBar seekBar) {
                                                }
                                            }
        );


    }


    public void  start() {

        Request request = new Request.Builder().url("ws://192.168.8.107:81").build();

        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws= client.newWebSocket(request, listener);
       webS = ws;
        client.dispatcher().executorService().shutdown();
    }

    //FALTA AGREGAR EL @ Y EL & a la variable a mandar!!!
    //FALTA AGREGAR EL @ Y EL & a la variable a mandar!!!
    //FALTA AGREGAR EL @ Y EL & a la variable a mandar!!!
    public void Send_State_led1() throws IOException {
        if (led1State == 0 ){
            led1State = 1;
        }else led1State = 0;
        estado1 = Integer.toString(led1State);
        LED1 = ("@" + estado1);
        sendMessage(LED1);
    }
    //FALTA AGREGAR EL @ Y EL & a la variable a mandar!!!
    //FALTA AGREGAR EL @ Y EL & a la variable a mandar!!!
    //FALTA AGREGAR EL @ Y EL & a la variable a mandar!!!
    public void Send_State_led2() throws IOException {
        if (led2State == 0 ){
            led2State = 1;
        }else led2State = 0;
        estado2 = Integer.toString(led2State);
        LED2 = ("&" + estado2);
        sendMessage(LED2);
    }

    public synchronized void sendMessage(String message) throws IOException {
        if (webS != null) {
            webS.send(message);
        } else {
            throw new ClosedChannelException();
        }
    }
    private void output(final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(null);
                output.setText(output.getText().toString()+"\n" + txt);

            }
        });

    }
}
