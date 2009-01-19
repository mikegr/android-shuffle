package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.BindingUtils;
import org.dodgybits.android.shuffle.util.MenuUtils;
import org.dodgybits.android.shuffle.view.TaskView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public abstract class AbstractTaskListActivity extends AbstractListActivity<Task> {

	private static final String cTag = "AbstractTaskListActivity";
	
	@Override
	protected void onResume() {
		super.onResume();
        setTitle(createTitle());
	}

	abstract CharSequence createTitle();
	
	@Override
	protected int getContentViewResId() {
		return R.layout.task_list;
	}

	@Override
	protected Uri getContentUri() {
		return Shuffle.Tasks.CONTENT_URI;
	}
	
	protected boolean showTaskContext() {
		return true;
	}
	
	@Override
	protected final String getItemName() {
		return getString(R.string.task_name);
	}
	
	@Override
	protected Cursor createItemQuery() {
		return managedQuery(getListContentUri(), Shuffle.Tasks.cExpandedProjection, null, null, null);
	}
	
	@Override
	protected ListAdapter createListAdapter(Cursor cursor) {
		ListAdapter adapter = new SimpleCursorAdapter(this,
						R.layout.task_view, cursor,
						new String[] { Shuffle.Tasks.DESCRIPTION },
						new int[] { R.id.description }) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Log.d(cTag, "getView position=" + position + ". Old view=" + convertView);
				Cursor cursor = (Cursor)getItem(position);
				Task task = readItem(cursor);
				TaskView taskView;
				if (convertView instanceof TaskView) {
					taskView = (TaskView) convertView;
				} else {
					taskView = new TaskView(parent.getContext(), showTaskContext());
				}
				boolean isSelected = false;
				taskView.updateView(task, isSelected);
				return taskView;
			}

		};
//		ListAdapter adapter = Test.createTaskListAdapter(this);
		return adapter;
	}

	@Override
    protected Task readItem(Cursor c) {
        return BindingUtils.readTask(c);
    }
    
    protected final void toggleComplete() {
    	Cursor c = (Cursor) getListAdapter().getItem(getSelectedItemPosition());
        BindingUtils.toggleTaskComplete(this, c, getListContentUri(), getSelectedItemId());
    }
    
    @Override
	protected boolean supportsViewAction() {
		return true;
	}
    
    @Override
    protected boolean isTaskList() {
    	return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        
        final boolean haveItems = getItemCount() > 0;

        // If there are any selected tasks we need to generate the actions that
        // can be performed on the current selection.  This will be a combination
        // of our own specific actions along with any extensions that can be
        // found.
        if (haveItems && getSelectedItemPosition() > -1) {
        	MenuUtils.addCompleteMenuItem(menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MenuUtils.COMPLETE_ID:
            toggleComplete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected Intent getClickIntent(Uri uri) {
    	return new Intent(Intent.ACTION_VIEW, uri);
    }

}

    