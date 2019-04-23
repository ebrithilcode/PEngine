package com.pengine.collisiondetection;

import com.pengine.collisiondetection.colliders.AbstractCollider;
import com.pengine.collisiondetection.detectors.ICollisionDetector;
import com.pengine.collisiondetection.holders.IColliderHolder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CollisionDetectionSystem {

    private Map<Class<? extends AbstractCollider>, Map<Class<? extends AbstractCollider>, ICollisionDetector<? extends AbstractCollider, ? extends AbstractCollider>>> detectors;
    private IColliderHolder colliderHolder;

    private CollisionDetectionSystem(IColliderHolder colliderHolder, Map<Class<? extends AbstractCollider>, Map<Class<? extends AbstractCollider>, ICollisionDetector<? extends AbstractCollider, ? extends AbstractCollider>>> detectors) {
        this.colliderHolder = colliderHolder;
        this.detectors = detectors;
    }

    public boolean addCollider(AbstractCollider collider) {
        return colliderHolder.add(collider);
    }

    public boolean removeCollider(AbstractCollider collider) {
        return colliderHolder.remove(collider);
    }

    @SuppressWarnings("unchecked")
    public void registerCollisionDetector(ICollisionDetector detector) {
        Type[] genericTypes = ((ParameterizedType) detector.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
        
        //check for overwrite of already existing ICollisionDetector in map
        if(detectors.get(genericTypes[0]) != null) {
            if(detectors.get(genericTypes[0]).get(genericTypes[1]) != null) {
                System.err.format("ICollisionDetector for AbstractCollider types %s and %s was overwritten. Ignore if this is on purpose.", ((Class<?>) genericTypes[0]).getSimpleName(), ((Class<?>) genericTypes[1]).getSimpleName());
            }
        }
            
        detectors.put((Class<? extends AbstractCollider>) genericTypes[0], new HashMap<Class<? extends AbstractCollider>, ICollisionDetector<? extends AbstractCollider, ? extends AbstractCollider>>());
        detectors.get(genericTypes[0]).put((Class<? extends AbstractCollider>) genericTypes[1], detector);
    }

    public void addCollisionDetectorsFromInnerClasses(Class<?> outerClass) {
        Class<?>[] innerClasses = outerClass.getDeclaredClasses();
        for(Class <?> c : innerClasses) {
            if(!ICollisionDetector.class.isAssignableFrom(c)) {
                System.err.format("Subclass %s in class %s does not implement the ICollisionDetector interface: Skipped.", c.getSimpleName(), outerClass.getSimpleName());
                continue;
            }
            try {
                registerCollisionDetector((ICollisionDetector) c.newInstance());
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

    public void manageCollisions() {
        Set<AbstractCollider> checkedColliders = new HashSet<>();
        for(Iterator<AbstractCollider> colliderIterator = colliderHolder.getAllColliders(); colliderIterator.hasNext();) {
            AbstractCollider current = colliderIterator.next();
            if(current.isDead()) {
                colliderIterator.remove();
                continue;
            }
            checkedColliders.add(current);
            for(Iterator<AbstractCollider> possibleIterator = colliderHolder.iteratorOfCollidersFor(current); possibleIterator.hasNext();) {
                AbstractCollider possibleCollider = possibleIterator.next();
                if(possibleCollider.isDead()) {
                    possibleIterator.remove();
                }
                else if(!checkedColliders.contains(possibleCollider)) {
                    Collision collision = getCollision(current, possibleCollider);
                    if(collision != null) collision.notifyColliderOwners();
                }

            }
        }
    }

    //eventhough this seems like complete spaghetti, in theory this should not be that inefficient,
    //since in most cases most of the if/else stuff wont even be checked. Might still be optimizable, though.
    @SuppressWarnings({"unchecked", "Duplicates"})
    private Collision getCollision(AbstractCollider a, AbstractCollider b) {
        Map<Class<? extends AbstractCollider>, ICollisionDetector<?, ?>> detectorMap = detectors.get(a.getClass());
        if(detectorMap == null) {
            detectorMap = detectors.get(b.getClass());
            if(detectorMap == null) {
                Class<?> superClass = a.getClass().getSuperclass();
                if(AbstractCollider.class.isAssignableFrom(superClass)) detectorMap = detectors.get(superClass);
                if(detectorMap == null) {
                    superClass = b.getClass().getSuperclass();
                    if(AbstractCollider.class.isAssignableFrom(superClass)) detectorMap = detectors.get(superClass);
                    if(detectorMap == null) {
                        System.err.println("No entries for the AbstractCollider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                        return null;
                    }
                    System.err.println("Collision Manager had to fall back to superclass of AbstractCollider: "+b.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
                    ICollisionDetector detector = detectorMap.get(a.getClass());
                    if(detector == null) {
                        superClass = a.getClass().getSuperclass();
                        if(AbstractCollider.class.isAssignableFrom(superClass)) detector = detectorMap.get(superClass);
                        if(detector == null) {
                            System.err.println("No entries for the AbstractCollider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                            return null;
                        }
                    }
                    return detector.getCollision(b, a);
                }
                System.err.println("Collision Manager had to fall back to superclass of AbstractCollider: "+a.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
                ICollisionDetector detector = detectorMap.get(b.getClass());
                if(detector == null) {
                    superClass = b.getClass().getSuperclass();
                    if(AbstractCollider.class.isAssignableFrom(superClass)) detector = detectorMap.get(superClass);
                    if(detector == null) {
                        System.err.println("No entries for the AbstractCollider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                        return null;
                    }
                    System.err.println("Collision Manager had to fall back to superclass of AbstractCollider: "+b.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
                }
                return detector.getCollision(a, b);
            }
            ICollisionDetector detector = detectorMap.get(a.getClass());
            if(detector == null) {
                Class<?> superClass = a.getClass().getSuperclass();
                if(AbstractCollider.class.isAssignableFrom(superClass)) detector = detectorMap.get(superClass);
                if(detector == null) {
                    System.err.println("No entries for the AbstractCollider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                    return null;
                }
                System.err.println("Collision Manager had to fall back to superclass of AbstractCollider: "+a.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
            }
            return detector.getCollision(b, a);
        }
        ICollisionDetector detector = detectorMap.get(b.getClass());
        if(detector == null) {
            Class<?> superClass = b.getClass().getSuperclass();
            if(AbstractCollider.class.isAssignableFrom(superClass)) detector = detectorMap.get(superClass);
            if(detector == null) {
                System.err.println("No entries for the AbstractCollider types: " + a.getClass().getSimpleName() + " and " + b.getClass().getSimpleName() + " were found nor for their superclasses. Dont't use Colliders of a type that you don't specify a CollisionChecker for.");
                return null;
            }
            System.err.println("Collision Manager had to fall back to superclass of AbstractCollider: "+b.getClass().getSimpleName()+". Try not to use Colliders of a type that you dont specify a CollisionChecker for.");
        }
        return detector.getCollision(a, b);
    }

}
