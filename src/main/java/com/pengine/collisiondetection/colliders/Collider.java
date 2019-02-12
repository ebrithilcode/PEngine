package com.pengine.collisiondetection.colliders;

import com.pengine.Entity;
import processing.core.PVector;

public abstract class Collider{

    //public boolean isTrigger = true;
    public PVector[] boundaryVertices;

    /*public List<Collider> isColliding;
    public List<Collider> wasColliding;
    public List<Collider> blackList;*/

    public Entity parent;

    public Collider(Entity parent) {
        this.parent = parent;
        /*isColliding = new ArrayList<>();
        wasColliding = new ArrayList<>();
        blackList = new ArrayList<>();*/
    }

    public String getIdentifier() {
        return "ERROR";
    }

}
