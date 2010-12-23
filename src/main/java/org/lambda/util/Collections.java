package org.lambda.util;

import org.lambda.Lambda;

import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServicesSupport;
import java.beans.beancontext.BeanContextSupport;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Karl Bennett
 */
public class Collections {

    private Collections() {
    }

    public static <R, E, RC extends Collection<R>, C extends Collection<E>> RC map(C collection, Lambda<R, E> lambda) {
        // TODO properly handle unwanted null arguments..
        if (collection == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - collection cannot be null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - lambda cannot be null.");

        RC mappedCollection = instantiateCollection((Class<RC>) collection.getClass());

        if (mappedCollection != null) {
            runOverCollection(collection, lambda, mappedCollection);
        }

        return mappedCollection;
    }

    public static <R, E, RC extends Collection<R>, C extends Collection<E>> RC map(Class<RC> returnType, C collection,
                                                                                   Lambda<R, E> lambda) {
        // TODO properly handle unwanted null arguments..
        if (collection == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - collection cannot be null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - lambda cannot be null.");

        RC mappedCollection = instantiateCollection(returnType);

        if (mappedCollection != null) {
            runOverCollection(collection, lambda, mappedCollection);
        }

        return mappedCollection;
    }

    public static <R, E, RC extends Collection<R>, C extends Collection<E>> RC map(RC returnCollection, C collection,
                                                                              Lambda<R, E> lambda) {
        // TODO properly handle unwanted null arguments..
        if (returnCollection == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - return collection is null.");
        if (collection == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - collection is null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - lambda is null.");

        runOverCollection(collection, lambda, returnCollection);

        return returnCollection;
    }

    public static <E, C extends Collection<E>> void mapC(C collection, Lambda<E, E> lambda) {
        if (collection == null) throw new NullPointerException("org.lambda.util.Collections.map - collection is null.");
        if (lambda == null) throw new NullPointerException("org.lambda.util.Collections.map - lambda is null.");

        runOverCollection(collection, lambda);
    }

    public static <R, E, RC extends Collection<R>, C extends Collection<Collection<E>>> RC mapCan(Class<RC> returnType,
                                                                                                  C collection,
                                                                                                  Lambda<R, E> lambda) {
        // TODO properly handle unwanted null arguments..
        if (returnType == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - returnType cannot be null.");
        if (collection == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - collection cannot be null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.Collections.map - lambda cannot be null.");

        RC mappedCollection = instantiateCollection(returnType);

        if (mappedCollection != null) {
            runOverDeepCollection(collection, lambda, mappedCollection);
        }

        return mappedCollection;
    }

    public static <R, E, RL extends List<R>, L extends List<E>> RL mapList(L list, Lambda<R, L> lambda) {
        if (list == null) throw new NullPointerException("org.lambda.util.Collections.map - list is null.");
        if (lambda == null) throw new NullPointerException("org.lambda.util.Collections.map - lambda is null.");

        RL mappedList = instantiateCollection((Class<RL>) list.getClass());

        if (mappedList != null) {
            runOverList(list, lambda, mappedList);
        }

        return mappedList;
    }


    private static <E, RC extends Collection<E>> RC instantiateCollection(Class<RC> returnType) {
        RC newCollection = null;

        try {
            // Boilerplate code to handle all the default Java collection interfaces. It would be nice if Java had a
            // default implementation of collection that all the other class used so it could be instantiated and then
            // cast into the required subclass. Unfortunately AbstractCollection isn't quite enough for what I need.
            if (returnType.isInterface()) {

                if (BeanContext.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new BeanContextSupport();
                } else if (BeanContextServices.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new BeanContextServicesSupport();
                } else if (BlockingDeque.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new LinkedBlockingDeque<E>();
                } else if (BlockingQueue.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new LinkedBlockingQueue<E>();
                } else if (Deque.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new ArrayDeque<E>();
                } else if (List.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new ArrayList<E>();
                } else if (NavigableSet.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new TreeSet<E>();
                } else if (Queue.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new PriorityQueue<E>();
                } else if (Set.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new HashSet<E>();
                } else if (SortedSet.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new TreeSet<E>();
                } else if (Collection.class.isAssignableFrom(returnType)) {
                    newCollection = (RC) new Vector();
                }

                if (newCollection == null) {
                    throw new InstantiationException(
                            "org.lambda.util.Collections.map - does not recognise the collection interface "
                                    + returnType.getName());
                }
            } else {
                newCollection = returnType.newInstance();
            }
        } catch (InstantiationException e) {
            // TODO properly handle failed collection instantiation.
            throw new RuntimeException("org.lambda.util.Collections.map - collection type could not be instantiated. "
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            // TODO properly handle illegal access exception.
            throw new RuntimeException("org.lambda.util.Collections.map - collection type could not be accessed. "
                    + e.getMessage());
        }

        return newCollection;
    }

    private static <R, E> void runOverCollection(Collection<E> collection, Lambda<R, E> lambda) {
        for (E element : collection) {
            lambda.lambda(element);
        }
    }

    private static <R, E> void runOverCollection(Collection<E> collection, Lambda<R, E> lambda,
                                                 Collection<R> mappedCollection) {
        for (E element : collection) {
            mappedCollection.add(lambda.lambda(element));
        }
    }

    private static <R, E> void runOverDeepCollection(Collection<Collection<E>> collection, Lambda<R, E> lambda,
                                                     Collection<R> mappedCollection) {
        for (Collection<E> element : collection) {
            runOverCollection(element, lambda, mappedCollection);
        }
    }

    private static <R, E, L extends List<E>> void runOverList(List<E> list, Lambda<R, L> lambda,
                                                              List<R> mappedList) {
        for (int i = 0; i < list.size(); i++) {
            mappedList.add(lambda.lambda((L) list.subList(i, list.size())));
        }
    }
}
