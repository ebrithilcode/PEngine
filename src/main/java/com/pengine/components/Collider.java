package com.pengine.components;

import com.pengine.GameObject;
import com.pengine.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class Collider extends Component {

    public boolean isTrigger = true;
    protected Vector[] globalPoints;
    Vector lastPos;

    List<Collider> isColliding;
    List<Collider> wasColliding;
    List<Collider> blackList;

    public Collider(GameObject g) {
        super(g);
        isColliding = new ArrayList<>();
        wasColliding = new ArrayList<>();
        blackList = new ArrayList<>();
    }

    public abstract List<Vector> collisionNormals(Collider other);

    public abstract Vector closestPoint(Vector p);

    public boolean update() {
        return false;
    }

    public boolean lateUpdate() {
        return false;
    }

    public boolean earlyUpdate() {
        moved = false;
        lastPos = parent.pos;
        return false;
    }

}
