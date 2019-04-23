package com.pengine.net;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ReflectiveSerializer {

    //only supports non-inherited classes
    public static void serializeReflectively(ObjectOutput objectOutput, Object object, String... ignoredFields) throws IOException {
        List<Field> fieldList = Arrays.asList(object.getClass().getDeclaredFields()); //getDeclaredFields() returns only a copy not the actual class fields I believe
        List<String> ignoreList = ignoredFields.length > 0 ? Arrays.asList(ignoredFields) : Collections.<String>emptyList();
        for(Iterator<Field> fieldIterator = fieldList.iterator(); fieldIterator.hasNext();) { //remove all static and ignored fields
            Field currentField = fieldIterator.next();
            int modifiers = currentField.getModifiers();
            if(Modifier.isStatic(modifiers) || ignoreList.contains(currentField.getName())) fieldIterator.remove();
        }
        objectOutput.writeInt(fieldList.size()); //write number of fields
        for(Field field : fieldList) {
            field.setAccessible(true); //make every field accessible
            objectOutput.writeObject(field.getName()); //write name first for later deserialization purposes
            try {
                Class<?> fieldClass = field.getType();
                if (fieldClass.isPrimitive()) { //if primitive simply write the value
                    if (fieldClass.equals(Integer.TYPE)) {
                        objectOutput.writeInt(field.getInt(object));
                    }
                    if (fieldClass.equals(Boolean.TYPE)) {
                        objectOutput.writeBoolean(field.getBoolean(object));
                    }
                    if (fieldClass.equals(Float.TYPE)) {
                        objectOutput.writeInt(field.getInt(object));
                    }
                }
                else { //if not primitive see how to write field in another field
                    if(fieldClass.isArray()) {
                        if(isArrayClassSerializable(fieldClass)) {
                            objectOutput.writeObject(field.get(object));
                        }
                        else { //only supports one-dimensional arrays for now, therefore exception TODO (?)
                            if(fieldClass.getComponentType().isArray()) throw new IllegalArgumentException("Array fields that are not serializable, inside of a non-serializable class are only supported with a single dimension yet.");
                            Object array = field.get(object);
                            int arrayLength = Array.getLength(array);
                            objectOutput.writeInt(arrayLength);
                            for(int j=0; j<arrayLength; j++) {
                                serializeReflectively(objectOutput, Array.get(array, j));
                            }
                        }
                    }
                    else if(Serializable.class.isAssignableFrom(fieldClass)) {
                        objectOutput.writeObject(field.get(object));
                    }
                    else {
                        serializeReflectively(objectOutput, field.get(object));
                    }
                }
            }
            catch(IllegalAccessException ignore) {}
        }

    }

    public static <T> T deserializeReflectively(ObjectInput objectInput, Class<T> type) throws IOException{
        T object = null;
        try {
            Constructor<T> objectConstructor = type.getDeclaredConstructor();
            objectConstructor.setAccessible(true);
            object = objectConstructor.newInstance();
        }
        catch (NoSuchMethodException e) {
            //TODO: no public constructor available for type passed as argument -> exit sketch
        }
        catch (InstantiationException e) {
            //TODO: argument type represents an abstract class -> exit sketch
        }
        catch (InvocationTargetException e) {
            //TODO: constructor threw an error -> exit sketch
        }
        catch (IllegalAccessException ignore) {} //can't happen

        int numFields = objectInput.readInt();

        for(int i=0; i<numFields; i++) {
            try {
                Field field = type.getDeclaredField((String) objectInput.readObject());
                field.setAccessible(true);
                Class<?> fieldClass = field.getType();
                if (fieldClass.isPrimitive()) {
                    if (fieldClass.equals(Integer.TYPE)) {
                        field.setInt(object, objectInput.readInt());
                    }
                    if (fieldClass.equals(Boolean.TYPE)) {
                        field.setBoolean(object, objectInput.readBoolean());
                    }
                    if (fieldClass.equals(Float.TYPE)) {
                        field.setFloat(object, objectInput.readFloat());
                    }
                }
                else {
                    if(fieldClass.isArray()) {
                        if(isArrayClassSerializable(fieldClass)) {
                            field.set(object, objectInput.readObject()); //if array is serializable just read it like default
                        }
                        else { //only supports one-dimensional arrays for now, therefore exception TODO (?)
                            if(fieldClass.getComponentType().isArray()) throw new IllegalArgumentException("Array fields that are not serializable, inside of a non-serializable class are only supported with a single dimension yet.");
                            Object array = Array.newInstance(fieldClass, objectInput.readInt());
                            for(int j=0; j<Array.getLength(array); j++) {
                                Array.set(array, j, deserializeReflectively(objectInput, fieldClass.getComponentType()));
                            }
                            field.set(object, array);
                        }
                    }
                    else if(Serializable.class.isAssignableFrom(fieldClass)) {
                        field.set(object, objectInput.readObject());
                    }
                    else {
                        field.set(object, deserializeReflectively(objectInput, fieldClass));
                    }
                }
            }
            catch(NoSuchFieldException e) {
                System.err.println("A field in a Processing class that exists on server side, does not exist on client side, are you using different versions?");
            }
            catch(IllegalAccessException|ClassNotFoundException ignore) {} //TODO write exceptions
        }
        return object;
    }

    public static boolean isArrayClassSerializable(Class<?> arrayClass) {
        Class<?> arrayComponentType = arrayClass.getComponentType(); //get component type of array
        while(arrayComponentType.isArray()) {
            arrayComponentType = arrayComponentType.getComponentType(); //if array is multidimensional get component type of highest dimension
        }
        return arrayComponentType.isPrimitive() || Serializable.class.isAssignableFrom(arrayComponentType);
    }

}
