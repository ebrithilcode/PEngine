package com.pengine.components.collisiondetection;

import com.pengine.Collision;
import com.pengine.components.colliders.Collider;

public interface CollisionDetector<T extends Collider, S extends Collider> {

    public Collision getCollision(T a, S b);

}
