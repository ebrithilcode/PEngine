package com.pengine.components.colliders;

import java.util.ArrayList;
import java.util.List;
import com.pengine.Vector;
import com.pengine.GameObject;
import com.pengine.components.Collider;

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
