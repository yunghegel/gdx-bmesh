package org.yunghegel.gdx.bmesh.structure;



import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.operations.FaceOperations;
import org.yunghegel.gdx.bmesh.util.LoopMapIterator;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Face extends Element implements Json.Serializable {
    /**
     * Any Loop of this Face.<br>
     * Never <code>null</code> on a valid object.
     */
    public Loop loop;


    public Face() {}

    public Vector3 normal = new Vector3();
    public Vector3 centroid = new Vector3();
    FaceOperations faceOperations;

    @Override
    protected void releaseElement() {
        loop = null;
    }

    @Override
    public void rebuild() {
        calculateNormal();
        calculateCentroid();
    }

    @Override
    public void getMatrixRepresentation() {

    }


    public Vector3 calculateNormal(){
        Vector3 normal = new Vector3();
        Vector3 v1 = new Vector3();
        Vector3 v2 = new Vector3();
        Vector3 v3 = new Vector3();
        v1 = getVertices().get(0).position.cpy();
        v2 = getVertices().get(1).position.cpy();
        v3 = getVertices().get(2).position.cpy();

        v2.sub(v1);
        v3.sub(v1);
        normal = v2.crs(v3);
        normal.nor();
        this.normal = normal;
        return normal;
    }

    public ArrayList<Face> getCoplanarFaces(){
        ArrayList<Face> coplanarFaces = new ArrayList<>();
        ArrayList<Face> tmp = getAdjacentFaces();
        for(Face f : tmp){
            if(faceOperations.coplanar(this,f))
                coplanarFaces.add(f);
        }
        return coplanarFaces;
    }

    public ArrayList<Face> getAdjacentFaces(){
        ArrayList<Face> adjacentFaces = new ArrayList<>();
        for(Edge e : edges()){
            e.faces().forEach(f -> {
                if(f != this)
                    adjacentFaces.add(f);
            });
        }
        return adjacentFaces;
    }

    public Vector3 calculateCentroid(){
        return faceOperations.centroid(this,centroid);
    }

    public boolean isFrontFacing(Camera camera){
        Vector3 camDir = new Vector3();
        Vector3 faceDir = new Vector3();
        camDir = camera.direction;
        faceDir = normal.cpy();
        float dot = camDir.dot(faceDir);
        return dot < .0f;
    }


    public Edge getAnyCommonEdge(Face face) {
        for(Loop l1 : loops()) {
            for(Loop l2 : face.loops()) {
                if(l1.edge == l2.edge)
                    return loop.edge;
            }
        }

        return null;
    }

    public boolean has(Edge edge){
        int hash = edge.hashCode();
        int thisHash = this.hashCode();

        return (hash & thisHash) == hash;
    }

    @Override
    public int hashCode() {
        //encodes a bitwise hashcode of the vertices of the face
        int hash = 0;
        for(Vertex v : getVertices())
            hash ^= v.hashCode();
        return hash;
    }

    public List<Edge> getCommonEdges(Face face) {
        List<Edge> edges = new ArrayList<>(4);
        for(Loop l1 : loops()) {
            for(Loop l2 : face.loops()) {
                if(l1.edge == l2.edge)
                    edges.add(loop.edge);
            }
        }

        return edges;
    }

    public int countCommonEdges(Face face) {
        int commonEdges = 0;
        for(Loop l1 : loops()) {
            for(Loop l2 : face.loops()) {
                if(l1.edge == l2.edge)
                    commonEdges++;
            }
        }

        return commonEdges;
    }


    public int countVertices(Face face) {
        int count = 0;
        Loop current = loop;
        do {
            current = current.nextFaceLoop;
            count++;
        } while(current != loop);
        return count;
    }


    public ArrayList<Vertex> getVertices() {
        return getVertices(new ArrayList<>(4));
    }

    public <C extends Collection<Vertex>> C getVertices(C collection) {
        for(Loop loop : loops())
            collection.add(loop.vertex);
        return collection;
    }

    public Iterable<Vertex> vertices() {
        return () -> new LoopMapIterator<>(new FaceLoopIterator(loop), loop -> loop.vertex);
    }


    public ArrayList<Edge> getEdges() {
        return getEdges(new ArrayList<>(4));
    }

    public <C extends Collection<Edge>> C getEdges(C collection) {
        for(Loop loop : loops())
            collection.add(loop.edge);
        return collection;
    }

    public Iterable<Edge> edges() {
        return () -> new LoopMapIterator<>(new FaceLoopIterator(loop), loop -> loop.edge);
    }


    public ArrayList<Loop> getLoops() {
        return getLoops(new ArrayList<>(4));
    }

    public <C extends Collection<Loop>> C getLoops(C collection) {
        Loop current = loop;
        do {
            collection.add(current);
            current = current.nextFaceLoop;
        } while(current != loop);

        return collection;
    }

    public Iterable<Loop> loops() {
        return () -> new FaceLoopIterator(loop);
    }

    /**
     * Searches for the Loop between the given vertices in this Face.
     * @param from The Loop's source vertex.
     * @param to The Loop's next vertex.
     * @return The Loop between the given vertices, or null if not found.
     */
    public Loop getLoop(Vertex from, Vertex to) {
        for(Loop loop : loops()) {
            if(loop.vertex == from && loop.nextFaceLoop.vertex == to)
                return loop;
        }

        return null;
    }

    @Override
    public void write(Json json) {
        json.writeObjectStart("face");
        json.writeValue("loop", loop.getIndex());
        json.writeValue("vertices",getVertices());
        json.writeValue("edges",getEdges());
        json.writeObjectEnd();

    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }


    private static final class FaceLoopIterator implements Iterator<Loop> {
        private final Loop startLoop;
        private Loop currentLoop;
        private boolean first = true;

        public FaceLoopIterator(Loop loop) {
            startLoop = loop;
            currentLoop = loop;
        }

        @Override
        public boolean hasNext() {
            return currentLoop != startLoop || first;
        }

        @Override
        public Loop next() {
            first = false;
            Loop loop = currentLoop;
            currentLoop = currentLoop.nextFaceLoop;
            return loop;
        }
    }
}
