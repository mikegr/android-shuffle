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
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
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
    	toggleComplete(getSelectedItemPosition(), getSelectedItemId());
    }

    protected final void toggleComplete(int position, long id) {
    	Cursor c = (Cursor) getListAdapter().getItem(position);
        BindingUtils.toggleTaskComplete(this, c, getListContentUri(), id);
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
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, view, menuInfo);

		// ... add complete command.
    	MenuUtils.addCompleteMenuItem(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(cTag, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
	        case MenuUtils.COMPLETE_ID:
	            toggleComplete(info.position, info.id);
	            return true;
        }
        return super.onContextItemSelected(item);
    }	
    
    protected Intent getClickIntent(Uri uri) {
    	return new Intent(Intent.ACTION_VIEW, uri);
    }

}

    