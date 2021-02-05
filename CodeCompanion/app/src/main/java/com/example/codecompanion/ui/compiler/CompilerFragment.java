package com.example.codecompanion.ui.compiler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codecompanion.MainActivity;
import com.example.codecompanion.R;
import com.example.codecompanion.util.MessageCreator;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.MessageViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class CompilerFragment extends Fragment {

    private MessageManager messageManager;
    private MessageCreator messageCreator;
    private MessageViewAdapter adapter;
    private List<String> data;

    private RecyclerView messages;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compiler, container, false);
        messageCreator = new MessageCreator(getContext(),root);
        messageManager = MessageManager.getInstance();
        messages = root.findViewById(R.id.message_list);
        messages.setLayoutManager(new LinearLayoutManager(root.getContext()));
        data = new ArrayList<>();
        data.addAll(messageManager.getErrors());
        data.addAll(messageManager.getWarnings());
        adapter = new MessageViewAdapter(root.getContext(), data);
        messages.setAdapter(adapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        messageManager.setListener(new MessageManager.MessageManagerListener() {
            @Override
            public void onDataChanged() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        adapter.notifyDataSetChanged();
                    }
                });
                for (String error:messageManager.getErrors()) {
                    data.add(error);
                    doUiChanges();
                }
                for (String warning:messageManager.getWarnings()) {
                    data.add(warning);
                    doUiChanges();
                }

            }
        });
        for (String error:messageManager.getErrors()) {
            data.add(error);
            doUiChanges();
        }
        for (String warning:messageManager.getWarnings()) {
            data.add(warning);
            doUiChanges();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        messageManager.removeListener();
    }

    private void doUiChanges(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemInserted(data.size());
            }
        });

    }
}