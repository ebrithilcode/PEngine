package com.pengine.components.renderers;

import com.pengine.components.AbstractRenderer;
import com.pengine.GameObject;

import static com.pengine.PEngine.APPLET;

public class CircleRenderer extends AbstractRenderer {

  private PVector off;
  private float radius;
  /*color*/public int c;

  {
    dontSendMePlease = false;
  }

  public CircleRenderer() {}
  public CircleRenderer(GameObject g) {
    super(g);
    off = new PVector(0, 0);
    radius = 10;
    c = APPLET.color(0, 0, 255);
  }

  public void show() {
    APPLET.fill(c);
    PVector position = off.copy().add(parent.pos);
    APPLET.ellipse(position.x, position.y, radius, radius);
  }

  public void setRadius(float v) {
    radius = v;
  }

  public void setOff(PVector v) {
    off = v;
  }
  public float getRadius() {
    return radius;
  }
  public PVector getOff() {
    return off;
  }


  @Override
  public String toString() {
    String ret = "";
    ret += classID;
    ret += objectID;
    ret = concateByteArray(ret, intToBytes(c));
    ret += (char) radius;

    return ret;
  }

  public static CircleRenderer createData(byte[] b, int... index) {
    index[0] ++;
    CircleRenderer rr = new CircleRenderer();
    rr.objectID = index[0]++;
    rr.c = bytesToInt(subarray(b, index, 4));
    rr.radius = b[index[0]++];
    return rr;
  }
  @Override
  public void updateData(byte[] b, int... index) {
    //Skip class and object id;
    index[0] += 2;
    c = bytesToInt(subarray(b, index, 4));
    radius = b[index[0]++];

  }
}
