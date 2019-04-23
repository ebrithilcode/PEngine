package com.pengine.net.rendering;

import com.pengine.net.rendering.AbstractObjectRendererPackage;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class AnimationRendererPackageAbstract extends AbstractObjectRendererPackage {

    private short frameIndex;

    public AnimationRendererPackageAbstract(float x, float y, float rotation, short renderingObjectIndex, short frameIndex) {
        super(x, y, rotation, renderingObjectIndex);
        this.frameIndex = frameIndex;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeShort(frameIndex);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        frameIndex = objectInput.readShort();
    }

}
