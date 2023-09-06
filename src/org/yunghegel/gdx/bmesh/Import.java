package org.yunghegel.gdx.bmesh;


import org.yunghegel.gdx.bmesh.lookup.ExactHashDeduplication;
import org.yunghegel.gdx.bmesh.lookup.HashGridDeduplication;
import org.yunghegel.gdx.bmesh.lookup.VertexDeduplication;

import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;

public class Import {
    private static final float DEFAULT_EPSILON = 0.01f;


    public static BMesh convert(Mesh mesh) {
        return convert(mesh, DEFAULT_EPSILON);
    }

    public static BMesh convert(Mesh mesh, float epsilon) {
        BMesh bmesh = new BMesh();
        return convert(mesh, bmesh, new HashGridDeduplication(bmesh, epsilon));
    }


    public static BMesh convertExact(Mesh mesh) {
        BMesh bmesh = new BMesh();
        return convert(mesh, bmesh, new ExactHashDeduplication(bmesh));
    }


    /**
     * Deduplicates the vertices first, so each vertex is only checked once.
     * @param bmesh
     * @return
     */
    private static BMesh convert(Mesh inputMesh, BMesh bmesh, VertexDeduplication dedup) {
        VertexAttribute attribute = inputMesh.getVertexAttribute(VertexAttributes.Usage.Position);
        int posOffset = attribute.offset / 4;
        int size = inputMesh.getVertexSize();
        int stride = size / 4;
        float[] vertices = new float[inputMesh.getNumVertices() * stride];
        inputMesh.getVertices(vertices);
        int numIndices = inputMesh.getNumIndices();
        short[] indices = new short[numIndices];
        inputMesh.getIndices(indices);
        Vertex[] indexMap = new Vertex[numIndices];

        for(int i=0; i<numIndices; ++i) {
            short index = indices[i];
            Vertex vertex = indexMap[index];
            if(vertex == null) {
                int offset = index * stride + posOffset;
                Vector3 position = new Vector3(vertices[offset], vertices[offset+1], vertices[offset+2]);
                vertex = dedup.getOrCreateVertex(position);
                vertex.setPosition(position);
                indexMap[index] = vertex;
//                bMesh.createVertex(position);
            }
        }
        //create triangles
        for(int i=0; i<numIndices; i+=3) {
            Vertex v0 = indexMap[indices[i]];
            Vertex v1 = indexMap[indices[i+1]];
            Vertex v2 = indexMap[indices[i+2]];
            //degenerate triangles?
            if(v0 != v1 && v0 != v2 && v1 != v2)
                bmesh.createFace(v0, v1, v2);
        }

        return bmesh;
    }


    public static BMesh importKeep(Mesh inputMesh) {
        // Keep normals
        // Keep duplication: Create virtual vertices for index targets with multiple uses
        // Keep triangulation: Indices -> Create Triangle objects in Triangulate
        // Copy and reuse arrays from buffer
        return null;
    }
}
