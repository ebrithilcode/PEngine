package com.pengine.collisiondetection.holders;

import com.pengine.collisiondetection.colliders.AbstractCollider;
import com.pengine.collisiondetection.colliders.ICollider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//brute force approach
public class SingleSet implements IColliderHolder {

    private Set<AbstractCollider> colliders;

    public SingleSet() {
        colliders = new HashSet<>();
    }

    @Override
    public void earlyUpdate() {}

    @Override
    public void update() {}

    @Override
    public void lateUpdate() {}

    @Override
    public boolean add(AbstractCollider collider) {
        return colliders.add(collider);
    }

    @Override
    public boolean remove(AbstractCollider collider) {
        return colliders.remove(collider);
    }

    @Override
    public Iterator<ICollider> iteratorOfCollidersFor(AbstractCollider collider) {
        return colliders.iterator();
    }

    @Override
    public Iterator<ICollider> getAllColliders() {
        return colliders.iterator();
    }

}
