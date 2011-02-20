package org.dodgybits.shuffle.android.core.model.persistence.selector;

import android.net.Uri;
import org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;

import java.util.List;

public class ContextSelector extends AbstractEntitySelector {
    private static final String cTag = "ContextSelector";

    private ContextSelector() {

    }

    @Override
    public Uri getContentUri() {
        return ContextProvider.Contexts.CONTENT_URI;
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
    public final String toString() {
        return String.format(
                "[ContextSelector sortOrder=%1$s active=%2$s deleted=%3$s]",
                mSortOrder, mActive, mDeleted);
    }

    @Override
    public Builder builderFrom() {
        return newBuilder().mergeFrom(this);
    }

    public static Builder newBuilder() {
        return Builder.create();
    }


    public static class Builder extends AbstractBuilder<ContextSelector> {

        private Builder() {
        }

        private static Builder create() {
            Builder builder = new Builder();
            builder.mResult = new ContextSelector();
            return builder;
        }
        public Builder mergeFrom(ContextSelector query) {
            super.mergeFrom(query);
            return this;
        }


    }
}
