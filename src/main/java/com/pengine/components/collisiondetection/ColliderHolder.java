package com.pengine.components.collisiondetection;

import com.pengine.components.colliders.Collider;

import java.util.Collection;


public abstract class ColliderHolder implements Collection<Collider> {

    @Override
    public boolean contains(Object o) {
        for(Collider c : getAllColliders()) {
            if (o == null ? e == null : o.equals(c)) return true;
        }
        return false;
    }

    @Override
    public int size() {
        return toArray().length;
    }


}
