package com.pengine.net.rendering;

import processing.core.PApplet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class AbstractPrimitiveRendererPackage implements IRendererPackage {

    protected int fillColor;
    protected int strokeColor;
    protected byte strokeWeight;

    public AbstractPrimitiveRendererPackage(int fillColor, int strokeColor, byte strokeWeight) {
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWeight = strokeWeight;
    }

    @Override
    public void render(PApplet applet, Object renderingObject) {
        if(fillColor != -1) {
            applet.fill(fillColor);
        }
        if(strokeColor != -1) {
            applet.strokeWeight(strokeWeight);
            applet.stroke(strokeWeight);
        }
    }

    @Override
    public short getRenderingObjectIndex() {
        return -1;
    }

    @Override
    public boolean manipulatesMatrix() {
        return false;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(fillColor);
        objectOutput.writeInt(strokeColor);
        objectOutput.writeByte(strokeWeight);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException {
        fillColor = objectInput.readInt();
        strokeColor = objectInput.readInt();
        strokeWeight = objectInput.readByte();
    }

}
