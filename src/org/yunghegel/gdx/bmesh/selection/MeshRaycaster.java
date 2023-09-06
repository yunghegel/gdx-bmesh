package org.yunghegel.gdx.bmesh.selection;

import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

@Deprecated
public class MeshRaycaster {

    public static class IntersectionResult<E extends Element> implements Comparable<IntersectionResult>{

        public E element;

        public IntersectionResult(E element) {
            this.element = element;
        }

        public Vector3 intersection;
        public Vector3[] triangle;
        public Vertex[] vertices;
        public Face face;
        public Vertex vertex;
        public float distance;

        @Override
        public int compareTo(IntersectionResult o) {
            //return the lowest distance first
            return Float.compare(distance, o.distance);
        }
    }

    public Matrix4 modelMatrix=new Matrix4();
    private Camera cam;
    private Viewport viewport;
    private BMesh mesh;
    public int castButton=0;
    ArrayList<Vector3> triangles=new ArrayList<Vector3>();

    Vector3 intersection=new Vector3();

    public MeshRaycaster(Camera cam, BMesh mesh){
        this.cam=cam;
        this.mesh=mesh;
        setTriangles();
    }

    public MeshRaycaster(Viewport viewport,Camera cam, BMesh mesh){
        this.cam=cam;
        this.mesh=mesh;
        this.viewport=viewport;
        setTriangles();
    }

    public void setTriangles(){
        triangles.clear();
        for(int i=0;i<mesh.faces().size();i++){
            ArrayList<Vertex> verts = mesh.faces().get(i).getVertices();
            for(int j=0;j<verts.size();j++){
                Vector3 v=new Vector3(verts.get(j).position);
                v.mul(modelMatrix);
                triangles.add(v);
            }
        }
    }

    public void setModelMatrix(Matrix4 modelMatrix){
        this.modelMatrix=modelMatrix;
    }

    public void setCastButton(int castButton){
        this.castButton=castButton;
    }

    public void setMesh(BMesh mesh){
        this.mesh=mesh;
    }

    Array<IntersectionResult> tmp=new Array<>();

    public IntersectionResult raycastTriangles(){
        tmp.clear();
        Ray ray;
        if(viewport!=null){
            ray=viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }else{
            ray=cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }
        for (Face face: mesh.faces().getAll()){
            ArrayList<Vertex> verts = face.getVertices();
            Vector3 v1=new Vector3(verts.get(0).position);
            Vector3 v2=new Vector3(verts.get(1).position);
            Vector3 v3=new Vector3(verts.get(2).position);

            v1.mul(modelMatrix);
            v2.mul(modelMatrix);
            v3.mul(modelMatrix);

            if(Intersector.intersectRayTriangle(ray, v1, v2, v3, intersection)){
                IntersectionResult<Face> result=new IntersectionResult<>(face);
                tmp.add(result);
                result.intersection=new Vector3(intersection);
                result.triangle=new Vector3[]{v1,v2,v3};
                result.vertices=new Vertex[]{verts.get(0),verts.get(1),verts.get(2)};
                result.distance=cam.position.dst(intersection);
                result.face=face;
                System.out.println("intersected face " + face.getIndex());

            }
            IntersectionResult<Face> closest;

            return tmp.peek();



        }
        return null;




    }

    public IntersectionResult<Vertex> raycastVertices(){
        tmp.clear();
        Ray ray;
        if(viewport!=null){
            ray=viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }else{
            ray=cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }
        IntersectionResult<Vertex> result;

        for (Vertex vertex: mesh.vertices().getAll()){
            Vector3 v=new Vector3(vertex.position);
            v.mul(modelMatrix);
            if(Intersector.intersectRaySphere(ray, v, 0.1f, intersection)){
                result=new IntersectionResult<>(vertex);
                result.intersection=new Vector3(intersection);
                result.distance=ray.origin.dst(intersection);
                tmp.add(result);

            }
        }
        if (tmp.size>0){
            tmp.sort();
            return tmp.get(0);
        }

        return null;
    }


    public IntersectionResult<Vertex> raycastVertices(Vertex ...verts){
        tmp.clear();
        Ray ray;
        if(viewport!=null){
            ray=viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }else{
            ray=cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }
        IntersectionResult<Vertex> result;

        for (Vertex vertex: verts){
            Vector3 v=new Vector3(vertex.position);
            v.mul(modelMatrix);
            if(Intersector.intersectRaySphere(ray, v, 0.1f, intersection)){
                result=new IntersectionResult<>(vertex);
                result.intersection=new Vector3(intersection);
                result.distance=ray.origin.dst(intersection);
                tmp.add(result);

            }
        }
        if (tmp.size>0){
            tmp.sort();
            return tmp.get(0);
        }

        return null;
    }

    public IntersectionResult<Face> raycastFaces(ArrayList<Face> faces) {
        tmp.clear();
        Ray ray;
        if(viewport!=null){
            ray=viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }else{
            ray=cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }
        IntersectionResult<Face> result;
        for (Face face : faces) {
            ArrayList<Vertex> verts = face.getVertices();
            Vector3 v1 = new Vector3(verts.get(0).position);
            Vector3 v2 = new Vector3(verts.get(1).position);
            Vector3 v3 = new Vector3(verts.get(2).position);

            v1.mul(modelMatrix);
            v2.mul(modelMatrix);
            v3.mul(modelMatrix);

            if (Intersector.intersectRayTriangle(ray, v1, v2, v3, intersection)) {
                result = new IntersectionResult<>(face);
                result.intersection = new Vector3(intersection);
                result.triangle = new Vector3[]{v1, v2, v3};
                result.vertices = new Vertex[]{verts.get(0), verts.get(1), verts.get(2)};
                result.distance = cam.position.dst(intersection);
                result.face = face;

                System.out.println(intersection);

                return result;

            }

        }
        return null;
    }


}
