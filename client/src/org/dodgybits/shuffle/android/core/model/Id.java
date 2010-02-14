package org.dodgybits.shuffle.android.core.model;

public final class Id {

    private final long mId;
    
    public static final Id NONE = new Id(0L);
    
    private Id(long id) {
        mId = id;
    }
    
    public long getId() {
        return mId;
    }
    
    public boolean isInitialised() {
        return mId != 0L;
    }
    
    @Override
    public String toString() {
        return isInitialised() ? String.valueOf(mId) : "";
    }
    
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Id) {
            result = ((Id)o).mId == mId;
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return (int)mId;
    }
    
    public static Id create(long id) {
        Id result = NONE;
        if (id != 0L) {
            result = new Id(id);
        }
        return result;
    }
    
}
