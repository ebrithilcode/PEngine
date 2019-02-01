package com.pengine;

public interface Updatable {

    boolean earlyUpdate();

    boolean update();

    boolean lateUpdate();

}
