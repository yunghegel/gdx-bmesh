package org.yunghegel.gdx.bmesh.util;

import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Edge;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.graphics.Camera;

import java.util.ArrayList;


public class ElementCulling {

    ArrayList<Face> visibleFaces=new ArrayList<Face>();
    ArrayList<Vertex> visibleVertices=new ArrayList<Vertex>();
    ArrayList<Edge> visibleEdges=new ArrayList<Edge>();

    private final BMesh bmesh;
    private Camera camera;
    public ElementCulling(BMesh bmesh, Camera camera){
        this.bmesh=bmesh;
        this.camera=camera;
    }

    public ElementCulling cull(){
        visibleFaces.clear();
        visibleVertices.clear();
        visibleEdges.clear();
        for(int i=0;i<bmesh.faces().size();i++){
            Face face=bmesh.faces().get(i);
            if(face.isFrontFacing(camera)){
                visibleFaces.add(face);
                face.setCulled(false);
            } else {
                face.setCulled(true);
            }
        }
        for(Face face:visibleFaces){
            face.getVertices().iterator().forEachRemaining(vertex -> {
                if(!visibleVertices.contains(vertex)){
                    visibleVertices.add(vertex);
                    vertex.setCulled(false);
                } else {
                    vertex.setCulled(true);
                }
            });
            face.getEdges().iterator().forEachRemaining(edge -> {
                if(!visibleEdges.contains(edge)){
                    visibleEdges.add(edge);
                    edge.setCulled(false);
                } else {
                    edge.setCulled(true);
                }
            });
        }

        return this;

    }

    public ArrayList<Face> getVisibleFaces(){
        return visibleFaces;
    }

    public ArrayList<Vertex> getVisibleVertices(){
        return visibleVertices;
    }

    public ArrayList<Edge> getVisibleEdges(){
        return visibleEdges;
    }



}
