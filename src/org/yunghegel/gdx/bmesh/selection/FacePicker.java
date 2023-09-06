package org.yunghegel.gdx.bmesh.selection;

import org.yunghegel.gdx.bmesh.selection.picking.PickerShader;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.utils.picking.Pickable;

public class FacePicker extends ElementPicker<Face> {


    private FrameBuffer fbo;


    public PickableFace[] pickables;


    public FacePicker(BMesh mesh, Viewport viewport,ModelBatch batch, Camera cam) {
        super(mesh, viewport, batch, cam);
    }

    public static class PickableFace extends PickableElement<Face> {

        public Face face;
        public ModelInstance instance;

        public PickableFace(Face face, ModelInstance instance) {
            super(face);
            this.face = face;
            this.instance = instance;
            encode();
        }

        @Override
        public int getID() {
            return face.getIndex();
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

    protected PickableElement<Face>[] createPickables() {
        pickables = new PickableFace[mesh.faces().size()];
        super.pickables = pickables;
        ModelBuilder builder = new ModelBuilder();
        for(Face face: mesh.faces()){
            builder.begin();
            createFacePart(face,builder);
            ModelInstance faceModel = new ModelInstance(builder.end());
            pickables[face.getIndex()] = new PickableFace(face, faceModel);
        }
        return pickables;
    }

    private void createFacePart(Face face,ModelBuilder builder){
        Material mat = new Material();

        MeshPartBuilder b = builder.part("edge "+face.getIndex(), GL20.GL_TRIANGLES, VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorUnpacked, mat);
        Vertex v1 = face.getVertices().get(0);
        Vertex v2 = face.getVertices().get(1);
        Vertex v3 = face.getVertices().get(2);

        b.triangle(v1.getPosition(), v2.getPosition(), v3.getPosition());
    }

    @Override
    public Face pick(int screenX,int screenY){
        Pickable picked = picker.pick(viewport,batch,cam,screenX,screenY,pickables);
        if(picked instanceof PickableFace){
            return ((PickableFace) picked).face;
        }
        return null;
    }

}
