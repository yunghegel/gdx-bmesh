package org.yunghegel.gdx.bmesh.selection.picking;

import org.yunghegel.gdx.bmesh.attribute.Element;

public class ColorEncoder {

    public static int decode(int rgba8888Code) {
        int id = (rgba8888Code & 0xFF000000) >>> 24;
        id += ((rgba8888Code & 0x00FF0000) >>> 16) * 256;
        id += ((rgba8888Code & 0x0000FF00) >>> 8) * 256 * 256;

        return id;
    }

    /**
     * Encodes a game object id to a GameObjectIdAttribute with rgb channels.

     * @return the game object id, encoded as rgb values
     */
    public static EncodedColorAttribute encodeRaypickColorId(int id) {
        EncodedColorAttribute encodedColorAttribute = new EncodedColorAttribute();
        encodeRaypickColorId(id, encodedColorAttribute);
        return encodedColorAttribute;
    }

    /**
     * Encodes a id to a GameObjectIdAttribute with rgb channels.
     *
     * @param id
     *            id
     * @param out
     *            encoded id as attribute
     */
    public static void encodeRaypickColorId(int id, EncodedColorAttribute out) {
        out.r = id & 0x000000FF;
        out.g = (id & 0x0000FF00) >>> 8;
        out.b = (id & 0x00FF0000) >>> 16;
    }

    public static void encodeRaypickColorId(Element element){
        int id = element.getIndex();
        EncodedColorAttribute encodedColorAttribute = new EncodedColorAttribute();
        encodeRaypickColorId(id, encodedColorAttribute);



    }



}
