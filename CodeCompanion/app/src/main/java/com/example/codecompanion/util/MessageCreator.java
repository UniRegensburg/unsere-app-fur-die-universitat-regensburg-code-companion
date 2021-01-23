package com.example.codecompanion.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class MessageCreator {

    private String text;
    private Context context;

    public MessageCreator(String text, Context context) {
        this.text = text;
        this.context = context;
    }

    // TODO: test if return-type textview is better
    public void createErrorMessage() {
        TextView error = createMessageTemplate();
        error.setBackground(Drawable.createFromPath("@drawable/message_error_template"));
    }

    // TODO: test if return-type textview is better
    public void createWarningMessage() {
        TextView warning = createMessageTemplate();
        warning.setBackground(Drawable.createFromPath("@drawable/message_warning_template"));
    }

    // TODO: implement rest of ui-styling
    private TextView createMessageTemplate() {
        TextView view = new TextView(context);
        view.setText(text);
        view.setPadding(8, 8, 8, 8);
        view.setTextSize(18);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return view;
    }

}
