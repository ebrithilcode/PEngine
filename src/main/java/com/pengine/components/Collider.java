package com.pengine.components;

import com.pengine.GameObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Collider extends Component {

    public boolean isTrigger = true;
    public PVector[] globalPoints;

    public List<Collider> isColliding;
    public List<Collider> wasColliding;
    public List<Collider> blackList;

    public Collider(GameObject g) {
        super(g);
        isColliding = new ArrayList<>();
        wasColliding = new ArrayList<>();
        blackList = new ArrayList<>();
    }

    public abstract List<PVector> collisionNormals(Collider other);

    public abstract PVector closestPoint(PVector p);

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
