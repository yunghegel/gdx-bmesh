package org.yunghegel.gdx.bmesh.operations;

import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;
import org.yunghegel.gdx.bmesh.attribute.type.Vec3Attribute;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.math.Vector3;

public class Inset {
    private final FaceOperations faceOps;
    private final ExtrudeFace extrusion;
    private final Vec3Attribute<Vertex> positions;

    private float thickness = 0.6f; // relative factor, TODO: absolute?
    private float depth = 1.0f;


    public Inset(BMesh bmesh, float thickness, float depth) {
        faceOps = new FaceOperations(bmesh);
        extrusion = new ExtrudeFace(bmesh);
        positions = Vec3Attribute.get(MeshAttribute.Position, bmesh.vertices());

        this.thickness = thickness;
        this.depth = depth;
    }


    public void apply(Face face) {
        extrusion.apply(face);
        extrusion.copyVertexAttributes();

        Vector3 p = new Vector3();
        Vector3 centroid = faceOps.centroid(face);
        Vector3 normal = faceOps.normal(face).scl(-depth);

        for(Vertex vertex : face.vertices()) {
            positions.get(vertex, p);
            p.sub(centroid);
            p.scl(thickness);
            p.add(centroid);
            positions.set(vertex, p);
        }

        extrusion.apply(face);
        extrusion.copyVertexAttributes();

        for(Vertex vertex : face.vertices()) {
            positions.get(vertex, p);
            p.add(normal);
            positions.set(vertex, p);
        }
    }
}
