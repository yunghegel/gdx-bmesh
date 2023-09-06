package org.yunghegel.gdx.bmesh.structure.ifs;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class IFSFaceMap {

    int[][] indexedFaces;
    int faceCount=0;
    int maxFaces;
    int faceSize=3;

    public IFSFaceMap(int maxFaces) {
        this.maxFaces = maxFaces;
        indexedFaces = new int[maxFaces][faceSize];
    }

    void check(int[] face) {
        if (face.length != faceSize) {
            throw new IllegalArgumentException("Face size must be " + faceSize);
        }
        if(faceCount >= maxFaces) {
            throw new IllegalStateException("Face count exceeds maxFaces");
        }
    }

    public int addFace(int[] face) {
        check(face);
        indexedFaces[faceCount] = face;
//        System.out.println("Face added at index " + faceCount + "with values " + Arrays.toString(face));
        return faceCount++;
    }

    public int[] getFace(int index){
        return indexedFaces[index];
    }

    public int[][] getFaces(){
        return indexedFaces;
    }

    public int getFaceCount(){
        return faceCount;
    }

    public int[] toOneDimensionalArray(){
        int[] faces = new int[faceCount*faceSize];
        for(int i=0;i<faceCount;i++){
            for(int j=0;j<faceSize;j++){
                faces[i*faceSize+j]=indexedFaces[i][j];
            }
        }
        return faces;
    }

    public short[] toOneDimensionalShortArray(){
        short[] faces = new short[faceCount*faceSize];
        for(int i=0;i<faceCount;i++){
            for(int j=0;j<faceSize;j++){
                faces[i*faceSize+j]=(short) indexedFaces[i][j];
            }
        }
        return faces;
    }

    public IntBuffer toIntBuffer(){
        IntBuffer buffer = IntBuffer.allocate(faceCount*faceSize);
        for(int i=0;i<faceCount;i++){
            for(int j=0;j<faceSize;j++){
                buffer.put(indexedFaces[i][j]);
            }
        }
        buffer.flip();
        return buffer;
    }

    public ShortBuffer toShortBuffer(){
        ShortBuffer buffer = ShortBuffer.allocate(faceCount*faceSize);
        for(int i=0;i<faceCount;i++){
            for(int j=0;j<faceSize;j++){
                buffer.put((short) indexedFaces[i][j]);
            }
        }
        buffer.flip();
        return buffer;
    }


}
