package bmesh.lookup;

import bmesh.structure.Vertex;
import com.badlogic.gdx.math.Vector3;

public interface VertexDeduplication {
    void addExisting(Vertex vertex);
    void clear();

    Vertex getVertex(Vector3 location);
    Vertex getOrCreateVertex(Vector3 location);
}
