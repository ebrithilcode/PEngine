package com.rsttst.pengine.colliders;

import processing.core.PVector;
import com.rsttst.pengine.Entity;
import com.rsttst.pengine.Collider;

import static java.lang.Math.abs;

public class RectRectCollider implements Collider {

  public RectRectCollider() {
  }

  public PVector getCollision(Entity a, Entity b) {
  	PVector opt = new PVector(Float.MAX_VALUE, Float.MAX_VALUE);
    PVector[] normalVectors = {ortho(PVector.sub(a.getHitboxVertex(1), a.getHitboxVertex(0))),
                               ortho(PVector.sub(a.getHitboxVertex(2), a.getHitboxVertex(1))),
                               ortho(PVector.sub(b.getHitboxVertex(1), b.getHitboxVertex(0))),
                               ortho(PVector.sub(b.getHitboxVertex(2), b.getHitboxVertex(1)))};

    for (int i=0; i<normalVectors.length; i++) {
      float res = checkProjection(normalVectors[i], a, b);
      if (res == 0) {
        return new PVector(0, 0);
      }
      if (abs(res) < opt.mag()) {
        opt = normalVectors[i].mult(res);
      }
    }
    return opt;
  }

  private static final PVector ortho(PVector p) {
    if (p.y == 0) {
      return new PVector(0, 1);
    }
    else {
      return new PVector(1, -p.x / p.y);
    }
  }

  private static final float checkProjection(PVector v, Entity a, Entity b) {
    float mag = v.mag();
    float minA = 0f, maxA = 0f, minB = 0f, maxB = 0f;
    for(int i=0; i<4; i++) {
      float tempA = v.dot(a.getHitboxVertex(i)) / mag;
      float tempB = v.dot(b.getHitboxVertex(i)) / mag;
      if(tempA < minA) minA = tempA;
      else if(tempA > maxA) maxA = tempA;
      if(tempB < minB) minB = tempB;
      else if(tempB > maxB) maxB = tempB;
    }

    if(minA <= minB && maxA >= minB) {
      return maxA-minB;
    }
    else if(minB <= minA && maxB >= minB) {
      return minA-maxB;
    }
    else {
      return 0;
    }
  }

}
