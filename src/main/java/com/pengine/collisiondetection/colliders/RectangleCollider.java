package com.pengine.collisiondetection.colliders;

import com.pengine.Entity;
import processing.core.PVector;

public class RectangleCollider extends PolygonCollider {

    public RectangleCollider(Entity parent, PVector[] vertices) throws IllegalArgumentException{
        if(vertices.length != 4) {
            throw new IllegalArgumentException("Invalid argument for RectangleCollider, boundary vertices must be 4.");
        }
        super(parent, vertices);
    }

    @Override
    public String getIdentifier() {
        return "RECTANGLE";
    }

    @Override
    public PVector[] getPerpendicularSide() {

    }
}
