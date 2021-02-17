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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codecompanion.R;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.TaskManager;
import com.example.codecompanion.util.TaskViewAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.codecompanion.R.drawable.button_blue;

public class TasksFragment extends Fragment {

    private RecyclerView tasksView;
    private TaskManager taskManager;
    private TaskViewAdapter adapter;
    private List<JSONObject> data;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);
        taskManager = TaskManager.getInstance();
        tasksView = root.findViewById(R.id.task_list);
        tasksView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        data = new ArrayList<>();
        if(taskManager.getTasks() != null) {
            data.addAll(taskManager.getTasks());
        }

        adapter = new TaskViewAdapter(root.getContext(), data);
        tasksView.setAdapter(adapter);
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
                        for(JSONObject task: taskManager.getTasks()) {
                            data.add(task);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        taskManager.removeListener();
    }
}