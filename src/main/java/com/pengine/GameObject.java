package com.pengine;

import java.util.ArrayList;
import java.util.List;

import static com.pengine.PEngine.APPLET;

public class GameObject implements Updatable {

  List<Component> components;
  public Vector pos;
  public float rot;
  float mass;
  boolean dead;
  float deltaTime;
  boolean noGravity;
  float maxRadius;
  Boundary bounds;
  float friction;
  //Bewegungen
  Vector vel;
  //Kreisofrequenz
  float omega;
  boolean lockRotation;
  float collisionEfficency;

  List<Collider> collisionsDuringFrame;
  List<Collider> collisionsPreviousFrame;

  GameObject() {
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

  void addVelocity(Vector v) {
    vel.add(v);
    for (Component c: components) {
      if (c instanceof Connection) {
        ((Connection)c).apply(v,0);
      }
    }
  }
  void addVelocityDiscret(Vector v) {
    vel.add(v);
  }
  void addAngularVelocity(float f) {
    if (!lockRotation) {
      omega += f;
      for (Component c: components) {
      if (c instanceof Connection) {
          ((Connection)c).apply(new Vector(0,0), 0);
        }
      }
    }
  }

  void shift(Vector v) {
    pos.add(v);
    // for (Component c: components) {
    //   if (c instanceof Connection) {
    //     ((Connection)c).connected.shift(v);
    //   }
    // }
  }
  Vector getMassCenter() {
    for (Component c: components) {
      if (c instanceof Connection) {
        return ((Connection)c).getMassCenter();
      }
    }
    return pos.copy();
  }
  float getMass() {
    // for (Component c: components) {
    //   if (c instanceof Connection) {
    //     return ((Connection)c).getMass();
    //   }
    // }
    return mass;
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
    //println("This object can leave");
  }

  public void handleKey(boolean pressed) {}

  void setup() {
    bounds = getBoundary();
  }

  public boolean earlyUpdate() {
    deltaTime = 1f/APPLET.frameRate;
    for (Component c: components) {
      c.earlyUpdate();
    }
    collisionsPreviousFrame = new ArrayList<>(collisionsDuringFrame);
    //benchmark?
    //collisionsPreviousFrame = (ArrayList<Collider>) ((ArrayList<Collider>)collisionsDuringFrame).clone();
    collisionsDuringFrame.clear();
    return false;
  }

  public boolean update() {
    for (Component c: components) {
      c.update();
    }
    return false;
  }

  public boolean lateUpdate() {
    for (Component c: components) {
      c.lateUpdate();
    }
    return false;
  }

  public void movement() {
    //Bewegung managen
    pos.add(new Vector((vel.x * deltaTime), (vel.y * deltaTime)));
    rot += omega * deltaTime;
    bounds = getBoundary();
    for (Component c: components) {
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
}
