package com.pengine;

import static com.pengine.PEngine.APPLET;

public class Collision {

  //CollisionInformation
  Collider c1;
  Collider c2;
  Vector out2;
  Vector collisionPoint;
  GameObject g1;
  GameObject g2;

  //Mass specific
  Vector mp1;
  Vector mp2;
  float m1;
  float m2;

  //Helping values
  Vector norm;
  Vector ra;
  Vector rb;
  float minertia1Inv;
  float minertia2Inv;

  public Collision (Collider p1, Collider p2, Vector o, Vector c) {
    c1 = p1;
    c2 = p2;
    out2 = o;
    collisionPoint = c;
  }

  public void resolve() {
    g1 = c1.parent;
    g2 = c2.parent;

    m1 = g1.getMass();
    m2 = g2.getMass();
    mp1 = g1.getMassCenter();
    mp2 = g2.getMassCenter();

    //Looking for smoother motion
    // float holder = g1.rot%HALF_PI;
    // if (abs(holder)<0.01) g1.rot-=holder;
    // else if (HALF_PI-holder<0.1) g1.rot+=holder;
    // holder = g2.rot%HALF_PI;
    // if (abs(holder)<0.01) g2.rot-= holder;
    // else if (HALF_PI-holder<0.1) g2.rot+=holder;


    solvePenetration();

    norm = out2.cmult(-1);
    norm.normalize();

    Vector tang = new Vector(-norm.y, norm.x);

    ra = collisionPoint.csub(mp1);
    rb = collisionPoint.csub(mp2);
    float j = calculateJ();
    norm.mult(j);
    Vector tangMovement = tang.cmult(g2.vel.csub(g1.vel).dot(tang) * (g1.friction+g2.friction)/2f);
    /*System.out.println("Old velocities");
    System.out.println(g1.vel);
    System.out.println(g2.vel);*/
    if (m1>0) {
      g1.addVelocity(norm.cdiv(m1));
      g1.addAngularVelocity(ra.cross(norm).z * minertia1Inv);

      g1.addVelocity(tangMovement.cmult(m1/(m1+m2)));
    }
    if (m2>0) {
      g2.addVelocity(norm.cdiv(m2).cmult(-1));
      g2.addAngularVelocity(-1*rb.cross(norm).z * minertia2Inv);
      g2.addVelocity(tangMovement.cmult(-m2/(m1+m2)));
    }
    /*System.out.println("New velocities");
    System.out.println(g1.vel);
    System.out.println(g2.vel);*/
  }

  protected void solvePenetration() {
    g1 = c1.parent;
    g2 = c2.parent;
    float notm1 = m1; //These masses are not correct, just some values to help solving penetration
    float notm2 = m2;
    if (m1>0)
    notm1 = c1.moved ? Float.MAX_VALUE-1:m1;
    if (m2>0)
    notm2 = c2.moved ? Float.MAX_VALUE-1:m2;
    out2.setMag(out2.mag()+5);
    Vector g1ToMove = new Vector(0,0);
    Vector g2ToMove = new Vector(0,0);
    if (notm1==-1) {
      if (notm2==-1) {
        g1ToMove = out2.cdiv(2f).cmult(-1);
        g2ToMove = out2.cdiv(2f);
      } else {
        g2ToMove = out2;
      }
    } else if (notm2==-1) {
      g1ToMove = out2.cmult(-1);
    } else if (notm1==notm2) {
      g1ToMove = out2.cdiv(2f).cmult(-1);
      g2ToMove = out2.cdiv(2f);
    } else {
      g1ToMove = out2.cmult(-notm2/(notm1+notm2));
      g2ToMove = out2.cmult(notm1/(notm1+notm2));
    }
    /*Vector g1OutDir = g1.vel.copy().normalize();
    Vector g2OutDir = g2.vel.copy().normalize();
    if ((notm1>0) && g1OutDir.mag()==0) {
      g1OutDir = out2.cmult(-1).normalize();
    }
    if ((notm2>0) && g2OutDir.mag()==0) {
      g2OutDir = out2.copy().normalize();
    }
    float g1Amount = g1ToMove.copy().normalize().dot(g1OutDir);
    float g2Amount = g2ToMove.copy().normalize().dot(g2OutDir);

    if (Float.isNaN(g1Amount)) g1Amount = 0;
    if (Float.isNaN(g2Amount)) g2Amount = 0;
    if (g1Amount==0&&g2Amount==0) {
      g1Amount=0.5f;
      g2Amount = 0.5f;
    }

    if (g1Amount!=0) {
      Vector out = g1OutDir.cmult(out2.mag() * (g1Amount) / (APPLET.abs(g1Amount)+APPLET.abs(g2Amount)));
      g1.shift(out);
    } //else g1.shift(g1ToMove);
    if (g2Amount!=0) {
      Vector out = g2OutDir.cmult(out2.mag()*(g2Amount) / (APPLET.abs(g1Amount)+APPLET.abs(g2Amount)));
      g2.shift(out);
    } //else g2.shift(g2ToMove);*/
    g1.shift(g1ToMove);
    g2.shift(g2ToMove);
  }

  protected float calculateJ() {
    float top = calculateTop();
    float bot = calculateBot();
    return top/bot;
  }

  protected float calculateTop() {
    Vector vap = g1.vel.cadd(new Vector(0,0,g1.omega).cross(ra));
    Vector vbp = g2.vel.cadd(new Vector(0,0,g2.omega).cross(rb));
    Vector vab = vap.csub(vbp);
    return -(1+(g1.collisionEfficency+g2.collisionEfficency)/2) * vab.dot(norm);
  }

  protected float calculateBot() {
    float sum = 0;
    sum += m1<0?0:1/m1;
    sum += m2<0?0:1/m2;
    minertia1Inv = m1<0?0:1/(m1*APPLET.pow(ra.mag(),2));
    minertia2Inv = m2<0?0:1/(m2*APPLET.pow(rb.mag(),2));
    sum += ra.cross(norm).dot(ra.cross(norm)) * minertia1Inv;
    sum += rb.cross(norm).dot(rb.cross(norm)) * minertia2Inv;
    return sum;
  }

}
