package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class HelpActivity extends Activity {
    private static final String cTag = "HelpActivity";
    public static final String cHelpPage = "helpPage";
    
	private Spinner mHelpSpinner;
	private TextView mHelpContent;
	private TableLayout mShortcuts;
    
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        setContentView(R.layout.help);
        
        mHelpSpinner = (Spinner) findViewById(R.id.help_screen);
        mHelpContent = (TextView) findViewById(R.id.help_text);
        mShortcuts = (TableLayout) findViewById(R.id.help_shortcuts);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.help_screens,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHelpSpinner.setAdapter(adapter);
        mHelpSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	public void onNothingSelected(AdapterView arg0) {
        		// do nothing
        	}
        	
        	public void onItemSelected(AdapterView parent, View v,
        			int position, long id) {
        		int resId = HelpActivity.this.getResources().getIdentifier("help" + position, "string", "org.dodgybits.android.shuffle");
        		mHelpContent.setText(HelpActivity.this.getText(resId));
    			mShortcuts.setVisibility(position == 7 ? View.VISIBLE : View.GONE);
        	}
        });
        
        setSelectionFromBundle(getIntent().getExtras());
	}
	
	private void setSelectionFromBundle(Bundle bundle) {
        int position = 0;
        if (bundle != null) {
        	position = bundle.getInt(cHelpPage, 0);
        }
        mHelpSpinner.setSelection(position);
	}
		
}
