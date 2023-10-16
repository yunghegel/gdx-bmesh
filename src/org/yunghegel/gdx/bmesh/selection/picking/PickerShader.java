package org.yunghegel.gdx.bmesh.selection.picking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import org.yunghegel.gdx.picking.PickerIDAttribute;


public class PickerShader extends BaseShader {

    private static PickerShader instance;

    public static PickerShader getInstance() {
        if (instance == null) {
            instance = new PickerShader();
            instance.init();
        }
        return instance;
    }

    private static final String VERTEX_SHADER = "attribute vec3 a_position;" + "uniform mat4 u_transMatrix;"
            + "uniform mat4 u_projViewMatrix;" + "void main(void) {"
            + "vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);" + "gl_Position = u_projViewMatrix * worldPos;"
            + "}";

    private static final String FRAGMENT_SHADER = "#ifdef GL_ES\n" + "precision highp float;\n" + "#endif \n"
            + "uniform vec3 u_color;" + "void main(void) {"
            + "gl_FragColor = vec4(u_color.r/255.0, u_color.g/255.0, u_color.b/255.0, 1.0);" + "}";

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));

    protected final int UNIFORM_COLOR = register(new Uniform("u_color"));

    private static Vector3 vec3 = new Vector3();

    public ShaderProgram program;

    public PickerShader() {
        super();
        program = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    public void init() {
        super.init(program, null);
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.context = context;
        this.context.setCullFace(GL20.GL_BACK);
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
        this.context.setDepthMask(true);

        program.begin();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
    }

    @Override
    public void render(Renderable renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);

        PickerIDAttribute goID = (PickerIDAttribute) renderable.material.get(PickerIDAttribute.Type);
        if (goID != null) {
            set(UNIFORM_COLOR, vec3.set(goID.r, goID.g, goID.b));
        }
        Gdx.gl.glLineWidth(5f);


        renderable.meshPart.render(program);
        Gdx.gl.glLineWidth(1f);

    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }

}