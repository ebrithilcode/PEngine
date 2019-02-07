package com.pengine.components;

import com.pengine.GameObject;
import com.pengine.InputInternet.Data;
import com.pengine.Updatable;
import processing.core.PVector;


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

  public PVector rotateVector(PVector vec, PVector center, float deg) {
    PVector toRot = vec.copy().sub(center);
    float re = APPLET.cos(deg);
    float im = APPLET.sin(deg);
    PVector newRot = new PVector(toRot.x * re - toRot.y * im, toRot.x * im + toRot.y * re);
    return newRot.add(center);
  }

  public PVector[] rectPoints(float wid, float hei) {
    return new PVector[] {new PVector(-wid/2f, -hei/2f), new PVector(wid/2f, -hei/2f), new PVector(wid/2f, hei/2f), new PVector(-wid/2f, hei/2f)};
  }

  

}
