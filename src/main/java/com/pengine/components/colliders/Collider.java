package com.pengine.components.colliders;

import com.pengine.Entity;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

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
