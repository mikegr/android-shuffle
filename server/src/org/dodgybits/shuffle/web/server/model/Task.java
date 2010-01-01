package org.dodgybits.shuffle.web.server.model;

import static org.dodgybits.shuffle.web.server.persistence.JdoUtils.toKey;
import static org.dodgybits.shuffle.web.server.persistence.JdoUtils.toKeys;
import static org.dodgybits.shuffle.web.server.persistence.JdoUtils.toKeyValue;
import static org.dodgybits.shuffle.web.server.persistence.JdoUtils.toKeyValues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.dodgybits.shuffle.web.client.model.ContextValue;
import org.dodgybits.shuffle.web.client.model.KeyValue;
import org.dodgybits.shuffle.web.client.model.ProjectValue;
import org.dodgybits.shuffle.web.client.model.TaskValue;
import org.dodgybits.shuffle.web.client.model.TaskValue.Builder;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Task implements Serializable {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key mKey;

    @Persistent
    private String mTitle;

    @Persistent
    private Text mDetails;

    @Persistent
    private Key mProjectKey;
    
    @Persistent
    private List<Key> mContextKeys;
    
    @Persistent
    private Date mDueDate;

    
    public final Key getKey() {
        return mKey;
    }

	public final String getTitle() {
		return mTitle;
	}

	public final Text getDetails() {
		return mDetails;
	}

	public final Key getProjectId() {
		return mProjectKey;
	}

	public final List<Key> getContextIds() {
		return mContextKeys;
	}

	public final Date getDueDate() {
		return mDueDate;
	}
	
	public final TaskValue toTaskValue() {
	    KeyValue<TaskValue> keyValue = toKeyValue(mKey); 
        KeyValue<ProjectValue> projectKey = toKeyValue(mProjectKey);
        ArrayList<KeyValue<ContextValue>> contextKeys = toKeyValues(mContextKeys);

        Builder builder = new Builder();
        builder.setId(keyValue)
	        .setTitle(mTitle)
	        .setDetails(mDetails == null ? null : mDetails.getValue())
	        .setProjectId(projectKey)
	        .setContextIds(contextKeys)
	        .setDueDate(mDueDate);
        return builder.build();
	}
	
    public static final Task fromTaskValue(TaskValue value) {
        Task task = new Task();
        task.mKey = toKey(value.getId());
        task.mTitle = value.getTitle();
        task.mDetails = new Text(value.getDetails());
        task.mProjectKey = toKey(value.getProjectId());
        task.mContextKeys = toKeys(value.getContextIds());
        task.mDueDate = value.getDueDate();
        return task;
    }
	
}
