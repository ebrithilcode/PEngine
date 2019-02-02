package com.pengine;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class PEngine {

  public static PApplet APPLET;

  private ArrayList<GameObject> objects = new ArrayList<>();
  private SAT sat = new SAT();
  private QuadTree qt = new QuadTree(sat);
  float maxMove = 0.1f;
  int steps = 1;
  public float floorFriction = 1.0f;
  public boolean useFloorFriction;
  public ArrayList<Vector> globalForces = new ArrayList<>();

  PhysicsThread pt = new PhysicsThread();

  public PEngine(PApplet applet) {
    APPLET = applet;

    APPLET.ellipseMode(APPLET.RADIUS);
    APPLET.imageMode(APPLET.CENTER);
    APPLET.rectMode(APPLET.CENTER);
    qt.room = new Boundary(new Vector(0,0), new Vector(APPLET.width, APPLET.height));
    qt.room.infinite = true;
  }

  void start() {
    pt.start();
  }

  void draw() {
    for (GameObject g : objects) {
      g.render();
    }
  }
  float getMaxSpeed() {
    float max = Float.MIN_VALUE;
    for (GameObject g: objects) {
      max = APPLET.max(max, g.vel.mag());
    }
    return max;
  }
  void addObject(GameObject g) {
    boolean inserted = false;
    for (int i=0;i<objects.size();i++) {
      if (objects.get(i).renderingPriority > g.renderingPriority) {
        objects.add(i, g);
        inserted = true;
        break;
      }
    }
    if (!inserted) objects.add(g);
    g.setup();
    qt.sortIn(g);
  }

  //hier braucht es einen cleveren Weg sich in die Processing keyhooks einzuklinken
  void keyPressed() {
    for (GameObject g: objects) {
      g.handleKey(true);
    }
  }

  void keyReleased() {
    for (GameObject g: objects) {
      g.handleKey(false);
    }
  }

  Collider noCollision(GameObject g) {
    List<Collider> possible = g.getAllComponentsOfType(Collider.class);
    Collider c1 = null;
    for (Collider c : possible) {
      if (!c.isTrigger) c1 = c;
    }
    if (c1 == null) return null;
    for (GameObject go : objects) {
      if (go != g) {
          possible = go.getAllComponentsOfType(Collider.class);
          Collider c2 = null;
          for (Collider c : possible) {
            if (!c.isTrigger) c2 = c;
          }
        if (c2 == null) continue;
        if (sat.isColliding(c1,c2)[0].mag()>0 && !c1.blackList.contains(c2) && !c2.blackList.contains(c1)) {
          return c2;
        }
      }
    }
    return null;
  }

  private class PhysicsThread extends Thread {

    int frameStart;
    float deltaTime = 0;

    boolean bruteForce = false;

    public void run() {
      while (true) {
        frameStart = APPLET.millis();
        managePhysics();
        deltaTime = (APPLET.millis()-frameStart) / 1000f;
        frameStart = APPLET.millis();
      }
    }

    void managePhysics() {
      for (int i=0;i<objects.size();i++) {
        if (objects.get(i).earlyUpdate()) {
          objects.remove(i);
          i--;
        }
      }

      //Hopefully soon quadtree collisionManagement in O(log n)
      if (!bruteForce) {
        qt.setup();
        qt.manage();


        //Just O(n^2)at the time
      } /*else {
        for (GameObject g: objects) {
          g.deltaTime = deltaTime;
          g.movement();
        }
        sat.manageCollisions(objects);
        sat.solve();
        for (GameObject g: objects) g.lateCollisionSetup();
      }*/


      for (int i=0;i<objects.size();i++) {
        if (objects.get(i).update()) {
          objects.remove(i);
          i--;
        }
      }

      //applies global forces (Like gravity)
      for (Vector v: globalForces) {
        for (GameObject g: objects) {
          if (g.getMass()>0 && !g.noGravity) {
            g.addVelocity(v.cdiv(g.getMass()*APPLET.frameRate));
          }
        }
      }

      //Floor friction
      if (useFloorFriction) {
        for (GameObject g: objects) {
          Vector fri = g.vel.cmult(-(g.friction+floorFriction)/2f);
          g.vel.add(fri);
        }
      }

      for (int i=0;i<objects.size();i++) {
        if (objects.get(i).lateUpdate()) {
          objects.remove(i);
          i--;
        }
      }
    }
  }
}
