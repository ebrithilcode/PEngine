package com.pengine.interaction;

import processing.core.PApplet;
import processing.event.KeyEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class UserKeyHandler {

    Map<Character, KeyMethodLink> keyToMethod = new HashMap<>();

    public UserKeyHandler(PApplet parent) {
        parent.registerMethod("keyEvent", this);
    }

    public void addKeyListener(char key, Object obj, String methodName, boolean press) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            keyToMethod.put(key, new KeyMethodLink(obj, m, press));
        }
        catch(NoSuchMethodException e) {
            System.err.println("Method that was tried to register \""+methodName+"\" does not exist...");
            System.err.println("Resumed without registering it.");
        }
    }

    public void keyEvent(KeyEvent event) {
            KeyMethodLink kml;
            if (event.getKey() == 0xffff) {
                kml = keyToMethod.get(event.getKeyCode());
            }
            else {
                kml = keyToMethod.get(event.getKey());
            }
            if(kml != null) {
                if((event.getAction() == KeyEvent.PRESS && kml.press) || (event.getAction() == KeyEvent.RELEASE && !kml.press)) {
                    try {
                        kml.met.invoke(kml.obj);
                    }
                    catch(IllegalAccessException e) {
                        System.err.println("Can't access method \""+kml.met.getName()+"\", try changing its access modifier.");
                    }
                    catch(InvocationTargetException e) {
                        System.err.println("Method \""+kml.met.getName()+" could not be called by object of class \""+kml.obj.getClass().getName()+"\".");
                        System.err.println("Did the object change after registering its method as a key listener?");
                    }
                }
            }
    }

    private class KeyMethodLink {

        private Method met;
        private Object obj;
        private boolean press;

        private KeyMethodLink(Object obj, Method met, boolean press) {
            this.obj = obj;
            this.met = met;
            this.press = press;
        }

    }

}
