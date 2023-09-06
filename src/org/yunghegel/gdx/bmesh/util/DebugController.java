package org.yunghegel.gdx.bmesh.util;

import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Edge;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

public class DebugController {

    BMesh bMesh;
    BMeshRenderer BMeshRenderer;
    ElementCulling elementCulling;

    boolean debug = false;

    boolean drawWireframe = false;

    boolean drawVertices = false;
    boolean drawFaces = false;
    boolean drawEdges = false;

    boolean showVertexIndices = false;
    boolean showFaceIndices = false;
    boolean showEdgeIndices = false;

    boolean cullElements = false;

    public static Color WIREFRAME_COLOR = Color.WHITE;
    public static Color VERTEX_COLOR = Color.RED;
    public static Color FACE_COLOR = Color.GREEN;
    public static Color EDGE_COLOR = Color.BLUE;
    public static Color EDGE_ARROW_COLOR = Color.YELLOW;



    public DebugController(BMesh bMesh, BMeshRenderer BMeshRenderer) {
        this.bMesh = bMesh;
        this.BMeshRenderer = BMeshRenderer;
        elementCulling = new ElementCulling(bMesh, BMeshRenderer.cam);
    }

    public void renderVertices() {
        if(!drawVertices) return;
        if(cullElements) BMeshRenderer.vertices(VERTEX_COLOR,showVertexIndices,elementCulling.cull().getVisibleVertices());
        else BMeshRenderer.vertices(bMesh,VERTEX_COLOR,showVertexIndices);
    }

    public void renderFaces() {
        if(!drawFaces) return;
        if(cullElements) BMeshRenderer.faces(FACE_COLOR,showFaceIndices,elementCulling.cull().getVisibleFaces());
        else BMeshRenderer.faces(bMesh,FACE_COLOR,showFaceIndices);
    }

    public void renderEdges() {
        if(!drawEdges) return;
        if(cullElements) BMeshRenderer.edges(true,EDGE_COLOR,EDGE_ARROW_COLOR,drawVertices,elementCulling.cull().getVisibleEdges());
        else BMeshRenderer.edges(bMesh,true,EDGE_COLOR,EDGE_ARROW_COLOR,showEdgeIndices);
    }

    public void renderEdgesAroundVertex(Vertex vertex) {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        vertex.edges().forEach(edges::add);
        BMeshRenderer.edges(true,EDGE_COLOR,EDGE_ARROW_COLOR,drawVertices,edges);
    }

    public void renderEdgesAroundFace(Face face) {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        face.edges().forEach(edges::add);
        BMeshRenderer.edges(true,EDGE_COLOR,EDGE_ARROW_COLOR,drawVertices,edges);
    }

    public void renderEdgesAroundEdge(Edge edge) {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        Edge e = edge;

        BMeshRenderer.edges(true,EDGE_COLOR,EDGE_ARROW_COLOR,drawVertices,edges);
    }

    public void renderEdgeLoop(Edge edge) {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        Edge e = edge;
        do {
            edges.add(e);
            e = (e.v1NextEdge==null)?e.v0NextEdge:e.v1NextEdge;

        } while (e != edge);
        BMeshRenderer.edges(true,EDGE_COLOR,EDGE_ARROW_COLOR,drawVertices,edges);
    }

    public void renderEdgePointers(Edge edge){
        BMeshRenderer.edge(edge,true,EDGE_COLOR,EDGE_ARROW_COLOR,false);
        BMeshRenderer.label("E",edge.midPoint,EDGE_COLOR,true,3);
        BMeshRenderer.edge(edge.v0PrevEdge,false,EDGE_COLOR,EDGE_ARROW_COLOR,false);
        BMeshRenderer.label("v0_PE",edge.v0PrevEdge.midPoint,EDGE_COLOR,true,3);
        BMeshRenderer.edge(edge.v0NextEdge,false,EDGE_COLOR,EDGE_ARROW_COLOR,false);
        BMeshRenderer.label("v0_NE",edge.v0NextEdge.midPoint,EDGE_COLOR,true,3);
        BMeshRenderer.edge(edge.v1PrevEdge,false,EDGE_COLOR,EDGE_ARROW_COLOR,false);
        BMeshRenderer.label("v1_PE",edge.v1PrevEdge.midPoint,EDGE_COLOR,true,3);
        BMeshRenderer.edge(edge.v1NextEdge,false,EDGE_COLOR,EDGE_ARROW_COLOR,false);
        BMeshRenderer.label("v1_NE",edge.v1NextEdge.midPoint,EDGE_COLOR,true,3);

        renderVerticesOfEdge(edge);
//        renderVerticesOfEdge(edge.v0PrevEdge);
//        renderVerticesOfEdge(edge.v1NextEdge);



    }

    public void renderVerticesOfEdge(Edge edge){
        BMeshRenderer.vertex(edge.vertex0,VERTEX_COLOR,false);
        BMeshRenderer.text(edge.vertex0.position,"v0");
        BMeshRenderer.vertex(edge.vertex1,VERTEX_COLOR,false);
        BMeshRenderer.text(edge.vertex1.position, "v1");
    }

}
