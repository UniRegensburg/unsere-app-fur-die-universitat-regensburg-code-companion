package com.example.codecompanion.util;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codecompanion.R;
import com.example.codecompanion.ui.compiler.ExpandedMessageFragment;

public abstract class AbstractCompilerMessageViewHolder extends RecyclerView.ViewHolder {
	private final TextView errorMessageTextView;
	private final TextView lineNumberTextView;
	private final ConstraintLayout layout;
	private ExpandedMessageFragment fragment;
	private String shortExplanation;

	public AbstractCompilerMessageViewHolder(View view, int errorMessageViewId, int lineNumberViewId, int layoutId) {
		super(view);
		errorMessageTextView = view.findViewById(errorMessageViewId);
		lineNumberTextView = view.findViewById(lineNumberViewId);
		layout = view.findViewById(layoutId);
		layout.setOnClickListener(v -> {
			AppCompatActivity activity = (AppCompatActivity) view.getContext();
			fragment = new ExpandedMessageFragment(shortExplanation, errorMessageViewId, errorMessageTextView.getText().toString());
			activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack("expandedMessage").commit();
		});
	}

	public TextView getErrorMessageTextView() {
		return errorMessageTextView;
	}

	public TextView getLineNumberTextView() {
		return lineNumberTextView;
	}

	public ConstraintLayout getLayout() {
		return layout;
	}

	public ExpandedMessageFragment getFragment() {
		return fragment;
	}

	public String getShortExplanation() {
		return shortExplanation;
	}

	public void setShortExplanation(String explanation) {
		this.shortExplanation = explanation;
	}
}
