package com.pengine.interact;

import processing.core.PApplet;
import processing.event.KeyEvent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
                            registerKeyListener(keySubscription.key(), keySubscription.press(), method, subscriberInstance);
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

    public void unregisterKeyListener(Object instance, String methodName) {
        Iterator<List<KeyBinding>> mapIterator = keyBindings.values().iterator();
        while(mapIterator.hasNext()) {
            List<KeyBinding> currentList = mapIterator.next();
            Iterator<KeyBinding> listIterator = currentList.iterator();
            while(listIterator.hasNext()) {
                KeyBinding currentBinding = listIterator.next();
                if(currentBinding.instance == instance && (currentBinding.method.getName().equals(methodName) || methodName == null)) {
                    listIterator.remove();
                    if(currentList.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }
        }
    }

    public void unregisterKeyListener(Object instance) {
        unregisterKeyListener(instance, null);
    }

    public void unregisterByKey(char key) {
        keyBindings.remove(key);
    }

    public void registerKeyListener(char key, boolean press, Method method, Object instance) {
        if(!keyBindings.containsKey(key)) keyBindings.put(key, new ArrayList<KeyBinding>());
        keyBindings.get(key).add(new KeyBinding(press, method, instance));
    }

    public void keyEvent(KeyEvent event) {
        char key;

        if (event.getKey() == 0xffff) {
            key = (char) event.getKeyCode();
        }
        else {
            key = event.getKey();
        }

        if(!keyBindings.containsKey(key)) return;

        Iterator<KeyBinding> bindingsIterator = keyBindings.get(key).iterator();
        while(bindingsIterator.hasNext()) {
            KeyBinding currentBinding = bindingsIterator.next();
            if ((event.getAction() == KeyEvent.PRESS && currentBinding.press) || (event.getAction() == KeyEvent.RELEASE && !currentBinding.press)) {
                if(currentBinding.getInstance() == null) {
                    System.err.format("KeyEvent subscriber for key %s that called method %s was removed. You must keep an instance of the subscriber.", key, currentBinding.method.getName());
                    bindingsIterator.remove();
                    continue;
                }
                try {
                    currentBinding.method.invoke(currentBinding.getInstance());
                }
                catch (IllegalAccessException e) {
                    System.err.format("Can't access method %s, try changing its access modifier.", currentBinding.method.getName());
                }
                catch (InvocationTargetException e) {
                    System.err.format("Method %s could not be called by instance of class %s:", currentBinding.method.getName(), currentBinding.instance.getClass().getSimpleName());
                    e.printStackTrace();
                }
            }
        }
        if(keyBindings.get(key).isEmpty()) {
            keyBindings.remove(key);
        }
    }

    private class KeyBinding {

        private Method method;
        private boolean press;
        private WeakReference<Object> instance;

        private KeyBinding(boolean press, Method method, Object instance) {
            this.press = press;
            this.method = method;
            this.instance = new WeakReference<>(instance);
        }

        private Object getInstance() {
            return instance.get();
        }

    }

}