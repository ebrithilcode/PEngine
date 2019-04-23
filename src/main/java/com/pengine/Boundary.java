package com.pengine;

public interface Boundary {

    boolean intersects(Boundary other);

    boolean contains(Boundary other);

    int getRelation(Boundary other);

}
