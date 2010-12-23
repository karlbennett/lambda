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
 * A collections utility that contains methods which through the use of the Lambda class provide the ability to run
 * logic across collections with far less boilerplate code.
 */
public class LambdaCollections {

    private LambdaCollections() {
    }

    /**
     * Run the logic within the lambda method across the given collection that contains objects of type <E>.
     * Then return a new collection containing the given return type <R>.
     *
     * @param collection - the collection that will have the logic run across each element.
     * @param lambda     - the Lambda object that contains the logic that will be run.
     * @param <R>        - the return type of the Lambda.lambda method and the type contained with the returned
     *                   collection.
     * @param <E>        - the argument type of the Lambda.lambda method and the type contained with the given
     *                   collection.
     * @param <RC>       - the type of the returned collection e.g. List, Set, Map... This does not have to be the same
     *                   as the type of the given collection.
     * @param <C>        - the type of the given collection e.g. List, Set, Map...
     * @return - the new collection built from the given collection after the logic has been run over each element.
     */
    public static <R, E, RC extends Collection<R>, C extends Collection<E>> RC map(C collection, Lambda<R, E> lambda) {
        // TODO properly handle unwanted null arguments..
        if (collection == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collection cannot be null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda cannot be null.");

        RC mappedCollection = instantiateCollection((Class<RC>) collection.getClass());

        if (mappedCollection != null) {
            runOverCollection(collection, lambda, mappedCollection);
        }

        return mappedCollection;
    }

    /**
     * Run the logic within the lambda method across the given collection that contains objects of type <E>.
     * Then return a new collection of the given type <RC> containing the given return type <R>.
     *
     * @param returnType - the Class of the type of collection that should be returned e.g. List.class, Set.class,
     *                   Map.class...
     * @param collection - the collection that will have the logic run across each element.
     * @param lambda     - the Lambda object that contains the logic that will be run.
     * @param <R>        - the return type of the Lambda.lambda method and the type contained with the returned
     *                   collection.
     * @param <E>        - the argument type of the Lambda.lambda method and the type contained with the given
     *                   collection.
     * @param <RC>       - the type of the returned collection e.g. List, Set, Map... This is set by the returnType
     *                   argument.
     * @param <C>        - the type of the given collection e.g. List, Set, Map...
     * @return - the new collection built from the given collection after the logic has been run over each element.
     */
    public static <R, E, RC extends Collection<R>, C extends Collection<E>> RC map(Class<RC> returnType, C collection,
                                                                                   Lambda<R, E> lambda) {
        // TODO properly handle unwanted null arguments..
        if (collection == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collection cannot be null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda cannot be null.");

        RC mappedCollection = instantiateCollection(returnType);

        if (mappedCollection != null) {
            runOverCollection(collection, lambda, mappedCollection);
        }

        return mappedCollection;
    }

    /**
     * Run the logic within the lambda method across the given collection that contains objects of type <E>.
     * Then place each processed element into the given collection of type <RC>.
     *
     * @param returnCollection - the collection that will have the processed elements added to it.
     * @param collection       - the collection that will have the logic run across each element.
     * @param lambda           - the Lambda object that contains the logic that will be run.
     * @param <R>              - the return type of the Lambda.lambda method and the type contained with the returned
     *                         collection.
     * @param <E>              - the argument type of the Lambda.lambda method and the type contained with the given
     *                         collection.
     * @param <RC>             - the type of the returned collection e.g. List, Set, Map... This is set by the
     *                         returnType argument.
     * @param <C>              - the type of the given collection e.g. List, Set, Map...
     * @return - the collection that was passed in as the returnCollection.
     */
    public static <R, E, RC extends Collection<R>, C extends Collection<E>> RC map(RC returnCollection, C collection,
                                                                                   Lambda<R, E> lambda) {
        // TODO properly handle unwanted null arguments..
        if (returnCollection == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - return collection is null.");
        if (collection == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collection is null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda is null.");

        runOverCollection(collection, lambda, returnCollection);

        return returnCollection;
    }

    /**
     * Run the logic within the lambda method across the given collection that contains objects of type <E>.
     * <p/>
     * The return type for the Lambda class is set strictly to Object. This is because the return type in this method
     * is irrelevant.
     *
     * @param collection - the collection that will have the logic run across each element.
     * @param lambda     - the Lambda object that contains the logic that will be run.
     * @param <E>        - the argument type of the Lambda.lambda method and the type contained with the given
     *                   collection.
     * @param <C>        - the type of the given collection e.g. List, Set, Map...
     */
    public static <E, C extends Collection<E>> void mapC(C collection, Lambda<Object, E> lambda) {
        if (collection == null)
            throw new NullPointerException("org.lambda.util.LambdaCollections.map - collection is null.");
        if (lambda == null) throw new NullPointerException("org.lambda.util.LambdaCollections.map - lambda is null.");

        runOverCollection(collection, lambda);
    }

    /**
     * Run the logic within the lambda method across the given collection that contains collections with objects of
     * type <E>. Then return a collapsed single dimension collection of all the processed elements.
     *
     * @param returnType - the Class of the type of collection that should be returned e.g. List.class, Set.class,
     *                   Map.class...
     * @param collection - the 2 dimensional collection that will have the logic run across each element.
     * @param lambda     - the Lambda object that contains the logic that will be run.
     * @param <R>        - the return type of the Lambda.lambda method and the type contained with the returned
     *                   collection.
     * @param <E>        - the argument type of the Lambda.lambda method and the type contained with the given
     *                   collections contains within the given collection.
     * @param <RC>       - the type of the returned collection e.g. List, Set, Map... This is set by the
     *                   returnType argument.
     * @param <C>        - the type of the given collection e.g. List<Set>, Set<Set>, Map<Collection>...
     * @return - a single dimension collection contain the lambda results.
     */
    public static <R, E, RC extends Collection<R>, C extends Collection<Collection<E>>> RC mapCan(Class<RC> returnType,
                                                                                                  C collection,
                                                                                                  Lambda<R, E> lambda) {
        // TODO properly handle unwanted null arguments..
        if (returnType == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - returnType cannot be null.");
        if (collection == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collection cannot be null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda cannot be null.");

        RC mappedCollection = instantiateCollection(returnType);

        if (mappedCollection != null) {
            runOverDeepCollection(collection, lambda, mappedCollection);
        }

        return mappedCollection;
    }

    /**
     * Run the logic within the lambda method across the given list that contains objects of type <E>.
     * The argument for the Lambda.lambda method when used with this method is a list containing the rest of the
     * elements that are yet to be processed e,g. the current element that is to be process along with the remaining
     * tail of the list. Then return a new list contain the result of the processed sub lists.
     *
     * @param list   - the list that will have the logic run across each element.
     * @param lambda - the Lambda object that contains the logic that will be run.
     * @param <R>    - the return type of the Lambda.lambda method and the type contained with the returned list.
     * @param <E>    - the argument type of the Lambda.lambda method and the type contained with the given list.
     * @param <RL>   - the type of the returned list e.g. ArrayList, LinkedList, Vector...
     * @param <L>    - The type of the list that will be passed into the lambda method as it's argument. This must be
     *               the same as the list that was passed in to be processed.
     * @return - a list containing all the results on the processed sub lists. This list will be the same length as the
     *         processed list.
     */
    public static <R, E, RL extends List<R>, L extends List<E>> RL mapList(L list, Lambda<R, L> lambda) {
        if (list == null) throw new NullPointerException("org.lambda.util.LambdaCollections.map - list is null.");
        if (lambda == null) throw new NullPointerException("org.lambda.util.LambdaCollections.map - lambda is null.");

        RL mappedList = instantiateCollection((Class<RL>) list.getClass());

        if (mappedList != null) {
            runOverList(list, lambda, mappedList);
        }

        return mappedList;
    }

    /**
     * Create a new instantiation of any collection class type that is passed to the method.
     * <p/>
     * Handles all of the standard Java collection interfaces and any instantiable collection implementation.
     * Cannot handle any collection interfaces outside of the Java api.
     *
     * @param returnType - the type of collection that is to be instantiated.
     * @param <E>        - the type of class that is contained within the collection.
     * @param <RC>       - the type of collection that is to be returned.
     * @return - a new empty instantiation of the given collection type.
     */
    private static <E, RC extends Collection<E>> RC instantiateCollection(Class<RC> returnType) {
        RC newCollection = null;

        try {
            // Boilerplate code to handle all the default Java collection interfaces. It would be nice if Java had a
            // default implementation of collection that all the other classes used so it could be instantiated and then
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
                            "org.lambda.util.LambdaCollections.map - does not recognise the collection interface "
                                    + returnType.getName());
                }
            } else {
                newCollection = returnType.newInstance();
            }
        } catch (InstantiationException e) {
            // TODO properly handle failed collection instantiation.
            throw new RuntimeException("org.lambda.util.LambdaCollections.map - collection type could not be instantiated. "
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            // TODO properly handle illegal access exception.
            throw new RuntimeException("org.lambda.util.LambdaCollections.map - collection type could not be accessed. "
                    + e.getMessage());
        }

        return newCollection;
    }

    /**
     * Run the given logic in the Lambda class over the given collection.
     * <p/>
     * The return type for the Lambda class is set strictly to Object. This is because the return type in this method
     * is irrelevant.
     *
     * @param collection - the collection that will have the logic run across each element.
     * @param lambda     - the Lambda object that contains the logic that will be run.
     * @param <E>        - the argument type of the Lambda.lambda method and the type contained with the given
     *                   collection.
     */
    private static <E> void runOverCollection(Collection<E> collection, Lambda<Object, E> lambda) {
        for (E element : collection) {
            lambda.lambda(element);
        }
    }

    /**
     * Run the given logic in the Lambda class over the given collection and place the processed elements into the
     * second collection.
     *
     * @param collection       - the collection that will have the logic run across each element.
     * @param lambda           - the Lambda object that contains the logic that will be run.
     * @param mappedCollection - the collection that will have the processed added to it.
     * @param <R>              - the return type of the Lambda.lambda method and the type contained with the returned
     *                         collection.
     * @param <E>              - the argument type of the Lambda.lambda method and the type contained with the given
     *                         collection.
     */
    private static <R, E> void runOverCollection(Collection<E> collection, Lambda<R, E> lambda,
                                                 Collection<R> mappedCollection) {
        for (E element : collection) {
            mappedCollection.add(lambda.lambda(element));
        }
    }

    /**
     * Run the logic within the lambda method across the given collection that contains collections with objects of
     * type <E> and collapse the processed elements into a single dimension collection.
     *
     * @param collection       - the 2 dimensional collection that will have the logic run across each element.
     * @param lambda           - the Lambda object that contains the logic that will be run.
     * @param mappedCollection - the single dimension collection that will have the processed elements added to it.
     * @param <R>              - the return type of the Lambda.lambda method and the type contained with the second
     *                         single dimension collection.
     * @param <E>              - the argument type of the Lambda.lambda method and the type of the collections contained
     *                         within the given collection.
     */
    private static <R, E> void runOverDeepCollection(Collection<Collection<E>> collection, Lambda<R, E> lambda,
                                                     Collection<R> mappedCollection) {
        for (Collection<E> element : collection) {
            runOverCollection(element, lambda, mappedCollection);
        }
    }

    /**
     * Run the logic within the lambda method across the given list that contains objects of type <E>.
     * The argument for the Lambda.lambda method when used with this method is a list containing the rest of the
     * elements that are yet to be processed e,g. the current element that is to be process along with the remaining
     * tail of the list. While processing the list add the processed element to the second list.
     *
     * @param list       - the list that will have the logic run across each element.
     * @param lambda     - the Lambda object that contains the logic that will be run.
     * @param mappedList - the list that will have the result of the processed sub lists added to it.
     * @param <R>        - the return type of the Lambda.lambda method and the type contained with the second list.
     * @param <E>        - the argument type of the Lambda.lambda method and the type contained with the given list.
     * @param <L>        - The type of the list that will be passed into the lambda method as it's argument. This must be
     *                   the same as the list that was passed in to be processed.
     */
    private static <R, E, L extends List<E>> void runOverList(List<E> list, Lambda<R, L> lambda,
                                                              List<R> mappedList) {
        for (int i = 0; i < list.size(); i++) {
            mappedList.add(lambda.lambda((L) list.subList(i, list.size())));
        }
    }
}
