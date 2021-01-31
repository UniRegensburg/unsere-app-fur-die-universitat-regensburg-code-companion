package com.example.codecompanion.util;

import org.webrtc.PeerConnection;

public class ConnectionStateManager {
    private ConnectionStateListener listener;
    private static ConnectionStateManager instance;
    private PeerConnection.PeerConnectionState status;
    private String id;

    public interface ConnectionStateListener{
        public void onConnect();
        public void onDisconnect();
    }
    private ConnectionStateManager() {}

    public static ConnectionStateManager getInstance() {
        if(ConnectionStateManager.instance == null) {
            ConnectionStateManager.instance = new ConnectionStateManager();
        }
        return ConnectionStateManager.instance;
    }

    public void setListener(ConnectionStateManager.ConnectionStateListener listener){
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
