
import dev.onvoid.webrtc.*;
import dev.onvoid.webrtc.PeerConnectionFactory;
import dev.onvoid.webrtc.RTCDataChannel;
import dev.onvoid.webrtc.RTCPeerConnection;
import dev.onvoid.webrtc.CreateSessionDescriptionObserver;


import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import static io.socket.client.Socket.EVENT_CONNECT;
import static io.socket.client.Socket.EVENT_DISCONNECT;


import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MainActivity {


    private Socket socket;
    private PeerConnectionFactory factory;
    private RTCPeerConnection peerConnection;
    private ExecutorService executor;
    private RTCDataChannel dc;
    private RTCDataChannelObserver dcO;
    private boolean isInitiator;

    public void init(){
        executor = Executors.newFixedThreadPool(1);

        RTCIceServer iceServer1 = new RTCIceServer();
        iceServer1.urls.add("stun:stun.l.google.com:19302");

        RTCIceServer iceServer2 = new RTCIceServer();
        iceServer2.urls.add("turn:nrg-esport.de:3478");
        iceServer2.password = "codeCompanion";
        iceServer2.username = "codeCompanion2020";

        List<RTCIceServer> iceServers = new ArrayList<>();
        iceServers.add(iceServer1);
        iceServers.add(iceServer2);

        RTCConfiguration rtcConfiguration = new RTCConfiguration();
        rtcConfiguration.iceServers = iceServers;

        executeAndWait(() ->{
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
                        socket.emit("message",message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDataChannel(RTCDataChannel dataChannel) {
                    dc = dataChannel;
                    System.out.println("New Data channel " + dc.getLabel());
                    dc.registerObserver(dcO = new RTCDataChannelObserver() {
                        @Override
                        public void onBufferedAmountChange(long previousAmount) {
                            System.out.println("Data channel buffered amount changed: " + dc.getLabel() + ": " + dc.getState());
                        }
                        @Override
                        public void onStateChange() {
                            System.out.println("Data channel state changed: " + dc.getLabel() + ": " + dc.getState());
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
                        }
                    });
                }

                @Override
                public void onIceConnectionChange(RTCIceConnectionState iceConnectionState) {
                    System.out.println(RTCPeerConnectionState.values());
                }
            });
        });
        connectSignaling();
    }

    public void connectSignaling(){
        try {
            socket = IO.socket("http://nrg-esport.de:3000/");
            socket.on(EVENT_CONNECT, args -> {
                System.out.println("Connected to Signalling");
                socket.emit("create or join", "foo");
            }).on("created", args -> {
                System.out.println("Room created");
                isInitiator = true;
            }).on("full", args -> {
                System.out.println("Room full");
            }).on("join", args -> {
                System.out.println("Join Room");
                if (isInitiator) {
                    RTCDataChannelInit dcInit = new RTCDataChannelInit();
                    dcInit.id = 1;
                    dc = peerConnection.createDataChannel("TestChannel", dcInit);
                    dc.registerObserver(dcO = new RTCDataChannelObserver() {
                        @Override
                        public void onBufferedAmountChange(long previousAmount) {
                            System.out.println("Data channel buffered amount changed: " + dc.getLabel() + ": " + dc.getState());
                        }

                        @Override
                        public void onStateChange() {
                            System.out.println("Data channel state changed: " + dc.getLabel() + ": " + dc.getState());
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
                        }
                    });
                }
            }).on("joined", args -> {
                System.out.println("Joined Room");
            }).on("message", args -> {
                try {
                    System.out.println("connectToSignallingServer: got a message");
                    System.out.println(args[0].toString());
                    JSONObject message = (JSONObject) args[0];
                    if (message.getString("type").equals("offer")) {
                        peerConnection.setRemoteDescription(new RTCSessionDescription(RTCSdpType.OFFER, message.getString("sdp")),new SimpleSdpObserverSet());
                        peerConnection.createAnswer(new RTCAnswerOptions(), new SimpleSdpObserverCreate() {
                            @Override
                            public void onSuccess(RTCSessionDescription sessionDescription) {
                                peerConnection.setLocalDescription(sessionDescription, new SimpleSdpObserverSet());
                                JSONObject message = new JSONObject();
                                try {
                                    message.put("type", "answer");
                                    message.put("sdp", sessionDescription.sdp);
                                    socket.emit("message", message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (message.getString("type").equals("answer")) {
                        peerConnection.setRemoteDescription(new RTCSessionDescription(RTCSdpType.ANSWER, message.getString("sdp")),new SimpleSdpObserverSet());
                    }
                    if (message.getString("type").equals("candidate")) {
                        System.out.println("connectToSignallingServer: receiving candidates");
                        RTCIceCandidate candidate = new RTCIceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
                        peerConnection.addIceCandidate(candidate);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).on(EVENT_DISCONNECT, args -> {
            }).on("ready", args -> {
                System.out.println("Room ready");
                if(isInitiator){
                    peerConnection.createOffer(new RTCOfferOptions(), new SimpleSdpObserverCreate() {
                        @Override
                        public void onSuccess(RTCSessionDescription sessionDescription) {
                            System.out.println("onSuccess");
                            peerConnection.setLocalDescription(sessionDescription,new SimpleSdpObserverSet());
                            JSONObject message = new JSONObject();
                            try {
                                message.put("type", "offer");
                                message.put("sdp", sessionDescription.sdp);
                                socket.emit("message",message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
        RTCDataChannelBuffer rtcDataChannelBuffer = new RTCDataChannelBuffer(buffer,false);
        dc.send(new RTCDataChannelBuffer(buffer, false));
    }


}