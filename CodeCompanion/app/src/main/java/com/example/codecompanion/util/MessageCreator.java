package com.example.codecompanion.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.codecompanion.R;

import static com.example.codecompanion.R.id.message_warning_template;
import static com.example.codecompanion.R.id.rectangles;

public class MessageCreator {

    private Context context;
    private View rootView;

    public MessageCreator(Context context,View rootView) {
        this.context = context;
        this.rootView = rootView;
    }

    // TODO: test if return-type textview is better
    public View createErrorMessage(String text) {
        TextView error = createMessageTemplate(text);
        error.setBackgroundResource(R.drawable.message_error_template);

        return error;
    }

    // TODO: test if return-type textview is better
    public View createWarningMessage(String text) {
        TextView warning = createMessageTemplate(text);
        warning.setBackgroundResource(R.drawable.message_warning_template);
        return warning;
    }

    // TODO: implement rest of ui-styling
    private TextView createMessageTemplate(String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setPadding(8, 8, 8, 8);
        view.setTextSize(18);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return view;
    }


}
