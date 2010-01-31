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

package org.dodgybits.android.shuffle.model;

import org.dodgybits.android.shuffle.service.Locator;
import org.dodgybits.shuffle.dto.ShuffleProtos.Project.Builder;

import android.text.TextUtils;

public class Project extends AbstractEntity implements TracksCompatible {
	public Long id;
	public final String name;
	public final Long defaultContextId;
	public final boolean archived;
    public Long tracksId;
    public final long modified;
    public final boolean isParallel;


    public Project(Long id, String name, 
            Long defaultContextId, boolean archived, 
            Long tracksId, long modified, boolean isParallel) {
		this.id = id;
		this.name = name;
		this.defaultContextId = defaultContextId;
		this.archived = archived;
        this.tracksId = tracksId;
        this.modified = modified;
        this.isParallel = isParallel;
    }
	
	public Project(String name, 
	        Long defaultContextId, boolean archived, 
            Long tracksId, long modified, boolean isParallel) {
		this(null, name, defaultContextId, archived, tracksId, modified, isParallel);
	}

    @Override
    public Long getTracksId() {
        return tracksId;
    }

    @Override
    public long getModified() {
        return modified;
    }

    @Override
    public String getLocalName() {
        return name;
    }

    @Override
    public void setTracksId(Long id) {
        tracksId = id;
    }

    @Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof Project) {
			result = TextUtils.equals(((Project)o).name, name );
		}
		return result;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	public org.dodgybits.shuffle.dto.ShuffleProtos.Project toDto() {
		Builder builder = org.dodgybits.shuffle.dto.ShuffleProtos.Project.newBuilder();
		builder
		    .setId(id)
		    .setName(name)
		    .setModified(toDate(modified))
		    .setParallel(isParallel);
		if (defaultContextId != null) {
			builder.setDefaultContextId(defaultContextId);
		}
        if (tracksId != null) {
            builder.setTracksId(tracksId);
        }
        
		return builder.build();
	}

	public static Project buildFromDto(
			org.dodgybits.shuffle.dto.ShuffleProtos.Project dto,
			Locator<Context> contextLocator
			) {
		// use locator to find default context since we may be mapping ids
		Long defaultContextId = null;
		if (dto.hasDefaultContextId()) {
			Context defaultContext = contextLocator.findById(dto.getDefaultContextId());
			if (defaultContext != null) {
				defaultContextId = defaultContext.id;
			}
		}
        Long tracksId = null;
        if (dto.hasTracksId()) {
            tracksId = dto.getTracksId();
        }
        long modified = fromDate(dto.getModified());
		
        boolean parallel = false;
		if (dto.hasParallel()) {
		    parallel = dto.getParallel();
		}
		
		return new Project(
				dto.getId(),
				dto.getName(),
				defaultContextId,
				false,
				tracksId,
				modified,
				parallel);
	}
}
