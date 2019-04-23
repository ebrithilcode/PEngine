package com.pengine.net;

final class NetConstants {

    static final int OPEN_CONNECTION = 0xBB40E64D;
    static final byte UPDATE = 1;
    static final byte CREATE = 2;
    static final byte DESTROY = 3;
    static final byte CLOSE_CONNECTION = 0;
    static final byte ERROR = -1;

    private NetConstants() {}

}
