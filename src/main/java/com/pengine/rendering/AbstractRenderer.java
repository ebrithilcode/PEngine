package com.pengine.rendering;

import com.pengine.Entity;
import com.pengine.net.NetworkApplicable;
import com.pengine.net.Server;

public abstract class AbstractRenderer implements IRenderer, NetworkApplicable {

    private final short UUID;
    Entity parent;
    byte priority;
    boolean visible;
    boolean isDead;

    protected AbstractRenderer(Entity parent, byte priority) {
        UUID = Server.getNewID();
    }

    @Override
    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public byte getPriority() {
        return priority;
    }

    public byte setPriority() {
        return priority;
    }

    @Override
    public int compareTo(IRenderer renderable) {
        if(this.equals(renderable)) return 0;

        if(priority >= renderable.getPriority()) {
            return 1;
        }
        else {
            return -1;
        }
    }

    @Override
    public boolean isDead() {
        return dead || parent == null || parent.isDead(); //TODO: reevaluate
    }

    //this should likely only rarely be something that is invoked
    @Override
    public boolean kill() {
        dead = true;
        parent.removeRenderable(this);
        return true; //should always work
    }

}