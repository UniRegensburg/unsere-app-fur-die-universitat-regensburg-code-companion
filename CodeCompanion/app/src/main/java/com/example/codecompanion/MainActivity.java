package com.example.codecompanion;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;

import com.example.codecompanion.cache.StatsCache;
import com.example.codecompanion.db.AppDatabase;
import com.example.codecompanion.db.ProjectInformation;
import com.example.codecompanion.services.ErrorMessageReceiverService;
import com.example.codecompanion.services.LinesOfCodeMessageReceiverService;
import com.example.codecompanion.util.ConnectionStateManager;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.TaskManager;
import com.example.codecompanion.services.WebRTC;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
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
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG =  "Main Activity";
    private static final String TASK_MESSAGE_TAG =  "task";
    private static final String ERROR_MESSAGE_TAG =  "add/remove";
    private static final String PROJECT_MESSAGE_TAG =  "projectName";
    private static final String LINES_OF_CODE_MESSAGE_TAG =  "stats";
    private BottomNavigationView bottomNavigation;
    private WebRTC webRTC;
    private BadgeDrawable connectionState;
    private MessageManager messageManager;
    private TaskManager taskManager;
    private ConnectionStateManager connectionStateManager;
    private ErrorMessageReceiverService errorMessageReceiverService;
    private LinesOfCodeMessageReceiverService linesOfCodeMessageReceiverService;
    private String id;

    public static final String DATABASE_TAG = "database";
    public static final String SHARED_PREFERENCES_STATS_TAG = "SHARED_PREFERENCES_STATS_TAG";
    public static boolean isExpandedMessageOpen = false;
    public static AppDatabase db;

    private boolean errorServiceBound = false;
    private boolean linesOfCodeServiceBound = false;
    private boolean webRTCServiceBound = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskManager = TaskManager.getInstance();
        connectionStateManager = ConnectionStateManager.getInstance();
        Intent webRTCIntent = (new Intent(this, WebRTC.class));
        bindService(webRTCIntent, webRTCServiceConnection, Context.BIND_AUTO_CREATE);
        createErrorMessageService();
        createLinesOfCodeService();
        messageManager = MessageManager.getInstance();
        createNavigation();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database").build();
    }


    private void createLinesOfCodeService() {
        Intent intent = (new Intent(this, LinesOfCodeMessageReceiverService.class));
        bindService(intent, linesOfCodeMessageConnection, Context.BIND_AUTO_CREATE);
    }

    private void createErrorMessageService() {
        Intent errorMessageIntent = (new Intent(this, ErrorMessageReceiverService.class));
        bindService(errorMessageIntent, errorMessageConnection, Context.BIND_AUTO_CREATE);
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

    private final ServiceConnection linesOfCodeMessageConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LinesOfCodeMessageReceiverService.LinesOfCodeMessageBinder binder = (LinesOfCodeMessageReceiverService.LinesOfCodeMessageBinder) iBinder;
            linesOfCodeMessageReceiverService = binder.getService();
            linesOfCodeServiceBound = true;
            messageManager.setLinesOfCodeMessageReceiverService(linesOfCodeMessageReceiverService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            linesOfCodeServiceBound = false;
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
                        if(message.contains(TASK_MESSAGE_TAG)) {
                            if(ConnectionStateManager.getInstance().getConnectionState() == PeerConnection.PeerConnectionState.CONNECTED) {
                                taskManager.handleTaskInfo(message);
                            }
                        } else if (message.contains(ERROR_MESSAGE_TAG)) {
                            errorMessageReceiverService.handleMessage(message);
                        } else if (message.contains(PROJECT_MESSAGE_TAG)) {
                            updateCurrentProjectTag(message);
                            updateStatsForProject();
                        } else if (message.contains(LINES_OF_CODE_MESSAGE_TAG)) {
                            linesOfCodeMessageReceiverService.handleMessage(message);
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

    private void updateStatsForProject() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ProjectInformation dbProject = db.projectInformationDAO().findByTag(StatsCache.currentProjectTag);
            StatsCache.currentProject = new ProjectInformation(StatsCache.currentProjectTag);
            StatsCache.projectOpenedDate = DateTime.now();

            if (dbProject == null) {
                db.projectInformationDAO().insert(StatsCache.currentProject);
                StatsCache.currentProject = db.projectInformationDAO().findByTag(StatsCache.currentProjectTag);
            } else {
                StatsCache.currentProject = dbProject;
            }
        });
    }

    private void updateCurrentProjectTag(String message) throws JSONException {
        JSONObject jsonObject = new JSONObject(message);
        StatsCache.currentProjectTag = jsonObject.getString("projectName") + "-" + jsonObject.getString("projectPath");
    }

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

    @Override
    public void onPause() {
        // save all changes to the current project if not null
        if (StatsCache.currentProject != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                DateTime now = DateTime.now();
                Seconds seconds = Seconds.secondsBetween(StatsCache.projectOpenedDate, now);
                int alreadySpentSeconds = db.projectInformationDAO().findSecondsSpentOnProjectById(StatsCache.currentProject.getId());

                // add the already saved seconds from the database to the seconds from this session, then update the entity
                StatsCache.currentProject.secondsSpentOnProject = seconds.getSeconds() + alreadySpentSeconds;
                db.projectInformationDAO().updateProject(StatsCache.currentProject);

                // reset the current session timer
                StatsCache.projectOpenedDate = DateTime.now();
            });
        }

        super.onPause();
    }

}