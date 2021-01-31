package com.example.codecompanion.util;

import org.webrtc.PeerConnection;

public class ConnectionState {
    private ConnectionStateListener listener;
    private static ConnectionState instance;
    private PeerConnection.PeerConnectionState status;
    private String id;

    public interface ConnectionStateListener{
        public void onConnect();
        public void onDisconnect();
    }
    private ConnectionState() {}

    public static ConnectionState getInstance() {
        if(ConnectionState.instance == null) {
            ConnectionState.instance = new ConnectionState();
        }
        return ConnectionState.instance;
    }

    public void setListener(ConnectionState.ConnectionStateListener listener){
        this.listener = listener;
    }

    public void removeListener(){
        this.listener = null;
    }

    public PeerConnection.PeerConnectionState getConnectionState() {
        return status;
    }


    public void stateChanged(PeerConnection.PeerConnectionState state) {
        status = state;
        if(state == PeerConnection.PeerConnectionState.CONNECTED ||
        state == PeerConnection.PeerConnectionState.CONNECTING) {
            if(this.listener != null) {
                listener.onConnect();
            }
        } else {
            if(this.listener != null) {
                listener.onDisconnect();
            }
        }

    }

    public String getConnectedToId() {
        return this.id;
    }


    public void setConnectedToId(String id) {
        this.id = id;
    }
}
