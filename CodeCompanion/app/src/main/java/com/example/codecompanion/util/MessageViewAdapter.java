package com.example.codecompanion.util;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codecompanion.R;
import com.example.codecompanion.models.CompilerMessage;
import com.example.codecompanion.models.SeverityType;

import java.util.List;


/**
 * @author PLACEBOBRO
 */
public class MessageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CompilerMessage> localDataSet;
    private final LayoutInflater mInflater;

    private static final int TYPE_ERROR = 1;
    private static final int TYPE_WARNING = 0;


    /**
     * ErrorViewHolder with error layout
     */
    class ErrorViewHolder extends AbstractCompilerMessageViewHolder {
        public ErrorViewHolder(View view) {
            super(view, R.id.error_rv_template, R.id.error);
        }
    }

    /**
     * WarningViewHolder with warning layout
     */
    class WarningViewHolder extends AbstractCompilerMessageViewHolder {
        public WarningViewHolder(View view) {
            super(view, R.id.warning_rv_template, R.id.warning);
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
        String shortExplanation = localDataSet.get(position).getShortExplanation();

        if(getItemViewType(position) == TYPE_ERROR) {
            ((ErrorViewHolder) holder).getTextView().setText(message);
            ((ErrorViewHolder) holder).setShortExplanation(shortExplanation);
        } else if (getItemViewType(position) == TYPE_WARNING) {
            ((WarningViewHolder) holder).getTextView().setText(message);
            ((WarningViewHolder) holder).setShortExplanation(shortExplanation);
        }

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
