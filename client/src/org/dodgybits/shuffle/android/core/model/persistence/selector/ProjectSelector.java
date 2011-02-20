package org.dodgybits.shuffle.android.core.model.persistence.selector;

import android.net.Uri;
import org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;

import java.util.List;

public class ProjectSelector extends AbstractEntitySelector {
    private static final String cTag = "ProjectSelector";

    private ProjectSelector() {
    }

    @Override
    public Uri getContentUri() {
        return ProjectProvider.Projects.CONTENT_URI;
    }


    @Override
    protected List<String> getSelectionExpressions(android.content.Context context) {
        List<String> expressions = super.getSelectionExpressions(context);

        addFlagExpression(expressions, AbstractCollectionProvider.ShuffleTable.ACTIVE, mActive);
        addFlagExpression(expressions, AbstractCollectionProvider.ShuffleTable.DELETED, mDeleted);

        return expressions;
    }

    public final String[] getSelectionArgs() {
        return null;
    }

    @Override
    public Builder builderFrom() {
        return newBuilder().mergeFrom(this);
    }

    @Override
    public final String toString() {
        return String.format(
                "[ProjectSelector sortOrder=%1$s active=%2$s deleted=%3$s]",
                mSortOrder, mActive, mDeleted);
    }

    public static Builder newBuilder() {
        return Builder.create();
    }


    public static class Builder extends AbstractBuilder<ProjectSelector> {

        private Builder() {
        }

        private static Builder create() {
            Builder builder = new Builder();
            builder.mResult = new ProjectSelector();
            return builder;
        }

        public Builder mergeFrom(ProjectSelector query) {
            super.mergeFrom(query);
            return this;
        }
    }
}
