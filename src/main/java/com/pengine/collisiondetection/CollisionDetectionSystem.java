package com.pengine.collisiondetection;

import com.pengine.collisiondetection.colliders.*;
import com.pengine.collisiondetection.collisiondetectors.CollisionDetector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionDetectionSystem {

    Map<Class<? extends Collider>, Map<Class<? extends Collider>, CollisionDetector<? extends Collider, ? extends Collider>>> detectors;
    Iterable<List<? extends Collider>> holder;

    private CollisionDetectionSystem(Iterable<List<? extends Collider>> holder, Map<Class<? extends Collider>, Map<Class<? extends Collider>, CollisionDetector<? extends Collider, ? extends Collider>>> detectors) {
        this.holder = holder;
        this.detectors = detectors;
    }

    @SuppressWarnings("unchecked")
    public void addCollisionDetector(CollisionDetector detector) {
        Type[] genericTypes = ((ParameterizedType) detector.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
        
        //check for overwrite
        if(detectors.get(genericTypes[0]) != null) {
            if(detectors.get(genericTypes[0]).get(genericTypes[1]) != null) {
                System.err.format("CollisionDetector for Collider types %s and %s was overwritten. Ignore if this is on purpose.", ((Class<?>) genericTypes[0]).getSimpleName(), ((Class<?>) genericTypes[1]).getSimpleName());
            }
        }
            
        detectors.put((Class<? extends Collider>) genericTypes[0], new HashMap<Class<? extends Collider>, CollisionDetector<? extends Collider, ? extends Collider>>());
        detectors.get(genericTypes[0]).put((Class<? extends Collider>) genericTypes[1], detector);
    }

    public void manageCollisions() {
        for(List<? extends Collider> colliderList : holder) {
            for(int i=0; i<colliderList.size(); i++) {
                for(int k=i+1; k<colliderList.size(); k++) {
                    Collision collision = getCollision(colliderList.get(i), colliderList.get(k));
                    if(collision != null) {
                        colliderList.get(i).onCollide(collision);
                        colliderList.get(k).onCollide(collision.reverse());
                    }
                }
            }
        }
    }
    
    @SuppressWarnings({"unchecked", "Duplicates"})
    private Collision getCollision(Collider a, Collider b) {
        Map<Class<? extends Collider>, CollisionDetector<?, ?>> detectorMap = detectors.get(a.getClass());
        if(detectorMap == null) {
            detectorMap = detectors.get(b.getClass());
            if(detectorMap == null) {
                Class<?> superClass = a.getClass().getSuperclass();
                if(Collider.class.isAssignableFrom(superClass)) detectorMap = detectors.get(superClass);
                if(detectorMap == null) {
                    superClass = b.getClass().getSuperclass();
                    if(Collider.class.isAssignableFrom(superClass)) detectorMap = detectors.get(superClass);
                    if(detectorMap == null) {
                        System.err.println("No entries for the Collider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                        return null;
                    }
                    System.err.println("Collision Manager had to fall back to superclass of Collider: "+b.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
                    CollisionDetector detector = detectorMap.get(a.getClass());
                    if(detector == null) {
                        superClass = a.getClass().getSuperclass();
                        if(Collider.class.isAssignableFrom(superClass)) detector = detectorMap.get(superClass);
                        if(detector == null) {
                            System.err.println("No entries for the Collider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                            return null;
                        }
                    }
                    // ?
                    return detector.getCollision(b, a).reverse();
                }
                System.err.println("Collision Manager had to fall back to superclass of Collider: "+a.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
                CollisionDetector detector = detectorMap.get(b.getClass());
                if(detector == null) {
                    superClass = b.getClass().getSuperclass();
                    if(Collider.class.isAssignableFrom(superClass)) detector = detectorMap.get(superClass);
                    if(detector == null) {
                        System.err.println("No entries for the Collider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                        return null;
                    }
                    System.err.println("Collision Manager had to fall back to superclass of Collider: "+b.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
                }
                return detector.getCollision(a, b);
            }
            CollisionDetector detector = detectorMap.get(a.getClass());
            if(detector == null) {
                Class<?> superClass = a.getClass().getSuperclass();
                if(Collider.class.isAssignableFrom(superClass)) detector = detectorMap.get(superClass);
                if(detector == null) {
                    System.err.println("No entries for the Collider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                    return null;
                }
                System.err.println("Collision Manager had to fall back to superclass of Collider: "+a.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
            }
            // ?
            return detector.getCollision(b, a).reverse();
        }
        CollisionDetector detector = detectorMap.get(b.getClass());
        if(detector == null) {
            Class<?> superClass = b.getClass().getSuperclass();
            if(Collider.class.isAssignableFrom(superClass)) detector = detectorMap.get(superClass);
            if(detector == null) {
                System.err.println("No entries for the Collider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                return null;
            }
            System.err.println("Collision Manager had to fall back to superclass of Collider: "+b.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
        }
        return detector.getCollision(a, b);
    }

    public static class Builder {

        private static Map<Class<? extends Collider>, Map<Class<? extends Collider>, CollisionDetector<?, ?>>> buildDetectors;
        private static Iterable<List<? extends Collider>> buildColliderHolder;

        static {
            buildDetectors = new HashMap<>();
            buildColliderHolder = null;
        }

        public static void setColliderHolder(Iterable<List<? extends Collider>> colliderHolder) {
            buildColliderHolder = colliderHolder;
        }

        public static CollisionDetectionSystem build() {
            if(buildColliderHolder == null) {
                System.err.println("No ColliderHolder was set for CollisionDetectionSystem.Builder. Returned null.");
                return null;
            }
            return new CollisionDetectionSystem(buildColliderHolder, buildDetectors);
        }

        public static void addCollisionDetectorsFromInnerClasses(Class<?> outerClass) {
            Class<?>[] innerClasses = outerClass.getDeclaredClasses();
            for(Class <?> c : innerClasses) {
                if(!CollisionDetector.class.isAssignableFrom(c)) {
                    System.err.format("Subclass %s in class %s does not implement the CollisionDetector interface: Skipped.", c.getSimpleName(), outerClass.getSimpleName());
                    continue;
                }
                try {
                    addSingleCollisionDetector((CollisionDetector) c.newInstance());
                }
                catch(IllegalAccessException e) {
                    System.err.format("Access modifier of the standard constructor in every subclass has to be public: Could not register subclass %s in class %s.", c.getSimpleName(), outerClass.getSimpleName());
                }
                catch(InstantiationException e) {
                    System.err.format("Instantiation of subclass %s in class %s failed. This might be due to a missing standard constructor in this subclass or an exception thrown by it.", c.getSimpleName(), outerClass.getSimpleName());
                    e.printStackTrace();
                }
            }
        }

        @SuppressWarnings({"unchecked", "Duplicates"})
        public static void addSingleCollisionDetector(CollisionDetector detector) {
            Type[] genericTypes = ((ParameterizedType) detector.getClass().getGenericInterfaces()[0]).getActualTypeArguments();

            //check for overwrite
            if(buildDetectors.get(genericTypes[0]) != null) {
                if(buildDetectors.get(genericTypes[0]).get(genericTypes[1]) != null) {
                    System.out.format("CollisionDetector for Collider types %s and %s was overwritten. Ignore if this is on purpose.", ((Class<?>) genericTypes[0]).getSimpleName(), ((Class<?>) genericTypes[1]).getSimpleName());
                }
            }
            buildDetectors.put((Class<? extends Collider>) genericTypes[0], new HashMap<Class<? extends Collider>, CollisionDetector<?, ?>>());
            buildDetectors.get(genericTypes[0]).put((Class<? extends Collider>) genericTypes[1], detector);
        }
    }

}
