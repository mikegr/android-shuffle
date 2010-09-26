/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.shuffle.android.core.model;

import org.dodgybits.shuffle.android.synchronisation.tracks.model.TracksEntity;

import android.text.TextUtils;


public class Context implements TracksEntity {
    private Id mLocalId = Id.NONE;
    private String mName;
    private int mColourIndex;
    private String mIconName;
    private long mModifiedDate;
    private boolean mDeleted;
    private Id mTracksId = Id.NONE;

    private Context() {
    };

    public final Id getLocalId() {
        return mLocalId;
    }

    public final String getName() {
        return mName;
    }

    public final int getColourIndex() {
        return mColourIndex;
    }
    
    public final String getIconName() {
        return mIconName;
    }
    
    public final long getModifiedDate() {
        return mModifiedDate;
    }

    public final Id getTracksId() {
        return mTracksId;
    }

    public final String getLocalName() {
        return mName;
    }

    @Override
    public boolean isDeleted() {
        return mDeleted;
    }
    
    public final boolean isInitialized() {
        if (TextUtils.isEmpty(mName)) {
            return false;
        }
        return true;
    }
    
    @Override
    public final String toString() {
        return String.format(
                "[Context id=%1$s name='%2$s' colourIndex='%3$s' " +
                "iconName=%4$s tracksId='%5$s' deleted=%6$s]",
                mLocalId, mName, mColourIndex,
                mIconName, mTracksId, mDeleted);
    }
    
    public static Builder newBuilder() {
        return Builder.create();
    }

    

    public static class Builder implements EntityBuilder<Context> {

        private Builder() {
        }

        private Context result;

        private static Builder create() {
            Builder builder = new Builder();
            builder.result = new Context();
            return builder;
        }

        public Id getLocalId() {
            return result.mLocalId;
        }

        public Builder setLocalId(Id value) {
            assert value != null;
            result.mLocalId = value;
            return this;
        }

        public String getName() {
            return result.mName;
        }

        public Builder setName(String value) {
            result.mName = value;
            return this;
        }
        
        public int getColourIndex() {
            return result.mColourIndex;
        }

        public Builder setColourIndex(int value) {
            result.mColourIndex = value;
            return this;
        }

        public String getIconName() {
            return result.mIconName;
        }

        public Builder setIconName(String value) {
            result.mIconName = value;
            return this;
        }
        
        public long getModifiedDate() {
            return result.mModifiedDate;
        }

        public Builder setModifiedDate(long value) {
            result.mModifiedDate = value;
            return this;
        }

        public Id getTracksId() {
            return result.mTracksId;
        }

        public Builder setTracksId(Id value) {
            assert value != null;
            result.mTracksId = value;
            return this;
        }

        public final boolean isInitialized() {
            return result.isInitialized();
        }

        public Context build() {
            if (result == null) {
                throw new IllegalStateException(
                        "build() has already been called on this Builder.");
            }
            Context returnMe = result;
            result = null;
            return returnMe;
        }
        
        public Builder mergeFrom(Context context) {
            setLocalId(context.mLocalId);
            setName(context.mName);
            setColourIndex(context.mColourIndex);
            setIconName(context.mIconName);
            setModifiedDate(context.mModifiedDate);
            setTracksId(context.mTracksId);
            setDeleted(context.mDeleted);
            return this;
        }

		@Override
		public EntityBuilder<Context> setDeleted(boolean value) {
			result.mDeleted = value;
			return this;
		}

    }

}
