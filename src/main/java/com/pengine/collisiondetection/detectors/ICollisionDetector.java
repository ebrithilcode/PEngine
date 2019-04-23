package com.pengine.collisiondetection.detectors;

import com.pengine.collisiondetection.Collision;
import com.pengine.collisiondetection.colliders.AbstractCollider;

public interface ICollisionDetector<T extends AbstractCollider, S extends AbstractCollider> {

    Collision getCollision(T a, S b);

}
