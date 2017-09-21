package br.com.aaascp.gerenciadordepedidos.presentation.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
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

public class ValueLabelView extends ConstraintLayout {

    @BindView(R.id.value_label_view_icon)
    ImageView iconView;

    @BindView(R.id.value_label_view_value)
    TextView valueView;

    @BindView(R.id.value_label_view_label)
    TextView labelView;

    private String value;
    private String label;
    private Drawable icon;
    private Drawable background;
    private int color;

    public ValueLabelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        extractAttributes(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.view_value_label, this, true);

        ButterKnife.bind(this, view);

        setAttributes();
    }

    public void setValue(String value) {
        valueView.setText(value);
    }

    private void extractAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ValueLabelView,
                0, 0);

        try {
            value = a.getString(R.styleable.ValueLabelView_value);
            label = a.getString(R.styleable.ValueLabelView_label);
            icon = a.getDrawable(R.styleable.ValueLabelView_icon);
            background = a.getDrawable(R.styleable.ValueLabelView_backgroundImage);
            color = a.getColor(
                    R.styleable.ValueLabelView_color,
                    ContextCompat.getColor(
                            context,
                            R.color.primary_text));

        } finally {
            a.recycle();
        }
    }

    private void setAttributes() {
        setValue();
        setBackground();
        labelView.setText(label);
        labelView.setTextColor(color);
        valueView.setTextColor(color);
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

    private void setBackground() {
        if (background != null) {
            setBackground(background);
        }
    }
}
