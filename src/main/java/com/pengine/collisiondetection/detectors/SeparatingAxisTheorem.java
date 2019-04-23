package com.pengine.collisiondetection.detectors;

import com.pengine.collisiondetection.Collision;
import com.pengine.collisiondetection.colliders.CircleCollider;
import com.pengine.collisiondetection.colliders.PolygonCollider;
import com.pengine.collisiondetection.colliders.RectangleCollider;
import processing.core.PVector;


public class SeparatingAxisTheorem {

    public class PolygonPolygon implements ICollisionDetector<PolygonCollider, PolygonCollider> {

        public Collision getCollision(PolygonCollider a, PolygonCollider b) {

            return null;

        }

    }

    public class PolygonRectangle implements ICollisionDetector<PolygonCollider, RectangleCollider> {

        public Collision getCollision(PolygonCollider a, RectangleCollider b) {

            return new Collision(a, b, new PVector(0, 0), new PVector(0, 0));

        }

    }

    public class PolygonTriangle implements ICollisionDetector<PolygonCollider, RectangleCollider> {

        public Collision getCollision(PolygonCollider a, RectangleCollider b) {

            return new Collision(a, b, new PVector(0, 0), new PVector(0, 0));

        }

    }

    public class PolygonCirlce implements ICollisionDetector<PolygonCollider, CircleCollider> {

        public Collision getCollision(PolygonCollider a, CircleCollider b) {

            return new Collision(a, b, new PVector(0, 0), new PVector(0, 0));

        }

    }

    public class RectangleRectangle implements ICollisionDetector<RectangleCollider, RectangleCollider> {

        public Collision getCollision(RectangleCollider a, RectangleCollider b) {

            return new Collision(a, b, new PVector(0, 0), new PVector(0, 0));

        }

    }

    public class CircleCirlce implements ICollisionDetector<CircleCollider, CircleCollider> {

        public Collision getCollision(CircleCollider a, CircleCollider b) {
                PVector gap = PVector.sub(a.getCenter(), b.getCenter());
                float intersectionSize = (a.getRadius() + b.getRadius()) - gap.mag();
                if(intersectionSize <= 0) return null;
                PVector wayOut = gap.setMag(intersectionSize);
                PVector collisionPoint = PVector.sub(b.getCenter(), gap.copy().setMag(b.getRadius()));
                return new Collision(a, b, wayOut, collisionPoint);
        }

    }


}
