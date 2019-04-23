package com.pengine;

import com.pengine.collisiondetection.CollisionDetectionSystem;
import com.pengine.collisiondetection.colliders.AbstractCollider;
import com.pengine.net.server.ServerHandler;
import com.pengine.rendering.IRenderer;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class PEngine {

    public static PApplet APPLET;
    public float floorFriction = 1.0f;
    public boolean useFloorFriction;
    public ArrayList<PVector> globalForces = new ArrayList<>();
    public PhysicsThread pt = new PhysicsThread();
    float maxMove = 0.1f;
    int steps = 1;
    private TreeSet<IRenderer> renderables;
    //private Set<AbstractCollider> colliders;

    private CollisionDetectionSystem cds;
    //private PhysicsSystem ps;

    private ServerHandler serverHandler;

    public PEngine(PApplet applet) {
        APPLET = applet;
        APPLET.registerMethod("draw", this);
        APPLET.registerMethod("dispose", this);

        renderables = new TreeSet<>();
    }

    public static PEngine createServer(PApplet applet, int port, Object[] finalData, String clientEventListeningMethod) {
        PEngine engine = new PEngine(applet);
        engine.serverHandler = new ServerHandler(engine, port, finalData, clientEventListeningMethod);
        return engine;
    }

    public ServerHandler getRenderHandler() {
        return serverHandler;
    }

    public void draw() {
        render();
        update();
    }

    public void dispose() {
        if(serverHandler != null) serverHandler.dispose();
    }

    public void killEntity(Entity entity) {
        entity.kill();
        entities.remove(entity);
        //cleanup renderer references, prob. outsource to other method
    }

    public boolean registerRenderable(IRenderer renderable) {
        return renderables.add(renderable);
    }

    public boolean unregisterRenderable(IRenderer renderable) {
        return renderables.remove(renderable);
    }

    public boolean registerCollider(AbstractCollider collider) {
        return cds.addCollider(collider);
    }

    public boolean unregisterCollider(AbstractCollider collider) {
        return cds.removeCollider(collider;
    }

    public Iterable<IRenderer> getRenderables() {
        return renderables;
    }


    public void addCollider(AbstractCollider collider) {
        engineColliders.add(collider);
        cds.addCollider(collider);
    }

    private void render() {
        APPLET.pushStyle();
        APPLET.rectMode(APPLET.CENTER);
        APPLET.ellipseMode(APPLET.CENTER);
        APPLET.imageMode(APPLET.CENTER);
        APPLET.shapeMode(APPLET.CENTER);

        //go through weak referenced renderables, that are assigned to entites, etc.
        for(Iterator<IRenderer> rendererIterator = renderables.iterator(); rendererIterator.hasNext();) {
            IRenderer currentRenderer = rendererIterator.next();
            if(currentRenderer.v) {
                referenceIterator.remove();
                continue;
            }
            renderable.render();
        }

        //go trough additional renderables assigned to the engine itself
        for(IRenderer renderable : engineRenderables) {
            renderable.render();
        }


        APPLET.popStyle();
    }

    private void update() {

    }

    float getMaxSpeed() {
        float max = Float.MIN_VALUE;
        for (GameObject g: engineList.getObjects()) {
            max = APPLET.max(max, g.vel.mag());
        }
        return max;
    }

    public void addEntity(Entity entity) {
        entity.setParent(this);
        entity.registerAllRenderers();
        entity.registerAllColliders();
    }

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



    //Physics


    public float  getPhysicsFrameRate() {
        return 1 / pt.deltaTime;
    }
    public float getPhysicsDelta() {
        return pt.deltaTime;
    }


    class PhysicsThread extends Thread {

        public float deltaTime;
        int frameStart;
        boolean bruteForce = false;

        @Override
        public void run() {
            while (true) {
                frameStart = APPLET.millis();
                managePhysics();
                APPLET.delay(PApplet.max(0, 17-(APPLET.millis()-frameStart)));
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
            for (PVector v: globalForces) {
                for (GameObject g: objects) {
                    if (g.getMass()>0 && !g.noGravity) {
                        g.addVelocity(v.cdiv(g.getMass()*APPLET.frameRate));
                    }
                }
            }

            //Floor friction
            if (useFloorFriction) {
                for (GameObject g: objects) {
                    PVector fri = g.vel.cmult(-(g.friction+floorFriction)/2f);
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
