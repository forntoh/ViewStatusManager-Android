package com.forntoh.viewstatusmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class StatusManager {

    private final boolean isConstraintLayout;
    private Context context;

    private View failedView = null, progressView = null;
    private ViewGroup parent;
    private Map<Integer, View> idMap = new HashMap<>();
    private Map<Integer, Integer> indexMap = new HashMap<>();

    private String errorTitle = null, errorDesc = null;

    private StatusManager(@NonNull View parent) {
        this.context = parent.getContext();
        this.parent = (ViewGroup) parent;
        isConstraintLayout = parent instanceof ConstraintLayout;
    }

    public static StatusManager from(@NonNull View parent) {
        return new StatusManager(parent);
    }

    public void setStatus(@IdRes int targetViewId, Status status) {
        this.setStatus(targetViewId, status, null);
    }

    public void setStatus(@IdRes int targetViewId, Status status, View.OnClickListener onClickListener) {

        if (status == Status.PROGRESS && idMap.get(targetViewId) == null) {
            idMap.put(targetViewId, parent.findViewById(targetViewId));
            indexMap.put(targetViewId, parent.indexOfChild(parent.findViewById(targetViewId)));
        }

        View targetView = idMap.get(targetViewId);
        int index = indexMap.get(targetViewId);

        ((Activity) context).runOnUiThread(() -> {

            checkConditions(targetView, onClickListener);

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
                    if (errorDesc == null) errorDesc = context.getString(R.string.empty_sub);
                    break;
                default:
                    toAdd = failedView;
                    if (errorTitle == null) errorTitle = context.getString(R.string.error);
                    if (errorDesc == null) errorDesc = context.getString(R.string.error_sub);
                    break;
            }

            ViewGroup.LayoutParams layoutParams;

            if (isConstraintLayout) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) targetView.getLayoutParams();
                ConstraintLayout.LayoutParams newParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                toAdd.setLayoutParams(params);

                newParams.topToTop = params.topToTop;
                newParams.topToBottom = params.topToBottom;
                newParams.leftToLeft = params.leftToLeft;
                newParams.leftToRight = params.leftToRight;
                newParams.bottomToBottom = params.bottomToBottom;
                newParams.bottomToTop = params.bottomToTop;
                newParams.rightToRight = params.rightToRight;
                newParams.rightToLeft = params.rightToLeft;

                newParams.startToStart = params.startToStart;
                newParams.startToEnd = params.startToEnd;
                newParams.endToEnd = params.endToEnd;
                newParams.endToStart = params.endToStart;

                newParams.verticalBias = params.verticalBias;
                newParams.horizontalBias = params.horizontalBias;

                newParams.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin);

                layoutParams = newParams;
            } else layoutParams = targetView.getLayoutParams();

            parent.removeViewAt(index);
            parent.addView(toAdd, index, layoutParams);

            ((TextView) failedView.findViewById(R.id.place_title)).setText(errorTitle);
            ((TextView) failedView.findViewById(R.id.place_sub)).setText(errorDesc);
            ((TextView) failedView.findViewById(R.id.place_sub)).append(", Tap here to retry");
        });
    }

    @SuppressLint("InflateParams")
    private void checkConditions(View targetView, View.OnClickListener onClickListener) {
        if (parent == null) throw new NullPointerException("Parent view cannot be null");
        if (targetView == null) throw new NullPointerException("Target view cannot be null");

        failedView = LayoutInflater.from(context).inflate(R.layout.error_layout, null);
        failedView.setOnClickListener(onClickListener);
        failedView.setId(targetView.getId());

        progressView = new ProgressBar(context);
        progressView.setPadding(0, getPadding(), 0, getPadding());
        progressView.setId(targetView.getId());
    }

    private int getPadding() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, context.getResources().getDisplayMetrics());
    }

}
