package org.yunghegel.gdx.bmesh.attribute.type;

import org.yunghegel.gdx.bmesh.attribute.BMeshData;
import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;

public class IntAttribute<E extends Element> extends MeshAttribute<E, int[]> {
    public IntAttribute(String name) {
        super(name);
    }


    public int get(E element) {
        return data[element.getIndex()];
    }

    public void set(E element, int value) {
        data[element.getIndex()] = value;
    }


    @Override
    public boolean equals(E a, E b) {
        return data[a.getIndex()] == data[b.getIndex()];
    }


    @Override
    protected int[] alloc(int size) {
        return new int[size];
    }

    public static <E extends Element> IntAttribute<E> get(String name, BMeshData<E> meshData) {
        return (IntAttribute<E>) getAttribute(name, meshData, int[].class);
    }
}