package com.pengine.collisiondetection.holderimpl;

import com.pengine.AABB;
import com.pengine.Entity;

import java.util.ArrayList;
import java.util.List;

import static com.pengine.PEngine.APPLET;

public class QuadTree {

    public static int MIN_ENTITIES_FOR_REDISTRIBUTION;
    public static int MAX_ENTITIES_FOR_CREATION;

    QuadTree parent;

    List<Entity> entities;

    List<Entity> toCheck;

    QuadTree[] children;

    private AABB boundingBox;
    float maxSpeed;

    public QuadTree(QuadTree quadTree) {
        entities = new ArrayList<>();
        //children = new QuadTree[0];
        boundingBox = new AABB(0, 0, APPLET.width, APPLET.height);
        parent = p;
    }

    public void pushToParent() {
        for (int i=entities.size()-1; i>=0; i--) {
            Entity e = entities.get(i);
            AABB b = e.bounds;
            if (!boundingBox.contains(b)) {
                entities.remove(i);
                parent.add(e);
            }
        }
    }

    public void add(Entity e) {
        AABB entityBoundings = e.getBoundingBox();
        if (entities.size() > 1) {
            if (children.length == 0) {
                children = new QuadTree[4];
                for (int i=0;i<4;i++) {
                    children[i] = new QuadTree(this);
                }
                children[0].boundingBox = new AABB();
                children[1].boundingBox = new AABB();
                children[2].boundingBox = new AABB();
                children[3].boundingBox = new AABB();
            }
            for (QuadTree c : children) {
                if (c.boundingBox.contains(entityBoundings)) {
                    c.add(e);
                    return;
                }
            }
        }
        entities.add(e);
    }

    public void manage() {
        toCheck = new ArrayList<Entity>();
        toCheck.addAll(entities);
        if (parent!=null)
            toCheck.addAll(parent.toCheck);


        //Some kind of mixed postorder thing?
        for (QuadTree qt: children) {
            qt.manage();
        }


        for (int i = entities.size()-1;i>=0;i--) {
            Entity g = entities.get(i);
            g.movement();
        }

        sat.manageCollisions(entities, toCheck);
        sat.solve();

        for (int i=entities.size()-1; i>=0; i--) {
            Entity g = entities.get(i);
            g.lateCollisionSetup();
        }
    }

    public void getMaxSpeed() {
        maxSpeed = 0;
        for (QuadTree c: children) {
            c.getMaxSpeed();
            maxSpeed = APPLET.max(c.maxSpeed, maxSpeed);
        }
        for (Entity g: entities) {
            maxSpeed = APPLET.max(e.vel.mag(), maxSpeed);
        }
    }

    public void setup() {
        //getMaxSpeed();
        postOrderSort();
    }

    public int search(Entity g) {
        int s = 0;
        for (QuadTree t: children) {
            s+=t.search(g);
        }
        if (entities.contains(g)) s++;
        return s;
    }

    private void postOrderSort() {
        for (QuadTree c: children) {
            c.postOrderSort();
        }
        sortUp();
    }

}
