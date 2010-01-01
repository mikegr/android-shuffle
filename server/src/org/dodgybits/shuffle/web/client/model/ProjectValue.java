package org.dodgybits.shuffle.web.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProjectValue implements Serializable {
	private KeyValue<ProjectValue> mKeyValue;
	private String mName;
	private KeyValue<ContextValue> mDefaultContextId;
	
	@SuppressWarnings("unused")
	private ProjectValue() {
		// required for GWT serialization
	}
	
	public ProjectValue(KeyValue<ProjectValue> id, String name, 
	        KeyValue<ContextValue> defaultContextId) {
		mKeyValue = id;
		mName = name;
		mDefaultContextId = defaultContextId;
	}

	public final KeyValue<ProjectValue> getId() {
		return mKeyValue;
	}

	public final String getName() {
		return mName;
	}

	public final KeyValue<ContextValue> getDefaultContextId() {
		return mDefaultContextId;
	}
	
    public static final class Builder {
        private KeyValue<ProjectValue> mKeyValue;
        private String mName;
        private KeyValue<ContextValue> mDefaultContextId;
        
        public Builder setId(KeyValue<ProjectValue> id) {
            mKeyValue = id;
            return this;
        }

        public Builder setName(String name) {
            mName = name;
            return this;
        }

        public Builder setDefaultContextId(KeyValue<ContextValue> id) {
            mDefaultContextId = id;
            return this;
        }

        public ProjectValue build() {
            return new ProjectValue(mKeyValue, mName, mDefaultContextId);
        }
    
    }
	
}
