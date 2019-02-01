package com.rsttst.pengine;

import processing.core.PVector;

public interface Collider {

  public PVector getCollision(Entity a, Entity b);

}
