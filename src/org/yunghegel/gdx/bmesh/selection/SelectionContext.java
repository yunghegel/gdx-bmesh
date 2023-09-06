package org.yunghegel.gdx.bmesh.selection;

import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Edge;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import org.yunghegel.gdx.bmesh.util.BMeshRenderer;
import org.yunghegel.gdx.bmesh.util.ElementCulling;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class SelectionContext {

    public BMesh mesh;

    private FacePicker facePicker;
    private EdgePicker edgePicker;
    private VertexPicker vertexPicker;

    public boolean mergeCoplanarTrianglesToQuads = true;

    private boolean enableMultipleSelection = true;

    private SelectionMode selectionMode;

    public ElementPicker enabledPicker;
    public Element selectedElement;
    public Array<Element> selectedElements = new Array<>();

    public static final int ENABLE_FACE_MODE = Input.Keys.F;

    public static final int ENABLE_EDGE_MODE = Input.Keys.E;

    public static final int ENABLE_VERTEX_MODE = Input.Keys.V;

    private Camera cam;

    private ArrayList<Face> tmpFaces = new ArrayList<>();
    private ArrayList<Edge> tmpEdges = new ArrayList<>();
    private ArrayList<Vertex> tmpVertices = new ArrayList<>();

    private ElementCulling culling;

    public SelectionContext(BMesh mesh, Viewport viewport, ModelBatch batch, Camera cam) {
        facePicker = new FacePicker(mesh, viewport, batch, cam);
        edgePicker = new EdgePicker(mesh, viewport, batch, cam);
        vertexPicker = new VertexPicker(mesh, viewport, batch, cam);
        this.cam = cam;
        this.mesh = mesh;
        selectionMode = SelectionMode.NONE;
    }

    public void setSelectionMode(SelectionMode mode) {
        selectionMode = mode;
        switch (mode) {
            case FACE:
                enabledPicker = facePicker;
                break;
            case EDGE:
                enabledPicker = edgePicker;
                break;
            case VERTEX:
                enabledPicker = vertexPicker;
                break;
            case NONE:
                enabledPicker = null;
                break;

        }
    }

    public void enableMultipleSelection(){
        enableMultipleSelection = true;

    }

    public void disableMultipleSelection(){
        enableMultipleSelection = false;
    }

    public SelectionMode getSelectionMode(){
        return selectionMode;
    }



    public void enableCulling(ElementCulling culling){
        this.culling = culling;
    }

    public Element pick(int screenX, int screenY) {
        if (enabledPicker != null) {

            //optionally cull elements that are not visible and eliminate unecessary draw calls
            if(culling!=null){
                culling.cull();
            }

            //query the picker for the current selection mode
            selectedElement = enabledPicker.pick(screenX, screenY);
            if (selectionMode.getElement().isInstance(selectedElement)) {

                //if the element is already selected, provide the mechanism to deselect it
                //TODO: seperate this logic so we can customize deselect behavior
                if(selectedElements.contains(selectedElement,false))
                    selectedElements.removeValue(selectedElement,false);

                //allow for multiple selection if shift is held
                else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && enableMultipleSelection) {
                    selectedElements.add(selectedElement);
                }

                //if not using multiple selection, deselect all other elements
                else {
                    deselectAll();
                    selectedElements.add(selectedElement);
                }

                //for face mode, we can optionally merge triangles which are both coplanar and adjacent into a single quad
                //useful for selecting faces which are part of a larger face, such as a cube - also more intuitive from a UX perspective
                /*
                 * TODO: support for n-gons -> might be too complicated, libGDX doesnt use n-gons anyway. requires triangulation algorithm
                 *  - also would be triangulating per-pick currently, so some sort of state/cache would be required to make it performant
                 */
                if(selectedElement instanceof Face && mergeCoplanarTrianglesToQuads){
                    Face f = (Face) selectedElement;
                    ArrayList<Face> faces;

//                    TODO: cache this list of coplanar faces and update when dirty to eliminate redundant topology iteration
                    faces = f.getCoplanarFaces();
                    faces.forEach(face -> {
                        if(!selectedElements.contains(face,false))
                            selectedElements.add(face);
                    });
                }
                selectedElement.setSelected(true);
                return selectedElement;
            } else {

            }
        }
        return null;

    }

    public Array<Element> getSelectedElements(){
        return selectedElements;
    }

    public void deselectAll(){
        selectedElements.forEach(element -> element.setSelected(false));
        selectedElements.clear();
    }

    public void processInput(){
        if(Gdx.input.isKeyJustPressed(ENABLE_FACE_MODE)){
            setSelectionMode(SelectionMode.FACE);
        }
        if(Gdx.input.isKeyJustPressed(ENABLE_EDGE_MODE)){
            setSelectionMode(SelectionMode.EDGE);
        }
        if(Gdx.input.isKeyJustPressed(ENABLE_VERTEX_MODE)){
            setSelectionMode(SelectionMode.VERTEX);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            setSelectionMode(SelectionMode.NONE);
        }
    }

    public void renderSelection(BMeshRenderer renderer){
        renderer.begin();
        renderer.setProjectionMatrix(cam.combined);

        tmpEdges.clear();
        tmpFaces.clear();
        tmpVertices.clear();


        for(Element selectedElement : selectedElements) {
        if(selectedElement instanceof Face){
            Face face = (Face) selectedElement;
            tmpFaces.add(face);

//            renderer.face(face,Color.ORANGE,false);
        }
        if(selectedElement instanceof Edge){
            Edge edge = (Edge) selectedElement;
            tmpEdges.add(edge);
//            renderer.edge(edge,true,Color.FIREBRICK,Color.RED,true);
        }
        if(selectedElement instanceof Vertex){
            Vertex vertex = (Vertex) selectedElement;
            tmpVertices.add(vertex);
//            renderer.vertex(vertex,Color.FIREBRICK,true);
        }}
        renderer.set(ShapeRenderer.ShapeType.Filled);
        for (Face f :tmpFaces) {
            renderer.face(f,Color.ORANGE,false);
        }

        renderer.vertices(Color.CORAL,false,tmpVertices);
        renderer.edges(true,Color.ORANGE,Color.CORAL,false,tmpEdges);
        renderer.set(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glLineWidth(2f);
        Color col = Color.ORANGE.cpy();
        col.a = 0.5f;
//        renderer.faces(col,false,tmpFaces);

        Gdx.gl.glLineWidth(1f);

        renderer.end();
    }

    public void renderDebugView(){
        enabledPicker.debugRender();
    }

    public void selectAll(){
        deselectAll();
        switch (selectionMode){
            case FACE:
                mesh.faces().forEach(selectedElements::add);

            case EDGE:
                mesh.edges().forEach(selectedElements::add);

            case VERTEX:
                mesh.vertices().forEach(selectedElements::add);
        }
    }

    public void setBMesh(BMesh bMesh){
        if(bMesh == null)
            return;
        if(bMesh==this.mesh)
            return;

        this.mesh = bMesh;
        facePicker.setBMesh(bMesh);
        edgePicker.setBMesh(bMesh);
        vertexPicker.setBMesh(bMesh);
    }

    }




