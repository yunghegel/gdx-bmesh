package org.yunghegel.gdx.bmesh.selection;

import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.structure.Edge;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Vertex;

public enum SelectionMode {
    FACE(Face.class),
    EDGE(Edge.class),
    VERTEX(Vertex.class),
    NONE(null);

    public final Class element;

    SelectionMode(Class<? extends Element> clazz) {
        this.element = clazz;
    }

    public Class getElement() {
        return element;
    }

}
