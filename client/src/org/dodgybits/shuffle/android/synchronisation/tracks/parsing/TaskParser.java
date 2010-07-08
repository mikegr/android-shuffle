package org.dodgybits.shuffle.android.synchronisation.tracks.parsing;

import java.text.ParseException;


import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.Task.Builder;

import org.dodgybits.shuffle.android.core.util.DateUtils;

import android.text.TextUtils;

public class TaskParser extends Parser<Task> {

	private Builder specificBuilder;
	protected IContextLookup mContextLookup;
	protected IProjectLookup mProjectLookup;

	public TaskParser(IContextLookup contextLookup, IProjectLookup projectLookup) {
		super("todo");
		mContextLookup = contextLookup;
		mProjectLookup = projectLookup;
		
		
		appliers.put("description",
				new Applier(){
					@Override
					public boolean apply(String value) {
						
						specificBuilder.setDescription(value);
						return true;
					}
			
		});
		appliers.put("notes",
				new Applier(){
					@Override
					public boolean apply(String value) {
						
						specificBuilder.setDetails(value);
						return true;
					}
			
		});
		appliers.put("id",
				new Applier(){
					@Override
					public boolean apply(String value) {
				        Id tracksId = Id.create(Long.parseLong(value));
				        specificBuilder.setTracksId(tracksId);
                        return true;
					}
			
		});
		appliers.put("updated-at",
				new Applier(){
					@Override
					public boolean apply(String value) {
						 
                         long date;
							try {
								date = DateUtils.parseIso8601Date(value);
								specificBuilder.setModifiedDate(date);
							    
							    return true;
							} catch (ParseException e) {
								return false;
							}
					}
			
		});
		appliers.put("context-id",
				new Applier(){
					@Override
					public boolean apply(String value) {
                        if (!TextUtils.isEmpty(value)) {
                            Id tracksId = Id.create(Long.parseLong(value));
                            Id context = mContextLookup.findContextIdByTracksId(tracksId);
                            if (context.isInitialised()) {
                                specificBuilder.setContextId(context);
                            }
                        }
                        return true;
					}
		});
		appliers.put("project-id",
				new Applier(){
					@Override
					public boolean apply(String value) {
                        if (!TextUtils.isEmpty(value)) {
                            Id tracksId = Id.create(Long.parseLong(value));
                            Id project = mProjectLookup.findProjectIdByTracksId(tracksId);
                            if (project.isInitialised()) {
                                specificBuilder.setProjectId(project);
                            }
                        }
                        return true;
					}
		});
		appliers.put("created-at",
				new Applier(){
					@Override
					public boolean apply(String value) {
						 
                         if (!TextUtils.isEmpty(value)) {
							try {
								long created = DateUtils.parseIso8601Date(value);
								specificBuilder.setCreatedDate(created);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								return false;
							}
                             
                         }
                        return true;
					}
		});
		appliers.put("due",
				new Applier(){
			@Override
			public boolean apply(String value) {
				 
                 if (!TextUtils.isEmpty(value)) {
					try {
						long due = DateUtils.parseIso8601Date(value);
						specificBuilder.setDueDate(due);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						return false;
					}
                     
                 }
                return true;
			}
});
		appliers.put("show-from",
				new Applier(){
					@Override
					public boolean apply(String value) {
						 
                         if (!TextUtils.isEmpty(value)) {
							try {
								long showFrom = DateUtils.parseIso8601Date(value);
								specificBuilder.setStartDate(showFrom);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								return false;
							}
                             
                         }
                        return true;
					}
		});
	}

	@Override
	protected EntityBuilder<Task> createBuilder() {
		return specificBuilder = Task.newBuilder();
	}

}
