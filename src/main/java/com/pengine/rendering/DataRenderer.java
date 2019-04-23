package com.pengine.rendering;

import com.pengine.Entity;

public abstract class DataRenderer extends AbstractRenderer {

    int finalDataIndex;

    public DataRenderer(Entity parent, byte priority, int finalDataIndex) {
        super(parent, priority);
        this.finalDataIndex = finalDataIndex;
    }

}
