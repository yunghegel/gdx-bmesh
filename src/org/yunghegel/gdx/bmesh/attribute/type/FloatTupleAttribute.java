package org.yunghegel.gdx.bmesh.attribute.type;


import org.yunghegel.gdx.bmesh.attribute.BMeshData;
import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;

public class FloatTupleAttribute<E extends Element> extends MeshAttribute<E, float[]> {
    public FloatTupleAttribute(String name, int components) {
        super(name, components);
    }


    public float getComponent(E element, int component) {
        return data[indexOf(element, component)];
    }

    public void setComponent(E element, int component, float value) {
        data[indexOf(element, component)] = value;
    }


    public void setValues(E element, float... values) {
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
            if(!FloatAttribute.floatEquals(data[indexA++], data[indexB++]))
                return false;
        }

        return true;
    }


    @Override
    protected float[] alloc(int size) {
        return new float[size];
    }


    public static <E extends Element> FloatTupleAttribute<E> get(String name, BMeshData<E> meshData) {
        return (FloatTupleAttribute<E>) getAttribute(name, meshData, float[].class);
    }

    public static <E extends Element> FloatTupleAttribute<E> getOrCreate(String name, int components, BMeshData<E> meshData) {
        FloatTupleAttribute<E> attribute = get(name, meshData);

        if(attribute == null) {
            attribute = new FloatTupleAttribute<>(name, components);
            meshData.addAttribute(attribute);
        }
        else if(attribute.numComponents != components)
            throw new IllegalStateException("Attribute with same name but different number of components already exists.");

        return attribute;
    }
}
