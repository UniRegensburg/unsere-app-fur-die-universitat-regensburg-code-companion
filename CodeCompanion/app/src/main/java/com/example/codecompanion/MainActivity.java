package com.example.codecompanion;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.codecompanion.services.ErrorMessageRecieverService;
import com.example.codecompanion.util.ConnectionStateManager;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.TaskManager;
import com.example.codecompanion.util.WebRTC;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.webrtc.PeerConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG =  "Main Activity";
    private BottomNavigationView bottomNavigation;
    private WebRTC webRTC;
    private BadgeDrawable connectionState;
    private MessageManager messageManager;
    private TaskManager taskManager;
    private ConnectionStateManager connectionStateManager;
    private ErrorMessageRecieverService errorMessageRecieverService;
    private String id;
    private boolean errorServiceBound = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskManager = TaskManager.getInstance();
        connectionStateManager = ConnectionStateManager.getInstance();
        Intent intent = (new Intent(this, ErrorMessageRecieverService.class));
        bindService(intent, errorMessageConnection, Context.BIND_AUTO_CREATE);
        messageManager = MessageManager.getInstance();
        createNavigation();
        webRTC = new WebRTC();
        webRTC.setWebRTCListener(new WebRTC.WebRTCListener() {
            @Override
            public void onConnectionStateChanged(PeerConnection.PeerConnectionState state) {
                connectionStateManager.getInstance().stateChanged(state);
                connectionStateManager.setConnectedToId(id);
                setBadgeForConnectionState(state.toString());
                Log.d(TAG,state.toString());
            }

            @Override
            public void onMessageReceived(String message) {
                try {
                    if(message.contains("task")) {
                        if(ConnectionStateManager.getInstance().getConnectionState() == PeerConnection.PeerConnectionState.CONNECTED) {
                            taskManager.handleTaskInfo(message);
                        }
                    } else {
                        errorMessageRecieverService.handleMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("Scan", "Cancelled scan");
            } else {
                Log.d("Scan", "Scanned: " + result.getContents());
                id = result.getContents();
                webRTC.init(this,id);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void createNavigation() {
        bottomNavigation = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_connect, R.id.navigation_compiler, R.id.navigation_tasks, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigation, navController);

        connectionState = bottomNavigation.getOrCreateBadge(R.id.navigation_connect);
        setBadgeForConnectionState("NOT_CONNECTED");
    }

    private void setBadgeForConnectionState(String state) {
        if(state == "CONNECTED") {
            connectionState.setBackgroundColor(getResources().getColor(R.color.primary_color1));
        }else {
            connectionState.setBackgroundColor(getResources().getColor(R.color.primary_color2));
        }
        connectionState.isVisible();
    }

    private ServiceConnection errorMessageConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ErrorMessageRecieverService.ErrorMessageBinder binder = (ErrorMessageRecieverService.ErrorMessageBinder) iBinder;
            errorMessageRecieverService = binder.getService();
            errorServiceBound = true;
            messageManager.setErrorMessageRecieverService(errorMessageRecieverService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            errorServiceBound = false;
        }
    };

}