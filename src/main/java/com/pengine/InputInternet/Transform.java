package com.pengine.InputInternet;

public class Transform {
    public PVector pos;
    public float rot;
    public int classID;
    public int objectID = -1;
    public Transform(){}
    public Transform(int i, PVector p) {
        classID = i;
        pos = p;
    }
    public Transform(int cid, int oid, PVector p, float r) {
        classID = cid;
        objectID = oid;
        pos = p;
        rot = r;
        byte[] additionalData;
    }
}