package com.example.codecompanion.onboarding.screens;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.codecompanion.R;

public class SecondScreen extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_screen, container, false);
        ViewPager2 viewPager2 = getActivity().findViewById(R.id.viewpager);
        Button startButton = view.findViewById(R.id.next2_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(2);
            }
        });

        return view;
    }
}