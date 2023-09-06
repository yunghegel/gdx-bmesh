package org.yunghegel.gdx.bmesh.attribute.type;

import org.yunghegel.gdx.bmesh.attribute.Element;
import com.badlogic.gdx.graphics.g3d.Attribute;


public class AttributeAttribute<E extends Element,T extends Attribute> extends ObjectAttribute<E, Attribute> {

    public AttributeAttribute(String name) {
        super(name, new ArrayAllocator<Attribute>() {
            @Override
            public Attribute[] alloc(int size) {
                return new Attribute[size];
            }
        });
    }

    public void create(E element, Attribute value) {
        data[indexOf(element)] = value;
    }

    public Attribute get(E element) {
        return data[indexOf(element)];
    }
}

