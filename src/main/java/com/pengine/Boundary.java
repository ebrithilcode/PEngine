package com.pengine;

public class Boundary {

    Vector pos;
    Vector dim;
    boolean infinite;

    Boundary(Vector p, Vector d) {
        pos = p;
        dim = d;
    }

    boolean contains(Boundary other) {
        if (infinite) return true;
        boolean inX = pos.x <= other.pos.x && pos.x+dim.x >= other.pos.x+other.dim.x;
        boolean inY = pos.y <= other.pos.y && pos.y+dim.y >= other.pos.y+other.dim.y;
        return inX&&inY;
    }

}
