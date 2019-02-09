package com.pengine.components.collisiondetection;

import com.pengine.Collision;
import com.pengine.components.Collider;

import java.util.List;

public abstract class CollisionDetectionSystem {

    //EnumMap<DetectorType, CollisionDetector> detectors = new EnumMap<>(DetectorType.class);
    CollisionDetector[] detectors;

    public CollisionDetectionSystem() {
        detectors = new CollisionDetector[15];
    }

    public void handleCollisions() {
    }

    public Collision getCollision(Collider a, Collider b) {
        return null;
    }

    public abstract void manageCollisions(List<? extends Collider> colliders);

    public void addCollisionDetector(int detectorType, CollisionDetector detector) {
        if(detectors[i] != null) {
            System.err.println("Collision detetctor was overwritten. Ignore if this is on purpose.");
        }
        detectors[detectorType] = detector;
    }

    public final class DetectorTypes {

        final int POLY_POLY = 0;
        final int POLY_TRI = 1;
        final int POLY_RECT = 2;
        final int POLY_CIRCLE = 3;
        final int POLY_COMPOUND = 4;

        final int TRI_TRI = 5;
        final int TRI_RECT = 6;
        final int TRI_CIRCLE = 7;
        final int TRI_COMPOUND = 8;

        final int RECT_RECT = 9;
        final int RECT_CIRCLE = 10;
        final int RECT_COMPOUND = 11;

        final int CIRLCE_CIRCLE = 12;
        final int CIRCLE_COMPOUND = 13;

        final int COMPOUND_COMPOUND = 14;

    }

}
