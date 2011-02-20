package org.dodgybits.shuffle.android.list.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import roboguice.event.EventManager;
import roboguice.inject.InjectorProvider;

public class ButtonBar extends LinearLayout implements View.OnClickListener {
    private Button mAddItemButton;
    private Button mOtherButton;
    private ImageButton mFilterButton;

    @Inject private EventManager mEventManager;

    public ButtonBar(Context context) {
        super(context);
        init(context);
    }

    public ButtonBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater vi = (LayoutInflater)context.
            getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.button_bar, this, true);

        // wire up this component
        ((InjectorProvider)context).getInjector().injectMembers(this);

        mAddItemButton = (Button)findViewById(R.id.add_item_button);
        Drawable addIcon = getResources().getDrawable(android.R.drawable.ic_menu_add);
        addIcon.setBounds(0, 0, 24, 24);
        mAddItemButton.setCompoundDrawables(addIcon, null, null, null);
        mAddItemButton.setOnClickListener(this);

        mOtherButton = (Button)findViewById(R.id.other_button);
        mOtherButton.setOnClickListener(this);

        mFilterButton = (ImageButton)findViewById(R.id.filter_button);
        mFilterButton.setOnClickListener(this);

    }

    public Button getAddItemButton() {
        return mAddItemButton;
    }

    public Button getOtherButton() {
        return mOtherButton;
    }

    public ImageButton getFilterButton() {
        return mFilterButton;
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add_item_button:
                mEventManager.fire(getContext(), new AddItemButtonClickEvent());
                break;

            case R.id.other_button:
                mEventManager.fire(getContext(), new OtherButtonClickEvent());
                break;

            case R.id.filter_button:
                mEventManager.fire(getContext(), new FilterButtonClickEvent());
                break;
        }
    }

    public class AddItemButtonClickEvent {};
    public class OtherButtonClickEvent {};
    public class FilterButtonClickEvent {};

}
