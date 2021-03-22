package com.example.codecompanion.ui.compiler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codecompanion.R;

import org.jetbrains.annotations.NotNull;

public class ExpandedMessageFragment extends Fragment {

    private TextView errorDescription;
    private final String explanation;

    public ExpandedMessageFragment(String shortExplanation) {
        this.explanation = shortExplanation;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expanded_description, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        errorDescription = view.findViewById(R.id.expanded_message);
        errorDescription.setText(explanation);
    }

    public void setText(String text) {
        errorDescription.setText(text);
    }
}
