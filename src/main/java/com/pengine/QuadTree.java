package com.pengine;

import com.pengine.SAT;

import java.util.ArrayList;
import java.util.List;

import static com.pengine.PEngine.APPLET;

public class QuadTree {

  QuadTree parent;
  List<GameObject> holdObjects;
  List<GameObject> toCheck;
  QuadTree[] children;
  Boundary room;
  float maxSpeed;
  SAT sat;

  public QuadTree(SAT s){
    sat = s;
    holdObjects = new ArrayList<GameObject>();
    children = new QuadTree[0];

    APPLET.println(APPLET);
    APPLET.println(APPLET.width);
    room = new Boundary(new Vector(0,0), new Vector(APPLET.width, APPLET.height));
  }

  public QuadTree(QuadTree p, SAT s) {
    sat = s;
    holdObjects = new ArrayList<GameObject>();
    children = new QuadTree[0];
    room = new Boundary(new Vector(0,0), new Vector(APPLET.width, APPLET.height));
    parent = p;
  }

  public void sortUp() {
    for (int i=holdObjects.size()-1;i>=0;i--) {
      GameObject g = holdObjects.get(i);
      Boundary b = g.bounds;
      if (!room.contains(b)) {
        holdObjects.remove(i);
        parent.sortIn(g);
      }
    }
  }

  public void sortIn(GameObject g) {
    Boundary b = g.bounds;
    if (holdObjects.size()>1) {
      if (children.length == 0) {
        children = new QuadTree[4];
        for (int i=0;i<4;i++) {
          children[i] = new QuadTree(this, sat);
        }
        children[0].room = new Boundary(room.pos, room.dim.cdiv(2));
        children[1].room = new Boundary(room.pos.cadd(new Vector(room.dim.x/2f,0)), room.dim.cdiv(2));
        children[2].room = new Boundary(room.pos.cadd(new Vector(room.dim.x/2f,room.dim.y/2f)), room.dim.cdiv(2));
        children[3].room = new Boundary(room.pos.cadd(new Vector(0,room.dim.y/2f)), room.dim.cdiv(2));
      }
      for (QuadTree c: children) {
        if (c.room.contains(b)) {
          c.sortIn(g);
          return;
        }
      }
    }
    holdObjects.add(g);
  }

  public void manage() {
    toCheck = new ArrayList<GameObject>();
    toCheck.addAll(holdObjects);
    if (parent!=null)
      toCheck.addAll(parent.toCheck);


    //Some kind of mixed postorder thing?
    for (QuadTree qt: children) {
      qt.manage();
    }


    for (GameObject g: holdObjects) {
      g.movement();
    }

    sat.manageCollisions(holdObjects, toCheck);
    sat.solve();

    for (GameObject g: holdObjects) {
      g.lateCollisionSetup();
    }
  }

  public void getMaxSpeed() {
    maxSpeed = 0;
    for (QuadTree c: children) {
      c.getMaxSpeed();
      maxSpeed = APPLET.max(c.maxSpeed, maxSpeed);
    }
    for (GameObject g: holdObjects) {
      maxSpeed = APPLET.max(g.vel.mag(), maxSpeed);
    }
  }

  public void setup() {
    //getMaxSpeed();
    postOrderSort();
  }

  public int search(GameObject g) {
    int s = 0;
    for (QuadTree t: children) {
      s+=t.search(g);
    }
    if (holdObjects.contains(g)) s++;
    return s;
  }

  private void postOrderSort() {
    for (QuadTree c: children) {
      c.postOrderSort();
    }
    sortUp();
  }

}
