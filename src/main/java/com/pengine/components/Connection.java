package com.pengine.components;

import com.pengine.GameObject;
import com.pengine.Vector;

public class Connection extends Component {

    float strength;
    GameObject connected;
    boolean pull;

    int distLock;
    int minDistLock;

    public Connection(GameObject g) {
        super(g);
        pull = false;
        distLock = -1;
        minDistLock = -1;
    }

    public boolean earlyUpdate() {
        return false;
    }

    public boolean update() {
        if (pull) {
            connected.addVelocity(parent.pos.csub(connected.pos).mult(strength));
        }
        if (distLock>0) {
            float trueDist = connected.pos.dist(parent.pos);
            if (trueDist>distLock) {
                connected.shift(parent.pos.csub(connected.pos).setMag(trueDist-distLock));
            }
        }
        if (minDistLock>0) {
            float trueDist = connected.pos.dist(parent.pos);
            if (trueDist<minDistLock) {
                connected.shift(connected.pos.csub(parent.pos).setMag(minDistLock-trueDist));
            }
        }
        return false;
    }

    public boolean lateUpdate() {
        return false;
    }

    public void apply(Vector speed, float angVel) {
        if (strength>0) {
            connected.addVelocity(speed.cmult(strength));
            connected.addAngularVelocity(angVel * strength);
        }
    }

    public Vector getMassCenter() {
        if (!(strength>0)) return parent.pos;
        Vector res = parent.pos.cmult(parent.mass);
        res.add(connected.getMassCenter().cmult(connected.getMass()*strength));
        res.div(parent.mass+connected.getMass()*strength);
        return res;
    }

    public float getMass() {
        if (strength>0)
            return (parent.mass+connected.getMass()*strength)/(1+strength);
        else
            return parent.mass;
    }
}
