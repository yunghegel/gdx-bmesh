package org.yunghegel.gdx.bmesh.operations;

import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;
import org.yunghegel.gdx.bmesh.attribute.type.Vec3Attribute;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

public class ScaleFace {
    private final Vec3Attribute<Vertex> positions;

    private float scale;
    private Function<Face, Vector3> pivotFunc;


    public ScaleFace(BMesh bmesh, float scale, Function<Face, Vector3> pivotFunction) {
        setPivotFunction(pivotFunction);
        positions = Vec3Attribute.get(MeshAttribute.Position, bmesh.vertices());
        this.scale = scale;
    }

    public ScaleFace(BMesh bmesh, float scale) {
        this(bmesh, scale, new CentroidPivot(bmesh));
    }

    public ScaleFace(BMesh bmesh) {
        this(bmesh, 1.0f, new CentroidPivot(bmesh));
    }


    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }


    public void setPivotFunction(Function<Face, Vector3> pivotFunction) {
        Objects.requireNonNull(pivotFunction);
        this.pivotFunc = pivotFunction;
    }


    public void apply(Face face) {
        Vector3 pivot = pivotFunc.apply(face);
        Vector3 p = new Vector3();

        for(Vertex vertex : face.vertices()) {
            p = vertex.position.cpy();
            System.out.println("original: " + p);
            p.sub(pivot);
            p.scl(scale);
            p.add(pivot);
            positions.set(vertex, p);
            vertex.setPosition(p);
            System.out.println("scaled: " + p);
        }
}

    public static class CentroidPivot implements Function<Face, Vector3> {
        private final FaceOperations faceOps;
        private final Vector3 store = new Vector3();

        public CentroidPivot(BMesh bmesh) {
            faceOps = new FaceOperations(bmesh);
        }

        @Override
        public Vector3 apply(Face face) {
            return faceOps.centroid(face, store);
        }
    }


    public static class FirstVertexPivot implements Function<Face, Vector3> {
        private final Vec3Attribute<Vertex> positions;
        private final Vector3 store = new Vector3();

        public FirstVertexPivot(BMesh bmesh) {
            positions = Vec3Attribute.get(MeshAttribute.Position, bmesh.vertices());
        }

        @Override
        public Vector3 apply(Face face) {
            Iterator<Vertex> it = face.vertices().iterator();
            if(it.hasNext())
                return positions.get(it.next(), store);

            return store.setZero();
        }
    }


    public static class PointPivot implements Function<Face, Vector3> {
        private final Vector3 pivotPoint = new Vector3();

        public PointPivot() {}

        public PointPivot(Vector3 pivotPoint) {
            setPivotPoint(pivotPoint);
        }

        public PointPivot(float xPivot, float yPivot, float zPivot) {
            setPivotPoint(xPivot, yPivot, zPivot);
        }


        public void setPivotPoint(Vector3 pivotPoint) {
            this.pivotPoint.set(pivotPoint);
        }

        public void setPivotPoint(float xPivot, float yPivot, float zPivot) {
            pivotPoint.set(xPivot, yPivot, zPivot);
        }

        public Vector3 getPivotPoint() {
            return pivotPoint;
        }

        @Override
        public Vector3 apply(Face face) {
            return pivotPoint;
        }
    }
}
