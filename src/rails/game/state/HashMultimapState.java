package rails.game.state;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * A stateful version of a HashMultimap
 */

public final class HashMultimapState<K,V> extends MultimapState<K,V> implements Iterable<V> {
    
    private final HashMultimap<K,V> map = HashMultimap.create();

    private HashMultimapState(Item parent, String id) {
        super(parent, id);
    }
    
    /** 
     * Creates an empty HashMultimapState 
     */
    public static <K,V> HashMultimapState<K,V> create(Item parent, String id){
        return new HashMultimapState<K,V>(parent ,id);
    }
    
    /**
     * Stores a key-value pair in the multimap
     * @param key key to store
     * @param value value to store
     * @return true if key-value pair is added, or false if the key value pair already exists
     */
    
    public boolean put(K key, V value) {
        if (map.containsEntry(key, value)) return false;
        new MultimapChange<K,V>(this, key, value, true);
        return true;
    }
    
    public ImmutableSet<V> get(K key) {
        return ImmutableSet.copyOf(map.get(key));
    }
    
    public boolean remove(K key, V value) {
        if (!map.containsEntry(key, value)) return false;
        new MultimapChange<K,V>(this, key, value, false);
        return true;
    }
    
    public Set<V> removeAll(K key) {
        return map.removeAll(key);
    }
    
    public boolean containsEntry(K key, V value) {
        return map.containsEntry(key, value);
    }
    
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsValue(V value) {
        return map.containsValue(value);
    }
    
    public int size() {
        return map.size();
    }
    
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    public ImmutableSet<K> keySet() {
        return ImmutableSet.copyOf(map.keySet());
    }

    public ImmutableSet<V> values() {
        return ImmutableSet.copyOf(map.values());
    }
    
    public Iterator<V> iterator() {
        return map.values().iterator();
    }
    
    @Override
    public String observerText() {
        return map.toString();
    }

    void change(K key, V value, boolean addToMap) {
        if (addToMap) {
            map.put(key, value);
        } else {
            map.remove(key, value);
        }
    }
}