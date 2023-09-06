package org.yunghegel.gdx.bmesh.structure.ifs;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Matrix4;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class IndexedFaceSet {

    IFSFaceMap faceMap;
    IFSVertexMap vertexMap;
    public int maxFaces, maxVertices;
    public VertexAttributes attributes;

    public IndexedFaceSet(VertexAttributes attributes, int maxFaces, int maxVertices) {
        this.maxFaces = maxFaces;
        this.maxVertices = maxVertices;
        this.attributes = attributes;
        faceMap = new IFSFaceMap(maxFaces);
        vertexMap = new IFSVertexMap(attributes, maxVertices);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[IndexedFaceSet]\n");
        sb.append("Vertex Attributes:\n");
        sb.append("------------------\n");
        for (VertexAttribute attribute : attributes) {
            sb.append(attribute.getClass().getSimpleName() + "offset: "+attribute.offset/4 + "\n");
        }
        sb.append("------------------\n");
        sb.append("Faces: "+faceMap.getFaceCount()+"\n");
        sb.append("Vertices: "+vertexMap.getVertexCount()+"\n");
        return sb.toString();
    }

    public int getOffset(int usage){
        return attributes.findByUsage(usage).offset/4;

    }

    public int addFace(int[] face) {
        return faceMap.addFace(face);
    }

    public int addVertex(float[] vertex) {
        return vertexMap.addVertex(vertex);
    }

    public int addVertex(float[] vertex, Matrix4 transform) {
        return vertexMap.addVertex(vertex, transform);
    }

    public void setAttributeAtIndex(VertexAttribute attribute, int index, float[] vals){
        vertexMap.setAttributeAtIndex(attribute, index, vals);
    }

    public int[][] getFaces(){
        return faceMap.getFaces();
    }

    public float[][] getVertices(){
        return vertexMap.getVertices();
    }

    public int getFaceCount(){
        return faceMap.getFaceCount();
    }

    public int getVertexCount(){
        return vertexMap.getVertexCount();
    }

    public int[] getFace(int index){
        return faceMap.getFace(index);
    }

    public float[] getVertex(int index){
        return vertexMap.getVertex(index);
    }

    public int[] asIntArray(){
        return faceMap.toOneDimensionalArray();
    }

    public short[] asShortArray(){
        return faceMap.toOneDimensionalShortArray();
    }

    public float[] asFloatArray(){
        return vertexMap.toOneDimensionalArray();
    }

    public IntBuffer asIntBuffer(){
        return faceMap.toIntBuffer();
    }

    public ShortBuffer asShortBuffer(){
        return faceMap.toShortBuffer();
    }

    public FloatBuffer asFloatBuffer(){
        return vertexMap.toFloatBuffer();
    }

}
