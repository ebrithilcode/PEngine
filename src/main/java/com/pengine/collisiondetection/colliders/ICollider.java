package com.pengine.collisiondetection.colliders;

import com.pengine.Entity;
import processing.core.PVector;

public interface ICollider {

    PVector[] getVertices();

    Entity getParent();

}
