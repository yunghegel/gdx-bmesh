package org.yunghegel.gdx.bmesh.util;

import org.yunghegel.gdx.bmesh.structure.Loop;

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
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public E next() {
        return mapFunc.apply(it.next());
    }
}
