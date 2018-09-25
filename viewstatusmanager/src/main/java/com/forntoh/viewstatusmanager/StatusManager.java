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
    private View targetView = null;
    private ViewGroup parent = null;

    private int targetViewId, index;

    private ViewGroup.LayoutParams targetViewLayoutParams;

    private String errorTitle = null, errorDescription = null;

    private View.OnClickListener errorClickListener;

    private StatusManager(int targetView) {
        this.targetViewId = targetView;
    }

    public static StatusManager with(@IdRes int targetView) {
        return new StatusManager(targetView);
    }

    public StatusManager from(@NonNull View parent) {
        this.context = parent.getContext();
        this.parent = (ViewGroup) parent;
        this.targetView = parent.findViewById(targetViewId);
        this.index = this.parent.indexOfChild(targetView);
        this.targetViewLayoutParams = this.targetView.getLayoutParams();
        return this;
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

    public void setStatus(Status status) {
        ((Activity) context).runOnUiThread(() -> {
            checkConditions();

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
    private void checkConditions() {
        if (parent == null) throw new NullPointerException("Parent view cannot be null");
        if (targetView == null) throw new NullPointerException("Target view cannot be null");
        if (progressView == null) {
            progressView = new ProgressBar(context);
            progressView.setPadding(0, getPadding(), 0, getPadding());
        }
        if (failedView == null)
            failedView = LayoutInflater.from(context).inflate(R.layout.error_layout, null);

        this.failedView.setOnClickListener(this.errorClickListener);

        this.failedView.setLayoutParams(targetViewLayoutParams);
        this.progressView.setLayoutParams(targetViewLayoutParams);
    }

    private int getPadding() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, context.getResources().getDisplayMetrics());
    }

}
