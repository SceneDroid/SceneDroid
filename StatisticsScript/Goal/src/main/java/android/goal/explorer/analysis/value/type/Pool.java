package android.goal.explorer.analysis.value.type;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Pool<T> {
    private final ConcurrentMap<T, T> pool = new ConcurrentHashMap<>();

    public T intern(T element) {
        T result = pool.putIfAbsent(element, element);
        if (result == null) {
            return element;
        } else {
            return result;
        }
    }

    public Set<T> getValues() {
        return new HashSet<T>(pool.values());
    }
}
