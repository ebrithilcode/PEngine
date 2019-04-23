package com.pengine.net.rendering;

import processing.core.PApplet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class EllipseRendererPackage extends AbstractPrimitiveRendererPackage {

    private float width;
    private float height;

    public EllipseRendererPackage(int fillColor, int strokeColor, byte strokeWeight, float width, float height) {
        super(fillColor, strokeColor, strokeWeight);
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(PApplet applet, Object renderingObject) {
        super.render(applet, renderingObject);
        applet.ellipse(0, 0, width, height);
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeFloat(width);
        objectOutput.writeFloat(height);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException {
        super.readExternal(objectInput);
        width = objectInput.readFloat();
        height = objectInput.readFloat();
    }

}
