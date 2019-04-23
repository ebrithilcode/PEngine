package com.pengine.collisiondetection.colliders;

import com.pengine.Entity;
import processing.core.PVector;

public class RectangleCollider extends PolygonCollider {

    public RectangleCollider(Entity parent, PVector[] vertices) throws IllegalArgumentException {
        super(parent, vertices);
        if(this.vertices.length != 4) {
            throw new IllegalArgumentException("Invalid argument for RectangleCollider, boundary vertices must be 4.");
        }
    }

    //slightly optimized since less rotate() is used, which uses expensive trigonometry functions
    @Override
    public void onParentRotate(float rotation) {
        vertices[0].sub(parent.getPosition());
        vertices[0].rotate(rotation);
        vertices[2] = new PVector(-vertices[0].x, -vertices[0].y);
        vertices[0].add(parent.getPosition());
        vertices[2].add(parent.getPosition());

        vertices[1].sub(parent.getPosition());
        vertices[1].rotate(rotation);
        vertices[3] = new PVector(-vertices[1].x, -vertices[1].y);
        vertices[1].add(parent.getPosition());
        vertices[3].add(parent.getPosition());
    }

    @Override
    public PVector[] getNormalVectorsOfSides() {
        PVector[] normals = new PVector[2];
        normals[0] = new PVector(vertices[1].y - vertices[0].y, vertices[0].x - vertices[1].x);
        normals[1] = new PVector(vertices[0].y - vertices[1].y, vertices[1].x - vertices[0].x);
        return normals;
    }

}
