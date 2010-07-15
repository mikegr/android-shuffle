package org.dodgybits.shuffle.android.synchronisation.tracks.parsing;

import java.text.ParseException;

import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Project.Builder;
import org.dodgybits.shuffle.android.core.util.DateUtils;

import android.text.TextUtils;
import android.util.Log;

public class ProjectParser extends Parser<Project> {

	private Builder specificBuilder;
	private IContextLookup mContextLookup;
	public ProjectParser(IContextLookup contextLookup, Analytics analytics) {
		super("project", analytics);
		mContextLookup = contextLookup;
		
		appliers.put("name",
				new Applier(){
					@Override
					public boolean apply(String value) {
						
						specificBuilder.setName(value);
						return true;
					}
			
		});
		appliers.put("state",
				new Applier(){
					@Override
					public boolean apply(String value) {
						
						String v = value.toLowerCase();
						
						if(v.equals("completed")) {
							Log.d("projectparser",v);
							specificBuilder.setHidden(true);
							return true;
						}
						
						if(v.equals("hidden")) {
							specificBuilder.setHidden(true);
							return true;
						}
						
						if(v.equals("active")) {
							specificBuilder.setHidden(false);
							return true;
						}
						
						return false;
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
		appliers.put("default-context-id",
				new Applier(){
					@Override
					public boolean apply(String value) {
                        if (!TextUtils.isEmpty(value)) {
                            Id tracksId = Id.create(Long.parseLong(value));
                            Id defaultContextId = mContextLookup.findContextIdByTracksId(tracksId);
                            if (defaultContextId.isInitialised()) {
                                specificBuilder.setDefaultContextId(defaultContextId);
                            }
                        }
                        return true;
					}
		});
	
	}
	@Override
	protected EntityBuilder<Project> createBuilder() {
		return specificBuilder = Project.newBuilder();
	}

}
