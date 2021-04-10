package com.example.codecompanion.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.codecompanion.R;

public class ProfileFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ImageButton helpButton = root.findViewById(R.id.help_button);
        helpButton.setOnClickListener(v -> Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.viewPagerFragment));
        return root;
    }
}
