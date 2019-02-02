package com.pengine.renderers;

import com.pengine.AbstractRenderer;
import com.pengine.GameObject;
import com.pengine.Vector;

import static com.pengine.PEngine.APPLET;

public class CircleRenderer extends AbstractRenderer {

  Vector off;
  float radius;
  /*color*/public int c;

  public CircleRenderer(GameObject g) {
    super(g);
    off = new Vector(0, 0);
    radius = 10;
    c = APPLET.color(0, 0, 255);
  }

  public void show() {
    APPLET.fill(c);
    Vector position = off.copy().add(parent.pos);
    APPLET.ellipse(position.x, position.y, radius, radius);
  }
}
