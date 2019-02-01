package com.pengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.pengine.PEngine.APPLET;

public class SAT {

  List<Collision> collisions;

  public SAT() {
    collisions = new ArrayList<>();
  }

  public Vector[] isColliding(Collider c1, Collider c2) {
    Vector collisionPoint = new Vector(0,0);
    //Circle circle
    if (c1 instanceof CircleCollider && c2 instanceof CircleCollider) {
      Vector out = circleCircle((CircleCollider) c1, (CircleCollider) c2);
      Vector dif = c2.parent.pos.csub(c1.parent.pos);
      dif.setMag(((CircleCollider)c1).radius);
      collisionPoint = c1.parent.pos.cadd(dif);
      return new Vector[] {out, collisionPoint};
    }

    //Mixed
    Collection<Vector> c1Normals = c1.collisionNormals(c2);
    Collection<Vector> c2Normals = c2.collisionNormals(c1);

    List<Vector> normals = new ArrayList<>();
    normals.addAll(c1Normals);
    normals.addAll(c2Normals);
    //for (Vector d: c2Normals) normals.add(d);

    for (Vector p : normals) {
      p.normalize();
    }

    Vector best = new Vector(1,1);
    float bVal = Float.MAX_VALUE;
    OverlapReturn bestORet = new OverlapReturn();
    boolean switched = c1 instanceof CircleCollider;
    boolean one = c1 instanceof CircleCollider || c2 instanceof CircleCollider;

    if (switched) {
      Collider help = c1;
      c1 = c2;
      c2 = help;
    }

    for (int i=0;i<normals.size();i++) {
      Vector n = normals.get(i);
      OverlapReturn test = one ? circleRect(c1.globalPoints, c2.globalPoints, n, ((CircleCollider) c2).radius) : rectRect(c1.globalPoints, c2.globalPoints, n);

      if (test.out2==0) {
        return new Vector[] {new Vector(0, 0)};
      }

      if (APPLET.abs(test.out2) < APPLET.abs(bVal)) {
        bVal = test.out2;
        best = n.copy();
        bestORet = test;
        bestORet.normalIndex = i;
      }
    }

    //Calculate collisionPoint
    if (bestORet.normalIndex < c1Normals.size()) {
      //c2 ihm sein Punkt verschieben
      switch (bestORet.circle) {
        case 0:
        collisionPoint = bestORet.p2.copy().add(best);
        break;
        case 2:
        Vector helpMe = best.copy();
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
        break;
      }
    }

    if (switched) best.mult(-1);
    return new Vector[] {best.mult(bVal), collisionPoint};
  }


  Projection[] projectBoth(Vector[] p1, Vector[] p2, Vector aim) {
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
    float x1 = px1.val;
    float x2 = px2.val;
    float y1 = py1.val;
    float y2 = py2.val;
    OverlapReturn ret = new OverlapReturn();
    ret.circle = circle;
    ret.out2 = 0;
    if (circle==0) {
      if (inbetween(x1,x2,y1) || inbetween(x1, x2, y2) || inbetween(y1, y2, x1)) {
        if (APPLET.abs(x1 - y2) < APPLET.abs(x2 - y1)) {
          ret.out2 = x1 - y2;
          ret.x1y2 = true;
          ret.p1 = px1.truePoint;
          ret.p2 = py2.truePoint;
        } else {
           ret.out2 = x2 - y1;
           ret.x1y2 = false;
           ret.p1 = px2.truePoint;
           ret.p2 = py1.truePoint;
        }
      }
    } else if (circle==1) {
     if (inbetween(y1, y2, x1+x2) || inbetween(y1, y2, x1 - x2) || inbetween(x1 - x2, x1 + x2, y1)) {
       if (APPLET.abs((x1 - x2) - y2) < APPLET.abs( (x1 + x2) - y1)) {
          ret.out2 = (x1-x2) - y2;
          ret.x1y2 = true;
          ret.p1 = px1.truePoint;
          ret.p2 = py2.truePoint;
        } else {
          ret.out2 = (x1+x2) - y1;
          ret.x1y2 =false;
          ret.p1 = px1.truePoint;
          ret.p2 = py1.truePoint;
        }
      }
    } else if (circle==2) {
      if (inbetween(x1, x2, y1+y2) || inbetween(x1, x2, y1 - y2) || inbetween(y1 - y2, y1 + y2, x1)) {
        if (APPLET.abs(x2 - (y1 - y2)) < APPLET.abs(x1 - (y1 + y2))) {
          ret.out2 = x2 - (y1 - y2);
          ret.x1y2 = false;
          ret.p1 = px2.truePoint;
          ret.p2 = py1.truePoint;
        } else {
          ret.out2 = x1 - (y1+y2);
          ret.x1y2 = true;
          ret.p1 = px1.truePoint;
          ret.p2 = py1.truePoint;
        }
      }
    }
    return ret;
  }

