package com.pengine.rendering;

import com.pengine.Entity;
import com.pengine.PEngine;

public class Primitive2DRenderer  extends  AbstractRenderer{

    protected int fillColor;
    protected int strokeColor;
    protected byte strokeWeight;

    public Primitive2DRenderer(Entity parent, byte priority, int fillColor, int strokeColor, byte strokeWeight) {
        super(parent, priority);
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWeight = strokeWeight;
    }

    @Override
    public void render() {
        PEngine.APPLET.strokeWeight(strokeWeight);
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public byte getStrokeWeight() {
        return strokeWeight;
    }

    public void setStrokeWeight(byte strokeWeight) {
        this.strokeWeight = strokeWeight;
    }

}
