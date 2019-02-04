package com.pengine.components.renderers;

import com.pengine.components.AbstractRenderer;
import com.pengine.GameObject;
import com.pengine.Vector;

import static com.pengine.PEngine.APPLET;

public class CircleRenderer extends AbstractRenderer {

  public static int classID;

  private Vector off;
  private float radius;
  /*color*/public int c;

  {
    dontSendMePlease = false;
  }

  public CircleRenderer() {}
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

  public void setRadius(float v) {
    radius = v;
  }

  public void setOff(Vector v) {
    off = v;
  }
  public float getRadius() {
    return radius;
  }
  public Vector getOff() {
    return off;
  }


  @Override
  public String toString() {
    String ret = "";
    ret += (char) classID;
    ret += (char) objectID;
    ret = concateByteArray(ret, intToBytes(c));
    ret += (char) radius;
    ret += '\n';
    return ret;
  }

  public static CircleRenderer createData(byte[] b, int... index) {
    System.out.println("Building a circleRenderer");
    index[0] ++;
    CircleRenderer rr = new CircleRenderer();
    rr.objectID = index[0]++;
    rr.c = bytesToInt(subarray(b, index, 4));
    rr.radius = b[index[0]++];
    com.pengine.InputInternet.Data.nextWord(b, index);
    return rr;
  }
  @Override
  public void updateData(byte[] b, int... index) {

    //Skip class and object id;
    index[0] += 2;
    c = bytesToInt(subarray(b, index, 4));
    radius = b[index[0]++];
    com.pengine.InputInternet.Data.nextWord(b, index);

  }
}
