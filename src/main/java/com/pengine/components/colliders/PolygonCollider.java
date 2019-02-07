package com.pengine.components.colliders;

import com.pengine.GameObject;
import com.pengine.components.Collider;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static com.pengine.PEngine.APPLET;

public class PolygonCollider extends Collider {

    public PVector[] localPoints;

    public PolygonCollider(GameObject g) {
        super(g);
    }

    public List<PVector> getCollisionNormals(Collider other) {
        List<PVector> allCollisionNormals = new ArrayList<>();
        for (int i=0; i<globalPoints.length; i++) {
            PVector dist = globalPoints[i].csub(globalPoints[(i+1)%globalPoints.length]);
            allCollisionNormals.add(new PVector(-dist.y, dist.x));
        }
        return allCollisionNormals;
    }

    public PVector closestPoint(PVector p) {
        PVector cl = globalPoints[0];
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
        globalPoints = new PVector[localPoints.length];
        for (int i = 0;i<localPoints.length; i++) {
            PVector help = localPoints[i].copy().add(parent.pos);
            help = rotateVector(help, parent.pos, parent.rot);
            globalPoints[i] = help;
        }
    }

    public void setLocalPoints(PVector[] p) {
        for (PVector pv : p) {
            parent.maxRadius = APPLET.max(pv.mag(), parent.maxRadius);
        }
        localPoints = p;
    }

    public void shiftPoints(PVector p) {
        for (PVector po : localPoints) {
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
