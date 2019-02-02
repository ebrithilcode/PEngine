package com.pengine.InputInternet;

import com.pengine.Vector;

public class Transform {
    public Vector pos;
    public float rot;
    public int classID;
    public int objectID = -1;
    public Transform(){}
    public Transform(int i, Vector p) {
        classID = i;
        pos = p;
    }
    public Transform(int cid,int oid, Vector p, float r) {
        classID = cid;
        objectID = oid;
        pos = p;
        rot = r;
    }
}