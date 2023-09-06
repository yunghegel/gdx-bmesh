package org.yunghegel.gdx.bmesh.util;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;


public class TriangleExtractor {

    private float[] positionBuffer;
    private short[] indexBuffer;
    private float[][] indexedPositions;

    public static interface TriangleIndexVisitor{
        void visitTriangleIndices(int i1, int i2, int i3);
    }

    private void processTriangles(TriangleIndexVisitor visitor) {
        for(int i=2; i<=indexBuffer.length; i+=3) {
            visitor.visitTriangleIndices(indexBuffer[i-2], indexBuffer[i-1], indexBuffer[i]);
        }
    }

    public abstract class TriangleLocationVisitor implements TriangleIndexVisitor {
        private final Vector3 p0 = new Vector3();
        private final Vector3 p1 = new Vector3();
        private final Vector3 p2 = new Vector3();

        @Override
        public final void visitTriangleIndices(int i0, int i1, int i2) {
            getVertex(i0, p0);
            getVertex(i1, p1);
            getVertex(i2, p2);
            visitTriangle(p0, p1, p2);
        }

        public abstract void visitTriangle(Vector3 p0, Vector3 p1, Vector3 p2);
    }

    public void process(TriangleIndexVisitor visitor) {
        processTriangles(visitor);
    }

    public final void setMesh(Mesh mesh) {
        indexBuffer = new short[mesh.getNumIndices()];
        indexedPositions = new float[indexBuffer.length/3][];
        positionBuffer = getVerticesByAttribute(mesh, VertexAttribute.Position());
        indexedPositions= getIndexedVertexList(mesh);

        mesh.getIndices(indexBuffer);
    }

    public int getIndex(int index) {
        return indexBuffer[index];
    }

    public int getNumIndices() {
        return indexBuffer.length;
    }

    public int getNumVertices() {
        return positionBuffer.length / 3;
    }

    public void getVertex(int index, Vector3 store) {
        index = indexBuffer[index];
        store.x = indexedPositions[index][0];
        store.y = indexedPositions[index][1];
        store.z = indexedPositions[index][2];
        System.out.println("getVertex: " + store);
    }

    public float[] getPositionArray() {
        return positionBuffer;
    }

    public short[] getIndexArray() {
        return indexBuffer;
    }

    public static float[] getVerticesByAttribute(Mesh mesh,VertexAttribute attribute){
        //we ONLY want the positions
        if(attribute.usage != VertexAttributes.Usage.Position)
            throw new IllegalArgumentException("attribute must be VertexAttributes.Usage.Position");
        int stride = mesh.getVertexSize() / 4;
        float[] vertices = new float[mesh.getNumVertices() * stride];
        mesh.getVertices(vertices);
        int posOffset = attribute.offset / 4;
        float[] result = new float[mesh.getNumVertices()*3];
        for(int i=0 ; i<result.length ; i+=3){
            int vindex = i/3 * stride + posOffset;
            float x = vertices[vindex];
            float y = vertices[vindex+1];
            float z = vertices[vindex+2];
            result[i] = x;
            result[i+1] = y;
            result[i+2] = z;
        }
        return result;
    }

    public static float[][] getIndexedVertexList(Mesh mesh){
        int stride = mesh.getVertexSize() / 4;
        float[] vertices = new float[mesh.getNumVertices() * stride];
        mesh.getVertices(vertices);
        short[] indices = new short[mesh.getNumIndices()];
        mesh.getIndices(indices);
        VertexAttribute attribute = mesh.getVertexAttribute(VertexAttributes.Usage.Position);
        int posOffset = attribute.offset / 4;
        float[][] result = new float[mesh.getNumIndices()/3][];
        for(int i=0 ; i<indices.length ; i+=3){
            int vertex = (int)(indices[i] & 0xFFFF);
            int vindex = vertex * stride + posOffset;
            float x = vertices[vindex];
            float y = vertices[vindex+1];
            float z = vertices[vindex+2];
            result[i/3] = new float[]{x,y,z};
        }
        return result;
    }

}
