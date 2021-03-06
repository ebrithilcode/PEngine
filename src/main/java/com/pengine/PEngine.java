package com.pengine;

import processing.core.PApplet;

import java.lang.reflect.InvocationTargetException;
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
import com.pengine.components.renderers.*;

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

  public Input userInput = new Input();

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
    registerClass(GameObject.class);
    registerClass(RectRenderer.class);
    registerClass(CircleRenderer.class);


    APPLET.ellipseMode(APPLET.RADIUS);
    APPLET.imageMode(APPLET.CENTER);
    APPLET.rectMode(APPLET.CENTER);
    sat = new SAT();
    qt = new QuadTree(sat);
    qt.room = new Boundary(new Vector(0,0), new Vector(APPLET.width, APPLET.height));
    qt.room.infinite = true;
    backgroundColor = APPLET.color(255);

    pt.start();

  }

  void draw() {
    //try {
      APPLET.background(backgroundColor);

      List<GameObject> objects = engineList.getObjects();

    System.out.println("Current List. "+objects.size());
      for (int i = 0; i < objects.size(); i++) {
        objects.get(i).render();
      }
    //} catch (Exception e) { System.out.println(e); }
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

    //I dont know why i was checking for that though it creates a bunch of problems, will just comment it
    //if (g.objectID >0) {
      registerObject(g);
    //}
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
  public void addObjectSilently(GameObject g) {
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
  protected void buildAndAddData(byte[] bytes, int[] iterator, String ip) {

    Data d = createData(bytes, iterator, ip);
    if (d!=null) {
      if (d instanceof GameObject) {
        addObjectSilently((GameObject) d);
        System.out.println("Adding an object to the qeue");
      }
      else if (d instanceof Input) engineList.addInput((Input) d);
      else if (server != null) engineList.addClientData(d);
      else if (client != null) engineList.addServerData(d);
    }
  }
  public Data createData(byte[] bytes, int[] iterator, String ip) {
    try {
        int num = (int) bytes[iterator[0]];

        Class c = idToClass.get(num);
        System.out.println("Class: "+c);
        java.lang.reflect.Method m = c.getMethod("createData", byte[].class, int[].class);
        System.out.println("Method: "+m);
        Data d = (Data) m.invoke(null, bytes, iterator);
        d.ip = ip;
        return d;
    } catch (Exception e) {
      System.out.println("Damn: " + e.getCause());
      //if (e instanceof InvocationTargetException) System.out.println((InvocationTargetException)e.getCause());
      try {
        Thread.sleep(500);

      } catch (Exception ef) {}
    }
    return null;
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

  public <T extends Data> void registerClass(Class<T> cl) {
    if (!classToId.containsKey(cl)) {
      int val = classToId.size();
      classToId.put(cl, val);
      idToClass.put(val, cl);
      try {
        cl.getDeclaredField("classID").setInt(null, val);
        Thread.sleep(100);
      } catch (Exception e) {System.out.println(e);}
    }
  }
  public void registerObject(Data g) {
    g.setID(uniqueObjId);
    System.out.println("Registering object at "+g.objectID);
    uniqueObjId++;
  }



  public void useData(byte[] bytes, String ip) {
    bytes = Data.decodeBytes(bytes);
    int[] iterator = new int[] {0};
    while (iterator[0] < bytes.length-1) {
      int obIPos = iterator[0]+1;
      Data d = dataAlreadyExists(bytes[obIPos]);
      if (d==null) {
        System.out.println("Creating a new GameObejct");
        buildAndAddData(bytes, iterator, ip);
      } else {
        d.updateData(bytes, iterator);
      }
    }
  }

  Data dataAlreadyExists(int id) {
    System.out.println("Looking for: "+id);
    for (Data d: engineList.getObjects()) {
      if (d.objectID == id) {
        System.out.println("Found: "+d.objectID);
        return d;
      }
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
