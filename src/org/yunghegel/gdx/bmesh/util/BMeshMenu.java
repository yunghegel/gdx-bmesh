package org.yunghegel.gdx.bmesh.util;


import org.yunghegel.gdx.bmesh.selection.SelectionContext;
import org.yunghegel.gdx.bmesh.selection.SelectionMode;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Edge;
import org.yunghegel.gdx.bmesh.structure.Face;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

import java.util.ArrayList;

public class BMeshMenu extends PopupMenu {

    Stage stage;
    BMesh bMesh;
    SelectionContext selectionManager;

    MenuItem selectionModeMenu;
    PopupMenu selectionModeSubmenu;
    MenuItem selectionModeVertex;
    MenuItem selectionModeEdge;
    MenuItem selectionModeFace;
    MenuItem selectionModeNone;

    MenuItem selectionQueryMenu;
    PopupMenu selectionQuerySubmenu;

    MenuItem selectNone;
    MenuItem selectAll;

    //EDGE SELECTION
    MenuItem edgeSelectionQueryMenu;
    PopupMenu edgeSelectionQuerySubmenu;

    MenuItem selectEdgeVertices;
    MenuItem selectEdgeFaces;

    MenuItem selectV0NextEdge;
    MenuItem selectV0PrevEdge;
    MenuItem selectV1NextEdge;
    MenuItem selectV1PrevEdge;

    //VERTEX SELECTION
    MenuItem vertexSelectionQueryMenu;
    PopupMenu vertexSelectionQuerySubmenu;

    //FACE SELECTION
    MenuItem faceSelectionQueryMenu;
    PopupMenu faceSelectionQuerySubmenu;
    MenuItem selectAdjacentFaces;
    MenuItem selectCoplanarFaces;






    public BMeshMenu(Stage stage, BMesh bMesh, SelectionContext selectionManager) {
        super();
        this.stage = stage;
        this.bMesh = bMesh;
        this.selectionManager = selectionManager;
        createSelectionMenu();
        createSelectionQueryMenu();
    }

