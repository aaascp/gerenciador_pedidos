package br.com.aaascp.gerenciadordepedidos.presentation.custom_views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.aaascp.gerenciadordepedidos.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andre on 18/09/17.
 */

public class DashboardButton extends ConstraintLayout {

    private static final int INVALID_RESOURCE = -1;

    @BindView(R.id.dashboard_button_icon)
    ImageView iconView;

    @BindView(R.id.dashboard_button_value)
    TextView valueView;

    @BindView(R.id.dashboard_button_label)
    TextView labelView;

    private String value;
    private String label;
    private Drawable icon;
    private int backgroundColor;

    public DashboardButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        extractAttributes(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.view_dashboard_button, this, true);

        ButterKnife.bind(this, view);

        setAttributes();
    }

    public void setValue(String value) {
        valueView.setText(value);
    }

    private void extractAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DashboardButton,
                0, 0);

        try {
            value = a.getString(R.styleable.DashboardButton_value);
            label = a.getString(R.styleable.DashboardButton_label);
            icon = a.getDrawable(R.styleable.DashboardButton_icon);
            backgroundColor = a.getColor(R.styleable.DashboardButton_backgroundColor, INVALID_RESOURCE);

        } finally {
            a.recycle();
        }
    }

    private void setAttributes() {
        setValue();
        setBackgroundColor();
        labelView.setText(label);
    }

    private void setValue() {
        if (icon != null) {
            iconView.setImageDrawable(icon);
            iconView.setVisibility(VISIBLE);
            valueView.setVisibility(GONE);
        } else {
            valueView.setText(value);
        }
    }

    private void setBackgroundColor() {
        if (backgroundColor == INVALID_RESOURCE) {
            return;
        }
//        int[][] states = new int[][] {
//                new int[] { android.R.attr.state_enabled}, // enabled
//                new int[] {-android.R.attr.state_enabled}, // disabled
//                new int[] {-android.R.attr.state_checked}, // unchecked
//                new int[] { android.R.attr.state_pressed}  // pressed
//        };
//
//        int[] colors = new int[] {
//                backgroundColor,
//                backgroundColor,
//                backgroundColor,
//                backgroundColor
//        };
//
//
//        setBackgroundTintList(
//                new ColorStateList(
//                        states,
//                        colors));
    }
}
