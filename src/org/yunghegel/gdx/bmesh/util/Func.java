package org.yunghegel.gdx.bmesh.util;

import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.math.Vector3;

public class Func {
    @FunctionalInterface
    public interface Unary<T> {
        void exec(T v);
    }

    @FunctionalInterface
    public interface Binary<T> {
        void exec(T a, T b);
    }


    @FunctionalInterface
    public interface MapVec3<T> {
        Vector3 get(T element, Vector3 store);
    }

    @FunctionalInterface
    public interface MapVertex<T> {
        Vertex get(T element);
    }
}
