package com.example.codecompanion.ui.compiler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.codecompanion.R;
import com.example.codecompanion.services.WebRTC;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.MessageViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompilerFragment extends Fragment {

    private MessageManager messageManager;
    private MessageViewAdapter adapter;
    private List<Map> data;
    public static final String REFRESH_DATA_MESSAGE = "REFRESH_DATA";

    private TextView compilerMessageField;
    private RecyclerView messages;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compiler, container, false);
        messageManager = MessageManager.getInstance();
        messages = root.findViewById(R.id.message_list);
        messages.setLayoutManager(new LinearLayoutManager(root.getContext()));
        compilerMessageField = root.findViewById(R.id.compiler_message_field);

        data = new ArrayList<>();
        data.addAll(messageManager.getErrors());
        data.addAll(messageManager.getWarnings());
        
        adapter = new MessageViewAdapter(root.getContext(), data);
        messages.setAdapter(adapter);

        addRefreshListener(root);
        return root;
    }

    private void addRefreshListener(View root) {
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefreshCompiler);
        pullToRefresh.setOnRefreshListener(() -> {
            try {
                messageManager.clearAllMessages();
                WebRTC.sendData(REFRESH_DATA_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pullToRefresh.setRefreshing(false);
        });

    }

    @Override
    public void onResume() {
        if(data.size() > 0) {
            compilerMessageField.setText("> i think you have some errors...");
        } else {
            compilerMessageField.setText("> seems to work correctly.");
        }
        super.onResume();
        messageManager.setListener(() -> getActivity().runOnUiThread(() -> {
            data.clear();
            data.addAll(messageManager.getErrors());
            data.addAll(messageManager.getWarnings());

            if(data.size() > 0) {
                compilerMessageField.setText("> i think you have some errors...");
            } else {
                compilerMessageField.setText("> seems to work correctly.");
            }
            adapter.notifyDataSetChanged();
        }));
    }

    @Override
    public void onPause() {
        super.onPause();
        messageManager.removeListener();
    }
}