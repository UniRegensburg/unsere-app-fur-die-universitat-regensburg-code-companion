package app.interfaces;

import dev.onvoid.webrtc.RTCPeerConnectionState;

public interface ApplicationServiceListener{
    void onStarted();
    void onConnectionStateChanged(RTCPeerConnectionState state);
}
