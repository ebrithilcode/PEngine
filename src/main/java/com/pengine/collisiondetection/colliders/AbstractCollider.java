package com.pengine.collisiondetection.colliders;

import com.pengine.Entity;
import com.pengine.rendering.AbstractRenderer;

public abstract class AbstractCollider implements ICollider {

    protected Entity parent;

    public AbstractCollider(Entity parent) {
        this.parent = parent;
    }

    public abstract AbstractRenderer getDefaultRenderer(); //utility method

    @Override
    public Entity getParent() {
        return parent;
    }

}
