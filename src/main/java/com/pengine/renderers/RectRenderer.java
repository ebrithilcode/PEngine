package com.pengine.renderers;

import com.pengine.AbstractRenderer;
import com.pengine.GameObject;
import com.pengine.Vector;

import static com.pengine.PEngine.APPLET;

public class RectRenderer extends AbstractRenderer {

  Vector[] localPoints;
  Vector[] globalPoints;
  /*color*/public int c;

  public RectRenderer(GameObject g) {
    super(g);
    c = APPLET.color(0,0,255);
  }

  @Override
  public boolean earlyUpdate() {
    setPoints();
    return false;
  }

  public void show() {
    APPLET.fill(c);
    APPLET.beginShape();
    for (Vector p: globalPoints) {
      if (p!=null)
      APPLET.vertex(p.x, p.y);
    }
    APPLET.endShape(APPLET.CLOSE);
  }

  public void setPoints() {
    globalPoints = new Vector[localPoints.length];
    for (int i = 0;i<localPoints.length; i++) {
      Vector help = localPoints[i].copy().add(parent.pos);
      help = rotateVector(help, parent.pos, parent.rot);
      globalPoints[i] = help;
    }
  }

  public void setLocalPoints(Vector[] p) {
    localPoints = p;
  }

}
