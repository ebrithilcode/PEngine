package com.pengine.components.collisiondetection;

import com.pengine.Collision;
import com.pengine.components.colliders.CircleCollider;
import com.pengine.components.colliders.PolygonCollider;
import com.pengine.components.colliders.RectangleCollider;
import processing.core.PVector;


public class SeperatedAxisTheorem {

    public class PolygonPolygon implements CollisionDetector<PolygonCollider, PolygonCollider> {

        public Collision getCollision(PolygonCollider a, PolygonCollider b) {

            return new Collision(a, b, new PVector(0,0), new PVector(0, 0));

        }

    }

    public class PolygonRectangle implements CollisionDetector<PolygonCollider, RectangleCollider> {

        public Collision getCollision(PolygonCollider a, RectangleCollider b) {

            return new Collision(a, b, new PVector(0,0), new PVector(0, 0));

        }

    }

    public class PolygonTriangle implements CollisionDetector<PolygonCollider, RectangleCollider> {

        public Collision getCollision(PolygonCollider a, RectangleCollider b) {

            return new Collision(a, b, new PVector(0,0), new PVector(0, 0));

        }

    }

    public class PolygonCirlce implements CollisionDetector<PolygonCollider, CircleCollider> {

        public Collision getCollision(PolygonCollider a, CircleCollider b) {

            return new Collision(a, b, new PVector(0,0), new PVector(0, 0));

        }

    }

    public class RectangleRectangle implements CollisionDetector<RectangleCollider, RectangleCollider> {

        public Collision getCollision(RectangleCollider a, RectangleCollider b) {

            return new Collision(a, b, new PVector(0,0), new PVector(0, 0));

        }

    }

    public class CircleCirlce implements  CollisionDetector<CircleCollider, CircleCollider> {

        public Collision getCollision(CircleCollider a, CircleCollider b) {

            return new Collision(a, b, new PVector(0,0), new PVector(0, 0));
            
        }

    }






}
