package org.yunghegel.gdx.bmesh.structure;


import org.yunghegel.gdx.bmesh.attribute.Element;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Loop extends Element implements Json.Serializable {
    /**
     * The associated Face.<br>
     * Never <code>null</code> on a valid object.
     */
    public Face face;

    /**
     * The associated Edge.<br>
     * Never <code>null</code> on a valid object.
     */
    public Edge edge;

    // Reference is needed for properly defining winding order.
    // Can't rely on BMEdge's reference, since BMEdge has no specifc direction.
    // Can also store in this loop whether the vertex was merged/remapped during conversion?
    /**
     * Source Vertex.<br>
     * Never {@code null} on a valid object.
     */
    public Vertex vertex;


    /**
     * The next Loop in the <i>Loop Cycle</i> around the associated Face.
     * Never {@code null} on a valid object.<br><br>
     * These references form a circular linked list of the Face's Loops.
     * This defines the winding order of the Face.
     */
    public Loop nextFaceLoop; // Blender calls this 'next'

    /**
     * The previous Loop in the <i>Loop Cycle</i> around the associated Face.
     * Never {@code null} on a valid object.<br><br>
     * These references form a circular linked list of the Face's Loops.
     * This defines the winding order of the Face.
     */
    public Loop prevFaceLoop; // Blender calls this 'prev'


    /**
     * The next Loop in the <i>Radial Cycle</i> around the associated Edge.
     * Never {@code null} on a valid object.<br><br>
     * These references form a circular linked list of the edge's loops.
     * No sorting takes place.<br>The <i>Radial Cycle</i> is <b>unordered</b>.
     */
    public Loop nextEdgeLoop = this; // Blender calls this 'radialNext'

    /**
     * The previous Loop in the <i>Radial Cycle</i> around the associated Edge.
     * Never {@code null} on a valid object.<br><br>
     * These references form a circular linked list of the edge's loops.
     * No sorting takes place.<br>The <i>Radial Cycle</i> is <b>unordered</b>.
     */
    public Loop prevEdgeLoop = this; // Blender calls this 'radialPrev'


    Loop() {}




    @Override
    protected void releaseElement() {
        face = null;
        edge = null;
        vertex = null;
        nextFaceLoop = null;
        prevFaceLoop = null;
        nextEdgeLoop = this;
        prevEdgeLoop = this;
    }

    @Override
    public void rebuild() {

    }

    @Override
    public void getMatrixRepresentation() {

    }


    public void faceSetBetween(final Loop prev, final Loop next) {
        //assert prev.nextFaceLoop == next;
        //assert next.prevFaceLoop == prev;

        prevFaceLoop = prev;
        nextFaceLoop = next;
        prev.nextFaceLoop = this;
        next.prevFaceLoop = this;
    }

    public void faceRemove() {
        nextFaceLoop.prevFaceLoop = prevFaceLoop;
        prevFaceLoop.nextFaceLoop = nextFaceLoop;
        prevFaceLoop = this;
        nextFaceLoop = this;
    }


    public void radialSetBetween(final Loop prev, final Loop next) {
        assert prev.nextEdgeLoop == next;
        assert next.prevEdgeLoop == prev;

        prevEdgeLoop = prev;
        nextEdgeLoop = next;
        prev.nextEdgeLoop = this;
        next.prevEdgeLoop = this;
    }

    public void radialRemove() {
        nextEdgeLoop.prevEdgeLoop = prevEdgeLoop;
        prevEdgeLoop.nextEdgeLoop = nextEdgeLoop;
        prevEdgeLoop = this;
        nextEdgeLoop = this;
    }

    @Override
    public void write(Json json) {

        json.writeObjectStart("loop");
        json.writeValue("index" , index);
        json.writeValue("face", face);
        json.writeValue("edge", edge);
        json.writeValue("vertex", vertex);
        json.writeValue("nextFaceLoop", nextFaceLoop.index);
        json.writeValue("prevFaceLoop", prevFaceLoop.index);
        json.writeValue("nextEdgeLoop", nextEdgeLoop.index);
        json.writeValue("prevEdgeLoop", prevEdgeLoop.index);
        json.writeObjectEnd();


    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }
}
