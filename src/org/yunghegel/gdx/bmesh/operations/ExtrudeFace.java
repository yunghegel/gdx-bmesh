package org.yunghegel.gdx.bmesh.operations;

import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Loop;
import org.yunghegel.gdx.bmesh.structure.Vertex;

import java.util.ArrayList;
import java.util.List;

public class ExtrudeFace {
    private final BMesh bmesh;

    private final transient List<Loop> tempLoops = new ArrayList<>(4);

    // Results
    private Face face = null;
    private final List<Vertex> originalVertices = new ArrayList<>(4);
    private final List<Face> resultFaces = new ArrayList<>(4);


    public ExtrudeFace(BMesh bmesh) {
        this.bmesh = bmesh;
    }


    public void apply(Face face) {
        // Disconnect face
        // Keep loops, but disconnect
        // Leave vertices, create new Vertices (without attributes)
        // (---> no, also new loops, because the original loops have attributes) ???
        //    -> no, keep loops because they belong to the face - attributes are for this face

        // n = num vertices
        // n new Faces -> quads
        // insert new faces

        this.face = face;

        try {
            resultFaces.clear();
            originalVertices.clear();

            // Gather loops and create new vertices for selected Face
            for(Loop loop : face.loops()) {
                tempLoops.add(loop);
                originalVertices.add(loop.vertex);

                loop.vertex = bmesh.createVertex();
                loop.edge.removeLoop(loop);
            }

            for(int i=0; i<tempLoops.size(); ++i) {
                int nextIndex = (i+1) % tempLoops.size();

                Loop loop = tempLoops.get(i);
                Loop nextLoop = tempLoops.get(nextIndex);

                Face newFace = bmesh.createFace(nextLoop.vertex, loop.vertex, originalVertices.get(i), originalVertices.get(nextIndex));
                resultFaces.add(newFace);

                loop.edge = loop.vertex.getEdgeTo(nextLoop.vertex);
                loop.edge.addLoop(loop);
            }
        }
        finally {
            tempLoops.clear();
        }
    }


    /**
     * Copy attributes from old vertices to the new ones.
     */
    public void copyVertexAttributes() {
        Loop loop = face.loop;
        for(int i=0; i<originalVertices.size(); ++i) {
            bmesh.vertices().copyAttributes(originalVertices.get(i), loop.vertex);
            loop = loop.nextFaceLoop;
        }
    }


    public List<Face> getResultFaces() {
        return resultFaces;
    }
}
