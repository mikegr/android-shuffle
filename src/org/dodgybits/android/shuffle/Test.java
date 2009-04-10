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

package org.dodgybits.android.shuffle;

import java.util.Date;

import org.dodgybits.android.shuffle.model.Context;
import org.dodgybits.android.shuffle.model.Project;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.model.Context.Icon;
import org.dodgybits.android.shuffle.view.TaskView;
import org.xmlpull.v1.XmlPullParser;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * Creates mock objects for the sake of testing.
 */
public class Test {
	private static final String cTag = "Test";
	
	private static Context[] sContexts = {
			new Context("Home", 3, Icon.NONE),
			new Context("Work", 4, Icon.NONE),
			new Context("Town", 1, Icon.NONE),
			new Context("Online", 6, Icon.NONE),
	};
	
	private static Project[] sProjects = {
			new Project("Android Project", null, false),
			new Project("Sell car", null, false),
			new Project("Reading", null, false),
	};
	
	private static Task[] sTasks = {
			new Task("Make it go", "Do lots of stuff", sContexts[0], sProjects[0], new Date(), null, null, 0, false),
			new Task("Create ad", "Do lots of stuff", sContexts[1], sProjects[1], new Date(), null, null, 1, false),
			new Task("Create udder", "Do lots of stuff", sContexts[3], sProjects[1], new Date(), null, null, 0, true),
			new Task("Fooled by Randomness", null, null, sProjects[2], new Date(), null, null, 0, true),
			new Task("Life and Limb", "Do lots of stuff", sContexts[2], sProjects[2], new Date(), null, null, 0, true),
	};
	
	public static ListAdapter createTaskListAdapter(android.content.Context context) {
		return new ArrayAdapter<Task>(context,
                android.R.layout.simple_list_item_1, sTasks) {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Log.d("Test", "getView position=" + position + ". Old view=" + convertView);
				Task task = getItem(position);
				TaskView taskView;
				if (convertView instanceof TaskView) {
					taskView = (TaskView) convertView;
				} else {
					taskView = new TaskView(parent.getContext());
				}
				taskView.updateView(task);
				return taskView;
			}
			
		};
	}

	public static ArrayAdapter<Context> createContextAdapter(android.content.Context context) {
        return new ArrayAdapter<Context>(context,
                android.R.layout.simple_list_item_1, sContexts);
	}

	public static ArrayAdapter<Project> createProjectAdapter(android.content.Context context) {
        return new ArrayAdapter<Project>(context,
                android.R.layout.simple_list_item_1, sProjects);
	}
	
	public static void dumpResources(android.content.Context context, String applicationName, int layoutId) {
		try {
	        Resources r2; 
	        r2 = context.getPackageManager().getResourcesForApplication(applicationName);
	        int resId = layoutId;
	        StringBuilder builder = new StringBuilder();
	        while (true) {
	        	XmlPullParser xpp;
	        	try {
	        		xpp = r2.getXml(resId);
	        	} catch (NotFoundException e) {
	        		Log.d(cTag, "Finished");
	        		break;
	        	}
	            boolean done = false;
	            boolean firstTag = true;
	            boolean startDocPrint = true;
	            int indent = 0;
	            for (int eventType = xpp.getEventType(); !done; eventType = xpp.next()) {
		            switch (eventType) {
		            case XmlPullParser.START_DOCUMENT:
		            	if (startDocPrint) {
			            	Log.d(cTag, "Start document " + context.getResources().getResourceName(resId) + '(' + resId + ')');
			            	startDocPrint = false;
		            	}
		            	break;
		            case XmlPullParser.END_DOCUMENT:
		                Log.d(cTag, builder.toString());
		                builder.setLength(0);
		                Log.d(cTag, "End document"); 
		            	done = true;
		            	break;
		            case XmlPullParser.START_TAG:
		                if (firstTag) {
			                builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); 
		                }
		                int count = xpp.getAttributeCount(); 
		                builder.append('<').append(xpp.getName()); 
		                if (firstTag) {
		                	// dodgy hack!!
		                	builder.append(" xmlns:android=\"http://schemas.android.com/apk/res/android\" ");
		                	firstTag = false;
		                }
		                for (int i = 0 ; i <count; ++i) { 
		                    String attrNS = xpp.getAttributeNamespace(i); 
		                    String attrName = xpp.getAttributeName(i); 
		                    if (attrNS != null) { 
		                    	int pos = attrNS.lastIndexOf('/');
		                    	if (pos > -1) attrNS = attrNS.substring(pos + 1);
		                    	attrName = attrNS + ":" + attrName; 
		                    } 
		                    String value = xpp.getAttributeValue(i); 
		                    builder.append(' ').append(attrName).append("=\"").append(value).append('"');
		                } 
		                builder.append(">");
		                indent++;
		                break;
		            case XmlPullParser.END_TAG:
		                builder.append("</").append(xpp.getName()).append(">");
		            	indent--;
		                break;
		            case XmlPullParser.TEXT:
		               	builder.append(xpp.getText().trim());
		               	break;
		           	default:
		                Log.v(cTag, "Ignored event: " + eventType);
		           		break;
		            }
	            }
	            resId++; 
	        } 
	    } catch (Exception e) {
	    	Log.e(cTag, "Failed to dump resources", e);
	    }
	}
	
}
