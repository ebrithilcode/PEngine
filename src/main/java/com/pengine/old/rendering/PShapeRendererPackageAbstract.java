package com.pengine.net.rendering;

import com.pengine.net.rendering.AbstractObjectRendererPackage;
import processing.core.PApplet;
import processing.core.PShape;

public class PShapeRendererPackageAbstract extends AbstractObjectRendererPackage {

    public PShapeRendererPackageAbstract(float x, float y, float rotation, short renderingObjectIndex) {
        super(x, y, rotation, renderingObjectIndex);
    }

    @Override
    public void render(PApplet applet, Object renderingObject) {
        super.render(applet, renderingObject);
        applet.shape((PShape) renderingObject, 0,0);
    }

}
