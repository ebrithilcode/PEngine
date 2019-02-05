package com.pengine;

public class AABB {

    float x;
    float y;
    float dx;
    float dy;

    //boolean infinite;

    public AABB(float x, float y, float dx, float dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public boolean contains(AABB other) {
        //if (infinite) return true;
        return (x <= other.x && x+dx >= other.x+other.dx) && (y <= other.y && y+dy >= other.y+other.dy);
    }

}
