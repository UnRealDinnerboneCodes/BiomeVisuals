package com.owen1212055.biomevisuals.nms;

import org.jetbrains.annotations.ApiStatus;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@ApiStatus.Internal
public class UnsafeUtils {

    private static Unsafe UNSAFE;

    static {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            UNSAFE = (Unsafe) unsafeField.get(null);
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
        }
    }

    public static void unsafeStaticSet(Field field, Object value) {
        UNSAFE.putObject(UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field), value);
    }

}
