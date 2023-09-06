package org.yunghegel.gdx.bmesh.operations;

import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;
import org.yunghegel.gdx.bmesh.attribute.type.Vec3Attribute;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Edge;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.math.Vector3;

public class EdgeOperations {
    private final BMesh bmesh;
    private final Vec3Attribute<Vertex> positions;

    public EdgeOperations(BMesh bmesh) {
        this.bmesh = bmesh;
        positions = Vec3Attribute.get(MeshAttribute.Position, bmesh.vertices());
    }

    public Vector3 calcCenter(Edge edge) {
        return calcCenter(edge, new Vector3());
    }

    public Vector3 calcCenter(Edge edge, Vector3 store) {
        positions.get(edge.vertex0, store);
        positions.addLocal(store, edge.vertex1);
        return store.scl(0.5f);
    }


    public boolean collinear(Edge edge1, Edge edge2) {
        // TODO: Are they only collinear if on the exact same line -> edges must be connected to eachother?

        Vector3 v1 = positions.get(edge1.vertex1);
        positions.subtractLocal(v1, edge1.vertex0);
        v1.nor();

        Vector3 v2 = positions.get(edge2.vertex1);
        positions.subtractLocal(v2, edge2.vertex0);
        v2.nor();

        return Math.abs(v1.dot(v2)) > 0.999f;
    }


    public Vertex splitAtCenter(Edge edge) {
        Vector3 center = calcCenter(edge);
        Vertex vertex = bmesh.splitEdge(edge);
        positions.set(vertex, center);
        return vertex;
    }


    public float length(Edge edge) {
        Vector3 d = positions.get(edge.vertex0);
        positions.subtractLocal(d, edge.vertex1);
        return d.len();
    }

}
