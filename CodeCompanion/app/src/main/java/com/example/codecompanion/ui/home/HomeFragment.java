package com.example.codecompanion.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.codecompanion.util.ConnectionStateManager;
import com.example.codecompanion.util.QrScanner;
import com.example.codecompanion.R;

import org.webrtc.PeerConnection;

public class HomeFragment extends Fragment {
    private Button connect;
    private View connectionStateView;
    private TextView connectionStateValue;
    private ConnectionStateManager connectionStateManager;
    private TextView connectionStateId;
    private Drawable connectedIcon;
    private Drawable disconnectedIcon;

    private QrScanner qrScanner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        qrScanner = new QrScanner();

        connectionStateManager = ConnectionStateManager.getInstance();
        connectionStateValue = root.findViewById(R.id.connection_state_value);
        connectionStateView = root.findViewById(R.id.connection_state_view);
        connectionStateId = root.findViewById(R.id.connection_state_id);
        connectedIcon = getResources().getDrawable(R.drawable.ic_baseline_phonelink_24);
        disconnectedIcon = getResources().getDrawable(R.drawable.ic_baseline_phonelink_off_24);
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
        connectionStateManager.setListener(new ConnectionStateManager.ConnectionStateListener() {
            @Override
            public void onConnect() {
                setConnected();
            }

            @Override
            public void onDisconnect() {
                setDisconnected();
            }
        });
        if(connectionStateManager.getConnectionState() == PeerConnection.PeerConnectionState.CONNECTED ||
        connectionStateManager.getConnectionState() == PeerConnection.PeerConnectionState.CONNECTING) {
            setConnected();
        }else {
            setDisconnected();
        }
    }

    private void setConnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setEnabled(false);
                connect.setBackgroundResource(R.drawable.button_grey);
                connect.setAlpha(.5f);
                connectionStateView.setBackgroundResource(R.drawable.button_green);
                connectionStateValue.setText("CONNECTED");
                connectionStateId.setText("to " + connectionStateManager.getConnectedToId());
                connectionStateValue.setCompoundDrawablesRelativeWithIntrinsicBounds(connectedIcon, null, null, null);
            }
        });
    }

    private void setDisconnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setEnabled(true);
                connect.setBackgroundResource(R.drawable.button_green);
                connect.setAlpha(1f);
                connectionStateView.setBackgroundResource(R.drawable.button_red);
                connectionStateValue.setText("NOT CONNECTED");
                connectionStateId.setText("");
                connectionStateValue.setCompoundDrawablesRelativeWithIntrinsicBounds(disconnectedIcon, null, null, null);
            }
        });
    }

    public void onPause() {
        super.onPause();
        connectionStateManager.removeListener();
    }
}