    private void createSelectionQueryMenu() {
        selectionQueryMenu = new MenuItem("Selection Query");
        selectionQuerySubmenu = new PopupMenu();
        selectionQueryMenu.setSubMenu(selectionQuerySubmenu);
        selectNone = new MenuItem("Deselect All");
        selectAll = new MenuItem("Select All");
        selectionQuerySubmenu.addItem(selectNone);
        selectionQuerySubmenu.addItem(selectAll);

        edgeSelectionQueryMenu = new MenuItem("Edge");
        vertexSelectionQueryMenu = new MenuItem("Vertex");
        faceSelectionQueryMenu = new MenuItem("Face");
        selectionQuerySubmenu.addItem(edgeSelectionQueryMenu);
        selectionQuerySubmenu.addItem(vertexSelectionQueryMenu);
        selectionQuerySubmenu.addItem(faceSelectionQueryMenu);

        //EDGE SUBMENU
        edgeSelectionQuerySubmenu = new PopupMenu();
        edgeSelectionQueryMenu.setSubMenu(edgeSelectionQuerySubmenu);
        selectEdgeVertices = new MenuItem("Edge Vertices");
        selectEdgeFaces = new MenuItem("Adjacent Faces");
        selectV0NextEdge = new MenuItem("V0 Next Edge");
        selectV0PrevEdge = new MenuItem("V0 Prev Edge");
        selectV1NextEdge = new MenuItem("V1 Next Edge");
        selectV1PrevEdge = new MenuItem("V1 Prev Edge");

        edgeSelectionQuerySubmenu.addItem(selectEdgeVertices);
        edgeSelectionQuerySubmenu.addItem(selectEdgeFaces);
        edgeSelectionQuerySubmenu.addSeparator();
        edgeSelectionQuerySubmenu.addItem(selectV0NextEdge);
        edgeSelectionQuerySubmenu.addItem(selectV0PrevEdge);
        edgeSelectionQuerySubmenu.addItem(selectV1NextEdge);
        edgeSelectionQuerySubmenu.addItem(selectV1PrevEdge);

        selectEdgeVertices.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectionManager.selectedElement instanceof Edge) {
                    Edge e = (Edge) selectionManager.selectedElement;
                    selectionManager.deselectAll();
                    selectionManager.selectedElements.add(e.vertex0, e.vertex1);
                    selectionManager.selectedElement=null;
                } else {
                    System.out.println("Edge selection query failed: selected element is not an edge");
                }
            }
        });

        selectEdgeFaces.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectionManager.selectedElement instanceof Edge) {
                    Edge e = (Edge) selectionManager.selectedElement;
                    selectionManager.deselectAll();
                    e.faces().forEach(f -> selectionManager.selectedElements.add(f));
                    selectionManager.selectedElement=null;
                } else {
                    System.out.println("Edge selection query failed: selected element is not an edge");
                }
            }
        });

        selectV0NextEdge.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectionManager.selectedElement instanceof Edge) {
                    Edge e = (Edge) selectionManager.selectedElement;
                    selectionManager.deselectAll();
                    selectionManager.selectedElements.add(e.getNextEdge(e.vertex0));
                    selectionManager.selectedElement=null;
                } else {
                    System.out.println("Edge selection query failed: selected element is not an edge");
                }
            }
        });

        selectV0PrevEdge.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectionManager.selectedElement instanceof Edge) {
                    Edge e = (Edge) selectionManager.selectedElement;
                    selectionManager.deselectAll();
                    selectionManager.selectedElements.add(e.getPrevEdge(e.vertex0));
                    selectionManager.selectedElement=null;
                } else {
                    System.out.println("Edge selection query failed: selected element is not an edge");
                }
            }
        });

        selectV1NextEdge.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectionManager.selectedElement instanceof Edge) {
                    Edge e = (Edge) selectionManager.selectedElement;
                    selectionManager.deselectAll();
                    selectionManager.selectedElements.add(e.getNextEdge(e.vertex1));
                    selectionManager.selectedElement=null;
                } else {
                    System.out.println("Edge selection query failed: selected element is not an edge");
                }
            }
        });

        selectV1PrevEdge.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectionManager.selectedElement instanceof Edge) {
                    Edge e = (Edge) selectionManager.selectedElement;
                    selectionManager.deselectAll();
                    selectionManager.selectedElements.add(e.getPrevEdge(e.vertex1));
                    selectionManager.selectedElement=null;
                } else {
                    System.out.println("Edge selection query failed: selected element is not an edge");
                }
            }
        });


        faceSelectionQuerySubmenu = new PopupMenu();
        faceSelectionQueryMenu.setSubMenu(faceSelectionQuerySubmenu);
        selectAdjacentFaces = new MenuItem("Adjacent Faces");
        selectCoplanarFaces = new MenuItem("Coplanar Faces");
        faceSelectionQuerySubmenu.addItem(selectAdjacentFaces);
        faceSelectionQuerySubmenu.addItem(selectCoplanarFaces);

        selectCoplanarFaces.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectionManager.selectedElement instanceof Face) {
                    Face f = (Face) selectionManager.selectedElement;

                    ArrayList<Face> coplanarFaces = f.getCoplanarFaces();
                    coplanarFaces.forEach(f2 -> selectionManager.selectedElements.add(f2));
                    selectionManager.selectedElement=null;
                } else {
                    System.out.println("Face selection query failed: selected element is not a face");
                }

            }
        });

        selectAdjacentFaces.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectionManager.selectedElement instanceof Face) {
                    Face f = (Face) selectionManager.selectedElement;
                    selectionManager.deselectAll();
                    for(Edge e : f.edges()) {
                        e.faces().forEach(f2 -> selectionManager.selectedElements.add(f2));
                    }
                    selectionManager.selectedElement=null;
                } else {
                    System.out.println("Face selection query failed: selected element is not a face");
                }
            }
        });

        vertexSelectionQuerySubmenu = new PopupMenu();
        vertexSelectionQueryMenu.setSubMenu(vertexSelectionQuerySubmenu);

        selectNone.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectionManager.selectedElements.clear();
            }
        });

        selectAll.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectionManager.selectAll();
            }
        });

        addItem(selectionQueryMenu);
    }

    public void show() {
        showMenu(stage, Gdx.input.getX(), Gdx.graphics.getHeight()-Gdx.input.getY());
    }

    void createSelectionMenu() {
        selectionModeMenu = new MenuItem("Selection Mode");
        selectionModeSubmenu = new PopupMenu();
        selectionModeMenu.setSubMenu(selectionModeSubmenu);
        selectionModeVertex = new MenuItem("Vertex");
        selectionModeEdge = new MenuItem("Edge");
        selectionModeFace = new MenuItem("Face");
        selectionModeNone = new MenuItem("None");
        selectionModeSubmenu.addItem(selectionModeVertex);
        selectionModeSubmenu.addItem(selectionModeEdge);
        selectionModeSubmenu.addItem(selectionModeFace);
        selectionModeSubmenu.addItem(selectionModeNone);

        selectionModeEdge.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectionManager.setSelectionMode(SelectionMode.EDGE);
            }
        });

        selectionModeVertex.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectionManager.setSelectionMode(SelectionMode.VERTEX);
            }
        });

        selectionModeFace.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectionManager.setSelectionMode(SelectionMode.FACE);
            }
        });

        selectionModeNone.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectionManager.setSelectionMode(SelectionMode.NONE);
            }
        });

        addItem(selectionModeMenu);
    }

    public void processInput(){
        if(Gdx.input.isButtonJustPressed(1)){
            show();
        }
    }



}
