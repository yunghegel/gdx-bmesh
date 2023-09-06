package org.yunghegel.gdx.bmesh.lookup;

import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.math.Vector3;

public interface VertexDeduplication {
    void addExisting(Vertex vertex);
    void clear();

    Vertex getVertex(Vector3 location);
    Vertex getOrCreateVertex(Vector3 location);
}
