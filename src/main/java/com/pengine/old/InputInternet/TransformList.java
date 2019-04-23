package com.pengine.old.InputInternet;


import java.util.ArrayList;

class TransformList extends Data {
    //Formatierung:
    //1.Byte Datentyp
    //2.Byte Tranform Anzahl
    //schleife:
    //  classID
    //  objectID
    //  4 bytes posx
    //  4 bytes posy
    //  4 bytes rotation

    ArrayList<Transform> positions = new ArrayList<>();

    TransformList() {}

    TransformList(byte[] recData) {
        recData = decodeBytes(recData);
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
            PVector vec = new PVector(byteToFloat(partValues), byteToFloat(partValues2));
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
        str += (char) positions.size();
        for (Transform p: positions) {
            str += (char) p.classID;
            str += (char) p.objectID;
            byte[] b1 = floatToByte(p.pos.x);
            byte[] b2 = floatToByte(p.pos.y);
            byte[] b3 = floatToByte(p.rot);
            for (int i=0;i<b1.length;i++) {
                str+= (char) b1[i];
            }
            for (int i=0;i<b2.length;i++) {
                str+= (char) b2[i];
            }
            for (int i=0;i<b3.length;i++) {
                str+= (char) b3[i];
            }
        }
        String newString = encodeString(str);

        newString += '\r';
        return newString;
    }
}