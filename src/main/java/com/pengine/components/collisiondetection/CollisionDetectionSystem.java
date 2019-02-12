package com.pengine.components.collisiondetection;

import com.pengine.Collision;
import com.pengine.components.colliders.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionDetectionSystem {

    Map<Class<? extends Collider>, Map<Class<? extends Collider>, CollisionDetector>> detectors;
    ColliderHolder holder;

    private CollisionDetectionSystem(ColliderHolder holder, Map<Class<? extends Collider>, Map<Class<? extends Collider>, CollisionDetector>> detectors) {
        this.holder = holder;
        this.detectors = detectors;
    }

    public void addCollisionDetector(CollisionDetector detector) {
        Type[] genericTypes = ((ParameterizedType) detector.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
        detectors.put((Class) genericTypes[0], new HashMap<Class<? extends Collider>, CollisionDetector>());
        detectors.get(genericTypes[0]).put((Class) genericTypes[1], detector);
    }

    private Collision getCollision(Collider a, Collider b) {
        CollisionDetector detector = detectors.get(a.getClass()).get(b.getClass());
        if(detector != null) return detector.getCollision(a, b);
        detector = detectors.get(b.getClass()).get(a.getClass());
        if(detector != null) return detector.getCollision(b, a);
        return null;
    }

    public static class Builder {

        private static CollisionDetector[] tempDetectors;

        static {
            tempDetectors = new CollisionDetector[15];
        }
    }

    public static final int POLY_POLY = 0;
    public static final int POLY_TRI = 1;
    public static final int POLY_RECT = 2;
    public static final int POLY_CIRCLE = 3;
    public static final int POLY_COMPOUND = 4;

    public static final int TRI_TRI = 5;
    public static final int TRI_RECT = 6;
    public static final int TRI_CIRCLE = 7;
    public static final int TRI_COMPOUND = 8;

    public static final int RECT_RECT = 9;
    public static final int RECT_CIRCLE = 10;
    public static final int RECT_COMPOUND = 11;

    public static final int CIRCLE_CIRCLE = 12;
    public static final int CIRCLE_COMPOUND = 13;

    public static final int COMPOUND_COMPOUND = 14;

}
