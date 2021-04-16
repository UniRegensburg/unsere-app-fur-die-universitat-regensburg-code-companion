package com.example.codecompanion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.codecompanion.util.TinyDB;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Splashfragment which shows logo when user opens the app. Also checks if the onBoardingView was already
 * shown
 */
public class SplashFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
        new Handler().postDelayed(() -> {
            if(onBoardingFinished()) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_splashFragment_to_navigation_connect);
            } else {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_splashFragment_to_viewPagerFragment);
            }
        }, 3000);

        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    private boolean onBoardingFinished() {
        TinyDB tinyDB = new TinyDB(getContext());
        return tinyDB.getBoolean("finished");
    }
}