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
import org.dodgybits.shuffle.web.client.model.ProjectValue;
import org.dodgybits.shuffle.web.client.model.ProjectValue.Builder;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Project implements Serializable {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key mKey;

    @Persistent
    private User mUser;
    
    @Persistent
    private String mName;
    
    @Persistent
    private Key mDefaultContextKey;
    
    
    public final Key getKey() {
        return mKey;
    }
    
    public final User getUser() {
        return mUser;
    }

    public final String getName() {
        return mName;
    }

    public final Key getDefaultContextKey() {
        return mDefaultContextKey;
    }
    
    public final ProjectValue toProjectValue() {
        KeyValue<ProjectValue> keyValue = toKeyValue(mKey); 
        KeyValue<ContextValue> defaultContextKey = toKeyValue(mDefaultContextKey);

        Builder builder = new Builder();
        builder.setId(keyValue)
            .setName(mName)
            .setDefaultContextId(defaultContextKey);
        return builder.build();
    }

    public static final Project fromProjectValue(User user, ProjectValue value) {
        Project project = new Project();
        project.mKey = toKey(value.getId());
        project.mName = value.getName();
        project.mDefaultContextKey = toKey(value.getDefaultContextId());
        project.mUser = user;
        return project;
    }
    
}

