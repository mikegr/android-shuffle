package org.dodgybits.shuffle.android.core.model.persistence.selector;

import org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider;

import java.util.List;

public class ContextSelector extends AbstractEntitySelector {
    private static final String cTag = "ContextSelector";

    private ContextSelector() {

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

    }
}
