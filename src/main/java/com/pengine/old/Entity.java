package com.rsttst.pengine;

import processing.core.PVector;
import processing.core.PApplet;

public abstract class Entity {

  protected PVector pos;
  protected PVector[] hitboxVertices;

  protected Entity(float x, float y, PVector[] hitboxVertices) {
    pos = new PVector(x, y);
    this.hitboxVertices = hitboxVertices;
  }

  public PVector getPos() {
    return pos;
  }

  public void setPos(PVector pos) {
    this.pos = pos;
  }
  public void setXPos(float x) {
    pos.x = x;
  }

  public void setYPos(float y) {
    pos.y = y;
  }

  public PVector[] getHitbox() {
    return hitboxVertices;
  }

  public PVector getHitboxVertex(int i) {
    return hitboxVertices[i];
  }

  abstract public void onCollide(Engine engine, Entity collider, PVector out);

  abstract public void render(PApplet applet);

  abstract public void update(Engine board);


}
