package com.pengine.net.rendering;

import com.pengine.net.rendering.AbstractObjectRendererPackage;
import processing.core.PApplet;
import processing.core.PImage;

public class PImageRendererPackageAbstract extends AbstractObjectRendererPackage {

    public PImageRendererPackageAbstract(float x, float y, float rotation, short renderingObjectIndex) {
        super(x, y, rotation, renderingObjectIndex);
    }

    @Override
    public void render(PApplet applet, Object renderingObject) {
        super.render(applet, renderingObject);
        applet.image((PImage) renderingObject, 0, 0);
    }

}
