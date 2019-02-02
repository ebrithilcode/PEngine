package com.pengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CircleCollider extends Collider {

    Vector off;
    float radius;

    {
        off = new Vector(0,0);
        globalPoints = new Vector[] {new Vector(0,0)};
    }

    public CircleCollider(GameObject g) {
        super(g);
    }

    public List<Vector> collisionNormals(Collider other) {
        List<Vector> ret = new ArrayList<>();
        Vector norm1 = other.closestPoint(off.copy().add(parent.pos));
        ret.add(new Vector(-norm1.y, norm1.x));
        return ret;
    }

    public Vector closestPoint(Vector p) {
        Vector diff = off.copy().add(parent.pos).sub(p);
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
}
