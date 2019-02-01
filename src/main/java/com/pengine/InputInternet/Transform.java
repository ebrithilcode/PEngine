package com.pengine.InputInternet;

import com.pengine.Vector;

public class Transform {
    Vector pos;
    float rot;
    int classID;
    int objectID = -1;
    Transform(){}
    Transform(int i, Vector p) {
        classID = i;
        pos = p;
    }
    Transform(int cid,int oid, Vector p, float r) {
        classID = cid;
        objectID = oid;
        pos = p;
        rot = r;
    }
}