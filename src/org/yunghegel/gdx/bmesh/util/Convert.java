package org.yunghegel.gdx.bmesh.util;

import org.yunghegel.gdx.bmesh.lookup.HashGridDeduplication;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import org.yunghegel.gdx.bmesh.structure.ifs.GdxIFS;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;

public class Convert {

    public static BMesh createFromGdxMesh(Mesh mesh){
        System.out.println("inititalizing a new BMesh from GdxMesh... \n");
        BMesh bMesh = new BMesh();



        System.out.println("building bmesh with vertex size of " + mesh.getVertexSize() + " and attributes" + mesh.getVertexAttributes().toString());
        System.out.println("it has " + mesh.getNumVertices() + " vertices and " + mesh.getNumIndices() + " indices");

        HashGridDeduplication deduplication = new HashGridDeduplication(bMesh);



        int size = mesh.getNumIndices();
        System.out.println("size: " + size);
        short[] indicesArray = new short[size];
        mesh.getIndices(indicesArray);
        int stride = mesh.getVertexSize() / 4;
        float[] vertices = new float[mesh.getNumVertices() * stride];
        mesh.getVertices(vertices);
        VertexAttribute attribute = mesh.getVertexAttribute(1);
        int posOffset = attribute.offset / 4;
        Vertex[] indexMap = new Vertex[mesh.getNumVertices()];
//        System.out.println("indices: " + Arrays.toString(indicesArray));


        for(int i = 0; i < size; i += 3) {
            int vertex = indicesArray[i] & '\uffff';
            int vindex = vertex * stride + posOffset;
            float x = vertices[vindex];
            float y = vertices[vindex + 1];
            float z = vertices[vindex + 2];

            Vector3 position = new Vector3(x, y, z);
            Vertex v = deduplication.getOrCreateVertex(position,indexMap,vertex);
            bMesh.attrPosition.set(v, position);
            v.setPosition(position);

            indexMap[vertex] = v;
            System.out.println(indexMap[vertex].getPosition());


        }
//        System.out.println(indexMap.length + " vertices created");

        //now, we use the index array to query the indexMap to get the vertices and create faces
        System.out.println("creating faces...");
        for(int i = 0; i < indicesArray.length; i += 3) {
           //since we depulicated vertices, there are less vertices than the indices array calls for
            //so, we need to get the position from the vertex array and find the corresponding vertex in the indexMapr
            int v1 = indicesArray[i] & '\uffff';
            int v2 = indicesArray[i + 1] & '\uffff';
            int v3 = indicesArray[i + 2] & '\uffff';

            float x1 = vertices[v1 * stride + posOffset];
            float y1 = vertices[v1 * stride + posOffset + 1];
            float z1 = vertices[v1 * stride + posOffset + 2];

            Vector3 position1 = new Vector3(x1, y1, z1);
            Vertex vertex1 = deduplication.getOrCreateVertex(position1);
            bMesh.attrPosition.set(vertex1, position1);
            vertex1.setPosition(position1);

            float x2 = vertices[v2 * stride + posOffset];
            float y2 = vertices[v2 * stride + posOffset + 1];
            float z2 = vertices[v2 * stride + posOffset + 2];

            Vector3 position2 = new Vector3(x2, y2, z2);
            Vertex vertex2 = deduplication.getOrCreateVertex(position2);
            bMesh.attrPosition.set(vertex2, position2);
            vertex2.setPosition(position2);


            float x3 = vertices[v3 * stride + posOffset];
            float y3 = vertices[v3 * stride + posOffset + 1];
            float z3 = vertices[v3 * stride + posOffset + 2];



            Vector3 position3 = new Vector3(x3, y3, z3);
            Vertex vertex3 = deduplication.getOrCreateVertex(position3);
            bMesh.attrPosition.set(vertex3, position3);
            vertex3.setPosition(position3);


//            System.out.println("vertex1: " + vertex1.getPosition() + " vertex2: " + vertex2.getPosition() + " vertex3: " + vertex3.getPosition());
//            System.out.println("idx: " + v1 + " " + v2 + " " + v3);

            bMesh.createFace(vertex1, vertex2, vertex3);



        }

    int count2=0;
        for(Vertex v : bMesh.vertices()){
            float x = v.getPosition().x;
            float y = v.getPosition().y;
            float z = v.getPosition().z;
            if (x==0 && y==0 && z==0) continue;
            for(Vertex v2 : bMesh.vertices()){



                if(v!=v2){
                    if(v.getPosition().equals(v2.getPosition())){
                        count2++;
                        v2.setDuplicated(true,v.getIndex());

                    }
                }
            }
        }




        System.out.println("bmesh created successfully!");
        return bMesh;
    }

    public static BMesh createFromGdxIFS(GdxIFS ifs){
        System.out.println("inititalizing a new BMesh from an IFS... \n");
        BMesh bMesh = new BMesh();
        float[][] ivs = ifs.getVertices();
        int[][] ifsFaces = ifs.getFaces();
        HashGridDeduplication deduplication = new HashGridDeduplication(bMesh);
        Vertex[] indexMap = new Vertex[ivs.length];
        int offset = ifs.getOffset(VertexAttributes.Usage.Position);

        int count = 0;
        for(int i =0;i<ivs.length;i++){
            float x = ivs[i][offset];
            float y = ivs[i][offset+1];
            float z = ivs[i][offset+2];
            Vector3 pos = new Vector3(x,y,z);
            Vertex v = bMesh.createVertex(pos);
            v.setPosition(pos);
            bMesh.attrPosition.set(v, pos);

            v.setPosition(pos);
            indexMap[i] = v;
            count++;
        }

        for(int i =0;i<ifsFaces.length;i++){
            int[] tri = new int[3];
            tri[0] = ifsFaces[i][0];
            tri[1] = ifsFaces[i][1];
            tri[2] = ifsFaces[i][2];
            Vertex v1 = bMesh.vertices().get(tri[0]);
            Vertex v2 = bMesh.vertices().get(tri[1]);
            Vertex v3 = bMesh.vertices().get(tri[2]);
            bMesh.createFace(indexMap[tri[0]], indexMap[tri[1]], indexMap[tri[2]]);
        }


        System.out.println("bmesh created successfully!");
        return bMesh;



    }



}
