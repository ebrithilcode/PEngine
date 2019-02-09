package com.pengine.components.renderers;

import com.pengine.components.AbstractRenderer;
import com.pengine.GameObject;
import com.pengine.Vector;

import static com.pengine.PEngine.APPLET;

public class RectRenderer extends AbstractRenderer {

  public static int classID;

  Vector[] localPoints = new Vector[] {new Vector(0,0)};
  Vector[] globalPoints = new Vector[0];


  /*color*/public int c;


  {
    dontSendMePlease = false;
    alwaysCreateNew = false;
  }

  public RectRenderer() {

  }
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
    try {
      globalPoints = new Vector[localPoints.length];
      for (int i = 0; i < localPoints.length; i++) {
        Vector help = localPoints[i].copy().add(parent.pos);
        help = rotateVector(help, parent.pos, parent.rot);
        globalPoints[i] = help;
      }
    } catch (Exception e) {}
  }

  public void setLocalPoints(Vector[] p) {
    localPoints = p;
  }

  @Override
  public String toString() {
    String ret = "";
    ret += (char) classID;
    ret += (char) objectID;
    ret += (char) (dontUpdateMe ? 1 : 0);
    if (!dontUpdateMe) {
      ret = concateByteArray(ret, intToBytes(c));
      ret += (char) localPoints.length;
      for (int i = 0; i < localPoints.length; i++) {
        ret += localPoints[i].toString();
      }
    }
    ret += '\n';
    return ret;
  }

  public static RectRenderer createData(byte[] b, int... index) {
    System.out.println("Building a Rectrenderer");
    RectRenderer rr = new RectRenderer();
    index[0] ++;
    rr.objectID = index[0]++;
    index[0]++;
    rr.c = bytesToInt(subarray(b, index, 4));
    int len = b[index[0]++];
    rr.localPoints = new Vector[len];
    for (int i=0;i<len;i++) {
      rr.localPoints[i] = Vector.createData(b, index);
    }
    com.pengine.InputInternet.Data.nextWord(b, index);
    return rr;
  }
  @Override
  public void updateData(byte[] b, int... index) {

    //Skip class and object id and dontUpdate;
    /*for (int i=0;i<localPoints.length+1;i++) {
      com.pengine.InputInternet.Data.nextWord(b, index);
    }
    return;
    */index[0] += 3;
    c = bytesToInt(subarray(b, index, 4));
    int len = b[index[0]++];
    if (localPoints.length!=len) {
      localPoints = new Vector[len];
      for (int i=0;i<localPoints.length;i++) {
        localPoints[i] = new Vector();
      }
    }
    for (int i=0;i<len;i++) {
      if (b[index[0]+1] == localPoints[i].objectID && !localPoints[i].alwaysCreateNew)
        //if (b[index[0]+2]==0)
        localPoints[i].updateData(b, index);
        //else com.pengine.InputInternet.Data.nextWord(b, index);
      else
        localPoints[i] = Vector.createData(b, index);
    }
    com.pengine.InputInternet.Data.nextWord(b, index);

  }

}
