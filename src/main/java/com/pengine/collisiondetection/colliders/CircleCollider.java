package com.pengine.collisiondetection.colliders;

import com.pengine.Entity;
import com.pengine.rendering.AbstractRenderer;
import com.pengine.rendering.CircleRenderer;
import processing.core.PVector;

public class CircleCollider extends AbstractCollider {

    private float radius;
    private PVector center;

    public CircleCollider(Entity parent, float radius, PVector center) {
        super(parent);
        this.radius = radius;
        this.center = center;
    }

    public CircleCollider(Entity parent, float radius) {
        this(parent, radius, new PVector(parent.getPosX(), parent.getPosY()));
    }

    public float getRadius() {
        return radius;
    }

    public PVector getCenter() {
        return center;
    }

    @Override
    public void onParentMove(PVector move) {
        center.add(move);
    }

    @Override
    public void onParentRotate(float rotation) {
        if(center.x == parent.getPosX() && center.y == parent.getPosY()) return;
        center.sub(parent.getPosition());
        center.rotate(rotation);
        center.add(parent.getPosition());
    }

    @Override
    public PVector closestPoint(PVector otherPoint) {
        PVector diff = PVector.sub(center, otherPoint);
        diff.setMag(diff.mag()-radius); //TODO: Any optimization for this? Seems to be optimizable
        return diff.add(otherPoint);
    }

    @Override
    public float getMaxCenterDist() {
        return center.dist(parent.getPosition()) + radius;
    }

    @Override
    public AbstractRenderer getDefaultRenderer() {
        return new CircleRenderer();
    }
}
