package com.pengine.components;

import com.pengine.GameObject;
import com.pengine.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class Collider extends Component {

    public boolean isTrigger = true;
    public Vector[] globalPoints;

    public List<Collider> isColliding;
    public List<Collider> wasColliding;
    public List<Collider> blackList;

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
        return false;
    }

}
