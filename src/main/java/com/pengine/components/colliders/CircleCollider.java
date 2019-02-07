package com.pengine.components.colliders;

import java.util.ArrayList;
import java.util.List;

import com.pengine.GameObject;
import com.pengine.components.Collider;

public class CircleCollider extends Collider {

    PVector off;
    public float radius;
    CollisionDetectionSystem;

    {
        off = new PVector(0,0);
        globalPoints = new PVector[] {new PVector(0,0)};
    }

    public CircleCollider(GameObject g) {
        super(g);
    }

    public List<PVector> collisionNormals(Collider other) {
        List<PVector> ret = new ArrayList<>();
        PVector norm1 = other.closestPoint(off.copy().add(parent.pos));
        ret.add(new PVector(-norm1.y, norm1.x));
        return ret;
    }

    public PVector closestPoint(PVector p) {
        PVector diff = off.copy().add(parent.pos).sub(p);
        diff.setMag(diff.mag()-radius);
        return diff.add(p);
    }

    @Override
    public boolean earlyUpdate() {
        super.earlyUpdate();
        globalPoints[0] = off.copy().add(parent.pos);
        return false;
    }

    @Override
    public void movement() {
        globalPoints[0] = off.cadd(parent.pos);
    }

    public void setRadius(float v) {
        radius = v;
        parent.maxRadius = v;
    }

    public void setOff(PVector v) {
        off = v;
    }
    public float getRadius() {
        return radius;
    }
    public PVector getOff() {
        return off;
    }
}
