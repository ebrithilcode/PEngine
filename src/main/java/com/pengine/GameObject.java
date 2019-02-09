package com.pengine;

import java.util.ArrayList;
import java.util.List;
import com.pengine.components.Component;
import com.pengine.components.Collider;
import com.pengine.components.Connection;
import com.pengine.components.AbstractRenderer;
import com.pengine.InputInternet.Data;

import static com.pengine.PEngine.APPLET;
import static com.pengine.PEngine.ENGINE;

public class GameObject extends Data implements Updatable {

  public static int classID;

  List<Component> components;
  public Vector pos;
  public float rot;
  public float mass;
  boolean dead;
  float deltaTime;
  public boolean noGravity;
  public float maxRadius;
  Boundary bounds;
  public float friction;
  //Bewegungen
  Vector vel;
  //Kreisofrequenz
  float omega;
  boolean lockRotation;
  public float collisionEfficency;

  public int renderingPriority;

  public List<Collider> collisionsDuringFrame;
  public List<Collider> collisionsPreviousFrame;

  //Networking relevant
  public PEngine engine;


  {
    dontSendMePlease = false;
  }

  public GameObject() {
    components = new ArrayList<>();
    pos = new Vector(0,0);
    mass = 1;
    dead = false;
    friction = 0f;
    vel = new Vector(0,0);
    collisionEfficency = 1;

    collisionsDuringFrame = new ArrayList<>();
    collisionsPreviousFrame = new ArrayList<>();
  }

  public void kill() {
    dead = true;
  }

  public <T extends Component> List<T> getAllComponentsOfType(Class<T> cl) {
    List<T> allComponentsOfType = new ArrayList<>();
    for (Component c: components) {
      if (cl.isInstance(c)) {
        if (c.active) allComponentsOfType.add(cl.cast(c));
      }
    }
    //if(allComponentsOfType.isEmpty()) return Collections.emptyList();
    return allComponentsOfType;
  }

  public <T extends Component> T getComponentOfType(Class<T> cl) {
    for (Component c: components) {
      if (cl.isInstance(c)) {
        if (c.active) return cl.cast(c);
      }
    }
    return null;
  }

  public void addComponent(Component c) {
    components.add(c);
    ENGINE.registerObject(c);
  }

  public void removeComponent(Class<? extends Component> cl) {
    for (Component c : components) {
      if(cl.isInstance(c)) {
        components.remove(c);
        return;
      }
    }
  }

  void removeAllComponent(Class<? extends Component> cl) {
    for (int i=components.size()-1; i>=0;i--) {
      Component c = components.get(i);
      if(cl.isInstance(c)) {
        components.remove(c);
      }
    }
  }

  public void addVelocity(Vector v) {
    vel.add(v);
    for (Component c: components) {
      if (c instanceof Connection) {
        ((Connection)c).apply(v,0);
      }
    }
  }
  public void addVelocityDiscret(Vector v) {
    vel.add(v);
  }
  public void addAngularVelocity(float f) {
    if (!lockRotation) {
      omega += f;
      for (Component c: components) {
      if (c instanceof Connection) {
          ((Connection)c).apply(new Vector(0,0), 0);
        }
      }
    }
  }

  public void shift(Vector v) {
    pos.add(v);
    // for (Component c: components) {
    //   if (c instanceof Connection) {
    //     ((Connection)c).connected.shift(v);
    //   }
    // }
  }
  public Vector getMassCenter() {
    for (Component c: components) {
      if (c instanceof Connection) {
        return ((Connection)c).getMassCenter();
      }
    }
    return pos.copy();
  }
  public float getMass() {
    // for (Component c: components) {
    //   if (c instanceof Connection) {
    //     return ((Connection)c).getMass();
    //   }
    // }
    return mass;
  }
  public void render() {
    System.out.println("I have "+components.size()+" components");
    for (int i=0;i<components.size();i++) {
      Component c = components.get(i);
      if (c instanceof AbstractRenderer) {
        ((AbstractRenderer)c).show();
      }
    }
  }

  public boolean onCollision(Collider other, Vector out, Collider mine) {
    mine.isColliding.add(other);
    collisionsDuringFrame.add(other);
    if (!mine.wasColliding.contains(other)) {
      onCollisionEnter(other, mine);
    }
    return true;
  }

  public void onCollisionEnter(Collider other, Collider mine) {}

  public void onCollisionLeave(Collider other, Collider mine) {
  }

  public void handleKey(boolean pressed) {}

  void setup() {
    bounds = getBoundary();
  }

