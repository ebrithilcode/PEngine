package com.pengine;

import processing.core.PApplet;
import java.util.List;
import java.util.ArrayList;


public class EngineApplet extends PApplet {
    public PEngine engine;
    public List<MethodListener> callback;

    public void main() {
        PApplet.main("EngineApplet");

    }
    public EngineApplet() {
        callback = new ArrayList<MethodListener>();
        engine = new PEngine(this);
    }

    public void setup() {
        fullScreen();
        for (MethodListener ml: callback) ml.setup();

        engine.setup();
    }

    public void draw() {

        engine.draw();

        for (MethodListener ml: callback) ml.draw();

    }

    public void keyPressed() {
        engine.keyPressed();
    }

    public void keyReleased() {
        engine.keyReleased();
    }

    public void mousePressed() {
        engine.mousePressed();
    }
    public void mouseReleased() {
        engine.mouseReleased();
    }
}
