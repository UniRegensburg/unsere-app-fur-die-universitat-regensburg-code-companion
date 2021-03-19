package com.example.codecompanion.util;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.codecompanion.R;
import com.example.codecompanion.ui.compiler.ExpandedMessageFragment;

import java.util.List;
import java.util.Map;


/**
 * @author PLACEBOBRO
 */
public class MessageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Map> localDataSet;
    private LayoutInflater mInflater;

    private static int TYPE_ERROR = 1;
    private static int TYPE_WARNING = 0;


    /**
     * ErrorViewHolder with error layout
     */
    class ErrorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ConstraintLayout layout;

        public ErrorViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.error_rv_template);
            layout = (ConstraintLayout) view.findViewById(R.id.error);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment fragment = new ExpandedMessageFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
                }
            });
        }
    }

    /**
     * WarningViewHolder with warning layout
     */
    class WarningViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ConstraintLayout layout;

        public WarningViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.warning_rv_template);
            layout = (ConstraintLayout) view.findViewById(R.id.warning);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment fragment = new ExpandedMessageFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
                }
            });
        }
    }

    public MessageViewAdapter(Context context, List<Map> dataSet) {
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
        if(localDataSet.get(position).get("tag").equals("WARNING")) {
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
        String message = (String) localDataSet.get(position).get("description");
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
