package com.pengine.collisiondetection;

import com.pengine.collisiondetection.colliders.AbstractCollider;
import com.pengine.collisiondetection.colliders.CircleCollider;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
public class SAT {

  List<Collision> collisions;

  public SAT() {
    collisions = new ArrayList<>();
  }

  public PVector[] isColliding(AbstractCollider c1, AbstractCollider c2) {
    PVector collisionPoint = new PVector(0,0);
    //Circle circle
    if (c1 instanceof CircleCollider && c2 instanceof CircleCollider) {
      PVector out = circleCircle((CircleCollider) c1, (CircleCollider) c2);
      PVector dif = c2.parent.pos.csub(c1.parent.pos);
      dif.setMag(((CircleCollider)c1).radius);
      collisionPoint = c1.parent.pos.cadd(dif);
      return new PVector[] {out, collisionPoint};
    }

    //Mixed
    Collection<PVector> c1Normals = c1.getCollisionNormals(c2);
    Collection<PVector> c2Normals = c2.getCollisionNormals(c1);

    List<PVector> normals = new ArrayList<>();
    normals.addAll(c1Normals);
    normals.addAll(c2Normals);
    //for (PVector d: c2Normals) normals.add(d);

    for (PVector p : normals) {
      p.normalize();
    }

    PVector best = new PVector(1,1);
    float bVal = Float.MAX_VALUE;
    OverlapReturn bestORet = new OverlapReturn();
    boolean switched = c1 instanceof CircleCollider;
    boolean one = c1 instanceof CircleCollider || c2 instanceof CircleCollider;

    if (switched) {
      AbstractCollider help = c1;
      c1 = c2;
      c2 = help;
    }

    for (int i=0;i<normals.size();i++) {
      PVector n = normals.get(i);
      OverlapReturn test = one ? circleRect(c1.globalPoints, c2.globalPoints, n, ((CircleCollider) c2).radius) : rectRect(c1.globalPoints, c2.globalPoints, n);

      if (test.out2==0) {
        return new PVector[] {new PVector(0, 0)};
      }

      if (PApplet.abs(test.out2) < PApplet.abs(bVal)) {
        bVal = test.out2;
        best = n.copy();
        bestORet = test;
        bestORet.normalIndex = i;
      }
    }

    //Calculate collisionPoint
    if (xor(bestORet.normalIndex < c1Normals.size(), switched)) {
      //c2 ihm sein Punkt verschieben
      switch (bestORet.circle) {
        case 0:
        collisionPoint = bestORet.p2.copy().add(best);
        break;
        case 2:
        PVector helpMe = best.copy();
        helpMe.setMag(helpMe.mag() - ((CircleCollider) c2).radius);
        collisionPoint = bestORet.p2.copy().add(helpMe);
        break;
      }
    } else {
      //c1 ihr sein Punkt verschieben
      switch (bestORet.circle) {
        case 0:
        collisionPoint = bestORet.p1.sub(best);
        break;
        case 2:
          System.out.println("I cant believe this is the case");
        break;
      }
    }

    if (switched) best.mult(-1);
    return new PVector[] {best.mult(bVal), collisionPoint};
  }

  private boolean xor(boolean b1, boolean b2) {
    //return (b1||b2)&& !(b1&&b2); lol
    // b1 != b2
    return b1 ^ b2;
  }

  Projection[] projectBoth(PVector[] p1, PVector[] p2, PVector aim) {
    float mag = aim.mag();

    Projection[] proP1 = new Projection[p1.length];
    Projection[] proP2 = new Projection[p2.length];

    for (int i=0;i<p1.length;i++) {
      proP1[i] = new Projection(p1[i].copy(),p1[i].dot(aim) / mag);
    }
    for (int i=0;i<p2.length;i++) {
      proP2[i] = new Projection(p2[i].copy(), p2[i].dot(aim) / mag);
    }
    Projection[] ret = new Projection[] {minAll(proP1), maxAll(proP1), minAll(proP2), maxAll(proP2)};
    return ret;
  }

  OverlapReturn overlap(Projection px1, Projection px2, Projection py1, Projection py2, int circle) {
    float x1 = px1.val, x2 = px2.val, y1 = py1.val, y2 = py2.val;
    OverlapReturn ret = new OverlapReturn();
    ret.circle = circle;
    ret.out2 = 0;
    if (circle==0) {
      if (inbetween(x1,x2,y1) || inbetween(x1, x2, y2) || inbetween(y1, y2, x1)) {
        if (PApplet.abs(x1 - y2) < PApplet.abs(x2 - y1)) {
          ret.out2 = x1 - y2;
          ret.p1 = px1.truePoint;
          ret.p2 = py2.truePoint;
        } else {
           ret.out2 = x2 - y1;
           ret.p1 = px2.truePoint;
           ret.p2 = py1.truePoint;
        }
      }
    } else if (circle==2) {
      if (inbetween(x1, x2, y1+y2) || inbetween(x1, x2, y1 - y2) || inbetween(y1 - y2, y1 + y2, x1)) {
        if (PApplet.abs(x2 - (y1 - y2)) < PApplet.abs(x1 - (y1 + y2))) {
          ret.out2 = x2 - (y1 - y2);
          ret.p1 = px2.truePoint;
          ret.p2 = py1.truePoint;
        } else {
          ret.out2 = x1 - (y1+y2);
          ret.p1 = px1.truePoint;
          ret.p2 = py1.truePoint;
        }
      }
    }
    return ret;
  }

