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

import android.text.TextUtils;

public class Project {
	public Integer id;
	public final String name;
	public final Integer defaultContextId;
	public final boolean archived;
	
	public Project(Integer id, String name, Integer defaultContextId, boolean archived) {
		this.id = id;
		this.name = name;
		this.defaultContextId = defaultContextId;
		this.archived = archived;
	}
	
	public Project(String name, Integer defaultContextId, boolean archived) {
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

	
}
