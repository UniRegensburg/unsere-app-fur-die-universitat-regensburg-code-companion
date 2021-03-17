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

import java.util.Random;

public class HomeFragment extends Fragment {
    private Button connect;
    private ConnectionStateManager connectionStateManager;
    private TextView connectionCode;
    private TextView connectionCodeText;
    private TextView homeMessageField;

    private QrScanner qrScanner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        qrScanner = new QrScanner();
        homeMessageField = root.findViewById(R.id.home_message_field);
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
        String[] funMessages = root.getResources().getStringArray(R.array.fun_messages_connect);
        String randomMessage = funMessages[new Random().nextInt(funMessages.length)];
        startTyping(randomMessage);

        return root;
    }

    private void startTyping(String text) {
        Thread thread = new Thread() {
            int i;

            @Override
            public void run() {
                try {
                    for (i = 0; i < text.length(); i++) {
                        Thread.sleep(75);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                homeMessageField.setText(text.substring(0, i));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();
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
                connect.setBackgroundResource(R.drawable.button_grey);
                connect.setAlpha(1f);
                connectionCode.setText("404");
                connectionCode.setTextColor(getResources().getColor(R.color.primary_color3));
                connectionCodeText.setText("oops, can't find you...");
            }
        });
    }

    public void onPause() {
        super.onPause();
        connectionStateManager.removeListener();
    }
}