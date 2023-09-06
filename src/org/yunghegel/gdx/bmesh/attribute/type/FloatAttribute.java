package org.yunghegel.gdx.bmesh.attribute.type;


import org.yunghegel.gdx.bmesh.attribute.BMeshData;
import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;


public class FloatAttribute<E extends Element> extends MeshAttribute<E, float[]> {
    private static final float EPSILON = 0.001f;


    public FloatAttribute(String name) {
        super(name);
    }


    public float get(E element) {
        return data[element.getIndex()];
    }

    public void set(E element, float value) {
        data[element.getIndex()] = value;
    }


    @Override
    public boolean equals(E a, E b) {
        return floatEquals(data[a.getIndex()], data[b.getIndex()]);
    }


    @Override
    protected float[] alloc(int size) {
        return new float[size];
    }


    public static <E extends Element> FloatAttribute<E> get(String name, BMeshData<E> meshData) {
        return (FloatAttribute<E>) getAttribute(name, meshData, float[].class);
    }

    public static <E extends Element> FloatAttribute<E> getOrCreate(String name, BMeshData<E> meshData) {
        FloatAttribute<E> attribute = get(name, meshData);
        if(attribute == null) {
            attribute = new FloatAttribute<>(name);
            meshData.addAttribute(attribute);
        }
        return attribute;
    }


    public static boolean floatEquals(float a, float b) {
        return Float.floatToIntBits(a) == Float.floatToIntBits(b) || Math.abs(a - b) <= EPSILON;
    }
}
