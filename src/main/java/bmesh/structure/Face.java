package bmesh.structure;



import bmesh.attribute.Element;
import bmesh.util.LoopMapIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Face extends Element {
    /**
     * Any Loop of this Face.<br>
     * Never <code>null</code> on a valid object.
     */
    public Loop loop;


    Face() {}


    @Override
    protected void releaseElement() {
        loop = null;
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
