package com.pengine;

import java.util.ArrayList;
import java.util.List;

public class RectCollider extends PolygonCollider {
    public RectCollider(GameObject g) {
        super(g);
    }

    @Override
    public List<Vector> collisionNormals(Collider other) {
        List<Vector> ret = new ArrayList<Vector>();
        for (int i=0;i<2;i++) {
            Vector dist = globalPoints[i].csub(globalPoints[(i+1)]);
            ret.add(new Vector(-dist.y, dist.x));
        }
        return ret;
    }
}
