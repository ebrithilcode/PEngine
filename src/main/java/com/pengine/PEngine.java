package com.pengine;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import com.pengine.InputInternet.ClientConnection;
import com.pengine.InputInternet.ServerConnection;
import com.pengine.InputInternet.Input;

public class PEngine {

  public static PApplet APPLET;

  public ArrayList<GameObject> objects = new ArrayList<>();
  private SAT sat;
  private QuadTree qt;
  float maxMove = 0.1f;
  int steps = 1;
  public float floorFriction = 1.0f;
  public boolean useFloorFriction;
  public ArrayList<Vector> globalForces = new ArrayList<>();

  public int backgroundColor;

  public PhysicsThread pt = new PhysicsThread();

  public Input userInput;

  //Networking relevant
  public HashMap<Class<? extends GameObject>, Integer> classToId;
  public HashMap<Integer, Class<? extends GameObject>> idToClass;
  ClientConnection client;
  ServerConnection server;

  public PEngine(PApplet applet) {
    APPLET = applet;
  }

  void setup() {
    classToId = new HashMap<>();
    idToClass = new HashMap<>();

    APPLET.ellipseMode(APPLET.RADIUS);
    APPLET.imageMode(APPLET.CENTER);
    APPLET.rectMode(APPLET.CENTER);
    sat = new SAT();
    qt = new QuadTree(sat);
    qt.room = new Boundary(new Vector(0,0), new Vector(APPLET.width, APPLET.height));
    qt.room.infinite = true;
    backgroundColor = APPLET.color(255);

    System.out.println("Got a new QuadTree: "+qt);
    pt.start();

  }

  void draw() {
    APPLET.background(backgroundColor);
    for (int i=0;i<objects.size();i++) {
      objects.get(i).render();
    }
  }
  float getMaxSpeed() {
    float max = Float.MIN_VALUE;
    for (GameObject g: objects) {
      max = APPLET.max(max, g.vel.mag());
    }
    return max;
  }

  public void addObject(GameObject g) {
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
    System.out.println("GameObejct: "+g);
    System.out.println("QuadTree: "+qt);
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

  void mousePressed() {}
  void mouseReleased() {}

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

  //Networking relevant

  public <T extends GameObject> void registerClass(Class<T> cl) {
    if (!classToId.containsKey(cl)) {
      int val = classToId.size();
      classToId.put(cl, val);
      idToClass.put(val, cl);
    }
  }

  public void startServer() {
    server = new ServerConnection(this);
    server.start();
  }
  public void startServer(int port) {
    server = new ServerConnection(this);
    server.port = port;
    server.start();
  }
  public void startClient() {
    client = new ClientConnection(this);
    client.start();
  }

  public void startClient(String ip, int port) {
    client = new ClientConnection(this);
    client.ip = ip;
    client.port = port;
    client.start();
  }
  public void stopServer() {
    server.end();
  }
  public void stopClient() {
    client.end();
  }


  public float  getPhysicsFrameRate() {
    return 1 / pt.deltaTime;
  }
  public float getPhysicsDelta() {
    return pt.deltaTime;
  }


  class PhysicsThread extends Thread {

    int frameStart;
    public float deltaTime;

    boolean bruteForce = false;
    public void run() {
      while (true) {
        frameStart = APPLET.millis();
        managePhysics();
        APPLET.delay(APPLET.max(0, 17-(APPLET.millis()-frameStart)));
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
        objects.get(i).deltaTime = deltaTime;
      }

      //Hopefully soon quadtree collisionManagement in logn
      if (!bruteForce) {
        qt.setup();
        qt.manage();


        //Just n squared at the time
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
