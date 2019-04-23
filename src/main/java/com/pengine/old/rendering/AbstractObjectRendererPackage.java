package com.pengine.net.rendering;

import processing.core.PApplet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class AbstractObjectRendererPackage implements IRendererPackage {

    protected float x;
    protected float y;
    protected float rotation;
    protected short renderingObjectIndex;

    public AbstractObjectRendererPackage(float x, float y, float rotation, short renderingObjectIndex) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.renderingObjectIndex = renderingObjectIndex;
    }

    @Override
    public void render(PApplet applet, Object renderingObject) {
        applet.translate(x, y);
        applet.rotate(rotation);
    }

    @Override
    public short getRenderingObjectIndex() {
        return renderingObjectIndex;
    }

    @Override
    public boolean manipulatesMatrix() {
        return true;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeFloat(x);
        objectOutput.writeFloat(y);
        objectOutput.writeFloat(rotation);
        objectOutput.writeShort(renderingObjectIndex);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        x = objectInput.readFloat();
        y = objectInput.readFloat();
        rotation = objectInput.readFloat();
        renderingObjectIndex = objectInput.readShort();
    }

}
