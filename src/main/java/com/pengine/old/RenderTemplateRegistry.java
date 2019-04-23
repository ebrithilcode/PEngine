package com.pengine.net.renderTemplates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pengine.PEngine.APPLET;

public final class RenderTemplateRegistry {

    private static final Map<Class<? extends  AbstractRenderTemplate>, Byte> classToIndex = new HashMap<>(4);
    private static final Map<Byte, Class<? extends AbstractRenderTemplate>> indexToClass = new HashMap<>(4);
    public static byte uniqueIDCounter;

    static {
        classToIndex.put(RectangleRenderTemplate.class, (byte) 0);
        classToIndex.put(EllipseRenderTemplate.class, (byte) 1);
        classToIndex.put(SpriteRenderTemplate.class, (byte) 2);
        classToIndex.put(AnimationRenderTemplate.class, (byte) 3);
        indexToClass.put((byte) 0, RectangleRenderTemplate.class);
        indexToClass.put((byte) 1, EllipseRenderTemplate.class);
        indexToClass.put((byte) 2, SpriteRenderTemplate.class);
        indexToClass.put((byte) 3, AnimationRenderTemplate.class);
        uniqueIDCounter = 0;
    }

    private RenderTemplateRegistry() {
    }

    @SuppressWarnings("unchecked")
    public static void autoRegisterRenderTemplates() {
        Class<?>[] classes = APPLET.getClass().getDeclaredClasses();
        List<Class<? extends AbstractRenderTemplate>> renderTemplates = new ArrayList<>();
        for(Class<?> clazz : classes) {
            if(AbstractRenderTemplate.class.isAssignableFrom(clazz)) registerRenderTemplate((Class<? extends AbstractRenderTemplate>) clazz);
        }
    }

    public static void registerRenderTemplate(Class<? extends AbstractRenderTemplate> clazz) {
        classToIndex.put(clazz, (byte) classToIndex.size());
    }

    public static <T extends AbstractRenderTemplate> Class<T> getClass(byte index){
        return (Class<T>) indexToClass.get(index);
    }

    public static byte getIndex(Class<? extends AbstractRenderTemplate> clazz) {
        return classToIndex.get(clazz);
    }

}
