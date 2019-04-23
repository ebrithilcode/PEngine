package com.pengine.collisiondetection;

import com.pengine.collisiondetection.colliders.AbstractCollider;
import processing.core.PVector;


public class Collision {

    protected AbstractCollider collider0;
    protected AbstractCollider collider1;
    PVector vectorOut;
    PVector collisionPoint;

    public Collision(AbstractCollider collider0, AbstractCollider collider1, PVector vectorOut, PVector collisionPoint) {
        this.collider0 = collider0;
        this.collider1 = collider1;
        this.vectorOut = vectorOut;
        this.collisionPoint = collisionPoint;
    }

    public void notifyColliderOwners() {
        collider0.getParent().onCollision(this);
        collider1.getParent().onCollision(reverse());
    }

    protected Collision reverse() {
        return new Collision(collider1, collider0, vectorOut, collisionPoint);
    }

}
