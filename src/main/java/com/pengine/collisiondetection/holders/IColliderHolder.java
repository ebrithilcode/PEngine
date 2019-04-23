package com.pengine.collisiondetection.holders;

import com.pengine.IUpdatable;
import com.pengine.collisiondetection.colliders.ICollider;

import java.util.Iterator;

public interface IColliderHolder extends IUpdatable {

    //using remove() on iteratorOfCollidersFor should work in a way that getAllColliders() removes it too
    Iterator<ICollider> iteratorOfCollidersFor(ICollider collider);

    Iterator<ICollider> getAllColliders();

    boolean add(ICollider collider);

    boolean remove(ICollider collider);

}
