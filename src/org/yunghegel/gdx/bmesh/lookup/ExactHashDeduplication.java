package org.yunghegel.gdx.bmesh.lookup;

import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;
import org.yunghegel.gdx.bmesh.attribute.type.Vec3Attribute;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public class ExactHashDeduplication implements VertexDeduplication {
    private final BMesh bmesh;
    private final Map<Vector3, Vertex> map = new HashMap<>();
    private final Vec3Attribute<Vertex> positions;


    public ExactHashDeduplication(BMesh bmesh) {
        this.bmesh = bmesh;
        positions = Vec3Attribute.get(MeshAttribute.Position, bmesh.vertices());
    }

    public ExactHashDeduplication(BMesh bmesh, Vec3Attribute<Vertex> attrPosition) {
        this.bmesh = bmesh;
        this.positions = attrPosition;
    }


    @Override
    public void addExisting(Vertex vertex) {
        Vector3 p = positions.get(vertex);
        map.put(p, vertex);
    }


    @Override
    public void clear() {
        map.clear();
    }


    @Override
    public Vertex getVertex(Vector3 position) {
        return map.get(position);
    }


    @Override
    public Vertex getOrCreateVertex(Vector3 position) {
        Vertex vertex = map.get(position);
        if(vertex == null) {
            vertex = bmesh.createVertex(position);
            map.put(position.cpy(), vertex);
        }

        return vertex;
    }
}
