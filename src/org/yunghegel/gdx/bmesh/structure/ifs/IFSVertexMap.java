package org.yunghegel.gdx.bmesh.structure.ifs;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class IFSVertexMap {

    VertexAttributes attributes;
    private final int vertexSize;
    private int vertexCount=0;
    private final int floatsPerVertex;
    private final int maxVertices;
    private float[][] indexedVertices;

    public IFSVertexMap(VertexAttributes attributes, int maxVerts) {
        this.attributes = attributes;
        vertexSize = attributes.vertexSize;
        floatsPerVertex = vertexSize / 4;
        maxVertices = attributes.vertexSize * maxVerts;
        indexedVertices = new float[maxVerts][floatsPerVertex];
    }

    void check(float[] vertex) {
        if (vertex.length != floatsPerVertex) {
            throw new IllegalArgumentException("Vertex size must be " + floatsPerVertex);
        }
        if(vertexCount >= maxVertices) {
            throw new IllegalStateException("Vertex count exceeds maxVertices");
        }
    }

    public int addVertex(float[] vertex) {
        check(vertex);
        indexedVertices[vertexCount] = vertex;
//        System.out.println("Vertex added at index " + vertexCount + "with values " + Arrays.toString(vertex));

        return vertexCount++;
    }

    public int addVertex(float[] vertex, Matrix4 transform){
        multiplyMatrix(vertex,transform);
        return addVertex(vertex);
    }

    public void setAttributeAtIndex(VertexAttribute attribute,int index,float[] vals){
        if(attributes.findByUsage(attribute.usage)!=attribute){
            throw new IllegalArgumentException("Attribute not present in vertex attributes");
        }
        if(vals.length!=attribute.numComponents){
            throw new IllegalArgumentException("Attribute value length does not match attribute components");
        }
        int offset = attribute.offset / 4;
        int numComponents = attribute.numComponents;
        int stride = vertexSize / 4;
        System.arraycopy(vals, 0, indexedVertices[index], offset + 0, numComponents);
        check(indexedVertices[index]);
    }

    public void setAttributeAtIndex(int usage,int index,float[] vals){
        VertexAttribute attribute = attributes.findByUsage(usage);
        if(attribute.usage!=usage){
            throw new IllegalArgumentException("Attribute not present in vertex attributes");
        }
        if(vals.length!=attribute.numComponents){
            throw new IllegalArgumentException("Attribute value length does not match attribute components");
        }
        int offset = attribute.offset / 4;
        int numComponents = attribute.numComponents;
        int stride = vertexSize / 4;
        System.arraycopy(vals, 0, indexedVertices[index], offset + 0, numComponents);
        check(indexedVertices[index]);
    }

    public float[] getVertex(int index){
        return indexedVertices[index];
    }

    public float[][] getVertices(){
        return indexedVertices;
    }

    public int getVertexCount(){
        return vertexCount;
    }

    public float[] toOneDimensionalArray(){
        float[] array = new float[vertexCount*floatsPerVertex];
        for(int i=0;i<vertexCount;i++){
            if (floatsPerVertex >= 0)
                System.arraycopy(indexedVertices[i], 0, array, i * floatsPerVertex + 0, floatsPerVertex);
        }
        return array;
    }

    public void multiplyMatrix(float[] vertices, Matrix4 matrix) {
        float[] result = new float[vertices.length];
        int offset = attributes.findByUsage(VertexAttributes.Usage.Position).offset / 4;
        for (int i = 0; i < vertices.length; i += vertexSize) {
            float x = vertices[i + offset];
            float y = vertices[i + 1 + offset];
            float z = vertices[i + 2 + offset];
            Vector3 vertex = new Vector3(x , y , z);
            vertex.mul(matrix);
            vertices[i + offset] = vertex.x;
            vertices[i + 1 + offset] = vertex.y;
            vertices[i + 2 + offset] = vertex.z;
        }

    }

    public FloatBuffer toFloatBuffer(){
        return FloatBuffer.wrap(toOneDimensionalArray());
    }







}
