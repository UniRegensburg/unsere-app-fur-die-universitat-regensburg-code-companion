package com.example.codecompanion.ui.compiler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.codecompanion.R;
import com.example.codecompanion.models.CompilerMessage;
import com.example.codecompanion.services.WebRTC;
import com.example.codecompanion.util.ConnectionStateManager;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.MessageViewAdapter;

import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CompilerFragment extends Fragment {

    private MessageManager messageManager;
    private MessageViewAdapter adapter;
    private List<CompilerMessage> data;
    public static final String REFRESH_DATA_MESSAGE = "REFRESH_DATA";
    private String[] funMessages;
    private String[] funMessagesEmpty;

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
        data.addAll(messageManager.getCompilerMessages());

        adapter = new MessageViewAdapter(root.getContext(), data);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int sourcePos = viewHolder.getAdapterPosition();
                int targetPos = target.getAdapterPosition();
                Collections.swap(data, sourcePos,targetPos);
                adapter.notifyItemMoved(sourcePos,targetPos);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        touchHelper.attachToRecyclerView(messages);
        messages.setAdapter(adapter);

        addRefreshListener(root);
        funMessages = root.getResources().getStringArray(R.array.fun_messages_compiler);
        funMessagesEmpty = root.getResources().getStringArray(R.array.fun_messages_compiler_empty);

        return root;
    }

    private void addRefreshListener(View root) {
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefreshCompiler);
        pullToRefresh.setOnRefreshListener(() -> {
            if (ConnectionStateManager.getInstance().getConnectionState() == PeerConnection.PeerConnectionState.CONNECTED) {
                try {
                    messageManager.clearAllMessages();
                    WebRTC.sendData(REFRESH_DATA_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pullToRefresh.setRefreshing(false);
        });

    }

    @Override
    public void onResume() {
        if(data.size() > 0) {
            compilerMessageField.setText(funMessages[new Random().nextInt(funMessages.length)]);
        } else {
            compilerMessageField.setText(funMessagesEmpty[new Random().nextInt(funMessagesEmpty.length)]);
        }
        super.onResume();
        messageManager.setListener(() -> getActivity().runOnUiThread(() -> {
            data.clear();
            data.addAll(messageManager.getCompilerMessages());

            if(data.size() > 0) {
                compilerMessageField.setText(funMessages[new Random().nextInt(funMessages.length)]);
            } else {
                compilerMessageField.setText(funMessagesEmpty[new Random().nextInt(funMessagesEmpty.length)]);
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