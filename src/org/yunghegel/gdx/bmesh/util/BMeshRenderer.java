package org.yunghegel.gdx.bmesh.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.bmesh.attribute.Element;
import org.yunghegel.gdx.bmesh.operations.EdgeOperations;
import org.yunghegel.gdx.bmesh.operations.FaceOperations;
import org.yunghegel.gdx.bmesh.operations.Inset;
import org.yunghegel.gdx.bmesh.structure.BMesh;
import org.yunghegel.gdx.bmesh.structure.Edge;
import org.yunghegel.gdx.bmesh.structure.Face;
import org.yunghegel.gdx.bmesh.structure.Vertex;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BMeshRenderer extends ShapeRenderer {

    private BitmapFont font;
    public Camera cam;
    public final SpriteBatch batch;
    FaceOperations faceOperations;
    EdgeOperations edgeOperations;
    Inset inset;
    ImmediateModeRenderer20 renderer;
    BMesh bmesh;
    public Matrix4 transform = new Matrix4();
    public Actor actor;
    public Actor root;
    public ScreenViewport viewport;
    public BMeshRenderer(BMesh bmesh, Camera camera) {
        super();
        this.cam = camera;
        this.bmesh = bmesh;
        setAutoShapeType(true);


//        batch.disableBlending();
        batch = new SpriteBatch();
        faceOperations = new FaceOperations(bmesh);
        edgeOperations = new EdgeOperations(bmesh);
        setProjectionMatrix(cam.combined);
        renderer= (ImmediateModeRenderer20) getRenderer();
        ShaderProgram shader = new ShaderProgram(Gdx.files.internal("wire.vs.glsl").readString(), Gdx.files.internal("wire.fs.glsl").readString());
        if (!shader.isCompiled()) throw new GdxRuntimeException("Error compiling shader: " + shader.getLog());

        ((ImmediateModeRenderer20) getRenderer()).setShader(shader);

//        Gdx.gl.glLineWidth(2);

    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public void setProjection2D(){
        setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    public void setProjection3D(){
        setProjectionMatrix(cam.combined);
        setTransformMatrix(transform);
    }

    interface TextRenderCall {

        void render();

    }

    Array<TextRenderCall> textRenderCalls = new Array<TextRenderCall>();

    public void pushTextRenderCalls() {
        for (TextRenderCall textRenderCall : textRenderCalls) {
            textRenderCall.render();
        }
        textRenderCalls.clear();
    }

    public void createTextRenderCall(final Vector2 worldCoords, final String text) {
        textRenderCalls.add(new TextRenderCall() {
            @Override
            public void render() {
                if(actor!= null){
//                   Gdx.gl.glViewport((int)actor.getX(), (int)actor.getY(), (int)actor.getWidth(), (int)actor.getHeight());
                }
                text(worldCoords, text);
            }
        });
    }





    public void text(Vector2 screenCoords, String text){
//        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
//        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin();
        if(actor!=null){

            float aspectX = actor.getWidth()/Gdx.graphics.getWidth();
            float aspectY = actor.getHeight()/Gdx.graphics.getHeight();


//            batch.setProjectionMatrix(new Matrix4().setToOrtho2D(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight()));
        } else {
        }
        font.draw(batch, text, screenCoords.x, screenCoords.y);

        batch.end();

    }

    public void text(Vector3 worldCoords, String text){
        Vector3 projPos = new Vector3();
        cam.project(projPos.set(worldCoords));
        projPos.y+=14;
        projPos.x+=25;
        createTextRenderCall(new Vector2(projPos.x, projPos.y), text);
        setProjection2D();
        Gdx.gl.glLineWidth(2);
        set(ShapeType.Filled);
        setColor(Color.WHITE);
        rectLine(projPos.x-12, projPos.y-10, projPos.x-4, projPos.y-8,2);
        Gdx.gl.glLineWidth(1);
    }

    public void triangle(Vector3 a, Vector3 b, Vector3 c,Color col){
        if(getCurrentType()==ShapeType.Line){
            setColor(col);
            line(a, b);
            line(b, c);
            line(c, a);
        }else{
            filledTriangle(a, b, c, col);
        }
    }

    public void circle(Vector3 worldCoords,float radius, boolean outlined,Color color){
        Vector3 projPos = new Vector3();
        cam.project(projPos.set(worldCoords));
        set(ShapeType.Filled);
        setProjection2D();
        if(outlined){
            setColor(Color.WHITE);
            circle(projPos.x, projPos.y, radius+0.5f);
            setColor(color);
            circle(projPos.x, projPos.y, radius);
        }else{
            setColor(color);
            circle(projPos.x, projPos.y, radius);
        }
        setProjection3D();
    }

    public boolean button(Element element,Vector3 worldCoords,float radius, boolean outlined,Color color,LabelClickedListener listener){
        Vector3 projPos = new Vector3();
        cam.project(projPos.set(worldCoords));
        set(ShapeType.Filled);
        setProjection2D();
        Color cpy = color.cpy();

        Circle circle = new Circle(projPos.x,projPos.y,radius);
        Vector2 touch = new Vector2(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY());
        if(circle.overlaps(new Circle(touch.add(0,radius/2f).sub(radius/2f,0),radius+3))){
            cpy.set(color.cpy().mul(0.8f));
            if(Gdx.input.isButtonJustPressed(0)){


                listener.clicked(element);


                if(bmesh.selectedElement!=null)
                    bmesh.selectedElement.setSelected(false);
                bmesh.selectedElement=element;
                element.setSelected(true);
                return true;
            }
        }

        if(outlined){
            setColor(Color.WHITE);
            circle(projPos.x, projPos.y, radius+0.5f);
            setColor(cpy);
            circle(projPos.x, projPos.y, radius);
        }else{
            setColor(color);
            circle(projPos.x, projPos.y, radius);
        }

        setProjection3D();
        return false;
    }

    public boolean elementLabelButton(Element element,Vector3 worldCoords,float radius,boolean outlined,Color color,LabelClickedListener listener){
        Vector3 projPos = new Vector3();
        cam.project(projPos.set(worldCoords));
        set(ShapeType.Filled);
        setProjection2D();
        boolean clicked=false;
        if(outlined){
            setColor(Color.WHITE);
            circle(projPos.x, projPos.y, radius+0.5f);
            setColor(color);
         clicked = button(element,worldCoords,radius,outlined,color,listener);
        }else{
            setColor(color);
            circle(projPos.x, projPos.y, radius);
        }
        setProjection3D();
        if(element.isSelected())
            text(worldCoords,element.getClass().getSimpleName().charAt(0)+"/"+element.getIndex());

        return clicked;

    }

    public void label(String text, Vector3 worldCoords, Color color, boolean outlined,float radius){
        circle(worldCoords,radius,outlined,color);
        text(worldCoords,text);
    }

    public void filledTriangle(Vector3 a,Vector3 b,Vector3 c,Color col){
        set(ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        check(ShapeType.Filled,null,3);
        col.a=0.5f;

        renderer.color(col.r,col.g,col.b,col.a);
        renderer.vertex(a.x,a.y,a.z);
        renderer.color(col.r,col.g,col.b,col.a);
        renderer.vertex(b.x,b.y,b.z);
        renderer.color(col.r,col.g,col.b,col.a);
        renderer.vertex(c.x,c.y,c.z);
//        col.a=1f;
    }

    public void quad(Vector3 a, Vector3 b, Vector3 c, Vector3 d,Color color){
        setColor(color);
        if(getCurrentType()==ShapeType.Line){
            line(a, b);
            line(b, c);
            line(c, d);
            line(d, a);
        }else{
            check(ShapeType.Filled,null,4);
            renderer.color(color.r,color.g,color.b,color.a);
            renderer.vertex(a.x,a.y,a.z);
            renderer.color(color.r,color.g,color.b,color.a);
            renderer.vertex(b.x,b.y,b.z);
            renderer.color(color.r,color.g,color.b,color.a);
            renderer.vertex(c.x,c.y,c.z);
            renderer.color(color.r,color.g,color.b,color.a);
            renderer.vertex(d.x,d.y,d.z);
        }
    }

    public void elementIndex(Element e,Vector3 worldCoords,Color color,float desaturateAmnt){
        Vector3 projPos = new Vector3();
        projPos.set(worldCoords);
        projPos.mul(getTransformMatrix());
//        cam.project(projPos);
        Color desaturated = color.cpy().mul(desaturateAmnt);
//        text(new Vector2(projPos.x, projPos.y), e.getClass().getSimpleName().substring(0,1)+"/"+e.getIndex());
//        Gdx.gl.glDisable(GL20.GL_BLEND);
        label(e.getClass().getSimpleName().charAt(0)+"/"+e.getIndex(),worldCoords.mul(getTransformMatrix()),desaturated,true,5);
    }

    public void face(Face face,Color color,boolean drawIndex){
//        Inset inset = new Inset(bmesh,.7f,1f);
//        inset.apply(face);
        setColor(color);
        ArrayList<Vertex> vertices = face.getVertices();

        triangle(vertices.get(0).position,vertices.get(1).position,vertices.get(2).position,color);

        if(vertices.size() == 3){
        }else if(vertices.size() == 4){
            quad(vertices.get(0).position,vertices.get(1).position,vertices.get(2).position,vertices.get(3).position,color);
        }else{
            throw new UnsupportedOperationException("Only triangles are supported");
        }
        Vector3 centroid = face.centroid;
        if(drawIndex)
            elementIndex(face,centroid,color,.7f);

    }

    public void faceWithButton(Face face,Color color,LabelClickedListener listener){

            face(face,color,false);
            elementLabelButton(face,face.centroid,4,true,color,listener);

    }

    public void vertexWithButton(Vertex vertex,Color color,LabelClickedListener listener){
        setColor(Color.TEAL.mul(.5f));
        set(ShapeType.Filled);
        elementLabelButton(vertex,vertex.position,4,true,color,listener);
    }

    public void edgeWithButton(Edge edge,Color color,LabelClickedListener listener){
        setColor(Color.TEAL.mul(.5f));
        set(ShapeType.Filled);
        edge(edge,true,color,Color.GOLD,false);
        elementLabelButton(edge,edge.midPoint,4,true,color,listener);
    }

    public void vertices(BMesh bmesh,Color color,boolean drawIndex){
        ArrayList<Vertex> tmp = new ArrayList<Vertex>();
        bmesh.faces().getAll().stream().filter(f -> f.isFrontFacing(cam)).forEach(f -> tmp.addAll(f.getVertices()));

        for(Vertex v : tmp){
            vertex(v,color,drawIndex);
        }
    }

    public void vertices(Color color,boolean drawIndex,ArrayList<Vertex> vertices){
        for(Vertex v : vertices){
            vertex(v,color,drawIndex);
        }
    }

    public void faces(BMesh bmesh,Color color,boolean drawIndex){
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        ArrayList<Face> tmp = new ArrayList<Face>();
        bmesh.faces().getAll().stream().filter(f -> f.isFrontFacing(cam)).forEach(tmp::add);


        for(Face f : tmp){



            setColor( color.cpy().mul(0.1f));
            setProjection3D();
            ArrayList<Vertex> vertices = f.getVertices();
            set(ShapeType.Filled);

            filledTriangle(vertices.get(0).position,vertices.get(1).position,vertices.get(2).position,color.cpy().sub(0.2f,0.2f,0.2f,.2f));

//            end();
//
//            begin();
            color.a=1f;
            setColor(color);
            set(ShapeType.Line);
            ArrayList<Vertex> verts = f.getVertices();
            for(int i = 0; i < verts.size(); i++){
                Vertex v1 = verts.get(i);
                Vertex v2 = verts.get((i+1)%verts.size());
                line(v1.position, v2.position);
            }



        }

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        if(!drawIndex)
            return;
        for(Face f:tmp){


            Gdx.gl.glDisable(GL20.GL_BLEND);
            Vector3 centroid = f.centroid;
//            centroid.mul(getTransformMatrix());
            elementIndex(f,centroid,color,1f);

        }

    }

    public void faces(Color color, boolean drawIndex, List<Face> faces){
        for(Face f : faces){
            ArrayList<Face> tmp = new ArrayList<Face>();
            tmp = f.getCoplanarFaces();
            if(!tmp.isEmpty()){
                Face face = tmp.get(0);
                Vertex v1 = face.getVertices().get(0);
                Vertex v2 = face.getVertices().get(1);
                Vertex v3 = face.getVertices().get(2);

                Vertex v4 = f.getVertices().get(0);
                Vertex v5 = f.getVertices().get(1);
                Vertex v6 = f.getVertices().get(2);


                quad(v1.position,v2.position,v3.position,v5.position,color);
            } else {
                face(f,color,drawIndex);
            }

        }
    }

    public void edges(BMesh bmesh,boolean arrow,Color lineColor,Color arrowColor,boolean drawIndex){
        ArrayList<Edge> tmp = new ArrayList<Edge>();
        bmesh.faces().getAll().stream().filter(f -> f.isFrontFacing(cam)).forEach(f -> tmp.addAll(f.getEdges()));

        for(Edge e : tmp){
            edge(e,arrow,lineColor,arrowColor,drawIndex);
        }
    }

    public void edges(boolean arrow,Color lineColor,Color arrowColor,boolean drawIndex,ArrayList<Edge> edges){
        for(Edge e : edges){
            edge(e,arrow,lineColor,arrowColor,drawIndex);
        }
    }





    public void edge(Edge edge,boolean arrow,Color lineColor,Color arrowColor,boolean drawIndex){
        if(edge==null){
            return;
        }
        Inset inset = new Inset(bmesh,.7f,1f);
        if(arrow) arrow(edge.vertex0.position, edge.vertex1.position, lineColor,arrowColor);
        else {
            setColor(lineColor);
            line(edge.vertex0.position, edge.vertex1.position);
        }
        Vector3 center = edgeOperations.calcCenter(edge);
        String label = "E/"+edge.getIndex();
        if(drawIndex)
            elementIndex(edge,center,lineColor,.9f);
        else
            circle(center,5,true,arrowColor);
    }

    public void vertex(Vertex vertex,Color color,boolean drawIndex){
        set(ShapeType.Filled);
        Vector3 projPos = new Vector3();
        cam.project(projPos.set(vertex.position));
        setProjection2D();
        setColor(Color.WHITE);
        circle(projPos.x, projPos.y,5);

//        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);

        setColor(color);
        circle(projPos.x, projPos.y,4);
        setProjection3D();
        setColor(color);

        Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
        Gdx.gl.glClear(Gdx.gl.GL_DEPTH_BUFFER_BIT);
        if(drawIndex)
            elementIndex(vertex,vertex.position,color,1);
        Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
    }

    public void arrow(Vector3 start, Vector3 end,Color lineColor,Color arrowColor){
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        lineColor.a=1f;
        setColor(lineColor);
        ShapeType type = getCurrentType();
        Vector3 projStart = new Vector3();
        Vector3 projEnd = new Vector3();
        cam.project(projStart.set(start));
        cam.project(projEnd.set(end));
        setProjection2D();
        rectLine(projStart.x, projStart.y, projEnd.x, projEnd.y,2);
        Vector2 dir = new Vector2(projEnd.x, projEnd.y).sub(projStart.x, projStart.y).nor();
        Vector2 perp = new Vector2(dir.y, -dir.x);
        Vector2 arrow1 = new Vector2(projEnd.x, projEnd.y).sub(dir.cpy().scl(10)).add(perp.cpy().scl(5));
        Vector2 arrow2 = new Vector2(projEnd.x, projEnd.y).sub(dir.cpy().scl(10)).add(perp.cpy().scl(-5));
        line(projEnd.x, projEnd.y, arrow1.x, arrow1.y);
        line(projEnd.x, projEnd.y, arrow2.x, arrow2.y);
        this.set(ShapeType.Filled);
        setColor(arrowColor);
//        triangle(projEnd.x, projEnd.y, arrow1.x, arrow1.y, arrow2.x, arrow2.y);
        //another arrow at midpoint
        Vector2 mid = new Vector2(projStart.x, projStart.y).add(projEnd.x, projEnd.y).scl(0.5f);

        //midpoint between mid and start
        Vector2 mid2=mid.cpy().add(projStart.x, projStart.y).scl(0.5f);
        Vector2 midArrow1 = new Vector2(mid2.x, mid2.y).sub(dir.cpy().scl(5)).add(perp.cpy().scl(5));
        Vector2 midArrow2 = new Vector2(mid2.x, mid2.y).sub(dir.cpy().scl(5)).add(perp.cpy().scl(-5));
        triangle(mid2.x, mid2.y, midArrow1.x, midArrow1.y, midArrow2.x, midArrow2.y);

        //midpoint between mid and end
        Vector2 mid3=mid.cpy().add(projEnd.x, projEnd.y).scl(0.5f);
        Vector2 midArrow3 = new Vector2(mid3.x, mid3.y).sub(dir.cpy().scl(5)).add(perp.cpy().scl(5));
        Vector2 midArrow4 = new Vector2(mid3.x, mid3.y).sub(dir.cpy().scl(5)).add(perp.cpy().scl(-5));
        triangle(mid3.x, mid3.y, midArrow3.x, midArrow3.y, midArrow4.x, midArrow4.y);


        setProjection3D();
        set(type);
    }

    public void arrow(Vector3 start, Vector3 end, float scl,Color lineColor,Color arrowColor){
        Vector3 dir = end.cpy().sub(start);
        Vector3 end2 = start.cpy().add(dir.cpy().scl(scl));
//        Vector3 start2 = start.cpy().add(dir.cpy().scl(scl));
        arrow(start, end2,lineColor,arrowColor);
    }

    public void arrow(Vector3 start, Vector3 end, float startOffsetScl, float endOffsetScl,Color lineColor,Color arrowColor){
        Vector3 dir = end.cpy().sub(start);
        Vector3 end2 = start.cpy().add(dir.cpy().scl(endOffsetScl));
        Vector3 start2 = start.cpy().add(dir.cpy().scl(startOffsetScl));
        arrow(start2, end2,lineColor,arrowColor);
    }

    public void dottedLine(Vector3 start, Vector3 end,int segments){
        Vector3 dir = end.cpy().sub(start);
        Vector3 step = dir.cpy().scl(1f/segments);
        Vector3 current = start.cpy();
        for(int i = 0; i < segments; i++){
            line(current, current.cpy().add(step));
            current.add(step);
        }
    }

    public void wireframe(BMesh mesh,Color color){
        Color col = getColor();
        setColor(color);
        setProjection3D();
        set(ShapeType.Line);
//        for(Face f : mesh.faces()){
//            ArrayList<Vertex> verts = f.getVertices();
//            for(int i = 0; i < verts.size(); i++){
//                Vertex v1 = verts.get(i);
//                Vertex v2 = verts.get((i+1)%verts.size());
//                line(v1.position, v2.position);
//            }
//            face(f,color,false);
//        }
        faces(color,false,mesh.faces().getAll());
        for(Vertex v : mesh.vertices().getAll()){
            set(ShapeType.Point);
            setColor(new Color(0.1f,0.1f,0.1f,1));
            GL11.glPointSize(6);
            point(v.position.x, v.position.y, v.position.z);
        }
        setColor(col);
    }

    public void indexedElementView(){
//        for (Vertex v : bmesh.vertices().getAll()) {
//            vertex(v,Ansi.FOREST,true);
//        }

            edges(bmesh,true,Color.FIREBRICK,Color.WHITE,true);

//        for (Face f : bmesh.faces().getAll()) {
//            set(ShapeType.Filled);
//            face(f,Ansi.SLATE,true);
//        }
    }

    public void circle(float x, float y, float z, Vector3 direction ,float radius,int segments){
        if (segments <= 0) throw new IllegalArgumentException("segments must be > 0.");
        float angle = 360f / segments;
        float cos = MathUtils.cosDeg(angle);
        float sin = MathUtils.sinDeg(angle);
        float cx = radius;
        float cy = 0;
        float cz = 0;
        check(ShapeType.Line,null,segments*2+2);
        for (int i = 0; i < segments; i++) {
            float temp = cx;
            cx = cos * cx - sin * cy;
            cy = sin * temp + cos * cy;
            line(x+cx, y+cy, z+cz, x+cx, y+cy, z+cz);
        }
        line(x+cx, y+cy, z+cz, x+radius, y, z);




    }

    int[][] edges = new int[][]{
            {0,1},
            {0,2},
            {0,4},
            {1,3},
            {1,5},
            {2,3},
            {2,6},
            {3,7},
            {4,5},
            {4,6},
            {5,7},
            {6,7}
    };

    public void bounds(BoundingBox boundingBox,Color color){
        setColor(color);
        set(ShapeType.Line);
        setProjection3D();
        Vector3 min = boundingBox.min.cpy().scl(1.02f);
        Vector3 max = boundingBox.max.cpy().scl(1.02f);
        Vector3[] corners = new Vector3[8];
        corners[0] = min;
        corners[1] = new Vector3(min.x, min.y, max.z);
        corners[2] = new Vector3(min.x, max.y, min.z);
        corners[3] = new Vector3(min.x, max.y, max.z);
        corners[4] = new Vector3(max.x, min.y, min.z);
        corners[5] = new Vector3(max.x, min.y, max.z);
        corners[6] = new Vector3(max.x, max.y, min.z);
        corners[7] = max;

        //inset the connections between corners so there is a gap between the lines
//        for(int i = 0; i < corners.length; i++){
//            Vector3 corner = corners[i];
//            Vector3 dir = corner.cpy().sub(boundingBox.getCenter(new Vector3()));
//            corners[i] = corner.cpy().add(dir.cpy().scl(0.01f));
//        }

        for(int[] edge : edges){
            Vector3 start = corners[edge[0]].cpy();
            Vector3 end = corners[edge[1]].cpy();
            //shorten the edges so they don't connect completely
            Vector3 normal = end.cpy().sub(start).nor();
            if(normal.x==1){
                start.x = start.x + (end.x-start.x)*0.08f;
                end.x = end.x - (end.x-start.x)*0.08f;
            }
            if(normal.y==1){
                start.y = start.y + (end.y-start.y)*0.08f;
                end.y = end.y - (end.y-start.y)*0.08f;
            }
            if(normal.z==1){
                start.z = start.z + (end.z-start.z)*0.08f;
                end.z = end.z - (end.z-start.z)*0.08f;
            }

            line(start, end);
        }
        Vector3 extMin = min.cpy().scl(1.05f);
        Vector3 extMax = max.cpy().scl(1.05f);
        Vector3[] extCorners = new Vector3[8];
        extCorners[0] = extMin;
        extCorners[1] = new Vector3(extMin.x, extMin.y, extMax.z);
        extCorners[2] = new Vector3(extMin.x, extMax.y, extMin.z);
        extCorners[3] = new Vector3(extMin.x, extMax.y, extMax.z);
        extCorners[4] = new Vector3(extMax.x, extMin.y, extMin.z);
        extCorners[5] = new Vector3(extMax.x, extMin.y, extMax.z);
        extCorners[6] = new Vector3(extMax.x, extMax.y, extMin.z);
        extCorners[7] = extMax;

        for(int[] edge : edges){
            Vector3 start = extCorners[edge[0]].cpy();
            Vector3 end = extCorners[edge[1]].cpy();
            line(start, end);
        }

    }

    public void circle(Vector3 center, Vector3 normal, float radius, int segments){
        circle(center.x, center.y, center.z, normal, radius, segments);
    }

    public interface LabelClickedListener{
        void clicked(Element element);
    }

}
