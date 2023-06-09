package bmesh.util;

import bmesh.structure.Loop;

import java.util.Iterator;
import java.util.function.Function;

public final class LoopMapIterator<E> implements Iterator<E> {
    private final Iterator<Loop> it;
    private final Function<Loop, E> mapFunc;

    public LoopMapIterator(Iterator<Loop> it, Function<Loop, E> mapFunc) {
        this.it = it;
        this.mapFunc = mapFunc;
    }

    @Override
    public final boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public final E next() {
        return mapFunc.apply(it.next());
    }
}
