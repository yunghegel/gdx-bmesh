package org.yunghegel.gdx.bmesh.structure.ifs;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GdxIFS extends IndexedFaceSet{

    public Mesh mesh;





    public GdxIFS(Mesh mesh, Matrix4 transform){
        super(mesh.getVertexAttributes(), mesh.getNumIndices(), mesh.getNumVertices()*(mesh.getVertexSize()/4));
        this.mesh = mesh;
        System.out.println("Creating GdxIFS from mesh...");

        float[] vertices = new float[mesh.getNumVertices()*mesh.getVertexSize()];
        short[] indices = new short[mesh.getNumIndices()];


        mesh.getVertices(vertices);
        mesh.getIndices(indices);

        for(VertexAttribute attribute : mesh.getVertexAttributes()){
            int offset = attribute.offset/4;
        }

        for(int i=0; i<indices.length; i+=3){
            int[] face = new int[3];
            face[0] = indices[i];
            face[1] = indices[i+1];
            face[2] = indices[i+2];
            addFace(face);
        }


        for(int i=0; i<maxVertices; i+=mesh.getVertexSize()/4){
            float[] vertex = new float[mesh.getVertexSize()/4];
            for(int j=0; j<vertex.length; j++){
                vertex[j] = vertices[i+j];
            }
            addVertex(vertex, transform);
        }

        System.out.println("GdxIFS created");
    }

    public float[] getPositionsArray(){
        int size = getVertexCount()*3;
        float[] positions = new float[size];
        for(int i=0; i<getVertexCount(); i++){
            positions[i*3] = getVertex(i)[0];
            positions[i*3+1] = getVertex(i)[1];
            positions[i*3+2] = getVertex(i)[2];
        }
        return positions;
    }

//    public Triangle[] getAsTriangles(){
//       Triangle[] triangles = new Triangle[getFaces().length];
//        for (int i = 0; i < getFaces().length; i++) {
//            int[] face = getFaces()[i];
//            Vector3 v1 = new Vector3(getVertex(face[0]));
//            Vector3 v2 = new Vector3(getVertex(face[1]));
//            Vector3 v3 = new Vector3(getVertex(face[2]));
//            Triangle triangle = new Triangle(v1,v2,v3);
//            triangles[i] = triangle;
//        }
//        return triangles;
//    }
//
//    public record Triangle(Vector3 v1, Vector3 v2, Vector3 v3) {
//
//        public boolean intersect(Ray ray){
//            return Intersector.intersectRayTriangle(ray,v1,v2,v3,null);
//        }
//
//        public void draw(ShapeRenderer sr, Camera camera, Stage stage){
//            sr.begin(ShapeRenderer.ShapeType.Line);
////            sr.setTransformMatrix(stage.getBatch().getTransformMatrix());
//            sr.setProjectionMatrix(camera.combined);
//
//            sr.line(v1,v2);
//            sr.line(v2,v3);
//            sr.line(v3,v1);
//            sr.end();
//        }
//
//    }

}
