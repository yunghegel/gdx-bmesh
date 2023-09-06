package org.yunghegel.gdx.bmesh.attribute.type;

import org.yunghegel.gdx.bmesh.attribute.Element;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;

public class MaterialAttribute<E extends Element> extends ObjectAttribute<E, Material> {


    public MaterialAttribute(String name, ArrayAllocator<Material> allocator) {
        super(name, allocator);
    }

    public void create(E element, Material value) {
        data[indexOf(element)] = value;
    }

    public Material get(E element) {
        return data[indexOf(element)];
    }

    public void set(E element, AttributeAttribute<E,Attribute>... attributes){
        Material material = data[indexOf(element)];
        for(AttributeAttribute<E,Attribute> attribute : attributes){
            material.set(attribute.get(element));
        }
    }

    }



