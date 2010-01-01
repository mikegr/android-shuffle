package org.dodgybits.shuffle.web.client.model;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ValueSelection<T> implements Serializable {
    static enum SelectionType {
        All, Some, None
    }
    
    private SelectionType mType;
    private ArrayList<KeyValue<T>> mKeyValues;
    
    @SuppressWarnings("unused")
    private ValueSelection() {
        // required for GWT serialization
    }

    private ValueSelection(SelectionType type) {
        mType = type;
    }
    
    public ValueSelection(ArrayList<KeyValue<T>> keyValues) {
        if (keyValues == null || keyValues.isEmpty()) {
            mType = SelectionType.All;
        } else {
            mType = SelectionType.Some;
            mKeyValues = keyValues;
        }
    }
    
    public ValueSelection<T> createNoneSelection() {
        return new ValueSelection<T>(SelectionType.None);
    }

    public ValueSelection<T> createAllSelection() {
        return new ValueSelection<T>(SelectionType.All);
    }
    
    public boolean isNone() {
        return mType == SelectionType.None;
    }
    
    public boolean isAll() {
        return mType == SelectionType.All;
    }
    
    public ArrayList<KeyValue<T>> getKeyValues() {
        return mKeyValues;
    }
    
    @Override
    public String toString() {
        String result;
        switch (mType) {
            case All:
                result="All";
                break;
            case None:
                result="None";
                break;
             default:
                 result = mKeyValues.toString();
                 break;
        }
        return "[ValueSelection " + result + "]";
    }
    
}
