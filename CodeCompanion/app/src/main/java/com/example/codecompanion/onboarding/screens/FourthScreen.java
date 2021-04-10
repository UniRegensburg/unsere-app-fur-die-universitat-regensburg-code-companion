package com.example.codecompanion.onboarding.screens;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.codecompanion.R;
import com.example.codecompanion.util.TinyDB;

public class FourthScreen extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth_screen, container, false);
        Button startButton = view.findViewById(R.id.finish_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_viewPagerFragment_to_navigation_connect);
                onBoardingFinished();
            }
        });

        return view;
    }

    private void onBoardingFinished() {
        TinyDB tinyDB = new TinyDB(getContext());
        tinyDB.putBoolean("finished", true);
    }



}