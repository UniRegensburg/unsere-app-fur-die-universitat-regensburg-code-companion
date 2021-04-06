package com.example.codecompanion.interfaces;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private RecyclerViewClickListener clickListener;

    public ListTouchListener(Context context, final RecyclerView recyclerView, final RecyclerViewClickListener clickListener){
        this.clickListener = clickListener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e){
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(child != null && clickListener != null){
                    clickListener.onClick(child,recyclerView.getChildLayoutPosition(child));
                }
                return true;
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
