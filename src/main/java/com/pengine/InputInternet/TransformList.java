package com.pengine.InputInternet;


import com.pengine.InputInternet.Data;
import com.pengine.Vector;
import com.pengine.InputInternet.Transform;
import java.util.ArrayList;
import java.util.List;

public class TransformList extends Data {
    //Formatierung:
    //1.Byte Datentyp
    //2.Byte Tranform Anzahl
    //schleife:
    //  classID
    //  objectID
    //  4 bytes posx
    //  4 bytes posy
    //  4 bytes rotation

    List<Transform> positions = new ArrayList<Transform>();
    TransformList() {}
    TransformList(byte[] recData) {
        int num = recData[1];
        int iterator = 2;
        for (int i=0;i<num;i++) {
            int classID = recData[iterator];
            iterator++;
            int objectID = recData[iterator];
            iterator++;
            byte[] partValues = new byte[4];
            for (int o=0;o<4;o++) {
                partValues[o] = recData[iterator];
                iterator++;
            }
            byte[] partValues2 = new byte[4];
            for (int o=0;o<4;o++) {
                partValues2[o] = recData[iterator];
                iterator++;
            }
            Vector vec = new Vector(byteToFloat(partValues), byteToFloat(partValues2));
            byte[] partValues3 = new byte[4];
            for (int o=0;o<4;o++) {
                partValues3[o] = recData[iterator];
                iterator++;
            }
            float rot = byteToFloat(partValues3);
            positions.add(new Transform(classID, objectID, vec, rot));
        }
    }
    @Override
    public String toString() {
        String str = "";
        str += (char) nr((byte)positions.size());
        for (Transform p: positions) {
            str += (char) nr((byte)p.classID);
            str += (char) nr((byte)p.objectID);
            byte[] b1 = floatToByte(p.pos.x);
            byte[] b2 = floatToByte(p.pos.y);
            byte[] b3 = floatToByte(p.rot);
            for (int i=0;i<b1.length;i++) {
                str+= (char) nr(b1[i]);
            }
            for (int i=0;i<b2.length;i++) {
                str+= (char) nr(b2[i]);
            }
            for (int i=0;i<b3.length;i++) {
                str+= (char) nr(b3[i]);
            }
        }
        str += '\r';
        return str;
    }
}