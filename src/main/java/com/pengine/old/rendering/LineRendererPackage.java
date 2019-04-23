package com.pengine.net.rendering;

import com.pengine.net.rendering.Primitive2DRendererPackage;
import processing.core.PApplet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class LineRendererPackage extends Primitive2DRendererPackage {

    private float x0;
    private float y0;
    private float x1;
    private float y1;

    public LineRendererPackage(float x, float y, float rotation, int fillColor, int strokeColor, byte strokeWeight, float x0, float y0, float x1, float y1) {
        super(x, y, rotation, fillColor, strokeColor, strokeWeight);
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    @Override
    public void render(PApplet applet, Object renderingObject) {
        super.render(applet, renderingObject);
        applet.line(x0, y0, x1, y1);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeFloat(x0);
        objectOutput.writeFloat(y0);
        objectOutput.writeFloat(x1);
        objectOutput.writeFloat(y1);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        x0 = objectInput.readFloat();
        y0 = objectInput.readFloat();
        x1 = objectInput.readFloat();
        y1 = objectInput.readFloat();
    }

}


