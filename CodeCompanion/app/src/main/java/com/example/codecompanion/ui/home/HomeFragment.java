package com.example.codecompanion.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.codecompanion.util.QrScanner;
import com.example.codecompanion.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button connect;
    private QrScanner qrScanner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        qrScanner = new QrScanner();
        connect = root.findViewById(R.id.connect_button);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScanner.start(getActivity());
            }
        });

        return root;
    }
}