  public boolean earlyUpdate() {
    deltaTime = 1f/APPLET.frameRate;
    for (int i = 0;i<components.size();i++) {
      Component c = components.get(i);
      c.earlyUpdate();
    }
    collisionsPreviousFrame = new ArrayList<>(collisionsDuringFrame);
    //benchmark?
    //collisionsPreviousFrame = (ArrayList<Collider>) ((ArrayList<Collider>)collisionsDuringFrame).clone();
    collisionsDuringFrame.clear();
    return false;
  }

  public boolean update() {
    for (int i = 0;i<components.size();i++) {
      Component c = components.get(i);
      c.update();
    }
    return false;
  }

  public boolean lateUpdate() {
    for (int i = 0;i<components.size();i++) {
      Component c = components.get(i);
      c.lateUpdate();
    }
    return false;
  }

  public void movement() {
    //Bewegung managen
    pos.add(new Vector((vel.x * deltaTime), (vel.y * deltaTime)));
    rot += omega * deltaTime;
    bounds = getBoundary();
    for (int i = 0;i<components.size();i++) {
      Component c = components.get(i);
      c.movement();
    }
  }

  public void lateCollisionSetup() {
    List<Collider> allColliders = getAllComponentsOfType(Collider.class);
    for (Collider c : allColliders) {
      for (Collider cm : c.wasColliding) {
        if (!c.isColliding.contains(cm)) onCollisionLeave(cm, c);
      }
      c.wasColliding = new ArrayList<>(c.isColliding);
      //c.wasColliding.addAll(c.isColliding);
      c.isColliding.clear();
    }
  }

  public Boundary getBoundary() {
    float left = pos.x + (vel.x<0 ? vel.x : 0) - maxRadius;
    float right = pos.x + (vel.x>0 ? vel.x : 0) + maxRadius;
    float up = pos.y + (vel.y<0 ? vel.y : 0) - maxRadius;
    float down = pos.y + (vel.y>0 ? vel.y : 0) + maxRadius;
    return new Boundary(new Vector(left,up), new Vector(right-left, down-up));
  }


  //Data type methods:

  public static GameObject createData(byte[] b, int[] index) {
    System.out.println("Creating gameObject");
    GameObject g = new GameObject();
    //Skip class ID;
    index[0]++;
    g.objectID = b[index[0]++];
    index[0]++;
    g.pos = Vector.createData(b, index);
    g.rot = byteToFloat(subarray(b,index,4));
    int comNum = b[index[0]++];
    for (int i=0;i<comNum;i++) {
      Component c = (Component) ENGINE.createData(b, index, "I dont give a shit.com");
      c.parent = g;
      g.components.add(c);
    }
    Data.nextWord(b, index);
    return g;
  }


  @Override
  public String toString() {
    String ret = "";

    ret += (char) classID;
    ret += (char) objectID;
    ret += (char) (dontUpdateMe ? 1 : 0);
    ret += pos.toString();
    ret = concateByteArray(ret, floatToByte(rot));
    //Count sendable components
    int sendable = 0;
    String attachLater = "";
    for (int i=0;i<components.size();i++) {
      if (!components.get(i).dontSendMePlease) {
        sendable++;
        attachLater += components.get(i).toString();
      }
    }
    ret += (char) sendable;
    ret += attachLater;
    ret += '\n';

    return ret;
  }

  public void updateData(byte[] b, int... index) {

    //Skip class and object id
    index[0] += 3;


    if (b[index[0]+1] == pos.objectID && !pos.alwaysCreateNew) {
      if (b[index[0]+2] == 0)
        pos.updateData(b, index);
      else
        Data.nextWord(b, index);
    } else {
      pos = Vector.createData(b, index);
    }
    rot = byteToFloat(subarray(b, index, 4));
    System.out.println("New position is " + pos.x);
    int compsToAdd = b[index[0]++];
    System.out.println("Waiting for: "+compsToAdd + " Components");
    for (int i=0;i<compsToAdd;i++) {
      Component c = alreadyContained(b[index[0]+1]);
      if (c != null && !c.alwaysCreateNew) {
        //if (b[index[0]+2]==0)
          c.updateData(b, index);
        //else
        //  Data.nextWord(b, index);
      } else {
        addComponent((Component) ENGINE.createData(b, index, "No shit sherlock"));
      }
    }
    System.out.println("Now I have "+components.size() + " Components");
    Data.nextWord(b, index);
  }

  Component alreadyContained(int id) {
    for (int i=0;i<components.size();i++) {
      if (components.get(i).objectID == id) return components.get(i);
    }
    return null;
  }
}
