package com.example.codecompanion.ui.compiler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.codecompanion.R;
import com.example.codecompanion.util.MessageCreator;
import com.example.codecompanion.util.MessageManager;

public class CompilerFragment extends Fragment {

    private MessageManager messageManager;
    private MessageCreator messageCreator;
    private LinearLayout linearLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compiler, container, false);
        messageCreator = new MessageCreator(getContext(),root);
        messageManager = MessageManager.getInstance();
        linearLayout = root.findViewById(R.id.message_list);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        messageManager.setListener(new MessageManager.MessageManagerListener() {
            @Override
            public void onDataChanged() {
                for (String error:messageManager.getErrors()) {
                    doUiChanges(messageCreator.createErrorMessage(error));
                }
                for (String warning:messageManager.getWarnings()) {
                    doUiChanges(messageCreator.createWarningMessage(warning));
                }
            }
        });
        for (String error:messageManager.getErrors()) {
            doUiChanges(messageCreator.createErrorMessage(error));
        }
        for (String warning:messageManager.getWarnings()) {
            doUiChanges(messageCreator.createWarningMessage(warning));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        messageManager.removeListener();
    }

    private void doUiChanges(View view){
        linearLayout.addView(view);
    }
}