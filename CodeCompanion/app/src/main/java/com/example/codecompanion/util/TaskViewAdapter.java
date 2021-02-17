package com.example.codecompanion.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codecompanion.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TaskViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<JSONObject> localDataSet;
    private LayoutInflater mInflater;

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.task_rv_template);
        }
    }

    public TaskViewAdapter(Context context, List<JSONObject> dataSet) {
        this.mInflater = LayoutInflater.from(context);
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            String task = (String) localDataSet.get(position).getString("description");
            ((ViewHolder) holder).checkBox.setText(task);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
