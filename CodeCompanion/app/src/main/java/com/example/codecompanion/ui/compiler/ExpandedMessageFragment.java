package com.example.codecompanion.ui.compiler;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.codecompanion.R;

import org.jetbrains.annotations.NotNull;

public class ExpandedMessageFragment extends Fragment {

    private TextView errorDescription;
    private final String explanation;
    private ImageView icon;
    private final int textViewId;

    public ExpandedMessageFragment(String shortExplanation, int textViewId) {
        this.explanation = shortExplanation;
        this.textViewId = textViewId;

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
        setIcon(textViewId);
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
