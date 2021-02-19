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
    private ConnectionStateManager connectionStateManager;
    private TextView connectionCode;
    private TextView connectionCodeText;

    private QrScanner qrScanner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        qrScanner = new QrScanner();

        connectionStateManager = ConnectionStateManager.getInstance();
        connectionCode = root.findViewById(R.id.connection_code);
        connectionCodeText = root.findViewById(R.id.connection_code_text);
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
                connect.setText("CONNECTED");
                connect.setBackgroundResource(R.drawable.button_grey);
                connect.setAlpha(.5f);
                connectionCode.setText("200");
                connectionCode.setTextColor(getResources().getColor(R.color.primary_color1));
                connectionCodeText.setText("ah there you are!");
            }
        });
    }

    private void setDisconnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setEnabled(true);
                connect.setText("CONNECT");
                connect.setBackgroundResource(R.drawable.button_green);
                connect.setAlpha(1f);
                connectionCode.setText("404");
                connectionCode.setTextColor(getResources().getColor(R.color.primary_color2));
                connectionCodeText.setText("oops, can't find you...");
            }
        });
    }

    public void onPause() {
        super.onPause();
        connectionStateManager.removeListener();
    }
}