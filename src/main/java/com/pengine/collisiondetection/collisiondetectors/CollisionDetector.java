package com.pengine.collisiondetection.collisiondetectors;

import com.pengine.collisiondetection.Collision;
import com.pengine.collisiondetection.colliders.Collider;

public interface CollisionDetector<T extends Collider, S extends Collider> {

    public Collision getCollision(T a, S b);

}
