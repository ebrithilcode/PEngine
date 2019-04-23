package com.pengine;

import com.pengine.collisiondetection.Collision;
import com.pengine.collisiondetection.colliders.AbstractCollider;
import com.pengine.collisiondetection.colliders.ICollider;
import com.pengine.rendering.IRenderer;
import processing.core.PConstants;
import processing.core.PVector;

public class Entity implements IUpdatable, IKillable {

    protected PEngine parent;

    protected IRenderer renderer;
    protected ICollider collider;

    protected PVector position;
    protected PVector velocity;
    protected float rotation;

    protected AABB bounds;

    protected float mass;
    protected float asperity; // = roughness of a surface

    protected boolean isDead;
    protected boolean isActive;

    protected boolean rotationLocked;
    protected boolean movementLocked;

    //public constructor for full control, factory methods to be used otherwise
    public Entity(PVector position, PVector velocity, float rotation, float mass, float asperity, boolean rotationLocked, boolean movementLocked) {
        this.position = position;
        this.velocity = velocity;
        this.rotation = rotation;
        this.mass = mass;
        this.asperity = asperity;
        this.rotationLocked = rotationLocked;
        this.movementLocked = movementLocked;
        isDead = false;
        isActive = true;
    }

    public static Entity createDefaultEntity(float x, float y, float rotation) {
        return new Entity(new PVector(x, y), new PVector(0, 0), rotation, 1, 1, false, false);
    }

    public static Entity createBasicEntity(float x, float y, float rotation, ICollider collider, IRenderer renderer) {
        Entity entity = createDefaultEntity(x, y, rotation);
        entity.collider = collider;
        entity.renderer = renderer;
        return entity;
    }

    public static Entity createBasicEntity(float x, float y, float rotation, AbstractCollider collider) {
        return createBasicEntity(x, y, rotation, collider, collider.getDefaultRenderer());
    }

    public static Entity createImmobileEntity(float x, float y, float rotation) {
        return new Entity(new PVector(x, y), new PVector(0, 0), rotation, 1, 1, true, true);
    }

    @Override
    public void earlyUpdate() {}

    @Override
    public void update() {}

    @Override
    public void lateUpdate() {}

    public boolean hasCollider() {
        return collider != null;
    }

    //It's a square atm, should probably work different
    //TODO: idk what to do about this, it kind of defeats the purpose of having AABBs... Use circles instead?
    /*public AABB generateBounds() {
        collider.getVertices();
        bounds = new AABB()
    }*/

    /*-------------------------------------- getters/setters + utility methods --------------------------------------*/

    //---- mass ----

    public float getMass() {
        return mass;
    }

    public boolean hasRenderer() {
        return renderer != null;
    }

    //---- bounds ----

    public void move(PVector vector) {
        position.add(vector);
    }

    public void rotate(float deltaRotation) {
        rotation = (rotation + deltaRotation) % PConstants.TAU;
    }

    //---- colliders ----

    //TODO: Collision handling
    public void onCollision(Collision collision) {}

    public void setMass(float mass) {
        if(mass < 0)
            this.mass = 0;
        else
            this.mass = mass;
    }

    /*public void deleteCollider() {
        setCollider(null);
    }*/

    //---- renderables ----

    public AABB getBounds() {
        return bounds;
    }

    public void setBounds(AABB bounds) {
        this.bounds = bounds;
    }

    public ICollider getCollider() {
        return collider;
    }

    //---- rotation and position ----

    public void setCollider(ICollider collider) {
        this.collider = collider;
    }

    //returns array instead of list so that it can be handled with more easily,
    //while also preventing changes to the list being made w/o setters.
    //efficiency should not matter here since the PEngine class accesses IRenderables of a client differently.
    public IRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(IRenderer renderer) {
        this.renderer = renderer;
    }

    public void deleteRenderer() {
        setRenderer(null);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getPosX() {
        return position.x;
    }

    public void setPosX(float x) {
        position.x = x;
    }

    public float getPosY() {
        return position.y;
    }

    public void setPosY(float y) {
        position.y = y;
    }

    /*-------------------------------------- IKillable methods --------------------------------------*/

    public PVector getPosition() {
        return position;
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    /*-------------------------------------- static factory methods --------------------------------------*/

    public PEngine getParent() {
        return parent;
    }

    public void setParent(PEngine parent) {
        this.parent = parent;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public boolean kill() {
        return isDead = true; //TODO: is this valid syntax? idk
    }
    
}
