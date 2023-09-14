package org.yunghegel.gdx.bmesh.selection;

import org.yunghegel.gdx.bmesh.selection.picking.PickerShader;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Edge;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.picking.Pickable;

public class EdgePicker extends ElementPicker<Edge> {

    public static class PickableEdge extends PickableElement<Edge> {

        Edge edge;
        public ModelInstance instance;

        public PickableEdge(Edge edge,ModelInstance instance) {
            super(edge);
            this.edge = edge;
            this.instance=instance;
            encode();
        }



        @Override
        public int getID() {
            return edge.getIndex();
        }

        @Override
        public Material getMaterial() {
            return instance.materials.first();
        }

        @Override
        public void renderPick(ModelBatch batch) {
            if (getElement().isCulled()) return;
            batch.render(instance, PickerShader.getInstance());
//            Gdx.gl.glLineWidth(1);
        }
    }



    public EdgePicker(BMesh mesh,Viewport viewport,ModelBatch batch,Camera cam){
        super(mesh,viewport,batch,cam);
    }

    private void createEdgePart(Edge edge,ModelBuilder modelBuilder){
        Material mat = new Material();

        MeshPartBuilder builder = modelBuilder.part("edge "+edge.getIndex(), GL20.GL_LINES, VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorUnpacked, mat);
        builder.line(edge.vertex0.position,edge.vertex1.position);
    }



    @Override
    protected PickableElement<Edge>[] createPickables(){
        ModelBuilder modelBuilder = new ModelBuilder();

        super.pickables=new PickableEdge[mesh.edges().size()];

        int i=0;
        for(Edge edge : mesh.edges()){
            modelBuilder.begin();
            createEdgePart(edge,modelBuilder);
            ModelInstance edgeModel = new ModelInstance(modelBuilder.end());
            pickables[i++]=new PickableEdge(edge,edgeModel);
        }

        return pickables;
    }

    @Override
    public Edge pick(int screenX,int screenY){

        Pickable picked = picker.pick(viewport,batch,cam,screenX,screenY,pickables);
        if(picked==null){
            return null;
        }
        return ((PickableEdge)picked).edge;
    }




}
