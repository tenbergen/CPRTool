package edu.oswego.cs.util;

public class DebugUtils {
    public static <T> void debug(T... elements) {
        if (elements == null || elements.length == 0) {
            System.out.println("No elements to debug.");
            return;
        }

        int index = 0;
        for (T element : elements) {
            System.out.println("Element " + (index++) + ":");
            System.out.println("  Class: " + (element != null ? element.getClass().getSimpleName() : "null"));
            System.out.println("  Value: " + element);
        }
    }
}
