package org.dodgybits.shuffle.web.server.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.dodgybits.shuffle.web.client.model.KeyValue;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class JdoUtils {
    private static PersistenceManagerFactory pmf =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private static ThreadLocal<PersistenceManager> threadLocalPm =
        new ThreadLocal<PersistenceManager>();

    public static PersistenceManager getPm() {
      PersistenceManager pm = threadLocalPm.get();
      if (pm == null) {
        pm = pmf.getPersistenceManager();
        threadLocalPm.set(pm);
      }
      return pm;
    }

    public static void closePm() {
      PersistenceManager pm = threadLocalPm.get();
      if (pm == null) {
        return;
      }
      if (!pm.isClosed()) {
        pm.close();
      }
      threadLocalPm.set(null);
    }

    public static <V> KeyValue<V> toKeyValue(Key key) {
        KeyValue<V> keyValue = null;
        if (key != null) {
            keyValue = new KeyValue<V>(KeyFactory.keyToString(key));
        }
        return keyValue;
    }

    public static <V> ArrayList<KeyValue<V>> toKeyValues(List<Key> keys) {
        ArrayList<KeyValue<V>> keyValues = new ArrayList<KeyValue<V>>();
        for (Key key : keys) {
            KeyValue<V> keyValue = toKeyValue(key);
            keyValues.add(keyValue);
        }
        return keyValues;
    }
    
    public static Key toKey(KeyValue<?> keyValue) {
        Key key = null;
        if (keyValue != null) {
            key = KeyFactory.stringToKey(keyValue.getValue());
        }
        return key;
    }

    public static <T> List<Key> toKeys(ArrayList<KeyValue<T>> keyValues) {
        List<Key> keys = new ArrayList<Key>();
        if (keyValues != null) {
            for (KeyValue<?> keyValue : keyValues) {
                keys.add(toKey(keyValue));
            }
        }
        return keys;
    }
    
}
