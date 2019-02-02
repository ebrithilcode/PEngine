package com.pengine.InputInternet;
import java.util.HashMap;
import static com.pengine.PEngine.APPLET;

public class Input extends Data {

  private HashMap<Integer, Boolean> setKeys = new HashMap<Integer, Boolean>();
  private HashMap<Integer, Boolean> mouseButtons = new HashMap<>();
  private int mouseWheel;
  private int mouseX;
  private int mouseY;

  Input() {

  }
  Input(byte[] data) {
    int iterator = 1;

    byte[] xBytes = new byte[4];
    for (int i=0;i<4;i++) {
      xBytes[i] = data[iterator];
      iterator++;
    }
    byte[] yBytes = new byte[4];
    for (int i=0;i<4;i++) {
      yBytes[i] = data[iterator];
      iterator++;
    }

    mouseX = bytesToInt(xBytes);
    mouseY = bytesToInt(yBytes);

    int num = data[iterator];
    iterator++;
    for (int i=0;i<num;i++) {
      int n = data[iterator];
      iterator++;
      boolean res = data[iterator] == 1;
      iterator++;
      setKeys.put(n, res);
    }

    num = data[iterator];
    iterator++;
    for (int i=0;i<num;i++) {
      int n = data[iterator];
      iterator++;
      boolean res = data[iterator] == 1;
      iterator++;
      mouseButtons.put(n, res);
    }

    mouseWheel = data[iterator];

  }
  void manageKey(int k, boolean down) {
      setKeys.put(k, down);
  }
  void manageMouseButton(int button, boolean down) {
    mouseButtons.put(button, down);
  }
  void manageMouseWheel(int ticks) {
    mouseWheel = ticks;
  }
  boolean isPressed(int k) {
    Boolean b = setKeys.get(k);
    //println("Is: "+(char) k+" pressed? "+b);
    return b!=null ? b: false;
  }

  void update(int mX, int mY) {
    mouseX = mX;
    mouseY = mY;
  }


  @Override
  public String toString() {
    String r = bytesToString(intToBytes(mouseX));
    r += bytesToString(intToBytes(mouseY));
    r += hmToString(setKeys);
    r += hmToString(mouseButtons);
    r += (char) (byte) mouseWheel;
    r += '\r';
    return r;
  }
  private String hmToString(HashMap<Integer, Boolean> hm) {
    StringBuilder sb = new StringBuilder();
    sb.append((char)hm.size());
    for (int k: hm.keySet()) {
      sb.append((char)  k);
      sb.append((char) (hm.get(k) ? 1:0));
    }
    return sb.toString();
  }

  byte[] intToBytes(int i) {
    return java.nio.ByteBuffer.allocate(4).putInt(i).array();
  }
  int bytesToInt(byte[] b) {
    return java.nio.ByteBuffer.wrap(b).getInt();
  }
  String bytesToString(byte[] b) {
    String r = "";
    for (byte c: b) {
      r += (char) c;
    }
    return r;
  }
}
