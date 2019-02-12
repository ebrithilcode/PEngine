package com.pengine;

import java.util.ArrayList;
import java.util.List;

import com.pengine.collisiondetection.colliders.Collider;
import com.pengine.components.Component;
import com.pengine.components.Connection;
import com.pengine.components.AbstractRenderer;
import processing.core.PVector;

import static com.pengine.PEngine.APPLET;

public class Entity {

    //List<Component> components;
    List<Collider> colliders;
    List<AbstractRenderer> renderers;

    public processing.core.PVector position;
    public processing.core.PVector heading;
    public float orientation;

    public float mass;
    public float surfaceFriction;

    boolean isDead;
    float deltaTime;

    public List<processing.core.PVector> additionalForces;
    public float maxRadius;

    AABB bounds;
    //Bewegungen
    PVector vel;

    //Kreisofrequenz
    float omega;
    boolean lockRotation;
    public float collisionEfficency;

    public int renderingPriority;

    public List<Collider> collisionsDuringFrame;
    public List<Collider> collisionsPreviousFrame;

    //Networking relevant
    public int objectID = -1;
    public PEngine engine;


    {
        dontSendMePlease = false;
    }

    public GameObject() {
        components = new ArrayList<>();
        pos = new PVector(0,0);
        mass = 1;
        dead = false;
        friction = 0f;
        vel = new PVector(0,0);
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

    public void addVelocity(PVector v) {
        vel.add(v);
        for (Component c: components) {
            if (c instanceof Connection) {
                ((Connection)c).apply(v,0);
            }
        }
    }
    public void addVelocityDiscret(PVector v) {
        vel.add(v);
    }

    public void addAngularVelocity(float f) {
        if (!lockRotation) {
            omega += f;
            for (Component c: components) {
                if (c instanceof Connection) {
                    ((Connection)c).apply(new PVector(0,0), 0);
                }
            }
        }
    }

    public void shift(PVector v) {
        pos.add(v);
        // for (Component c: components) {
        //   if (c instanceof Connection) {
        //     ((Connection)c).connected.shift(v);
        //   }
        // }
    }
    public PVector getMassCenter() {
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
        for (Component c: components) {
            if (c instanceof AbstractRenderer) {
                ((AbstractRenderer)c).show();
            }
        }
    }

    public boolean onCollision(Collider other, PVector out, Collider mine) {
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
        pos.add(new PVector((vel.x * deltaTime), (vel.y * deltaTime)));
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
        return new Boundary(new PVector(left,up), new PVector(right-left, down-up));
    }


    //Data type methods:

    public static GameObject createData(byte[] b, int... index) {
        GameObject g = new GameObject();
        //Skip class ID;
        index[0]++;
        g.objectID = index[0]++;
        g.pos = PVector.createData(b, index);
        int comNum = b[index[0]++];
        for (int i=0;i<comNum;i++) {
            g.components.add((Component) Component.createData(b, index));
            g.components.get(i).parent = g;
        }
        return g;
    }


    @Override
    public String toString() {
        String ret = "";
        ret += classID;
        ret += objectID;
        ret += pos.toString();
        ret += (char) components.size();
        for (int i=0;i<components.size();i++) {
            ret += components.get(i).toString();
        }
        return ret;
    }

    public void updateData(byte[] b, int... index) {
        //Skip class and object id
        index[0] += 2;
        if (b[index[0]+1] == pos.objectID)
            pos.updateData(b, index);
        else
            pos = PVector.createData(b, index);

        for (int i=0;i<b[index[0]++];i++) {
            if (b[index[0]+1] == components.get(i).objectID)
                components.get(i).updateData(b, index);
            else
                components.add(i, (Component) Component.createData(b, index));
        }
    }
}
