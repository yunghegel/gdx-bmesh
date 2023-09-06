package org.yunghegel.gdx.bmesh.structure;


import org.yunghegel.gdx.bmesh.attribute.Element;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Iterator;

public class Vertex extends Element implements Json.Serializable {
    /**
     * Any adjacent Edge.<br>
     * Can be <code>null</code> when there are no adjacent Edges (e.g. point meshes).
     */
    public Edge edge;
    public Vector3 position = new Vector3();


    public Vertex() {}

    @Override
    public int hashCode() {
        //encodes the position of the vertex, bitwise such that multiple hashcodes can be combined in a single integer
        return (int) (position.x * 1000) ^ (int) (position.y * 1000) ^ (int) (position.z * 1000);
    }

    public boolean has(Element element){
        int hash = element.hashCode();
        int thisHash = this.hashCode();
        return (hash & thisHash) == hash;
    }


    @Override
    public void releaseElement() {
        edge = null;
    }

    @Override
    public void rebuild() {

    }

    @Override
    public void getMatrixRepresentation() {

    }

    public void setPosition(Vector3 position) {
        this.position.set(position);
    }

    public Vector3 getPosition() {
        return position;
    }


    /**
     * Inserts Edge at end of disk cycle at this Vertex.
     * @param edge A newly created Edge which is adjacent to this Vertex.
     */
    public void addEdge(Edge edge) {
        // Edge cannot already belong to a disk cycle
        // Will throw if edge is null or not adjacent
        // TODO: Does this make modifications more difficult?
//        if(edge.getNextEdge(this)==null && edge.getPrevEdge(this)==null){
//            edge.diskSetBetween(this, edge, edge);
//            return;
//        }


        if(edge.getNextEdge(this) != edge){
//            return;
//            throw new IllegalArgumentException("Edge already belongs to a disk cycle");
        }

        assert edge.getPrevEdge(this) == edge;

        // Insert edge at end of disk cycle
        if(this.edge == null)
            this.edge = edge;
        else
            edge.diskSetBetween(this, this.edge.getPrevEdge(this), this.edge);
    }


    public void removeEdge(Edge edge) {
        // Do this first so it will throw if edge is null or not adjacent
        Edge next = edge.getNextEdge(this);

        if(this.edge == edge) {
            if(next == edge) {
                // Edge was the only one in disk cycle
                assert edge.getPrevEdge(this) == edge;
                this.edge = null;
            } else {
                edge.diskRemove(this);
                this.edge = next;
            }

            return;
        }

        // Check for null so it will throw IllegalArgumentException and not NPE, regardless of this object's state
        if(this.edge != null) {
            // Check if 'edge' exists in disk cycle
            // TODO: Start from 'edge' and check if 'this.edge' is reachable? -> Less iterations?
            //       Or remove this check?
            Edge current = this.edge.getNextEdge(this);
            while(current != this.edge) {
                if(current == edge) {
                    edge.diskRemove(this);
                    return;
                }

                current = current.getNextEdge(this);
            }
        }

        throw new IllegalArgumentException("Edge does not exist in disk cycle for Vertex");
    }


    // TODO: Allow multiple Edges between two vertices?
    public Edge getEdgeTo(Vertex other) {
        if(edge == null)
            return null;

        Edge current = this.edge;
        int iterations = 0;
        do {
            iterations++;
            if(iterations > 100)
                return null;
            if(current.connects(this, other))
                return current;
            current = current.getNextEdge(this);
        } while(current != this.edge);

        return null;
    }


    public Face getCommonFace(Vertex other) {
        for(Edge edgeThis : edges()) {
            for(Loop loopThis : edgeThis.loops()) {
                for(Edge edgeOther : other.edges()) {
                    for(Loop loopOther : edgeOther.loops()) {
                        if(loopThis.face == loopOther.face)
                            return loopThis.face;
                    }
                }
            }
        }

        return null;
    }


    public Iterable<Edge> edges() {
        return VertexEdgeIterator::new;
    }

    @Override
    public void write(Json json) {
        json.writeObjectStart("vertex");
        json.writeValue("index", index);
        json.writeValue("position", position);
        json.writeValue("edge", edge);
        json.writeObjectEnd();

    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }

    private class VertexEdgeIterator implements Iterator<Edge> {
        private Edge current;
        private boolean first;

        public VertexEdgeIterator() {
            current = Vertex.this.edge;
            first = (current != null);
        }

        @Override
        public boolean hasNext() {
            return (current != Vertex.this.edge) || first;
        }

        @Override
        public Edge next() {
            first = false;
            Edge edge = current;
            current = current.getNextEdge(Vertex.this);
            return edge;
        }
    }



    /*public Iterable<Face> faces() {
        return VertexFaceIterator::new;
    }*/
}
