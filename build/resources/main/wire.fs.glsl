#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_col;


void main() {
    //half alpha v_col
    gl_FragColor = vec4(v_col.rgb, 0.5);

}
