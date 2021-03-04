package app.interfaces;

import dev.onvoid.webrtc.RTCPeerConnectionState;

public interface WebRTCListener {
    public void onConnectionStateChanged(RTCPeerConnectionState state);
}
