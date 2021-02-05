package com.example.codecompanion.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codecompanion.R;

import java.util.List;
import java.util.Map;

public class MessageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Map> localDataSet;
    private LayoutInflater mInflater;

    private static int TYPE_ERROR = 1;
    private static int TYPE_WARNING = 0;

    class ErrorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ErrorViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.error_rv_template);
        }
    }

    class WarningViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public WarningViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.warning_rv_template);
        }
    }

    public MessageViewAdapter(Context context, List<Map> dataSet) {
        this.mInflater = LayoutInflater.from(context);
        localDataSet = dataSet;
    }

    @Override
    public int getItemViewType(int position) {
        if(localDataSet.get(position).get("tag").equals("WARNING")) {
            return TYPE_WARNING;
        } else {
            return TYPE_ERROR;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view;
        if(viewType == TYPE_ERROR) {
            view = mInflater.inflate(R.layout.rv_message_error, parent, false);
            return new ErrorViewHolder(view);
        } else{
            view = mInflater.inflate(R.layout.rv_message_warning, parent, false);
            return new WarningViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String message = (String) localDataSet.get(position).get("text");
        if(getItemViewType(position) == TYPE_ERROR) {
            ((ErrorViewHolder) holder).textView.setText(message);
        } else {
            ((WarningViewHolder) holder).textView.setText(message);
        }

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
