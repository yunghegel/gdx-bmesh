package org.yunghegel.gdx.bmesh.attribute.type;

import org.yunghegel.gdx.bmesh.attribute.BMeshData;
import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;

public class IntTupleAttribute<E extends Element> extends MeshAttribute<E, int[]> {
    public IntTupleAttribute(String name, int components) {
        super(name, components);
    }


    public int getComponent(E element, int component) {
        return data[indexOf(element, component)];
    }

    public void setComponent(E element, int component, int value) {
        data[indexOf(element, component)] = value;
    }


    public void setValues(E element, int... values) {
        if(values.length != numComponents)
            throw new IllegalArgumentException("Number of values does not match number of components.");

        int index = indexOf(element);
        for(int i=0; i<numComponents; ++i)
            data[index++] = values[i];
    }


    @Override
    public boolean equals(E a, E b) {
        int indexA = indexOf(a);
        int indexB = indexOf(b);

        for(int i=0; i<numComponents; ++i) {
            if(data[indexA++] != data[indexB++])
                return false;
        }

        return true;
    }


    @Override
    protected int[] alloc(int size) {
        return new int[size];
    }


    public static <E extends Element> IntTupleAttribute<E> get(String name, BMeshData<E> meshData) {
        return (IntTupleAttribute<E>) getAttribute(name, meshData, int[].class);
    }

    public static <E extends Element> IntTupleAttribute<E> getOrCreate(String name, int components, BMeshData<E> meshData) {
        IntTupleAttribute<E> attribute = get(name, meshData);

        if(attribute == null) {
            attribute = new IntTupleAttribute<>(name, components);
            meshData.addAttribute(attribute);
        }
        else if(attribute.numComponents != components)
            throw new IllegalStateException("Attribute with same name but different number of components already exists.");

        return attribute;
    }
}