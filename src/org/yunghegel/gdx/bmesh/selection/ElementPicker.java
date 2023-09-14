package org.yunghegel.gdx.bmesh.selection;

import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.picking.Picker;
<<<<<<< HEAD
import org.yunghegel.gdx.picking.Pickable;
=======
>>>>>>> 76f512ba5f8efcf77743584d87e35e4603c29ebc


public abstract class ElementPicker<E extends Element> {

    protected BMesh mesh;
    protected Viewport viewport;
    protected ModelBatch batch;
    protected Camera cam;

    protected Picker picker;
    public PickableElement<E>[] pickables;

    public ElementPicker(BMesh mesh, Viewport viewport, ModelBatch batch, Camera cam) {
        this.mesh = mesh;
        this.viewport = viewport;
        this.batch = batch;
        this.cam = cam;

        picker = new Picker();
        pickables = createPickables();

    }

    abstract E pick(int screenX,int screenY);

    abstract protected PickableElement<E>[] createPickables();

    public void debugRender(){
        batch.begin(cam);
        for (PickableElement<E> pickable : pickables) {
            pickable.renderPick(batch);
        }
        batch.end();
    }

    public void setBMesh(BMesh mesh) {
        this.mesh = mesh;
        pickables =  createPickables();
    }
}
