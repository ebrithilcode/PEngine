package com.pengine.components.colliders;

import java.util.ArrayList;
import java.util.List;

import com.pengine.GameObject;
import com.pengine.components.Collider;
import processing.core.PVector;

public class RectangleCollider extends PolygonCollider {
    public RectangleCollider(GameObject g) {
        super(g);
    }

    @Override
    public List<PVector> getCollisionNormals(Collider other) {
        List<PVector> ret = new ArrayList<PVector>();
        for (int i=0;i<2;i++) {
            PVector dist = globalPoints[i].csub(globalPoints[(i+1)]);
            ret.add(new PVector(-dist.y, dist.x));
        }
        return ret;
    }
}