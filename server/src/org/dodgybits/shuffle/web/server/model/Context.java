package org.dodgybits.shuffle.web.server.model;

import static org.dodgybits.shuffle.web.server.persistence.JdoUtils.toKey;
import static org.dodgybits.shuffle.web.server.persistence.JdoUtils.toKeyValue;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.dodgybits.shuffle.web.client.model.ContextValue;
import org.dodgybits.shuffle.web.client.model.KeyValue;
import org.dodgybits.shuffle.web.client.model.ContextValue.Builder;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Context implements Serializable {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key mKey;

    @Persistent
    private User mUser;
    
    @Persistent
	private String mName;
	
	public final Key getKey() {
		return mKey;
	}

	public final User getUser() {
	    return mUser;
	}
	
	public final String getName() {
		return mName;
	}

    public final ContextValue toContextValue() {
        KeyValue<ContextValue> keyValue = toKeyValue(mKey); 

        Builder builder = new Builder();
        builder.setId(keyValue)
            .setName(mName);
        return builder.build();
    }
    
    public static final Context fromContextValue(User user, ContextValue value) {
        Context context = new Context();
        context.mKey = toKey(value.getId());
        context.mName = value.getName();
        context.mUser = user;
        return context;
    }
	
}