  PVector circleCircle(CircleCollider c1, CircleCollider c2) {
    PVector ret = c2.globalPoints[0].copy().sub(c1.globalPoints[0]);
    ret.setMag(PApplet.max((c1.radius + c2.radius) - ret.mag(), 0));
    return ret;
  }
  OverlapReturn circleRect(PVector[] p1, PVector[] p2, PVector aim, float rad) {
    Projection[] projected = projectBoth(p1, p2, aim);
    OverlapReturn or = overlap(projected[0], projected[1], projected[2], new Projection(new PVector(0,0),rad), 2);
    return or;
  }
  OverlapReturn rectRect(PVector[] p1, PVector[] p2, PVector aim) {
    Projection[] projected = projectBoth(p1, p2, aim);
    return overlap(projected[0], projected[1], projected[2], projected[3], 0);
  }
  boolean inbetween(float x1, float x2, float y) {
    return (x1 <= y && x2 >= y);
  }




  private Projection minAll(Projection[] ar) {
    float m = ar[0].val;
    Projection best = ar[0];
    for (Projection f: ar) {
      if (f.val<m) {
        m = f.val;
        best = f;
      } if (f.val == m) {
        best.truePoint.add(f.truePoint).div(2f);
      }
    }
    return best;
  }

  private Projection maxAll(Projection[] ar) {
    float m = ar[0].val;
    Projection best = ar[0];
    for (Projection f: ar) {
      if (f.val>m) {
        m = f.val;
        best = f;
      } else if (f.val==m) {
        best.truePoint.add(f.truePoint).div(2f);
      }
    }
    return best;
  }

  //Brute force
  /*void manageCollisions(List<GameObject> objects) {
    for (int i=0;i<objects.size()-1;i++) {
      List<AbstractCollider> possibles1 = objects.get(i).getAllComponentsOfType(AbstractCollider.class);
      for (AbstractCollider c1: possibles1) {
        for (int o=i+1;o<objects.size();o++) {
          for (AbstractCollider c2: objects.get(o).getAllComponentsOfType(AbstractCollider.class)) {
            if (!c1.blackList.contains(c2) && !c2.blackList.contains(c1)) {
              PVector[] p = isColliding(c1,c2);
              if (p[0].mag()!=0) {
                boolean a1 = objects.get(i).onCollision(c2, p[0].cmult(-1), c1);
                boolean a2 = objects.get(o).onCollision(c1, p[0].copy(), c2);
                if (a1&&a2&&!c1.isTrigger&&!c2.isTrigger)
                collisions.add(new Collision(c1, c2, p[0], p[1]));
              }
            }
          }
        }
      }
    }
  }*/

  public void manageCollisions(List<GameObject> obs1, List<GameObject> obs2) {
        for (int i=0;i<obs1.size();i++) {
            List<AbstractCollider> possibles1 = obs1.get(i).getAllComponentsOfType(AbstractCollider.class);
            for (AbstractCollider c1: possibles1) {
                for (int o=i+1;o<obs2.size();o++) {
                    for (AbstractCollider c2: obs2.get(o).getAllComponentsOfType(AbstractCollider.class)) {
                        if (!c1.blackList.contains(c2) && !c2.blackList.contains(c1)) {
                            PVector[] p = isColliding(c1,c2);
                            if (p[0].mag()!=0) {
                                boolean a1 = obs1.get(i).onCollision(c2, p[0].cmult(-1), c1);
                                boolean a2 = obs2.get(o).onCollision(c1, p[0].copy(), c2);
                                if (a1&&a2&&!c1.isTrigger&&!c2.isTrigger)
                                    collisions.add(new Collision(c1, c2, p[0], p[1]));
                            }
                        }
                    }
                }
            }
        }
    }

  public void solve() {
    //Sort by mass
    for (int i=0;i<collisions.size()-1;i++) {
      for (int o=i+1;o<collisions.size();o++) {
        float mc1_1 = minToMax(collisions.get(i).c1.parent.mass);
        float mc1_2 = minToMax(collisions.get(i).c2.parent.mass);
        float mc2_1 = minToMax(collisions.get(o).c1.parent.mass);
        float mc2_2 = minToMax(collisions.get(o).c2.parent.mass);
        if (mc1_1+mc1_2>mc2_1+mc2_2) {
          Collision c = collisions.get(i);
          collisions.set(i, collisions.get(o));
          collisions.set(o, c);
        }
      }
    }
    //solve
    for (int i=collisions.size()-1; i>=0; i--) {
      Collision col = collisions.get(i);
      if (!col.c1.isTrigger && !col.c2.isTrigger) {
        col.resolve();
        col.c1.moved = true;
        col.c2.moved = true;
      }
      collisions.remove(i);
    }
  }
  private static float minToMax(float f) {
    return f>0? f : Float.MAX_VALUE;
  }

  /* -------------------------------- private inner classes for return purposes -------------------------------- */

  private class OverlapReturn {

    PVector p1;
    PVector p2;
    float out2;
    boolean x1y2;
    int circle;
    int normalIndex;

    OverlapReturn() {}

  }

  private class Projection {

    PVector truePoint;
    float val;

    Projection(PVector tp, float v) {
      truePoint = tp;
      val = v;
    }

  }

}
