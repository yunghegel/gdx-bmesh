package org.yunghegel.gdx.bmesh.operations.normals;

import com.badlogic.gdx.math.Vector3;

public class NewellNormal {

    public static void addToNormal(Vector3 nStore, Vector3 last, Vector3 current) {
        nStore.x += (last.y - current.y) * (last.z + current.z);
        nStore.y += (last.z - current.z) * (last.x + current.x);
        nStore.z += (last.x - current.x) * (last.y + current.y);
    }

}
