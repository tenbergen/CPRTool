package edu.oswego.cs.util;

public class DebugUtils {
    public static <T> void debug(T... elements) {
        if (elements == null || elements.length == 0) {
            System.out.println("No elements to debug.");
            return;
        }

        int index = 0;
        StringBuilder sb = new StringBuilder();
        for (T element : elements) {
            sb.append("Element ")
                    .append(index++)
                    .append(" {Class: ")
                    .append(element != null ? element.getClass().getSimpleName() : "null")
                    .append(", Value: ")
                    .append(element)
                    .append("}; ");
        }
        System.out.println(sb.toString());
    }

    public static void SOUT(String s) {
        System.out.println(s);
    }
}
