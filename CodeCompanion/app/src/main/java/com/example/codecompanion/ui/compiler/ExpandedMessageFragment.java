package com.example.codecompanion.ui.compiler;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codecompanion.MainActivity;
import com.example.codecompanion.R;
import com.example.codecompanion.services.WebRTC;
import com.example.codecompanion.util.ConnectionStateManager;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.webrtc.PeerConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ExpandedMessageFragment extends Fragment {

    private final String GOOGLE_QUERY_STRING = "https://www.google.com/search?q=";
    private final int BUTTON_TIMEOUT_IN_MILLISECONDS = 250; // used to prevent accidental doubletaps

    private TextView errorDescription;
    private final String explanation;
    private String error;
    private ImageView icon;
    private final int textViewId;
    private TextView desktopLink;
    private TextView phoneLink;
    private DateTime phoneButtonClickedTime;
    private DateTime desktopButtonClickedTime;

    public ExpandedMessageFragment(String shortExplanation, int textViewId, String error) {
        this.explanation = shortExplanation;
        this.textViewId = textViewId;
        this.error = error;
        phoneButtonClickedTime = DateTime.now().minusSeconds(10);
        desktopButtonClickedTime = DateTime.now().minusSeconds(10);
        MainActivity.isExpandedMessageOpen = true;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expanded_description, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        errorDescription = view.findViewById(R.id.expanded_message);
        errorDescription.setText(explanation);

        icon = view.findViewById(R.id.exp_message_icon);
        desktopLink = view.findViewById(R.id.dekstop_link_textview);
        phoneLink = view.findViewById(R.id.phone_link_textview);

        prepareErrorForUrl();
        setIcon(textViewId);
        setLinks();
    }

    // formats the query url by replacing spaces for '+' and adds the suffix "Java"
    private void prepareErrorForUrl() {
        error = error.replace(" ", "+");
        error += "+Java";
    }

    private void setLinks() {
        // add click listeners to the TextViews
        setPhoneLink();
        setDesktopLink();
    }

    private void setDesktopLink() {
        desktopLink.setOnClickListener(view -> {
            if (isButtonOnTimeout(desktopButtonClickedTime)) {
                return;
            }

            desktopButtonClickedTime = DateTime.now();
            Map<String, String> message = new HashMap<>();
            message.put("google-query", GOOGLE_QUERY_STRING + error);
            String messageString = new Gson().toJson(message);

            if (ConnectionStateManager.getInstance().getConnectionState() == PeerConnection.PeerConnectionState.CONNECTED) {
                try {
                    WebRTC.sendData(messageString);
                } catch (Exception e) {
                    Log.e("WebRTC", "Could not send message for Google Query");
                    e.printStackTrace();
                }
            }
        });
    }

    private void setPhoneLink() {
        phoneLink.setOnClickListener(view -> {
            if (!isButtonOnTimeout(phoneButtonClickedTime)) {
                phoneButtonClickedTime = DateTime.now();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_QUERY_STRING + error));
                startActivity(browserIntent);
            }
        });
    }

    // method to check if button is on timeout - to prevent accidental doubletaps
    // app will crash here if user leaves message fragment open for longer than 25 days.
    // let's hope that this will not happen
    private boolean isButtonOnTimeout(DateTime buttonClickedTime) {
        DateTime now = DateTime.now();
        Period period = new Period(buttonClickedTime, now, PeriodType.millis());

        return period.getMillis() < BUTTON_TIMEOUT_IN_MILLISECONDS;
    }

    public void setText(String text) {
        errorDescription.setText(text);
    }

    public void setIcon(int id) {
        if(id == R.id.error_rv_template) {
            icon.setImageResource(R.drawable.ic_baseline_error_outline_24);
            icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_color3)));
        }else if(id == R.id.warning_rv_template) {
            icon.setImageResource(R.drawable.ic_baseline_warning_amber_24);
            icon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_color2)));
        }
    }
}
