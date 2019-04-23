package com.pengine.collisiondetection.colliders;

import com.pengine.Entity;
import processing.core.PVector;

public class PolygonCollider extends AbstractCollider {

    //in actual coordinates not relative
    protected PVector[] vertices;

    public PolygonCollider(Entity parent, PVector[] vertices) {
        super(parent);
        this.vertices = vertices;
    }

    @Override
    public void onParentMove(PVector move) {
        for(PVector vertex : vertices) {
            vertex.add(move);
        }
    }

    @Override
    public void onParentRotate(float rotation) {
        for(PVector vertex : vertices) {
            vertex.sub(parent.getPosition());
            vertex.rotate(rotation);
            vertex.add(parent.getPosition());
        }
    }

    public PVector[] getNormalVectorsOfSides() {
        PVector[] normals = new PVector[vertices.length];
        int lastIndex = normals.length-1;
        for (int i=0; i<lastIndex; i++) {
            //calculate normal vector of side ([i][i+1])
            normals[i] = new PVector(vertices[i+1].y - vertices[i].y, vertices[i].x - vertices[i+1].x);
        }
        normals[lastIndex] = new PVector(vertices[0].y - vertices[lastIndex].y, vertices[0].x - vertices[lastIndex].x);
        return normals;
    }

    //does NOT copy (if it really needs to will add later)
    @Override
    public PVector closestPoint(PVector otherPoint) {
        PVector closestVertex = vertices[0];
        float smallestDistance = closestVertex.dist(otherPoint);
        for (int i=1; i < vertices.length; i++) {
            float distance = vertices[i].dist(otherPoint);
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestVertex = vertices[i];
            }
        }
        return closestVertex;
    }

    @Override
    public float getMaxCenterDist() {
        float greatestDistance = vertices[0].dist(parent.getPosition());
        for (int i=1; i < vertices.length; i++) {
            float distance = vertices[i].dist(parent.getPosition());
            if (distance > greatestDistance) {
                greatestDistance = distance;
            }
        }
        return greatestDistance;
    }
}
