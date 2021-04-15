package com.example.codecompanion.util;

import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * ConnectionStateManager which checks the state of the connection
 */
public class ConnectionStateManager {
    // TODO create List of listeners
    private final List<ConnectionStateListener> listeners = new ArrayList<>();
    private static ConnectionStateManager instance;
    private PeerConnection.PeerConnectionState status;
    private String id;

    public interface ConnectionStateListener{
        void onConnect();
        void onDisconnect();
    }
    private ConnectionStateManager() {}

    public static ConnectionStateManager getInstance() {
        if(ConnectionStateManager.instance == null) {
            ConnectionStateManager.instance = new ConnectionStateManager();
        }
        return ConnectionStateManager.instance;
    }

    public void addListener(ConnectionStateManager.ConnectionStateListener listener){
        listeners.add(listener);
    }

    public void removeListener(ConnectionStateManager.ConnectionStateListener listener){
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public PeerConnection.PeerConnectionState getConnectionState() {
        return status;
    }


    public void stateChanged(PeerConnection.PeerConnectionState state) {
        status = state;
        if(state == PeerConnection.PeerConnectionState.CONNECTED ||
        state == PeerConnection.PeerConnectionState.CONNECTING) {
            if(this.listeners != null) {
                for (ConnectionStateListener listener : listeners) {
                    listener.onConnect();
                }

            }
        } else {
            if(this.listeners != null) {
                for (ConnectionStateListener listener : listeners) {
                    listener.onDisconnect();
                }

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
