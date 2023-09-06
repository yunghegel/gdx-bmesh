attribute vec3 a_position;
attribute vec4 a_color;

uniform mat4 u_projModelView;

varying vec4 v_col;


void main() {
    v_col = a_color;
    gl_Position = u_projModelView * vec4(a_position, 1.0);
}
