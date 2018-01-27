package com.oblivionburn.nlp;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import java.lang.reflect.Field;

public class LiteText extends AppCompatEditText
{
    private final Context context;

    boolean canPaste()
    {
        return false;
    }

    @Override
    public boolean isSuggestionsEnabled()
    {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                this.setInsertionDisabled();
                break;
            case MotionEvent.ACTION_UP:
                this.performClick();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick()
    {
        super.performClick();
        return true;
    }

    public LiteText(Context context)
    {
        super(context);
        this.context = context;
        init();
    }

    public LiteText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        init();
    }

    public LiteText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init()
    {
        this.setCustomSelectionActionModeCallback(new ActionModeCallbackInterceptor());
        this.setLongClickable(false);
    }

    private void setInsertionDisabled()
    {
        try
        {
            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editorObject = editorField.get(this);

            Class editorClass = Class.forName("android.widget.Editor");
            Field mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled");
            mInsertionControllerEnabledField.setAccessible(true);
            mInsertionControllerEnabledField.set(editorObject, false);
        }
        catch (Exception ignored)
        {
            // ignore exception here
        }
    }

    private class ActionModeCallbackInterceptor implements ActionMode.Callback
    {
        private final String TAG = LiteText.class.getSimpleName();

        public boolean onCreateActionMode(ActionMode mode, Menu menu) { return false; }
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) { return false; }
        public void onDestroyActionMode(ActionMode mode) {}
    }
}
