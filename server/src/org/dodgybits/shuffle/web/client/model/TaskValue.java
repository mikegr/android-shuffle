package org.dodgybits.shuffle.web.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("serial")
public class TaskValue implements Serializable {
	private KeyValue<TaskValue> mKeyValue;
	private String mTitle;
	private String mDetails;
	private KeyValue<ProjectValue> mProjectId;
	private ArrayList<KeyValue<ContextValue>> mContextIds;
	private Date mDueDate;
	
	@SuppressWarnings("unused")
	private TaskValue() {
		// required for GWT serialization
	}
	
	public TaskValue(
			KeyValue<TaskValue> id, 
			String title, String details,
			KeyValue<ProjectValue> projectId, ArrayList<KeyValue<ContextValue>> contextIds, 
			Date dueDate) {
		mKeyValue = id;
		mTitle = title;
		mDetails = details;
		mProjectId = projectId;
		mContextIds = contextIds;
		mDueDate = dueDate;
	}

	public final KeyValue<TaskValue> getId() {
		return mKeyValue;
	}

	public final String getTitle() {
		return mTitle;
	}

	public final String getDetails() {
		return mDetails;
	}

	public final KeyValue<ProjectValue> getProjectId() {
		return mProjectId;
	}

	public final ArrayList<KeyValue<ContextValue>> getContextIds() {
		return mContextIds;
	}

	public final Date getDueDate() {
		return mDueDate;
	}
	
    public static final class Builder {
        private KeyValue<TaskValue> mKeyValue;
        private String mTitle;
        private String mDetails;
        private KeyValue<ProjectValue> mProjectId;
        private ArrayList<KeyValue<ContextValue>> mContextIds;
        private Date mDueDate;
        
        public Builder setId(KeyValue<TaskValue> id) {
            mKeyValue = id;
            return this;
        }

        public final Builder setKeyValue(KeyValue<TaskValue> keyValue) {
            mKeyValue = keyValue;
            return this;
        }

        public final Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public final Builder setDetails(String details) {
            mDetails = details;
            return this;
        }

        public final Builder setProjectId(KeyValue<ProjectValue> projectId) {
            mProjectId = projectId;
            return this;
        }

        public final Builder setContextIds(ArrayList<KeyValue<ContextValue>> contextIds) {
            mContextIds = contextIds;
            return this;
        }

        public final Builder setDueDate(Date dueDate) {
            mDueDate = dueDate;
            return this;
        }

        public TaskValue build() {
            return new TaskValue(mKeyValue, mTitle, mDetails, 
                    mProjectId, mContextIds, mDueDate);
        }
        
    }
	
}
