package org.yunghegel.gdx.bmesh.selection.picking;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class EncodedColorAttribute extends Attribute {
    public final static String Alias = "goID";
    public final static long Type = register(Alias);

    public int r = 255;
    public int g = 255;
    public int b = 255;

    public final static boolean is(final long mask) {
        return (mask & Type) == mask;
    }

    public EncodedColorAttribute() {
        super(Type);
    }

    public EncodedColorAttribute(EncodedColorAttribute other) {
        super(Type);
    }

    @Override
    public EncodedColorAttribute copy() {
        return new EncodedColorAttribute(this);
    }

    @Override
    public int hashCode() {
        return r + g * 255 + b * 255 * 255;
    }

    @Override
    public int compareTo(Attribute o) {
        return -1; // FIXME implement comparing
    }

    @Override
    public String toString() {
        return "GameObjectIdAttribute{" + "r=" + r + ", g=" + g + ", b=" + b + '}';
    }
}
