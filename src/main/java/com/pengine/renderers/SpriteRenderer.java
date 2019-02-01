package com.pengine.renderers;

import com.pengine.AbstractRenderer;
import com.pengine.GameObject;
import com.pengine.Vector;
import processing.core.PImage;

import static com.pengine.PEngine.APPLET;

public class SpriteRenderer extends AbstractRenderer {

  PImage img;
  Vector off;

  SpriteRenderer(GameObject g) {
    super(g);
    off = new Vector(0,0);
  }

  public void show() {
    APPLET.image(img, parent.pos.x, parent.pos.y);
  }

}
