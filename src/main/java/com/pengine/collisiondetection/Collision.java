package com.pengine.collisiondetection;

import com.pengine.collisiondetection.colliders.Collider;
import processing.core.PVector;

import static com.pengine.PEngine.APPLET;

public class Collision {

  //CollisionInformation
  Collider c1;
  Collider c2;
  PVector out2;
  PVector collisionPoint;

  //Mass specific
  PVector mp1;
  PVector mp2;
  float m1;
  float m2;

  //Helping values
  PVector norm;
  PVector ra;
  PVector rb;
  float minertia1Inv;
  float minertia2Inv;

  public Collision (Collider p1, Collider p2, PVector o, PVector c) {
    c1 = p1;
    c2 = p2;
    out2 = o;
    collisionPoint = c;
  }

  public Collision reverse() {
    return new Collision(c2, c1, PVector.mult(out2, -1), collisionPoint);
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

    PVector tang = new PVector(-norm.y, norm.x);

    ra = collisionPoint.csub(mp1);
    rb = collisionPoint.csub(mp2);
    float j = calculateJ();
    norm.mult(j);
    PVector tangMovement = tang.cmult(g2.vel.csub(g1.vel).dot(tang) * (g1.friction+g2.friction)/2f);
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
    PVector g1ToMove = new PVector(0,0);
    PVector g2ToMove = new PVector(0,0);
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
    /*PVector g1OutDir = g1.vel.copy().normalize();
    PVector g2OutDir = g2.vel.copy().normalize();
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
      PVector out = g1OutDir.cmult(out2.mag() * (g1Amount) / (APPLET.abs(g1Amount)+APPLET.abs(g2Amount)));
      g1.shift(out);
    } //else g1.shift(g1ToMove);
    if (g2Amount!=0) {
      PVector out = g2OutDir.cmult(out2.mag()*(g2Amount) / (APPLET.abs(g1Amount)+APPLET.abs(g2Amount)));
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
    PVector vap = g1.vel.cadd(new PVector(0,0,g1.omega).cross(ra));
    PVector vbp = g2.vel.cadd(new PVector(0,0,g2.omega).cross(rb));
    PVector vab = vap.csub(vbp);
    return -(1+(g1.collisionEfficency+g2.collisionEfficency)/2) * vab.dot(norm);
  }

  protected float calculateBot() {
    float sum = 0;
    sum += m1<0 ? 0 : 1/m1;
    sum += m2<0 ? 0 : 1/m2;
    minertia1Inv = m1<0 ? 0 : 1/(m1*APPLET.pow(ra.mag(),2));
    minertia2Inv = m2<0 ? 0 : 1/(m2*APPLET.pow(rb.mag(),2));
    sum += ra.cross(norm).dot(ra.cross(norm)) * minertia1Inv;
    sum += rb.cross(norm).dot(rb.cross(norm)) * minertia2Inv;
    return sum;
  }

}