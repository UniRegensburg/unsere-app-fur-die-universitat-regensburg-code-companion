package com.example.codecompanion.ui.tasks;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codecompanion.R;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.TaskManager;
import com.example.codecompanion.util.TaskViewAdapter;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TasksFragment extends Fragment {

    private RecyclerView tasksView;
    private TaskManager taskManager;
    private TaskViewAdapter adapter;
    private List<JSONObject> data;
    private String[] funMessages;
    private String[] funMessagesEmpty;

    private TextView taskMessageField;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);
        taskManager = TaskManager.getInstance();
        tasksView = root.findViewById(R.id.task_list);
        tasksView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        taskMessageField = root.findViewById(R.id.task_message_field);

        data = new ArrayList<>();
        if(taskManager.getTasks() != null) {
            data.addAll(taskManager.getTasks());
        }

        adapter = new TaskViewAdapter(root.getContext(), data);

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
        touchHelper.attachToRecyclerView(tasksView);
        tasksView.setAdapter(adapter);

        funMessages = root.getResources().getStringArray(R.array.fun_messages_tasks);
        funMessagesEmpty = root.getResources().getStringArray(R.array.fun_messages_tasks_empty);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        taskManager.setListener(new TaskManager.TaskManagerListener() {
            @Override
            public void onTaskReceived() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        for(JSONObject task: taskManager.getTasks()) {
                            data.add(task);
                            taskMessageField.setText(funMessages[new Random().nextInt(funMessages.length)]);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        if(data.size() > 0) {
            taskMessageField.setText(funMessages[new Random().nextInt(funMessages.length)]);
        } else {
            taskMessageField.setText(funMessagesEmpty[new Random().nextInt(funMessagesEmpty.length)]);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        taskManager.removeListener();
    }
}