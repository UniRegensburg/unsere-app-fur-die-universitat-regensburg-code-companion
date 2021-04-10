package com.example.codecompanion;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;

import com.example.codecompanion.services.ErrorMessageReceiverService;
import com.example.codecompanion.util.ConnectionStateManager;
import com.example.codecompanion.util.DeadlineReceiver;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.TaskManager;
import com.example.codecompanion.services.WebRTC;
import com.example.codecompanion.util.TinyDB;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.webrtc.PeerConnection;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements TaskManager.DeadlineLineListener {

    private static final String TAG =  "Main Activity";
    private BottomNavigationView bottomNavigation;
    private WebRTC webRTC;
    private BadgeDrawable connectionState;
    private MessageManager messageManager;
    private TaskManager taskManager;
    private ConnectionStateManager connectionStateManager;
    private ErrorMessageReceiverService errorMessageReceiverService;
    private String id;
    private TinyDB tinyDB;
    public static boolean isExpandedMessageOpen = false;

    private boolean errorServiceBound = false;
    private boolean webRTCServiceBound = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tinyDB = new TinyDB(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskManager = TaskManager.getInstance();
        connectionStateManager = ConnectionStateManager.getInstance();
        Intent webRTCIntent = (new Intent(this, WebRTC.class));
        bindService(webRTCIntent, webRTCServiceConnection, Context.BIND_AUTO_CREATE);
        Intent errorMessageIntent = (new Intent(this, ErrorMessageReceiverService.class));
        bindService(errorMessageIntent, errorMessageConnection, Context.BIND_AUTO_CREATE);
        messageManager = MessageManager.getInstance();
        taskManager.setDeadlineListener(this);
        createNavigation();
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

        bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

            }
        });
    }

    private void setBadgeForConnectionState(String state) {
        if (state.equals("CONNECTED")) {
            connectionState.setBackgroundColor(getResources().getColor(R.color.primary_color1));
        } else {
            connectionState.setBackgroundColor(getResources().getColor(R.color.primary_color3));
        }
        connectionState.isVisible();
    }

    private final ServiceConnection errorMessageConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ErrorMessageReceiverService.ErrorMessageBinder binder = (ErrorMessageReceiverService.ErrorMessageBinder) iBinder;
            errorMessageReceiverService = binder.getService();
            errorServiceBound = true;
            messageManager.setErrorMessageReceiverService(errorMessageReceiverService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            errorServiceBound = false;
        }
    };

    private final ServiceConnection webRTCServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WebRTC.WebRTCServiceBinder binder = (WebRTC.WebRTCServiceBinder) iBinder;
            webRTC = binder.getService();
            webRTCServiceBound = true;
            webRTC.setWebRTCListener(new WebRTC.WebRTCListener() {
                @Override
                public void onConnectionStateChanged(PeerConnection.PeerConnectionState state) {
                    ConnectionStateManager.getInstance().stateChanged(state);
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
                            errorMessageReceiverService.handleMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            webRTCServiceBound = false;
        }
    };

    @Override
    public void onBackPressed() {
        if (isExpandedMessageOpen) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack("expandedMessage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            isExpandedMessageOpen = false;
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDeadlineReceived(Date deadline, String titleTask) {

        String title = tinyDB.getString("title");
        boolean alarm1 = tinyDB.getBoolean("alarm1");
        boolean alarm2 = tinyDB.getBoolean("alarm2");
        boolean alarm3 = tinyDB.getBoolean("alarm3");

        if(!titleTask.equals(title)){
            System.out.println("Title not the same");
            tinyDB.putString("title",titleTask);
            for(int i = 0; i < 3;i++) {
                setNotification(deadline,i);
            }
            tinyDB.putBoolean("alarm1",true);
            tinyDB.putBoolean("alarm2",true);
            tinyDB.putBoolean("alarm3",true);
        }else{
            System.out.println("Title the same");
            if(!alarm1){
                setNotification(deadline,1);
                tinyDB.putBoolean("alarm1",true);
            }
            if(!alarm2){
                setNotification(deadline,2);
                tinyDB.putBoolean("alarm2",true);
            }
            if(!alarm3){
                setNotification(deadline,3);
                tinyDB.putBoolean("alarm3",true);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setNotification(Date deadline,int code){
        Intent notifyIntent = new Intent(this, DeadlineReceiver.class);
        long date;
        String deadlineString;
        switch (code) {
            case 0:
                date = deadline.getTime() - DateUtils.DAY_IN_MILLIS * 1;
                deadlineString = "1 day";
                break;
            case 1:
                deadlineString = "3 days";
                date = deadline.getTime() - DateUtils.DAY_IN_MILLIS * 3;
                break;
            default:
                deadlineString = "1 week";
                date = deadline.getTime() - DateUtils.DAY_IN_MILLIS * 7;
                break;
        }
        notifyIntent.putExtra("deadline", deadlineString);
        notifyIntent.putExtra("code", code);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, code, notifyIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date, pendingIntent);
    }
}