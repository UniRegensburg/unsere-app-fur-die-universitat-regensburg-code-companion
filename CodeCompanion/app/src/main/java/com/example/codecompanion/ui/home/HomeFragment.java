package com.example.codecompanion.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.codecompanion.util.ConnectionStateManager;
import com.example.codecompanion.util.QrScanner;
import com.example.codecompanion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.webrtc.PeerConnection;

import java.util.Random;

/**
 * Home fragment which is displayed on startup, and upon pressing the home button in
 * the bottom navigation bar
 * Shows current connection status and allows starting the connection process
 */
public class HomeFragment extends Fragment {
    private Button connect;
    private ConnectionStateManager connectionStateManager;
    private ImageView connectionStatusView;
    private TextView connectionCodeText;
    private TextView homeMessageField;
    private ConnectionStateManager.ConnectionStateListener connectionStateListener = createConnectionStateListener();

    private QrScanner qrScanner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

        qrScanner = new QrScanner();
        homeMessageField = root.findViewById(R.id.home_message_field);
        connectionStateManager = ConnectionStateManager.getInstance();
        connectionCodeText = root.findViewById(R.id.connection_code_text);
        connectionStatusView = root.findViewById(R.id.connection_status);
        connect = root.findViewById(R.id.connect_button);
        connect.setOnClickListener(v -> qrScanner.start(getActivity()));
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
        connectionStateManager.addListener(connectionStateListener);
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
                connectionCodeText.setText("ah there you are!");
                connectionStatusView.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.primary_color1));
                connectionStatusView.setBackgroundResource(R.drawable.ic_baseline_task_alt_24);


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
                connectionCodeText.setText("oops, can't find you...");
                connectionStatusView.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.primary_color3));
                connectionStatusView.setBackgroundResource(R.drawable.ic_baseline_cancel_24);


            }
        });
    }

    public void onPause() {
        super.onPause();
        connectionStateManager.removeListener(connectionStateListener);
    }

    private ConnectionStateManager.ConnectionStateListener createConnectionStateListener() {
        return new ConnectionStateManager.ConnectionStateListener() {
            @Override
            public void onConnect() {
                setConnected();
            }

            @Override
            public void onDisconnect() {
                setDisconnected();
            }
        };
    }
}