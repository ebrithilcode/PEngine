package com.pengine.interact;

import processing.core.PApplet;
import processing.event.KeyEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyEventRegistry {

    private Map<Character, List<KeyBinding>> keyBindings;

    public KeyEventRegistry(PApplet applet) {
        keyBindings = new HashMap<>();
        applet.registerMethod("keyEvent", this);
        autoRegisterListeners(applet);
    }

    private void autoRegisterListeners(PApplet applet) {
        for(Field field : applet.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(KeyEventSubscriber.class)) {
                if(field.getType().isPrimitive()) {
                    System.err.format("%s was not registered as a KeyEventSubscriber, only non-primitive types are allowed.", field.getName());
                    continue;
                }
                try {
                    Object subscriberInstance = field.get(applet);
                    for(SubscribeKey keySubscription : field.getAnnotation(KeyEventSubscriber.class).keySubscriptions()) {
                        try {
                            Method method = subscriberInstance.getClass().getMethod(keySubscription.methodName());
                            addKeyListener(keySubscription.key(), keySubscription.press(), method, subscriberInstance);
                        }
                        catch(NoSuchMethodException e) {
                            System.err.format("Method %s in class %s does not exist. It's registration was skipped", keySubscription.methodName(), subscriberInstance.getClass().getSimpleName());
                        }
                    }
                }
                catch(IllegalAccessException e) {
                    System.err.format("Could not access field %s. Try changing it's modifier to public.", field.getName());
                }
            }
        }
    }

    public void addKeyListener(char key, boolean press, Method method, Object instance) {
        if(!keyBindings.containsKey(key)) keyBindings.put(key, new ArrayList<KeyBinding>());
        keyBindings.get(key).add(new KeyBinding(press, method, instance));
    }

    public void keyEvent(KeyEvent event) {
            List<KeyBinding> keyBindingsToCall;
            if (event.getKey() == 0xffff) {
                keyBindingsToCall = keyBindings.get(event.getKeyCode());
            }
            else {
                keyBindingsToCall = keyBindings.get(event.getKey());
            }
            if(keyBindingsToCall != null) {
                for(KeyBinding keyBinding : keyBindingsToCall) {
                    if ((event.getAction() == KeyEvent.PRESS && keyBinding.press) || (event.getAction() == KeyEvent.RELEASE && !keyBinding.press)) {
                        try {
                            keyBinding.method.invoke(keyBinding.instance);
                        }
                        catch (IllegalAccessException e) {
                            System.err.format("Can't access method %s, try changing its access modifier.", keyBinding.method.getName());
                        }
                        catch (InvocationTargetException e) {
                            System.err.format("Method %s could not be called by instance of class %s:", keyBinding.method.getName(), keyBinding.instance.getClass().getSimpleName());
                            e.printStackTrace();
                        }
                    }
                }
            }
    }

    private class KeyBinding {

        private Method method;
        private boolean press;
        private Object instance;

        private KeyBinding(boolean press, Method method, Object instance) {
            this.press = press;
            this.method = method;
            this.instance = instance;
        }

    }

}