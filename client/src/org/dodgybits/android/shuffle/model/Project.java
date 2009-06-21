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

import org.dodgybits.shuffle.dto.ShuffleProtos.Project.Builder;

import android.text.TextUtils;

public class Project {
	public Long id;
	public final String name;
	public final Long defaultContextId;
	public final boolean archived;
	
	public Project(Long id, String name, Long defaultContextId, boolean archived) {
		this.id = id;
		this.name = name;
		this.defaultContextId = defaultContextId;
		this.archived = archived;
	}
	
	public Project(String name, Long defaultContextId, boolean archived) {
		this(null, name, defaultContextId, archived);
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
		builder.setId(id).setName(name);
		if (defaultContextId != null) {
			builder.setDefaultContextId(defaultContextId);
		}
		return builder.build();
	}
	
	public static Project buildFromDto(
			org.dodgybits.shuffle.dto.ShuffleProtos.Project dto) {
		return new Project(
				dto.getId(),
				dto.getName(),
				dto.getDefaultContextId(),
				false);
	}
	
}
