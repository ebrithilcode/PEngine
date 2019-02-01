package com.pengine.old;

public class HelpMethods {

    public float sin(float val) {
        return Math.sin(val);
    }

    public float cos(float val) {
        return Math.cos(val);
    }

    public float pow(float base, float val) {
        return Math.pow(base, val);
    }

    public float sqrt(float val) {
        return Math.sqrt(val);
    }

    public float sq(float val) {
        return val * val;
    }

    public float min(float a, float b) {
        return Math.min(a, b);
    }

    public float max(float a, float b) {
        return Math.max(a, b);
    }

    public void rect(float x, float y, float w, float h) {
        rect((float) x, (float) y, (float) w, (float) h);
    }

    public void ellipse(float x, float y, float w, float h) {
        ellipse((float) x, (float) y, (float) w, (float) h);
    }

    public void translate(float x, float y) {
        translate((float) x, (float) y);
    }

    public void rotate(float deg) {
        rotate((float) deg);
    }

    public void vertex(float a, float b) {
        vertex((float) a, (float) b);
    }

    /*void image(PImage i, float x, float y) {
        image(i, (float) x, (float) y);
    }*/

    public float abs(float v) {
        return Math.abs(v);
    }

}
