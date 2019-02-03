package com.pengine.InputInternet;

import java.util.ArrayList;
import java.util.List;
import com.pengine.Vector;

public class Data {

  public static int classID;
  public int objectID;

  Data() {}
  Data(byte[] recData) {

  }

  public static Data createData(byte[] bytes, int... index) {
    //Skip class and object ID
    index[0] += 2;
    return new Data();
  }

  public void updateData(byte[] bytes, int... index) {
    //Skip class and object id
    index[0] += 2;
  }

  @Override
  public String toString() {
    return ((char) classID) + ((char) objectID);
  }

  float byteToFloat(byte[] b) {
    return java.nio.ByteBuffer.wrap(b).getFloat();
  }
  byte[] floatToByte(float f) {
    return java.nio.ByteBuffer.allocate(4).putFloat(f).array();
  }
  byte[] intToBytes(int i) {
    return java.nio.ByteBuffer.allocate(4).putInt(i).array();
  }
  int bytesToInt(byte[] b) {
    return java.nio.ByteBuffer.wrap(b).getInt();
  }

  static String encodeString(String str) {
    String ret = "";
    for (int i=0;i<str.length();i++) {
      char c = str.charAt(i);
      switch (c) {
        case '\r':
          ret += "ab";
          break;
        case 'a':
          ret += "aa";
          break;
        default:
          ret += c;
          break;
      }
    }
    return ret;
  }
  static String decodeString(String str) {
    String ret = "";
    for (int i=0;i<str.length();i++) {
      char c = str.charAt(i);
      if (c=='a') {
        i++;
        char b = str.charAt(i);
        if (b=='a') ret += 'a';
        else if (b=='b') ret += '\r';
      } else ret+=c;
    }

    return ret;
  }
  static byte[] decodeBytes(byte[] byt) {
    ArrayList<Byte> ret = new ArrayList<Byte>();
    for (int i=0;i<byt.length;i++) {
      byte b = byt[i];
      if (b==97) {
        i++;
        byte c = byt[i];
        if (c==97) ret.add((byte) 97);
        else if (c=='b') ret.add((byte)13);
      } else ret.add(b);
    }
    return toArray(ret);
  }

  byte[] toArray(ArrayList<Byte> list) {
    byte[] ret = new byte[list.size()];
    for (int i=0;i<list.size();i++) {
      ret[i] = list.get(i);
    }
    return ret;
  }

  String concateByteArray(String s, byte[] b) {
    for (byte by: b) {
      s += (char) by;
    }
    return s;
  }
  byte[] subarray(byte[] b, int... index, int leng) {
    byte[] ret = new byte[leng];
    for (int i=0;i<ret.length;i++) {
      ret[i] = b[index[0]++];
    }
    return ret;
  }

}
