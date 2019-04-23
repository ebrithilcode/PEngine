package com.pengine.rendering;

import com.pengine.Entity;
import com.pengine.net.rendering.AbstractRendererPackage;
import com.pengine.net.rendering.PShapeRendererPackageAbstract;
import processing.core.PShape;

import static com.pengine.PEngine.APPLET;

public class PShapeRenderer extends AbstractRenderer {

    PShape shape;

    public PShapeRenderer(Entity parent, byte priority, int finalDataInde) {
        super(parent, priority);
        this.shape = shape;
    }

    @Override
    public void render() {
        APPLET.pushMatrix();
        APPLET.translate(parent.getPosX(), parent.getPosY());
        APPLET.rotate(parent.getRotation());
        APPLET.shape(shape, 0, 0);
        APPLET.popMatrix();
    }

    @Override
    public boolean hasFinalData() {
        return true;
    }

    @Override
    public Object getFinalData(short index) {
        finalDataIndex = index;
        return shape;
    }

    @Override
    public AbstractRendererPackage getRenderTemplate() {
        return new PShapeRendererPackageAbstract(parent.getPosX(), parent.getPosY(), parent.getRotation(), finalDataIndex);
    }

}

