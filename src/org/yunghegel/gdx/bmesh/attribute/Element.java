package org.yunghegel.gdx.bmesh.attribute;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.utils.IntArray;

public abstract class Element {

    public static final int FLAG_VIRTUAL = 1 << 31;
    public static final int FLAG_VISITED = 1 << 30;
    public static final int IS_MODIFIED = 1 << 29;
    public static final int IS_SELECTED = 1 << 28;
    public static final int IS_CULLED = 1 << 27;
    public static final int IS_DUPLICATED = 1 << 26;

    private Material material=new Material();

    public IntArray duplicateIds=new IntArray();


    protected int index = -1;
    private int flags = 0;

    public int id=-1;


    protected Element() {}


    public final int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public final boolean isAlive() {
        return index >= 0;
    }

    final boolean isListed() {
        return !checkFlags(FLAG_VIRTUAL);
    }


    final void release() {
        index = -1;
        flags = 0;
        releaseElement();
    }

    protected abstract void releaseElement();

    public void setVisited(boolean visited) {
        if (visited)
            setFlags(FLAG_VISITED);
        else
            unsetFlags(FLAG_VISITED);
    }

    public void setCulled(boolean culled) {
        if (culled)
            setFlags(IS_CULLED);
        else
            unsetFlags(IS_CULLED);
    }

    public boolean isCulled() {
        return checkFlags(IS_CULLED);
    }

    public void setDuplicated(boolean duplicated,int id) {
        if (duplicated) {
            setFlags(IS_DUPLICATED);
            duplicateIds.add(id);
        }

        else
            unsetFlags(IS_DUPLICATED);
    }

    public boolean isDuplicated() {
        return checkFlags(IS_DUPLICATED);
    }

    public void markElementAsModified(boolean modified) {
        if (modified)
            setFlags(IS_MODIFIED);
        else
            unsetFlags(IS_MODIFIED);
    }

    public void setSelected(boolean selected) {
        if (selected)
            setFlags(IS_SELECTED);
        else
            unsetFlags(IS_SELECTED);
    }

    public boolean isSelected() {
        return checkFlags(IS_SELECTED);
    }

    public boolean isModified() {
        return checkFlags(IS_MODIFIED);
    }

    public boolean isVisited() {
        return checkFlags(FLAG_VISITED);
    }

    final void setFlags(int flags) {
        this.flags |= flags;
    }

    final void unsetFlags(int flags) {
        this.flags &= ~flags;
    }

    final boolean checkFlags(int flags) {
        return (this.flags & flags) == flags;
    }

    public boolean has(Element element){
        int hash = element.hashCode();
        int thisHash = this.hashCode();
        return (hash & thisHash) == hash;
    }


    public abstract void rebuild();

    public void update(){
        if(isModified()){
            rebuild();
            markElementAsModified(false);
        }

    }

    abstract public void getMatrixRepresentation();

}
