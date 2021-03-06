package app;

import app.interfaces.WebRTCListener;
import app.services.application.ApplicationService;
import com.intellij.openapi.components.ServiceManager;
import dev.onvoid.webrtc.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.socket.client.Socket.*;

/**
 * Implements all WebRTC functionality
 */
public class WebRTC {

    public Socket socket;
    private PeerConnectionFactory factory;
    private RTCPeerConnection peerConnection;
    private ExecutorService executor;
    private RTCDataChannel dc;
    private String id;
    private boolean isConnecting;
    private WebRTCListener listener;
    private boolean hasSendAnswer;

    private String stunServer = "stun:stun.l.google.com:19302";
    private String turnServer = "turn:nrg-esport.de:3478";
    private String socketUri = "http://62.75.151.5:3000/";

    public void init(String id){
        System.out.println("INIT WEBRTC");
        this.id = id;
        isConnecting = false;
        hasSendAnswer = false;
        executor = Executors.newFixedThreadPool(1);
        RTCIceServer iceServer1 = new RTCIceServer();
        iceServer1.urls.add(stunServer);

        RTCIceServer iceServer2 = new RTCIceServer();
        iceServer2.urls.add(turnServer);
        iceServer2.password = "codeCompanion";
        iceServer2.username = "codeCompanion2020";

        List<RTCIceServer> iceServers = new ArrayList<>();
        iceServers.add(iceServer1);
        iceServers.add(iceServer2);

        RTCConfiguration rtcConfiguration = new RTCConfiguration();
        rtcConfiguration.iceServers = iceServers;

        executeAndWait(new Runnable() {
            @Override
            public void run() {
                factory = new PeerConnectionFactory();
                peerConnection = factory.createPeerConnection(rtcConfiguration, new PeerConnectionObserver() {

                    @Override
                    public void onIceCandidate(RTCIceCandidate iceCandidate) {
                        JSONObject message = new JSONObject();

                        try {
                            message.put("type", "candidate");
                            message.put("label", iceCandidate.sdpMLineIndex);
                            message.put("id", iceCandidate.sdpMid);
                            message.put("candidate", iceCandidate.sdp);

                            System.out.println("onIceCandidate: sending candidate " + message);
                            socket.emit("message", message, id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDataChannel(RTCDataChannel dataChannel) {
                        dc = dataChannel;
                        System.out.println("New Data channel " + dc.getLabel());
                        dc.registerObserver(new RTCDataChannelObserver() {
                            @Override
                            public void onBufferedAmountChange(long previousAmount) {
                                System.out.println("Data channel buffered amount changed: " + dc.getLabel() + ": " + dc.getState());
                            }

                            @Override
                            public void onStateChange() {
                                System.out.println("Data channel state changed: " + dc.getLabel() + ": " + dc.getState());
                                if(dc.getState() == RTCDataChannelState.OPEN){
                                    ServiceManager.getService(ApplicationService.class).getTaskHandler().sendTaskInfo();
                                }
                            }

                            @Override
                            public void onMessage(RTCDataChannelBuffer buffer) {
                                if (buffer.binary) {
                                    System.out.println("Received binary msg over " + dc);
                                    return;
                                }
                                ByteBuffer data = buffer.data;
                                final byte[] bytes = new byte[data.capacity()];
                                data.get(bytes);
                                String strData = new String(bytes);
                                System.out.println("Got msg: " + strData + " over " + dc);
                                listener.onMessageReceived(strData);
                            }
                        });
                    }

                    @Override
                    public void onIceConnectionChange(RTCIceConnectionState iceConnectionState) {
                        System.out.println("Ice Connection State Changed: " + peerConnection.getIceConnectionState().toString());
                        if(peerConnection.getIceConnectionState() == RTCIceConnectionState.DISCONNECTED){
                            System.out.println("STOPPED");

                        }
                    }

                    @Override
                    public void onConnectionChange(RTCPeerConnectionState state) {
                        System.out.println("Connection State Changed: " + peerConnection.getConnectionState().toString());
                        if(listener != null){
                            listener.onConnectionStateChanged(state);
                        }
                    }
                });
            }
        });


    }

    public void startSignaling() {
        System.out.println("STARTING SIGNALING");
        URI uri = URI.create(socketUri);
        socket = IO.socket(uri);
        socket.on(EVENT_CONNECT, args -> {
            System.out.println("Connected to Signalling");
            socket.emit("create or join", id);
            listener.onConnectedToSignaling();
        }).on("created", args -> {
            System.out.println("Room created");
        }).on("full", args -> {
            System.out.println("Room full");
        }).on("join", args -> {
            System.out.println("Join Room");
        }).on("joined", args -> {
            System.out.println("Joined Room");
        }).on("message", args -> {
            try {
                System.out.println("connectToSignallingServer: got a message");
                System.out.println(args[0].toString());
                JSONObject message = (JSONObject) args[0];
                if (message.getString("type").equals("offer")) {
                    peerConnection.setRemoteDescription(new RTCSessionDescription(RTCSdpType.OFFER, message.getString("sdp")),new SimpleSdpObserverSet());
                }
                if (message.getString("type").equals("candidate")) {
                    System.out.println("connectToSignallingServer: receiving candidates");
                    RTCIceCandidate candidate = new RTCIceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
                    peerConnection.addIceCandidate(candidate);
                    if(!hasSendAnswer){
                        hasSendAnswer = true;
                        peerConnection.createAnswer(new RTCAnswerOptions(), new CreateSessionDescriptionObserver(){
                            @Override
                            public void onSuccess(RTCSessionDescription sessionDescription) {
                                peerConnection.setLocalDescription(sessionDescription, new SimpleSdpObserverSet());
                                JSONObject message = new JSONObject();
                                try {
                                    message.put("type", "answer");
                                    message.put("sdp", sessionDescription.sdp);
                                    socket.emit("message", message,id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                System.out.println(error);
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                System.out.println("Crashed");
                e.printStackTrace();
            }
        }).on(EVENT_CONNECT_ERROR, args -> {
            System.out.println(args[0]);
        }).on(EVENT_DISCONNECT, args -> {
            System.out.println("Disconnected");
        }).on("ready", args -> {
            System.out.println("Room ready");
            isConnecting = true;
        });
    }

    public void connect (){
        socket.connect();
    }

    private void executeAndWait(Runnable runnable) {
        try {
            executor.submit(runnable).get();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void sendData(final String data) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
        dc.send(new RTCDataChannelBuffer(buffer, false));
    }


    public RTCPeerConnectionState getConnectionState() {
        if(peerConnection != null){
             return peerConnection.getConnectionState();
        }else{
            return null;
        }
    }

    public RTCDataChannelState getDataChannelState(){
        if(dc.getState() != null){
            return dc.getState();
        }else{
            return null;
        }
    }

    public void closeConnection(){
        if(peerConnection != null){
            socket.disconnect();
            socket.close();
            peerConnection.close();
            peerConnection = null;
        }
    }

    public void safeCloseRoom(){
        if(peerConnection != null){
            if(!isConnecting){
                System.out.println("Safe closing connection");
                socket.disconnect();
                socket.close();
                peerConnection.close();
            }
        }
    }

    public String getId(){
        return id;
    }

    public void setWebRTCListener(WebRTCListener listener) {
        this.listener = listener;
    }

    public interface WebRTCListener{
        void onConnectionStateChanged(RTCPeerConnectionState state);
        void onMessageReceived(String message);
        void onConnectedToSignaling();
    }
}
