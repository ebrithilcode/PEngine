package com.pengine.components;

import com.pengine.GameObject;
import com.pengine.InputInternet.Data;
import com.pengine.Vector;
import com.pengine.Updatable;


import static com.pengine.PEngine.APPLET;

public abstract class Component extends Data implements Updatable {

  public GameObject parent;
  public String name = "";
  public boolean active = true;
  public boolean moved = false;

  protected Component() {
  }

  protected Component(GameObject p) {
    parent = p;
    parent.addComponent(this);
  }

  public abstract boolean earlyUpdate();

  public abstract boolean update();

  public abstract boolean lateUpdate();

  public void movement() {}

  public Vector rotateVector(Vector vec, Vector center, float deg) {
    Vector toRot = vec.copy().sub(center);
    float re = APPLET.cos(deg);
    float im = APPLET.sin(deg);
    Vector newRot = new Vector(toRot.x * re - toRot.y * im, toRot.x * im + toRot.y * re);
    return newRot.add(center);
  }

  public Vector[] rectPoints(float wid, float hei) {
    return new Vector[] {new Vector(-wid/2f, -hei/2f), new Vector(wid/2f, -hei/2f), new Vector(wid/2f, hei/2f), new Vector(-wid/2f, hei/2f)};
  }

  

}
