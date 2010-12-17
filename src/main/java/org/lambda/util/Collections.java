package org.lambda.util;

import org.lambda.Lambda;

import java.util.Collection;
import java.util.List;

/**
 * @author Karl Bennett
 */
public class Collections {

    private Collections() {
    }

    public static <E, C extends Collection<E>> C map(C collection, Lambda<E, E> lambda) {
        if (collection == null) throw new NullPointerException("org.lambda.util.Collections.map - collection is null.");
        if (lambda == null) throw new NullPointerException("org.lambda.util.Collections.map - lambda is null.");

        C mappedCollection = null;

        try {
            mappedCollection = (C) collection.getClass().newInstance();
        } catch (InstantiationException e) {
            // TODO properly handle failed collection instantiation.
            throw new RuntimeException("org.lambda.util.Collections.map - collection type could not be instantiated. "
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            // TODO properly handle illegal access exception.
            throw new RuntimeException("org.lambda.util.Collections.map - collection type could not be accessed. "
                    + e.getMessage());
        }

        if (mappedCollection != null) {
            for (E element : collection) {
                mappedCollection.add(lambda.lambda(element));
            }
        }

        return mappedCollection;
    }

    public static <E, C extends Collection<E>> void mapC(C collection, Lambda<E, E> lambda) {
        if (collection == null) throw new NullPointerException("org.lambda.util.Collections.map - collection is null.");
        if (lambda == null) throw new NullPointerException("org.lambda.util.Collections.map - lambda is null.");

        for (E element : collection) {
            lambda.lambda(element);
        }
    }

    public static <E, L extends List<E>> L mapList(L list, Lambda<E, L> lambda) {
        if (list == null) throw new NullPointerException("org.lambda.util.Collections.map - list is null.");
        if (lambda == null) throw new NullPointerException("org.lambda.util.Collections.map - lambda is null.");

        L mappedList = null;

        try {
            mappedList = (L) list.getClass().newInstance();
        } catch (InstantiationException e) {
            // TODO properly handle failed list instantiation.
            throw new RuntimeException("org.lambda.util.Collections.map - list type could not be instantiated. "
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            // TODO properly handle illegal access exception.
            throw new RuntimeException("org.lambda.util.Collections.map - list type could not be accessed. "
                    + e.getMessage());
        }

        if (mappedList != null) {
            for (int i = 0; i < list.size(); i++) {
                mappedList.add(lambda.lambda((L) list.subList(i, list.size())));
            }
        }

        return mappedList;
    }
}
