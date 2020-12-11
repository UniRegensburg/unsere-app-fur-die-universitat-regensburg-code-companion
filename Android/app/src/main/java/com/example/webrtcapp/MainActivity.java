package com.example.webrtcapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;


import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

import static io.socket.client.Socket.EVENT_CONNECT;
import static io.socket.client.Socket.EVENT_DISCONNECT;
import static org.webrtc.SessionDescription.Type.OFFER;
import static org.webrtc.SessionDescription.Type.ANSWER;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CompleteActivity";

    private Socket socket;
    private boolean isInitiator;

    private TextView state;
    private TextView lastMessage;
    private Button send;
    private Button qr;
    private EditText input;

    private PeerConnection peerConnection;
    private PeerConnectionFactory factory;
    private DataChannel dc;
    private MediaConstraints constraints;
    private DataChannel.Observer dcO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        state = findViewById(R.id.state);
        lastMessage = findViewById(R.id.message);
        send = findViewById(R.id.send);
        input = findViewById(R.id.input);
        qr = findViewById(R.id.qr);

       send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData(input.getText().toString());
            }
        });

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        PeerConnectionFactory.InitializationOptions initializationOptions =  PeerConnectionFactory.InitializationOptions.builder(this)
                .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        factory = PeerConnectionFactory.builder()
                .setOptions(options)
                .createPeerConnectionFactory();
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun2.1.google.com:19302").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("turn:nrg-esport.de:3478").setUsername("codeCompanion").setPassword("codeCompanion2020").createIceServer());
        PeerConnection.RTCConfiguration rtcConfig =   new PeerConnection.RTCConfiguration(iceServers);
        constraints = new MediaConstraints();

        peerConnection = factory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver() {

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                JSONObject message = new JSONObject();

                try {
                    message.put("type", "candidate");
                    message.put("label", iceCandidate.sdpMLineIndex);
                    message.put("id", iceCandidate.sdpMid);
                    message.put("candidate", iceCandidate.sdp);

                    Log.d(TAG, "onIceCandidate: sending candidate " + message);
                    socket.emit("message",message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                super.onDataChannel(dataChannel);
                dc = dataChannel;
                Log.d(TAG, "New Data channel " + dc.label());
                dc.registerObserver(dcO = new DataChannel.Observer() {
                    public void onBufferedAmountChange(long previousAmount) {
                        Log.d(TAG, "Data channel buffered amount changed: " + dc.label() + ": " + dc.state());
                    }
                    @Override
                    public void onStateChange() {
                        Log.d(TAG, "Data channel state changed: " + dc.label() + ": " + dc.state());
                    }
                    @Override
                    public void onMessage(final DataChannel.Buffer buffer) {
                        if (buffer.binary) {
                            Log.d(TAG, "Received binary msg over " + dc);
                            return;
                        }
                        ByteBuffer data = buffer.data;
                        final byte[] bytes = new byte[data.capacity()];
                        data.get(bytes);
                        String strData = new String(bytes);
                        lastMessage.setText(strData);
                        Log.d(TAG, "Got msg: " + strData + " over " + dc);
                    }
                });
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                super.onIceConnectionChange(iceConnectionState);
                Log.d(TAG,peerConnection.connectionState().toString());
                if(iceConnectionState != null){
                    if(iceConnectionState == PeerConnection.IceConnectionState.CONNECTED){
                        state.setText("Connected");
                    }
                }
            }
        });
        start();
    }


    @Override
    protected void onDestroy() {
        if (socket != null) {
            socket.disconnect();
        }
        super.onDestroy();
    }

    private void start() {
        connectToSignallingServer();
    }

    private void connectToSignallingServer() {
        try {
            socket = IO.socket("http://nrg-esport.de:3000/");
            state.setText("Connecting");
            socket.on(EVENT_CONNECT, args -> {
                Log.d(TAG, "connectToSignallingServer: connect");
                socket.emit("create or join", "foo");
            }).on("created", args -> {
                Log.d(TAG, "connectToSignallingServer: created");
                isInitiator = true;
            }).on("full", args -> {
                Log.d(TAG, "connectToSignallingServer: full");
            }).on("join", args -> {
                Log.d(TAG, "connectToSignallingServer: join");
                Log.d(TAG, "connectToSignallingServer: Another peer made a request to join room");
                if(isInitiator){
                    DataChannel.Init dcInit = new DataChannel.Init();
                    dcInit.id = 1;
                    dc = peerConnection.createDataChannel("TestChannel",dcInit);
                    dc.registerObserver(dcO = new DataChannel.Observer() {
                        public void onBufferedAmountChange(long previousAmount) {
                            Log.d(TAG, "Data channel buffered amount changed: " + dc.label() + ": " + dc.state());
                        }
                        @Override
                        public void onStateChange() {
                            Log.d(TAG, "Data channel state changed: " + dc.label() + ": " + dc.state());
                        }
                        @Override
                        public void onMessage(final DataChannel.Buffer buffer) {
                            if (buffer.binary) {
                                Log.d(TAG, "Received binary msg over " + dc);
                                return;
                            }
                            ByteBuffer data = buffer.data;
                            final byte[] bytes = new byte[data.capacity()];
                            data.get(bytes);
                            String strData = new String(bytes);
                            lastMessage.setText(strData);
                            Log.d(TAG, "Got msg: " + strData + " over " + dc);
                        }
                    });
                }
            }).on("joined", args -> {
                Log.d(TAG, "connectToSignallingServer: joined");
            }).on("message", args -> {
                try {
                    Log.d(TAG, "connectToSignallingServer: got a message");
                    Log.d(TAG, args[0].toString());
                    JSONObject message = (JSONObject) args[0];
                    if (message.getString("type").equals("offer")) {
                        peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, message.getString("sdp")));
                        peerConnection.createAnswer(new SimpleSdpObserver() {
                            @Override
                            public void onCreateSuccess(SessionDescription sessionDescription) {
                                peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                                JSONObject message = new JSONObject();
                                try {
                                    message.put("type", "answer");
                                    message.put("sdp", sessionDescription.description);
                                    socket.emit("message", message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, constraints);
                    }
                    if (message.getString("type").equals("answer")) {
                        peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, message.getString("sdp")));
                    }
                    if (message.getString("type").equals("candidate")) {
                        Log.d(TAG, "connectToSignallingServer: receiving candidates");
                        IceCandidate candidate = new IceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
                        peerConnection.addIceCandidate(candidate);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).on(EVENT_DISCONNECT, args -> {
                Log.d(TAG, "connectToSignallingServer: disconnect");
            }).on("ready", args -> {
               Log.d(TAG,"READY");
               if(isInitiator){
                   peerConnection.createOffer(new SimpleSdpObserver() {
                       @Override
                       public void onCreateSuccess(SessionDescription sessionDescription) {
                           Log.d(TAG, "onCreateSuccess");
                           peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                           JSONObject message = new JSONObject();
                           try {
                               message.put("type", "offer");
                               message.put("sdp", sessionDescription.description);
                               socket.emit("message",message);
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }
                   }, constraints);
               }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendData(final String data) {
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        dc.send(new DataChannel.Buffer(buffer, false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("Scan", "Cancelled scan");
            } else {
                Log.d("Scan", "Scanned: " + result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}