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

public class Project implements TracksEntity {
	private Id mLocalId = Id.NONE;
	private String mName;
	private Id mDefaultContextId = Id.NONE;
    private long mModifiedDate;
    private boolean mParallel;
    private boolean mArchived;
    private Id mTracksId = Id.NONE;
	private boolean mDeleted;
	private boolean mActive = true;
	

    private Project() {
    };
    

    public final Id getLocalId() {
        return mLocalId;
    }

    public final String getName() {
        return mName;
    }

    public final Id getDefaultContextId() {
        return mDefaultContextId;
    }
    
    public final long getModifiedDate() {
        return mModifiedDate;
    }

    public final boolean isParallel() {
        return mParallel;
    }

    public final boolean isArchived() {
        return mArchived;
    }
    
    public final Id getTracksId() {
        return mTracksId;
    }

    public final String getLocalName() {
        return mName;
    }
    
    @Override
    public final boolean isDeleted() {
        return mDeleted;
    }

    public final boolean isValid() {
        if (TextUtils.isEmpty(mName)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean isActive() {
        return mActive;
    }

    @Override
    public final String toString() {
        return String.format(
                "[Project id=%1$s name='%2$s' defaultContextId='%3$s' " +
                "parallel=%4$s archived=%5$s tracksId='%6$s' deleted=%7$s active=%8$s]",
                mLocalId, mName, mDefaultContextId,
                mParallel, mArchived, mTracksId, mDeleted, mActive);
    }
    
    public static Builder newBuilder() {
        return Builder.create();
    }


    public static class Builder implements EntityBuilder<Project> {

        private Builder() {
        }

        private Project result;

        private static Builder create() {
            Builder builder = new Builder();
            builder.result = new Project();
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
        
        public Id getDefaultContextId() {
            return result.mDefaultContextId;
        }

        public Builder setDefaultContextId(Id value) {
            assert value != null;
            result.mDefaultContextId = value;
            return this;
        }

        public long getModifiedDate() {
            return result.mModifiedDate;
        }

        public Builder setModifiedDate(long value) {
            result.mModifiedDate = value;
            return this;
        }

        public boolean isParallel() {
            return result.mParallel;
        }

        public Builder setParallel(boolean value) {
            result.mParallel = value;
            return this;
        }

        public boolean isArchived() {
            return result.mArchived;
        }

        public Builder setArchived(boolean value) {
            result.mArchived = value;
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
        
        public boolean isActive() {
            return result.mActive;
        }
        
        @Override
        public Builder setActive(boolean value) {
            result.mActive = value;
            return this;
        }


        public boolean isDeleted() {
            return result.mDeleted;
        }

        @Override
        public Builder setDeleted(boolean value) {
            result.mDeleted = value;
            return this;
        }

        public final boolean isInitialized() {
            return result.isValid();
        }

        public Project build() {
            if (result == null) {
                throw new IllegalStateException(
                        "build() has already been called on this Builder.");
            }
            Project returnMe = result;
            result = null;
            return returnMe;
        }
        
        public Builder mergeFrom(Project project) {
            setLocalId(project.mLocalId);
            setName(project.mName);
            setDefaultContextId(project.mDefaultContextId);
            setModifiedDate(project.mModifiedDate);
            setParallel(project.mParallel);
            setArchived(project.mArchived);
            setTracksId(project.mTracksId);
            setDeleted(project.mDeleted);
            setActive(project.mActive);
            return this;
        }

    }

}
