package org.yunghegel.gdx.bmesh.selection;

import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.utils.picking.Pickable;

public abstract class PickableElement<E extends Element> implements Pickable {

        protected E element;

        public PickableElement(E element) {
            this.element = element;
        }

        @Override
        public int getID() {
            return element.getIndex();
        }

        public E getElement(){
            return element;
        }

}
