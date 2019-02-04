package com.pengine;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import com.pengine.InputInternet.ClientConnection;
import com.pengine.InputInternet.ServerConnection;
import com.pengine.InputInternet.Input;
import com.pengine.InputInternet.Data;
import com.pengine.components.Collider;
import com.pengine.components.Component;
import com.pengine.components.colliders.*;

public class PEngine {

  public static PApplet APPLET;
  public static PEngine ENGINE;


  public EngineList engineList = new EngineList();

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
  public HashMap<Class<? extends Data>, Integer> classToId;
  public HashMap<Integer, Class<? extends Data>> idToClass;
  ClientConnection client;
  ServerConnection server;
  private int uniqueObjId = 0;

  public PEngine(PApplet applet) {
    APPLET = applet;
    ENGINE = this;
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

    List<GameObject> objects = engineList.getObjects();
    for (int i=0;i<objects.size();i++) {
      objects.get(i).render();
    }
  }
  float getMaxSpeed() {
    float max = Float.MIN_VALUE;
    for (GameObject g: engineList.getObjects()) {
      max = APPLET.max(max, g.vel.mag());
    }
    return max;
  }

  public void addObject(GameObject g) {

    g.engine = this;

    if (g.objectID >0) {
      g.objectID = uniqueObjId;
      uniqueObjId++;
    }
    boolean inserted = false;
      List<GameObject> objects = engineList.getObjects();
    for (int i=0;i<objects.size();i++) {
      if (objects.get(i).renderingPriority > g.renderingPriority) {
        engineList.addObject(i, g);
        inserted = true;
        break;
      }
    }
    if (!inserted) objects.add(g);
    g.setup();
    qt.sortIn(g);
  }
  public void createData(byte[] bytes, int[] iterator) {
    try {
        Data d = (Data) idToClass.get(bytes[iterator[0]]).getMethod("createData", byte[].class, int[].class).invoke(bytes, iterator);
        if (d instanceof GameObject) addObject((GameObject) d);
        else if (d instanceof Input) engineList.addInput((Input)d);
        else if (server != null) engineList.addClientData(d);
        else if (client != null) engineList.addServerData(d);
    } catch (Exception e) {}
  }
  //hier braucht es einen cleveren Weg sich in die Processing keyhooks einzuklinken
  void keyPressed() {
    userInput.manageKey(APPLET.key, true);
    for (GameObject g: engineList.getObjects()) {
      g.handleKey(true);
    }
  }

  void keyReleased() {
    userInput.manageKey(APPLET.key, false);
    for (GameObject g: engineList.getObjects()) {
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
    for (GameObject go : engineList.getObjects()) {
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
    } else System.out.println("Already contained");
  }

  public void startServer() {
    server = new ServerConnection(this);
    server.start();
  }
  public void startServer(int port) {
    server = new ServerConnection(this);
    server.port = port;
    server.startServer();
    server.start();
  }
  public void startClient() {
    client = new ClientConnection(this);
    client.connect();
    client.start();
  }

  public void startClient(String ip, int port) {
    client = new ClientConnection(this);
    client.ip = ip;
    client.port = port;
    client.connect();
    client.start();
  }
  public void stopServer() {
    server.end();
  }
  public void stopClient() {
    client.end();
  }

  public void useData(byte[] bytes) {
    bytes = Data.decodeBytes(bytes);
    int[] iterator = new int[] {0};
    while (iterator[0] < bytes.length) {
      Data d = dataAlreadyExists(bytes[iterator[0]+1]);
      if (d==null) {
        createData(bytes, iterator);
      } else {
        d.updateData(bytes, iterator);
      }
    }
  }

  Data dataAlreadyExists(int id) {
    for (Data d: engineList.getObjects()) {
      if (d.objectID == id) return d;
    }
    for (Data d: engineList.getInputs()) {
      if (d.objectID==id) return d;
    }
    for (Data d: engineList.getClientData()) {
      if (d.objectID == id) return d;
    }
    return null;
  }

  //Physics


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
      List<GameObject> objects = engineList.getObjects();
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
