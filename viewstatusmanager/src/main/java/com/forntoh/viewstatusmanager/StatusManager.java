package com.forntoh.viewstatusmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatusManager {

    private Context context;

    private View progressView = null;
    private View failedView = null;
    private ViewGroup parent;

    private String errorTitle = null, errorDescription = null;

    private View.OnClickListener errorClickListener;

    private StatusManager(@NonNull View parent) {
        this.context = parent.getContext();
        this.parent = (ViewGroup) parent;
    }

    public static StatusManager from(@NonNull View parent) {
        return new StatusManager(parent);
    }

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public void setOnErrorClickListener(View.OnClickListener errorClickListener) {
        this.errorClickListener = errorClickListener;
    }

    public void setStatus(@IdRes int targetViewId, Status status) {
        View targetView = parent.findViewById(targetViewId);
        int index = this.parent.indexOfChild(targetView);

        ((Activity) context).runOnUiThread(() -> {

            checkConditions(targetView);

            View toAdd;
            switch (status) {
                case PROGRESS:
                    toAdd = progressView;
                    break;
                case SUCCESS:
                    toAdd = targetView;
                    break;
                case EMPTY:
                    toAdd = failedView;
                    if (errorTitle == null) errorTitle = context.getString(R.string.empty);
                    if (errorDescription == null)
                        errorDescription = context.getString(R.string.empty_sub);
                    break;
                default:
                    toAdd = failedView;
                    if (errorTitle == null) errorTitle = context.getString(R.string.error);
                    if (errorDescription == null)
                        errorDescription = context.getString(R.string.error_sub);
                    break;
            }

            parent.removeViewAt(index);
            parent.addView(toAdd, index);

            ((TextView) failedView.findViewById(R.id.place_title)).setText(errorTitle);
            ((TextView) failedView.findViewById(R.id.place_sub)).setText(errorDescription);
            ((TextView) failedView.findViewById(R.id.place_sub)).append(", Tap here to retry");
        });
    }

    @SuppressLint("InflateParams")
    private void checkConditions(View targetView) {
        if (parent == null) throw new NullPointerException("Parent view cannot be null");
        if (targetView == null) throw new NullPointerException("Target view cannot be null");
        if (progressView == null) {
            progressView = new ProgressBar(context);
            progressView.setPadding(0, getPadding(), 0, getPadding());
        }
        if (failedView == null)
            failedView = LayoutInflater.from(context).inflate(R.layout.error_layout, null);

        this.failedView.setOnClickListener(this.errorClickListener);

        this.failedView.setLayoutParams(targetView.getLayoutParams());
        this.progressView.setLayoutParams(targetView.getLayoutParams());
    }

    private int getPadding() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, context.getResources().getDisplayMetrics());
    }

}
