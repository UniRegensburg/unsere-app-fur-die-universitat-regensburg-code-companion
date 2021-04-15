package com.example.codecompanion.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.codecompanion.interfaces.CustomPeerConnectionObserver;
import com.example.codecompanion.interfaces.*;

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
import static org.webrtc.SessionDescription.Type.ANSWER;

/**
 * Complete WebRTC class which handles all WebRTC logic
 */
public class WebRTC extends Service {

    private static final String TAG = "WebRTC";
    private final IBinder binder = new WebRTCServiceBinder();

    private Socket socket;

    private PeerConnection peerConnection;
    private PeerConnectionFactory factory;
    private static DataChannel dc1;
    private MediaConstraints constraints;
    private String id;
    private WebRTCListener listener;

    public WebRTC() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void init(Context context, String id){
        this.id = id;
        PeerConnectionFactory.InitializationOptions initializationOptions =  PeerConnectionFactory.InitializationOptions.builder(context)
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
                    socket.emit("message",message,id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                super.onIceConnectionChange(iceConnectionState);
                Log.d(TAG,"Ice Connection State Changed: " + peerConnection.iceConnectionState().toString());
            }

            @Override
            public void onConnectionChange(PeerConnection.PeerConnectionState state) {
                System.out.println("Connection State Changed: " + peerConnection.connectionState().toString());
                listener.onConnectionStateChanged(peerConnection.connectionState());
            }
        });

        start();
    }

    private void start(){
        try {
            socket = IO.socket("http://nrg-esport.de:3000/");
            socket.on(EVENT_CONNECT, args -> {
                Log.d(TAG, "connectToSignallingServer: connect");
                socket.emit("create or join", id);
            }).on("created", args -> {
                Log.d(TAG, "connectToSignallingServer: created");
            }).on("full", args -> {
                Log.d(TAG, "connectToSignallingServer: full");
            }).on("join", args -> {
                Log.d(TAG, "connectToSignallingServer: join");
                Log.d(TAG, "connectToSignallingServer: Another peer made a request to join room");
            }).on("joined", args -> {
                Log.d(TAG, "connectToSignallingServer: joined");
            }).on("message", args -> {
                try {
                    Log.d(TAG, "connectToSignallingServer: got a message");
                    Log.d(TAG, args[0].toString());
                    JSONObject message = (JSONObject) args[0];
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

                DataChannel.Init dcInit = new DataChannel.Init();
                dc1 = peerConnection.createDataChannel(id,dcInit);
                dc1.registerObserver(new DataChannel.Observer() {
                    public void onBufferedAmountChange(long previousAmount) {
                        Log.d(TAG, "Data channel buffered amount changed: " + dc1.label() + ": " + dc1.state());
                    }
                    @Override
                    public void onStateChange() {
                        Log.d(TAG, "Data channel state changed: " + dc1.label() + ": " + dc1.state());
                    }
                    @Override
                    public void onMessage(final DataChannel.Buffer buffer) {
                        if (buffer.binary) {
                            Log.d(TAG, "Received binary msg over " + dc1);
                            return;
                        }
                        ByteBuffer data = buffer.data;
                        final byte[] bytes = new byte[data.capacity()];
                        data.get(bytes);
                        String strData = new String(bytes);
                        listener.onMessageReceived(strData);
                        Log.d(TAG, "Got msg: " + strData + " over " + dc1);
                    }
                });

                peerConnection.createOffer(new SimpleSdpObserver() {
                        @Override
                        public void onCreateSuccess(SessionDescription sessionDescription) {
                            Log.d(TAG, "onCreateSuccess");
                            peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                            JSONObject message = new JSONObject();
                            try {
                                message.put("type", "offer");
                                message.put("sdp", sessionDescription.description);
                                socket.emit("message",message,id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        }, constraints);
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setWebRTCListener(WebRTCListener listener) {
        this.listener = listener;
    }

    public static void sendData(final String data) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        dc1.send(new DataChannel.Buffer(buffer, false));
    }

    public interface WebRTCListener{
        void onConnectionStateChanged(PeerConnection.PeerConnectionState state);
        void onMessageReceived(String message);
    }

    public class WebRTCServiceBinder extends Binder {
        public WebRTC getService() {
            return WebRTC.this;
        }
    }
}
