package com.example.codecompanion.ui.tasks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.codecompanion.entity.Task;
import com.example.codecompanion.interfaces.ListTouchListener;
import com.example.codecompanion.interfaces.RecyclerViewClickListener;
import com.example.codecompanion.util.MessageManager;
import com.example.codecompanion.util.TaskManager;
import com.example.codecompanion.util.TaskViewAdapter;
import com.example.codecompanion.util.TinyDB;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TasksFragment extends Fragment {

    private RecyclerView tasksView;
    private TaskManager taskManager;
    private TaskViewAdapter adapter;
    private ArrayList<Task> data;
    private String[] funMessages;
    private String[] funMessagesEmpty;
    private TinyDB tinyDB;

    private TextView taskMessageField;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);
        taskManager = TaskManager.getInstance();
        tasksView = root.findViewById(R.id.task_list);
        tasksView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        taskMessageField = root.findViewById(R.id.task_message_field);
        tinyDB = new TinyDB(getContext());

        data = new ArrayList<>();
        data = tinyDB.getListObject("tasks");
        if(taskManager.getTasks() != null) {
            if(data.isEmpty()){
                data.addAll(taskManager.getTasks());
            }else{
                ArrayList<Task> dataNew = new ArrayList<>();
                dataNew.addAll(taskManager.getTasks());
                ArrayList<String> compList = new ArrayList<>();
                ArrayList<String> compListNew = new ArrayList<>();
                for(int i = 0;i < data.size();i++){
                    compList.add(data.get(i).getDescription());
                }
                for(int j = 0;j < dataNew.size();j++){
                    compListNew.add(dataNew.get(j).getDescription());
                }
                if(!compList.containsAll(compListNew)){
                    Log.d("CREATE","Not the same");
                    data.clear();
                    data.addAll(taskManager.getTasks());
                }
            }
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

        tasksView.addOnItemTouchListener(new ListTouchListener(getContext(), tasksView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                boolean isChecked = data.get(position).isChecked();
                data.get(position).setChecked(!isChecked);
                adapter.notifyDataSetChanged();
            }
        }));

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
                        ArrayList<Task> dataNew = new ArrayList<>();
                        if (data.isEmpty()) {
                            data = taskManager.getTasks();
                        }else{
                            dataNew = taskManager.getTasks();
                            ArrayList<String> compList = new ArrayList<>();
                            ArrayList<String> compListNew = new ArrayList<>();
                            for(int i = 0;i < data.size();i++){
                                compList.add(data.get(i).getDescription());
                            }
                            for(int j = 0;j < dataNew.size();j++){
                              compListNew.add(dataNew.get(j).getDescription());

                            }
                            if(!compList.containsAll(compListNew)){
                                Log.d("ON TASK","Not the same");
                                data.clear();
                                data.addAll(taskManager.getTasks());
                            }
                        }
                        adapter.notifyDataSetChanged();
                        taskMessageField.setText(funMessages[new Random().nextInt(funMessages.length)]);
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
        tinyDB.putListObject("tasks",data);
        taskManager.removeListener();
    }
}