package com.pengine.components.renderers;

import com.pengine.components.AbstractRenderer;
import com.pengine.GameObject;

import static com.pengine.PEngine.APPLET;

public class RectRenderer extends AbstractRenderer {

  PVector[] localPoints;
  PVector[] globalPoints = new PVector[0];
  /*color*/public int c;


  {
    dontSendMePlease = false;
  }

  public RectRenderer() {}
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
    for (PVector p: globalPoints) {
      if (p!=null)
      APPLET.vertex(p.x, p.y);
    }
    APPLET.endShape(APPLET.CLOSE);
  }

  public void setPoints() {
    globalPoints = new PVector[localPoints.length];
    for (int i = 0;i<localPoints.length; i++) {
      PVector help = localPoints[i].copy().add(parent.pos);
      help = rotateVector(help, parent.pos, parent.rot);
      globalPoints[i] = help;
    }
  }

  public void setLocalPoints(PVector[] p) {
    localPoints = p;
  }

  @Override
  public String toString() {
    String ret = "";
    ret += classID;
    ret += objectID;
    ret = concateByteArray(ret, intToBytes(c));
    ret += (char) localPoints.length;
    for (int i=0;i<localPoints.length;i++) {
      ret += localPoints[i].toString();
    }
    return ret;
  }

  public static RectRenderer createData(byte[] b, int... index) {
    RectRenderer rr = new RectRenderer();
    index[0] ++;
    rr.objectID = index[0]++;
    rr.c = bytesToInt(subarray(b, index, 4));
    int len = b[index[0]++];
    rr.localPoints = new PVector[len];
    for (int i=0;i<len;i++) {
      rr.localPoints[i] = PVector.createData(b, index);
    }
    return rr;
  }
  @Override
  public void updateData(byte[] b, int... index) {
    //Skip class and object id;
    index[0] += 2;
    c = bytesToInt(subarray(b, index, 4));
    int len = b[index[0]++];
    if (localPoints.length!=len) {
      localPoints = new PVector[len];
      for (int i=0;i<localPoints.length;i++) {
        localPoints[i] = new PVector();
      }
    }
    for (int i=0;i<len;i++) {
      if (b[index[0]+1] == localPoints[i].objectID)
        localPoints[i].updateData(b, index);
      else
        localPoints[i] = PVector.createData(b, index);
    }

  }

}
