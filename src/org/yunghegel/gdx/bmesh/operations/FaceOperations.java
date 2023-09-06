package org.yunghegel.gdx.bmesh.operations;

import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;
import org.yunghegel.gdx.bmesh.attribute.type.Vec3Attribute;
import org.yunghegel.gdx.bmesh.operations.normals.NewellNormal;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Loop;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;

public class FaceOperations {
    private final BMesh bmesh;
    private final Vec3Attribute<Vertex> positions;


    public FaceOperations(BMesh bmesh) {
        this.bmesh = bmesh;
        positions = Vec3Attribute.get(MeshAttribute.Position, bmesh.vertices());
    }


    public Vector3 normal(Face face) {
        return normal(face, new Vector3());
    }

    // Newell's Method that also works for concave polygons: https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal
    public Vector3 normal(Face face, Vector3 store) {
        Loop current = face.loop;
        Loop next = current.nextFaceLoop;

        Vector3 vCurrent = new Vector3();
        Vector3 vNext = new Vector3();
        positions.get(current.vertex, vCurrent);
        store.setZero();

        do {
            positions.get(next.vertex, vNext);
            NewellNormal.addToNormal(store, vCurrent, vNext);

            vCurrent.set(vNext);
            current = next;
            next = next.nextFaceLoop;
        } while(current != face.loop);

        return store.nor();
    }


    public Vector3 normalConvex(Face face) {
        return normalConvex(face.loop, new Vector3());
    }

    public Vector3 normalConvex(Face face, Vector3 store) {
        return normalConvex(face.loop, store);
    }

    public Vector3 normalConvex(Loop loop, Vector3 store) {
        Vertex vertex = loop.vertex;
        Vertex vNext = loop.nextFaceLoop.vertex;
        Vertex vPrev = loop.prevFaceLoop.vertex;

        Vector3 v1 = positions.get(vertex);

        store.set(v1);
        positions.subtractLocal(store, vNext);
        positions.subtractLocal(v1, vPrev);

        return store.crs(v1).nor();
    }


    public Vector3 centroid(Face face) {
        return centroid(face, new Vector3());
    }

    public Vector3 centroid(Face face, Vector3 store) {
        int numVertices = 0;
        store.setZero();

        for(Vertex vertex : face.vertices()) {
            positions.addLocal(store, vertex);
            numVertices++;
        }

        return store.scl(1f / numVertices);
    }


    public boolean coplanar(Face face1, Face face2) {
        Vector3 normal1 = normal(face1);
        Vector3 normal2 = normal(face2);
        return normal1.dot(normal2) > 0.9999f;
    }


    /**
     * Face needs to be planar.
     * @param face
     * @return Area of polygon.
     */
    public float area(Face face) {
        Vector3 normal = normal(face);
        return area(face, normal);
    }

    public float area(Face face, Vector3 normal) {
        Iterator<Vertex> it = face.vertices().iterator();
        Vertex firstVertex = it.next();

        Vector3 p1 = positions.get(firstVertex);
        Vector3 p2 = new Vector3();
        Vector3 sum = new Vector3();

        // Stoke's theorem? Green's theorem?
        while(it.hasNext()) {
            positions.get(it.next(), p2);
            sum.add( p1.crs(p2) );
            p1.set(p2);
        }

        // Close loop. Will be zero if p1 == p2 (when face has only one side).
        positions.get(firstVertex, p2);
        sum.add( p1.crs(p2) );

        float area = sum.dot(normal) * 0.5f;
        return Math.abs(area);
    }


    public float areaTriangle(Face face) {
        Vector3 v0 = positions.get(face.loop.vertex);
        Vector3 v1 = v0.cpy();
        positions.subtractLocal(v0, face.loop.nextFaceLoop.vertex);
        positions.subtractLocal(v1, face.loop.nextFaceLoop.nextFaceLoop.vertex);

        return v0.crs(v1).len() * 0.5f;
    }


    public void makePlanar(Face face) {
        // TODO: ... Find plane with smallest deviation from existing vertices?
    }
}
