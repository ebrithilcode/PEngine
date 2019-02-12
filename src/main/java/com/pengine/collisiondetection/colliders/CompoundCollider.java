package com.pengine.collisiondetection.colliders;

import com.pengine.Entity;
import processing.core.PVector;

import java.util.List;

public class CompoundCollider<T extends Collider> extends Collider {

    public CompoundCollider(Entity parent) {
        super(parent);
    }

    @Override
    public List<PVector> getCollisionNormals(Collider other) {
        return null;
    }

    @Override
    public PVector closestPoint(PVector p) {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "COMPOUND";
    }
}
