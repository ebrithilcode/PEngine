package com.pengine;

public class AABB {

    protected float x0;
    protected float y0;
    protected float x1;
    protected float y1;

    //boolean infinite;

    public AABB(float x, float y, float dx, float dy) {
        this.x0 = x;
        this.y0 = y;
        this.x1 = x+dx;
        this.y1 = y+dy;
    }

    /**
     * Checks relationship between this AABB and another one.
     *
     * @param other another AABB which relation with this one should be checked
     * @return      a number corresponding to the relation between the the two AABBs
     *              0: No intersection nor containment
     *              1: The AABB passed as an argument is contained by the one the methods is invoked on.
     *              2: The AABB the method is invoked on is contained by the one passed as an argument
     *              3: The AABBs intersect
     */
    public int getRelation(AABB other) {
        //TODO improve this method
        if (other.x1 < this.x0 || this.x1 < other.x0 || this.y1 < other.y0 || other.y1 < this.y0) return 0;
        if((this.x0 <= other.x0 && this.x1 >= other.x1) && (this.y0 <= other.y0 && this.y1 >= other.y1)) return 1;
        if((other.x0 <= this.x0 && other.x1 >= this.x1) && (other.y0 <= this.y0 && other.y1 >= this.y1)) return 2;
        return 3;
    }

    public boolean contains(AABB other) {
        return ((this.x0 <= other.x0 && this.x1 >= other.x1) && (this.y0 <= other.y0 && this.y1 >= other.y1));
    }

    public float getX0() {
        return x0;
    }

    public float getY0() {
        return y0;
    }

    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getDX() {
        return x1-x0;
    }

    public float getDY() {
        return y1-y0;
    }
}
