package org.dodgybits.shuffle.android.synchronisation.tracks.parsing;

import java.text.ParseException;

import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Context.Builder;
import org.dodgybits.shuffle.android.core.util.DateUtils;




public class ContextParser extends Parser<Context> {

	private Builder specificBuilder;

	public ContextParser(Analytics analytics) {
		super("context", analytics);
		
		appliers.put("name",
				new Applier(){
					@Override
					public boolean apply(String value) {
						
						specificBuilder.setName(value);
						return true;
					}
			
		});
		
		appliers.put("hide",
				new Applier(){
					@Override
					public boolean apply(String value) {
						boolean v = Boolean.parseBoolean(value);
						specificBuilder.setHidden(v);
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
	}

	@Override
	protected EntityBuilder<Context> createBuilder() {
		return specificBuilder = Context.newBuilder();
	}


}

