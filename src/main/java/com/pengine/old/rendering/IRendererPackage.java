package com.pengine.net.rendering;

import processing.core.PApplet;

import java.io.Externalizable;

public interface IRendererPackage extends Externalizable {

    void render(PApplet applet, Object renderingObject);

    short getRenderingObjectIndex();

    boolean manipulatesMatrix();

}
