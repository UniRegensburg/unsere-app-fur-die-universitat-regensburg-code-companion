package com.example.codecompanion.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codecompanion.R;
import com.example.codecompanion.models.CompilerMessage;
import com.example.codecompanion.models.SeverityType;

import java.util.List;
import java.util.Map;


/**
 * @author PLACEBOBRO
 */
public class MessageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CompilerMessage> localDataSet;
    private LayoutInflater mInflater;

    private static int TYPE_ERROR = 1;
    private static int TYPE_WARNING = 0;


    /**
     * ErrorViewHolder with error layout
     */
    class ErrorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ErrorViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.error_rv_template);
        }
    }

    /**
     * WarningViewHolder with warning layout
     */
    class WarningViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public WarningViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.warning_rv_template);
        }
    }

    public MessageViewAdapter(Context context, List<CompilerMessage> dataSet) {
        this.mInflater = LayoutInflater.from(context);
        localDataSet = dataSet;
    }

    /**
     * Checks the type of given map and returns it
     * @param position
     * @return type of given map
     */
    @Override
    public int getItemViewType(int position) {
        if(localDataSet.get(position).getSeverityType() == SeverityType.WARNING) {
            return TYPE_WARNING;
        } else {
            return TYPE_ERROR;
        }

    }

    /**
     * Decides which layout to inflate based on type
     * @param parent
     * @param viewType
     * @return the used viewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_ERROR) {
            view = mInflater.inflate(R.layout.rv_message_error, parent, false);
            return new ErrorViewHolder(view);
        } else{
            view = mInflater.inflate(R.layout.rv_message_warning, parent, false);
            return new WarningViewHolder(view);
        }


    }

    /**
     * Sets text of selected view template
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String message = localDataSet.get(position).getDescription();
        String explanation = localDataSet.get(position).getShortExplanation();
        if(getItemViewType(position) == TYPE_ERROR) {
            ((ErrorViewHolder) holder).textView.setText(message + "\n" + explanation);
        } else {
            ((WarningViewHolder) holder).textView.setText(message + "\n" + explanation);
        }

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
