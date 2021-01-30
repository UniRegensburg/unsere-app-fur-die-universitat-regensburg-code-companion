package com.example.codecompanion.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.codecompanion.util.ConnectionState;
import com.example.codecompanion.util.QrScanner;
import com.example.codecompanion.R;

import org.webrtc.PeerConnection;

public class HomeFragment extends Fragment {
    private Button connect;
    private QrScanner qrScanner;
    private ConnectionState connectionState;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        qrScanner = new QrScanner();
        connectionState = ConnectionState.getInstance();
        connect = root.findViewById(R.id.connect_button);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScanner.start(getActivity());
            }
        });

        return root;
    }

    public void onResume() {
        super.onResume();
        connectionState.setListener(new ConnectionState.ConnectionStateListener() {
            @Override
            public void onConnect() {
                doUIChanges(false, "Connected", R.drawable.button_green);
            }

            @Override
            public void onDisconnect() {
                doUIChanges(true, "Connect here", R.drawable.button_blue);
            }
        });
        if(connectionState.getConnectionState() == PeerConnection.PeerConnectionState.CONNECTED ||
        connectionState.getConnectionState() == PeerConnection.PeerConnectionState.CONNECTING) {
            doUIChanges(false, "Connected", R.drawable.button_green);
        }else {
            doUIChanges(true, "Connect here", R.drawable.button_blue);
        }
    }

    private void doUIChanges(boolean enabled, String text, int buttonColor) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setEnabled(enabled);
                connect.setBackgroundResource(buttonColor);
                connect.setText(text);
            }
        });
    }

    public void onPause() {
        super.onPause();
        connectionState.removeListener();
    }
}