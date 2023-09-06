package org.yunghegel.gdx.bmesh.lookup;

import org.yunghegel.gdx.bmesh.attribute.MeshAttribute;
import org.yunghegel.gdx.bmesh.attribute.type.Vec3Attribute;

import org.yunghegel.gdx.bmesh.util.HashGrid;
import com.badlogic.gdx.math.Vector3;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Vertex;

import java.util.ArrayList;
import java.util.List;

public class HashGridDeduplication implements VertexDeduplication {

    private static final int[][][] WALK_DIRECTION = { // [8][7][3]
            {{-1, 0, 0}, {0, -1, 0}, {0, 0, -1},    {-1, -1, 0}, {-1, 0, -1}, {0, -1, -1},  {-1, -1, -1}},  // -X, -Y, -Z
            {{-1, 0, 0}, {0, -1, 0}, {0, 0, 1},     {-1, -1, 0}, {-1, 0, 1}, {0, -1, 1},    {-1, -1, 1}},   // -X, -Y, +Z
            {{-1, 0, 0}, {0, 1, 0}, {0, 0, -1},     {-1, 1, 0}, {-1, 0, -1}, {0, 1, -1},    {-1, 1, -1}},   // -X, +Y, -Z
            {{-1, 0, 0}, {0, 1, 0}, {0, 0, 1},      {-1, 1, 0}, {-1, 0, 1}, {0, 1, 1},      {-1, 1, 1}},    // -X, +Y, +Z
            {{1, 0, 0}, {0, -1, 0}, {0, 0, -1},     {1, -1, 0}, {1, 0, -1}, {0, -1, -1},    {1, -1, -1}},   // +X, -Y, -Z
            {{1, 0, 0}, {0, -1, 0}, {0, 0, 1},      {1, -1, 0}, {1, 0, 1}, {0, -1, 1},      {1, -1, 1}},    // +X, -Y, +Z
            {{1, 0, 0}, {0, 1, 0}, {0, 0, -1},      {1, 1, 0}, {1, 0, -1}, {0, 1, -1},      {1, 1, -1}},    // +X, +Y, -Z
            {{1, 0, 0}, {0, 1, 0}, {0, 0, 1},       {1, 1, 0}, {1, 0, 1}, {0, 1, 1},        {1, 1, 1}},     // +X, +Y, +Z
    };


    private final float epsilon;
    private final float epsilonSquared;
    private final float cellSize;

    private final BMesh bmesh;
    private final Vec3Attribute<Vertex> positions;
    private final HashGrid<List<Vertex>> grid;
    private final Vector3 p = new Vector3();


    public HashGridDeduplication(BMesh bmesh) {
        this(bmesh, HashGrid.DEFAULT_CELLSIZE);
    }

    public HashGridDeduplication(BMesh bmesh, float epsilon) {
        this.bmesh = bmesh;
        this.epsilon = epsilon;
        epsilonSquared = epsilon * epsilon;
        cellSize = epsilon * 2.0f;

        grid = new HashGrid<>(cellSize);
        positions = Vec3Attribute.get(MeshAttribute.Position, bmesh.vertices());
    }


    @Override
    public void addExisting(Vertex vertex) {
        positions.get(vertex, p);

        HashGrid.Index gridIndex = grid.getIndexForCoords(p);
        List<Vertex> vertices = grid.get(gridIndex);
        if(vertices == null) {
            vertices = new ArrayList<>(1);
            grid.set(gridIndex, vertices);
        }

        vertices.add(vertex);
    }

    public void remove(Vertex vertex) {
        positions.get(vertex, p);

        HashGrid.Index gridIndex = grid.getIndexForCoords(p);
        List<Vertex> vertices = grid.get(gridIndex);
        if(vertices != null) {
            vertices.remove(vertex);
            if(vertices.isEmpty()) {
                grid.remove(gridIndex);
            }
        }
    }


    @Override
    public void clear() {
        grid.clear();
    }


    @Override
    public Vertex getVertex(Vector3 position) {
        HashGrid.Index gridIndex = grid.getIndexForCoords(position);
        List<Vertex> vertices = grid.get(gridIndex);

        if(vertices != null) {
            Vertex vertex = searchVertex(vertices, position);
            if(vertex != null)
                return vertex;
        }

        return searchVertexWalk(gridIndex, position);
    }


    @Override
    public Vertex getOrCreateVertex(Vector3 position) {
        HashGrid.Index gridIndex = grid.getIndexForCoords(position);
        List<Vertex> centerVertices = grid.get(gridIndex);

        if(centerVertices != null) {
            Vertex vertex = searchVertex(centerVertices, position);
            if(vertex != null)
                return vertex;
        }

        Vertex vertex = searchVertexWalk(gridIndex, position);
        if(vertex != null)
            return vertex;

        if(centerVertices == null) {
            centerVertices = new ArrayList<>(1);
            grid.set(gridIndex, centerVertices);
        }

        vertex = bmesh.createVertex(position);
        centerVertices.add(vertex);
        return vertex;
    }

    public Vertex getOrCreateVertex(Vector3 position, Vertex[] indexList,int idx) {
        HashGrid.Index gridIndex = grid.getIndexForCoords(position);
        List<Vertex> centerVertices = grid.get(gridIndex);

        //if we determine that a vertex with this position already exists, we need to populate the indexList with that vertex
        //this ensures that when we are creating faces, we can query the indexList without getting a null pointer exception
        if(centerVertices != null) {
            Vertex vertex = searchVertex(centerVertices, position);
            if(vertex != null) {
                indexList[idx] = vertex;
                return vertex;
            }
        }

        Vertex vertex = searchVertexWalk(gridIndex, position);
        if(vertex != null) {
            indexList[idx] = vertex;
            return vertex;
        }

        if(centerVertices == null) {
            centerVertices = new ArrayList<>(1);
            grid.set(gridIndex, centerVertices);
        }

        vertex = bmesh.createVertex(position);
        centerVertices.add(vertex);
        indexList[idx] = vertex;
        return vertex;

    }




    private int[][] getWalkDirections(HashGrid.Index gridIndex, Vector3 position) {
        float pivotX = (gridIndex.x * cellSize) - epsilon;
        float pivotY = (gridIndex.y * cellSize) - epsilon;
        float pivotZ = (gridIndex.z * cellSize) - epsilon;

        int index = 0;
        if(position.z > pivotZ) index |= 1;
        if(position.y > pivotY) index |= 2;
        if(position.x > pivotX) index |= 4;

        return WALK_DIRECTION[index];
    }


    private Vertex searchVertexWalk(HashGrid.Index gridIndex, Vector3 position) {
        int[][] directions = getWalkDirections(gridIndex, position);

        for(int[] dir : directions) {
            List<Vertex> vertices = grid.getNeighbor(gridIndex, dir[0], dir[1], dir[2]);
            if(vertices == null)
                continue;

            Vertex vertex = searchVertex(vertices, position);
            if(vertex != null)
                return vertex;
        }

        return null;
    }


    private Vertex searchVertex(List<Vertex> vertices, Vector3 position) {
        for(Vertex vertex : vertices) {
            positions.get(vertex, p);
            if(p.dst2(position) <= epsilonSquared)
                return vertex;
        }

        return null;
    }
}
