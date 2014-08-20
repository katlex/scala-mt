package com.katlex.scala_mt;

public class Arrays {

    public static String mkString(Object [] array, String middle) {
        return mkString(array, null, middle, null);
    }

    public static String mkString(Object [] array, String first, String separator, String last) {

        StringBuilder sb = new StringBuilder();

        if (first != null) {
            sb.append(first);
        }

        for (Object obj : array) {
            sb.append(obj);
            sb.append(separator);
        }

        if (array.length > 0) {
            int len = separator.length();
            sb.delete(sb.length() - len, sb.length());
        }

        if (last != null) {
            sb.append(first);
        }

        return sb.toString();
    }
}
