package org.yunghegel.gdx.bmesh.util;

import com.badlogic.gdx.graphics.VertexAttribute;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class GdxMeshInterop {

    Mesh mesh;
    BMesh bmesh;

    float[] vertices;
    short[] indices;

    public int vertexSize;
    public int numVertices;
    public int numIndices;

    FloatBuffer vertexBuffer;
    ShortBuffer indexBuffer;


    public GdxMeshInterop(Mesh mesh, BMesh bmesh) {
        this.mesh = mesh;
        this.bmesh = bmesh;

        vertexBuffer = mesh.getVerticesBuffer();
        indexBuffer = mesh.getIndicesBuffer();
        this.numVertices = this.mesh.getNumVertices();
        this.numIndices = this.mesh.getNumIndices();
        this.vertexSize = this.mesh.getVertexSize();

        vertices = new float[this.numVertices * this.vertexSize];
        indices = new short[this.numIndices];

        mesh.getIndices(indices);
        mesh.getVertices(vertices);

        System.out.println("numVertices: " + numVertices + " size: "+ vertices.length+ "\n" + Arrays.toString(vertices));
        System.out.println("numIndices: " + numIndices  + " size: "+ indices.length + "\n" + Arrays.toString(indices));
    }

    public void setVertexPositionAtIndex(short index, float x, float y, float z) {
        VertexAttributes vertexAttributes = mesh.getVertexAttributes();
        int offset = vertexAttributes.getOffset(VertexAttributes.Usage.Position);

        int vertexSize = mesh.getVertexSize() / 4;
        int vertCount = mesh.getNumVertices() * mesh.getVertexSize() / 4;

        float[] vertices = new float[vertCount];
        //short[] indices = new short[mesh.getNumIndices()];

        mesh.getVertices(vertices);
        //mesh.getIndices(indices);

        // Get XYZ vertices position data
        float vx = vertices[index * vertexSize + offset];
        float vy = vertices[index * vertexSize + offset + 1];
        float vz = vertices[index * vertexSize + offset + 2];
        System.out.println("Vertex " + index + " unmodified position: " + vx + ", " + vy + ", " + vz);
        vertices[index * vertexSize + offset] = x;
        vertices[index * vertexSize + offset + 1] = y;
        vertices[index * vertexSize + offset + 2] = z;
        System.out.println("Vertex " + index + " modified position: " + x + ", " + y + ", " + z);
        mesh.setVertices(vertices);
    }

    public void setAttributeAtIndex(short index, VertexAttribute attribute, float[] values){
        int stride = mesh.getVertexSize() / 4;
        int offset = attribute.offset / 4;
        int size = attribute.numComponents;

        if (values.length != size) {
            throw new IllegalArgumentException("values.length != size");
        }

        float[] vertices = new float[mesh.getNumVertices() * stride];
        mesh.getVertices(vertices);

        for(int i=0 ; i<values.length ; i++){
            int vertex = i / size;
            int vindex = vertex * stride + offset;
            vertices[vindex] = values[i];
        }

        mesh.setVertices(vertices);
    }

}