  Vector circleCircle(CircleCollider c1, CircleCollider c2) {
    Vector ret = c2.globalPoints[0].copy().sub(c1.globalPoints[0]);
    ret.setMag(APPLET.max((c1.radius + c2.radius) - ret.mag(), 0));
    return ret;
  }
  OverlapReturn circleRect(Vector[] p1, Vector[] p2, Vector aim, float rad) {
    Projection[] projected = projectBoth(p1, p2, aim);
    OverlapReturn or = overlap(projected[0], projected[1], projected[2], new Projection(new Vector(0,0),rad), 2);
    return or;
  }
  OverlapReturn rectRect(Vector[] p1, Vector[] p2, Vector aim) {
    Projection[] projected = projectBoth(p1, p2, aim);
    return overlap(projected[0], projected[1], projected[2], projected[3], 0);
  }
  boolean inbetween(float x1, float x2, float y) {
    return (x1 <= y && x2 >= y);
  }




  public Projection minAll(Projection[] ar) {
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

  public Projection maxAll(Projection[] ar) {
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
  public void manageCollisions(List<GameObject> objects) {
    for (int i=0;i<objects.size()-1;i++) {
      List<Collider> possibles1 = objects.get(i).getAllComponentsOfType(Collider.class);
      for (Collider c1: possibles1) {
        for (int o=i+1;o<objects.size();o++) {
          for (Collider c2: objects.get(o).getAllComponentsOfType(Collider.class)) {
            if (!c1.blackList.contains(c2) && !c2.blackList.contains(c1)) {
              Vector[] p = isColliding(c1,c2);
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
  }

  //Hopefully logn
  /*void manageCollisions(ArrayList<GameObject> obs1, ArrayList<GameObject> obs2) {
    for (int i=0;i<obs1.size();i++) {
      if (obs1.get(i).getComponent(Collider.class) == null) continue;
      for (int o=i+1;o<obs2.size();o++) {
        if (obs2.get(o).getComponent(Collider.class)== null) continue;
          Collider c1 = (Collider) obs1.get(i).getComponent(Collider.class);
          Collider c2 = (Collider) obs2.get(o).getComponent(Collider.class);
          Vector[] p = sat.isColliding(c1,c2);
          if (p[0].mag()!=0) {
            obs1.get(i).onCollision(c2, p[0].cmult(-1));
            obs2.get(o).onCollision(c1, p[0]);
            collisions.add(new Collision(c1, c2, p[0], p[1]));
          }
      }
    }
  }*/

  public void solve() {
    //Sort with (by?) mass
    for (int i=0;i<collisions.size()-1;i++) {
      for (int o=i+1;o<collisions.size();o++) {
        float mc1_1 = collisions.get(i).c1.parent.mass;
        mc1_1 = mc1_1>0? mc1_1 : Float.MAX_VALUE;
        float mc1_2 = collisions.get(i).c2.parent.mass;
        mc1_2 = mc1_2>0? mc1_2 : Float.MAX_VALUE;
        float mc2_1 = collisions.get(o).c1.parent.mass;
        mc2_1 = mc2_1>0 ? mc2_1 : Float.MAX_VALUE;
        float mc2_2 = collisions.get(o).c2.parent.mass;
        mc2_2 = mc2_2>0 ? mc2_2 : Float.MAX_VALUE;
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

  /* -------------------------------- private inner classes for return purposes -------------------------------- */

  private class OverlapReturn {

    Vector p1;
    Vector p2;
    float out2;
    boolean x1y2;
    int circle;
    int normalIndex;

    OverlapReturn() {}

  }

  private class Projection {

    Vector truePoint;
    float val;

    Projection(Vector tp, float v) {
      truePoint = tp;
      val = v;
    }

  }

}
