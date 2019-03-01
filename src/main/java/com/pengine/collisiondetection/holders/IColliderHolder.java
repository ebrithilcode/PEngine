package com.pengine.collisiondetection.holders;

import com.pengine.IUpdatable;
import com.pengine.collisiondetection.colliders.AbstractCollider;

import java.util.Iterator;

public interface IColliderHolder extends IUpdatable {

    //using remove() on iteratorOfCollidersFor should work in a way that getAllColliders() removes it too
    Iterator<AbstractCollider> iteratorOfCollidersFor(AbstractCollider collider);

    Iterator<AbstractCollider> getAllColliders(AbstractCollider collider);

    boolean add(AbstractCollider collider);

    boolean remove(AbstractCollider collider);

}
