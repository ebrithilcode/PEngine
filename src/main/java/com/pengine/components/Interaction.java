/*package com.pengine.components;

import processing.core.PApplet;
import processing.event.KeyEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.pengine.PEngine.APPLET;

public class Interaction extends Component {

    Map<Character, KeyBinding> keyBindings;

    public Interaction(GameObject g) {
        super(g);
        keyBindings = new Map<>();
        //als library
        if (APPLET == null) {
          System.out.err("Interaction Component must be instantiated after the main engine, this instance will be useless.");
        }
        else {
          APPLET.registerMethod("keyEvent", this);
        }
    }

    public void addKeyListener(char key, String methodName, boolean press) {
        try {
            parent.getClass().getMethod(methodName);
            keyToMethod.put(key, new KeyMethodLink(methodName, press));
        }
        catch(NoSuchMethodException e) {
            System.err.println("Method that was tried to register \""+methodName+"\" does not exist...");
            System.err.println("Resumed without registering it.");
        }
    }

    public void keyEvent(KeyEvent event) {
            KeyBinding keyBinding;
            if (event.getKey() == 0xffff) {
                keyBinding = keyBindings.get(event.getKeyCode());
            }
            else {
                keyBinding = keyBindings.get(event.getKey());
            }
            if(keyBinding != null) {
                if((event.getAction() == KeyEvent.PRESS && keyBinding.onPress) || (event.getAction() == KeyEvent.RELEASE && !keyBinding.onPress)) {
                    try {
                        parent.getClass().getMethod(keyBinding.methodName).invoke(parent);
                    }
                    catch(IllegalAccessException e) {
                        System.err.println("Can't access method \""+keyBinding.methodName+"\", try changing its access modifier.");
                    }
                    catch(InvocationTargetException e) {
                        System.err.println("Method \""+keyBinding.methodName+" could not be called by object of class \""+parent.getClass().getName()+"\".");
                        System.err.println("Did the object change after registering its method as a key listener?");
                    }
                }
            }
    }

    private class KeyBinding {

        private String methodName;
        private boolean onPress;

        private KeyMethodLink(String methodName, boolean onPress) {
            this.methodName = methodName;
            this.onPress = onPress;
        }

    }

}






}
*/