package com.pengine.renderers;

import com.pengine.AbstractRenderer;
import com.pengine.GameObject;
import com.pengine.Vector;
import processing.core.PImage;

import static com.pengine.PEngine.APPLET;

public class ImageRenderer extends AbstractRenderer {

  Vector off;
  PImage img;
  /*color*/int c;
  boolean useColor;

  public ImageRenderer(GameObject g) {
    super(g);
    useColor = false;
    off = new Vector(0, 0);
  }

  public void setImage(PImage img) {
    this.img = img;
  }

  public void setSize(int x, int y) {
    img.resize(x,y);
  }

  public void show() {
    Vector pos = off.cadd(parent.pos);
    APPLET.pushMatrix();
    APPLET.translate(pos.x, pos.y);
    APPLET.rotate(parent.rot);
    if(useColor) APPLET.tint(c);
    APPLET.image(img, 0,0);
    if(useColor) APPLET.noTint();
    APPLET.popMatrix();
  }
}
