package org.dodgybits.shuffle.web.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ContextValue implements Serializable {
	private KeyValue<ContextValue> mKeyValue;
	private String mName;
	
	@SuppressWarnings("unused")
	private ContextValue() {
		// required for GWT serialization
	}
	
	public ContextValue(KeyValue<ContextValue> id, String name) {
		mKeyValue = id;
		mName = name;
	}

	public final KeyValue<ContextValue> getId() {
		return mKeyValue;
	}

	public final String getName() {
		return mName;
	}

	public static final class Builder {
	    private KeyValue<ContextValue> mKeyValue;
	    private String mName;
	    
	    public Builder setId(KeyValue<ContextValue> id) {
	        mKeyValue = id;
	        return this;
	    }

        public Builder setName(String name) {
            mName = name;
            return this;
        }
        
        public ContextValue build() {
            return new ContextValue(mKeyValue, mName);
        }
	
	}
	
}
