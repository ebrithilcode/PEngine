package com.pengine;

import static com.pengine.PEngine.APPLET;

public class Vector extends Data{

  public float x;
  public float y;
  public float z;

  public Vector(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vector(float px, float py, float pz) {
    x = px;
    y = py;
    z = pz;
  }

  public Vector add(Vector o) {
    x+=o.x;
    y+=o.y;
    return this;
  }

  public Vector sub(float a, float b) {
    x-=a;
    y-=b;
    return this;
  }

  public Vector csub(float a, float b) {
    return new Vector(x-a, y-b);
  }

  public Vector addVelocity(float a, float b) {
    x+=a;
    y+=b;
    return this;
  }

  public Vector cadd(float a, float b) {
    return new Vector(x+a, y+b);
  }

  public Vector cadd(Vector o) {
    return new Vector(o.x+x, o.y+y);
  }

  public Vector sub(Vector o) {
    x-=o.x;
    y-=o.y;
    return this;
  }

  public Vector csub(Vector o) {
    return new Vector(x-o.x, y-o.y);
  }

  public Vector div(float o) {
    if (o!=0) {
      x/=o;
      y/=o;
    }
    return this;
  }

  public Vector cdiv(float o) {
    if (o!=0)
    return new Vector(x/o, y/o);
    return new Vector(0,0);
  }

  public Vector mult(float o) {
    x*=o;
    y*=o;
    return this;
  }

  public Vector cmult(float o) {
    return new Vector(x*o, y*o);
  }

  public float dot(Vector o) {
    return o.x*x+o.y*y+o.z*z;
  }

  public float mag() {
    return APPLET.sqrt(APPLET.sq(x)+APPLET.sq(y));
  }

  public Vector setMag(float val) {
    div(mag()).mult(val);
    return this;
  }

  public Vector copy() {
    return new Vector(x,y);
  }

  public Vector normalize() {
    div(mag());
    return this;
  }

  public float dist(Vector o) {
    return o.csub(this).mag();
  }

  public Vector cross(Vector other) {
    return new Vector(y*other.z - z * other.y, z*other.x - x*other.z, x*other.y-y*other.x);
  }

  public Vector cut() {
    if (x<APPLET.pow(10,-5)) x = 0;
    if (y<APPLET.pow(10,-5)) y = 0;
    if (1-x<APPLET.pow(10,-5)) x = 1;
    if (1-y<APPLET.pow(10, -5)) y = 1;
    return this;
  }

  @Override
  public String toString() {
    String ret = "";
    ret += classID;
    ret += objectID;
    ret = concateByteArray(ret, floatToByte(x));
    ret = concateByteArray(ret, floatToByte(y));
    return ret;
  }

  @Override
  public static Vector createData(byte[] b, int... index) {
    Vector v = new Vector();
    s//Skip class ID
    index[0]++;
    v.objectID = b[index[0]++];
    v.x = byteToFloat(subarray(b, index, 4));
    v.y = byteToFloat(subarray(b, index, 4));
    return v;
  }

  @Override
  public void updateData(byte[] b, int... index) {
    //Skip class and object id
    index[0] += 2;
    x = byteToFloat(subarray(b, index[0], 4));
    y = byteToFloat(subarray(b, index, 4));
  }

}
