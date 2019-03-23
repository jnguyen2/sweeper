package edu.fandm.jnguyen.sweeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.ArrayList;

public class ButtonGrid extends GridLayout {

    private Integer buttonCount;
    private ArrayList<Button> buttons = new ArrayList<>();

    public ButtonGrid(Context context) {
        super(context);
    }

    public ButtonGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ButtonGrid);
        buttonCount = array.getInteger(R.styleable.ButtonGrid_buttonCount, 0);
        array.recycle();
        addButtons(context);
    }

    private void addButtons(Context context) {
        for (int i = 0; i < buttonCount; i++) {
            Button button = new Button(context);
            buttons.add(button);
            addView(button);
        }
    }

    public Button getButton(Integer index) {
        return buttons.get(index);
    }
}
