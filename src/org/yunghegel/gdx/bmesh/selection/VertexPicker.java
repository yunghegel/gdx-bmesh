package org.yunghegel.gdx.bmesh.selection;

import org.yunghegel.gdx.bmesh.selection.picking.PickerShader;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.viewport.Viewport;

public class VertexPicker extends ElementPicker<Vertex> {

    public VertexPicker(BMesh mesh, Viewport viewport, ModelBatch batch, Camera cam) {
        super(mesh, viewport, batch, cam);
    }

    public static class PickableVertex extends PickableElement<Vertex> {

        public Vertex vertex;
        public ModelInstance instance;

        public PickableVertex(Vertex vertex, ModelInstance instance) {
            super(vertex);
            this.vertex = vertex;
            this.instance = instance;
            encode();
        }

        @Override
        public int getID() {
            return vertex.getIndex();
        }

        @Override
        public Material getMaterial() {
            return instance.materials.first();
        }

        @Override
        public void renderPick(ModelBatch batch) {
            if (getElement().isCulled()) return;
            batch.render(instance, PickerShader.getInstance());
        }


    }



    @Override
    public Vertex pick(int screenX, int screenY) {
        Ray ray = viewport.getPickRay(screenX, screenY);
        for(Vertex v: mesh.vertices()){
            if(Intersector.intersectRaySphere(ray,v.getPosition(),0.05f,null)){
                return v;
            }


        }
        return null;
    }

    @Override
    protected PickableElement<Vertex>[] createPickables() {
        ModelBuilder builder = new ModelBuilder();
        pickables = new PickableElement[mesh.vertices().size()];
        for(Vertex vertex: mesh.vertices()){
            builder.begin();
            createVertexPart(vertex,builder);
            ModelInstance instance = new ModelInstance(builder.end());
            PickableVertex pickable = new PickableVertex(vertex, instance);
            pickables[vertex.getIndex()] = pickable;
        }

        return pickables;
    }

    public void createVertexPart(Vertex vertex, ModelBuilder builder){
        MeshPartBuilder b = builder.part("vertex"+vertex.getIndex(), GL20.GL_POINTS, VertexAttributes.Usage.Position,new Material());
        b.vertex(vertex.getPosition().x,vertex.getPosition().y,vertex.getPosition().z);
    }
}