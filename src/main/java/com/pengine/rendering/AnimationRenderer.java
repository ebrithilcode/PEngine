package com.pengine.rendering;

import com.pengine.Entity;
import com.pengine.net.rendering.AbstractRendererPackage;
import com.pengine.net.rendering.AnimationRendererPackageAbstract;
import com.pengine.net.server.ClientInstance;
import processing.core.PImage;

import static com.pengine.PEngine.APPLET;

public class AnimationRenderer extends AbstractRenderer {

    private PImage[] frames;
    private int frameRate;
    private short index;
    private short finalDataIndex;

    public AnimationRenderer(Entity parent, byte priority, PImage[] frames, int frameRate) {
        super(parent, priority);
        this.frames = frames;
        this.frameRate = frameRate;
    }

    public AnimationRenderer(Entity parent, byte priority, int frameRate, String path, String fileExtension, int width, int height, int numFrames) {
        super(parent, priority);
        frames = new PImage[numFrames];
        for (int i=1; i<=numFrames; i++) {
            frames[i] = APPLET.loadImage(path + i, fileExtension);
            frames[i].resize(width, height);
        }
    }

    public void render() {
        PImage img = getFrameAndUpdate();
        APPLET.pushMatrix();
        APPLET.translate(parent.getPosX(), parent.getPosY());
        APPLET.rotate(parent.getRotation());
        APPLET.image(img,0,0);
        APPLET.popMatrix();
    }

    private PImage getFrameAndUpdate() {
        if (APPLET.frameCount % frameRate == 0) {
            index++;
            index %= frames.length;
        }
        return frames[index];
    }

    @Override
    public AbstractRendererPackage getRenderTemplate(ClientInstance client) {
        return new AnimationRendererPackageAbstract(priority, parent.getPosX(), parent.getPosY(), parent.getRotation(), finalDataIndex, index);
    }

    public boolean hasFinalData() {
        return true;
    }

    public Object getFinalData(short index) {
        finalDataIndex = index;
        return frames;
    }

}
