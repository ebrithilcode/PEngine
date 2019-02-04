package com.pengine.components.colliders;

import com.pengine.GameObject;
import com.pengine.Vector;
import com.pengine.components.Collider;

import java.util.ArrayList;
import java.util.List;

import static com.pengine.PEngine.APPLET;

public class PolygonCollider extends Collider {

    public Vector[] localPoints;

    public PolygonCollider(GameObject g) {
        super(g);
    }

    public List<Vector> collisionNormals(Collider other) {
        List<Vector> allCollisionNormals = new ArrayList<>();
        for (int i=0; i<globalPoints.length; i++) {
            Vector dist = globalPoints[i].csub(globalPoints[(i+1)%globalPoints.length]);
            allCollisionNormals.add(new Vector(-dist.y, dist.x));
        }
        return allCollisionNormals;
    }

    public Vector closestPoint(Vector p) {
        Vector cl = globalPoints[0];
        float d = cl.dist(p);

        for (int i=1;i<globalPoints.length;i++) {
            float c = globalPoints[i].dist(p);
            if (c<d) {
                d = c;
                cl = globalPoints[i];
            }
        }
        return cl.copy();
    }

    public void setPoints() {
        globalPoints = new Vector[localPoints.length];
        for (int i = 0;i<localPoints.length; i++) {
            Vector help = localPoints[i].copy().add(parent.pos);
            help = rotateVector(help, parent.pos, parent.rot);
            globalPoints[i] = help;
        }
    }

    public void setLocalPoints(Vector[] p) {
        for (Vector pv : p) {
            parent.maxRadius = APPLET.max(pv.mag(), parent.maxRadius);
        }
        localPoints = p;
    }

    public void shiftPoints(Vector p) {
        for (Vector po : localPoints) {
            po.add(p);
        }
    }

    @Override
    public boolean earlyUpdate() {
        super.earlyUpdate();
        setPoints();
        return false;
    }

    @Override
    public  void movement() {
        setPoints();
    }

}
