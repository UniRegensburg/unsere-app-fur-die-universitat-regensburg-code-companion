package com.example.codecompanion.onboarding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.codecompanion.R;
import com.example.codecompanion.onboarding.screens.FirstScreen;
import com.example.codecompanion.onboarding.screens.FourthScreen;
import com.example.codecompanion.onboarding.screens.SecondScreen;
import com.example.codecompanion.onboarding.screens.ThirdScreen;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new FirstScreen());
        fragmentList.add(new SecondScreen());
        fragmentList.add(new ThirdScreen());
        fragmentList.add(new FourthScreen());

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        ViewPagerAdapter adapter = new ViewPagerAdapter(requireActivity().getSupportFragmentManager(), getLifecycle(), fragmentList);
        ViewPager2 viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);


        return view;
    }
}