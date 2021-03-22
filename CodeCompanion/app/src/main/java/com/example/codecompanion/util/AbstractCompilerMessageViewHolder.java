package com.example.codecompanion.util;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codecompanion.R;
import com.example.codecompanion.ui.compiler.ExpandedMessageFragment;

public abstract class AbstractCompilerMessageViewHolder extends RecyclerView.ViewHolder {
	private final TextView textView;
	private final ConstraintLayout layout;
	private ExpandedMessageFragment fragment;
	private String explanation;

	public AbstractCompilerMessageViewHolder(View view, int textViewId, int layoutId) {
		super(view);
		textView = (TextView) view.findViewById(textViewId);
		layout = (ConstraintLayout) view.findViewById(layoutId);
		layout.setOnClickListener(v -> {
			AppCompatActivity activity = (AppCompatActivity) view.getContext();
			fragment = new ExpandedMessageFragment(explanation);
			activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
		});
	}

	public TextView getTextView() {
		return textView;
	}

	public ConstraintLayout getLayout() {
		return layout;
	}

	public ExpandedMessageFragment getFragment() {
		return fragment;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
}
