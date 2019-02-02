package com.pengine;

import processing.core.PApplet;
import java.util.List;
import java.util.ArrayList;


public class EngineApplet extends PApplet {
    public static PEngine engine;
    public static List<MethodListener> callback = new ArrayList<MethodListener>();

    //For different screen sizes;
    private float screenScale;
    private float xOff;
    private float yOff;



    public void main() {
        PApplet.main("com.pengine.EngineApplet");
    }
    public EngineApplet() {
        engine = new PEngine(this);
    }
    public void settings() {
        fullScreen();
        System.out.println("Settings");
    }
    public void setup() {
        setupScreen();

        System.out.println("In setup: "+callback.size());
        engine.setup();
        for (MethodListener ml: callback) ml.setup();

    }

    public void draw() {
        pushMatrix();
        translate(yOff, xOff);
        scale(screenScale, screenScale);

        engine.draw();

        for (MethodListener ml: callback) ml.draw();

        popMatrix();

    }
    public void addCallBack(MethodListener el) {
        System.out.println("Adding callback");
        callback.add(el);
        System.out.println("Size: "+callback.size());
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

    private void setupScreen() {
        if (width/height < 1920f/1080f) {
            xOff = 0;
            yOff = (height - (width*1080/1920f))/2f;
            screenScale = width / 1920f;
        } else {
            xOff = (width - (height * 1920 / 1080f)) / 2f;
            yOff = 0;
            screenScale = height / 1080f;
        }
    }
}
