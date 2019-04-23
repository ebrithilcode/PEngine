package com.pengine.old.InputInternet;
import java.util.HashMap;

public class Input extends Data {

    private HashMap<Character, Boolean> setKeys = new HashMap<Character, Boolean>();
    private HashMap<Character, Boolean> mouseButtons = new HashMap<>();
    private int mouseWheel;
    private int mouseX;
    private int mouseY;

    public Input() {

    }

    public Input(byte[] data) {
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
            char n = (char) data[iterator];
            iterator++;
            boolean res = data[iterator] == 1;
            iterator++;
            setKeys.put(n, res);
        }

        num = data[iterator];
        iterator++;
        for (int i=0;i<num;i++) {
            char n = (char) data[iterator];
            iterator++;
            boolean res = data[iterator] == 1;
            iterator++;
            mouseButtons.put(n, res);
        }

        mouseWheel = data[iterator];

    }

    public Input setIP(String ip) {
        this.ip = ip;
        return this;
    }

    public void manageKey(int k, boolean down) {
        if (down && !isPressed(k))
            setKeys.put( (char)k, down);
    }

    public void manageMouseButton(int button, boolean down) {
        mouseButtons.put( (char) button, down);
    }

    public void manageMouseWheel(int ticks) {
        mouseWheel = ticks;
    }

    public boolean isPressed(int k) {
        Boolean b = setKeys.get(k);
        //println("Is: "+(char) k+" pressed? "+b);
        return b!=null ? b: false;
    }

    public void update(int mX, int mY) {
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

    private String hmToString(HashMap<Character, Boolean> hm) {
        StringBuilder sb = new StringBuilder();
        sb.append((char)hm.size());
        for (char k: hm.keySet()) {
            sb.append((char)  k);
            sb.append((char) (hm.get(k) ? 1:0));
        }
        return sb.toString();
    }

    String bytesToString(byte[] b) {
        String r = "";
        for (byte c: b) {
            r += (char) c;
        }
        return r;
    }
}
