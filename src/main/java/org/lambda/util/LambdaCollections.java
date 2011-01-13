package org.lambda.util;

import org.lambda.Lambda;

import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServicesSupport;
import java.beans.beancontext.BeanContextSupport;
import java.lang.reflect.Array;
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
     * Run the logic within the lambda method across the provided collection/s that contain objects of type <E>.
     * Then return a new collection containing the given return type <R>.
     *
     * @param lambda      - the Lambda object that contains the logic that will be run.
     * @param collections - the collection/s that will have the logic run across each element.
     * @param <R>         - the return type of the Lambda.lambda method and the type contained with the returned
     *                    collection.
     * @param <E>         - the argument type of the Lambda.lambda method and the type contained with the given
     *                    collection/s.
     * @param <RC>        - the type of the returned collection e.g. List, Set, Map... This does not have to be the same
     *                    as the type of the given collection.
     * @param <C>         - the type of the given collection e.g. List, Set, Map...
     * @return - a new collection containing the results of the lambda method.
     */
    public static <R, E, RC extends Collection<R>, C extends Collection<E>> RC map(Lambda<R, E> lambda,
                                                                                   C... collections) {
        // TODO properly handle unwanted null arguments..
        if (collections == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collections cannot be null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda cannot be null.");

        RC mappedCollection = collections.length == 0 ? null
                : instantiateCollection((Class<RC>) collections[0].getClass());

        if (mappedCollection != null) {
            runOverCollection(mappedCollection, lambda, collections);
        }

        return mappedCollection;
    }

    /**
     * Run the logic within the lambda method across the provided collection/s that contain objects of type <E>.
     * Then return a new collection of the given type <RC> containing the given return type <R>.
     *
     * @param returnType  - the Class of the type of collections that should be returned e.g. List.class, Set.class,
     *                    Map.class, ArrayList.class...
     * @param lambda      - the Lambda object that contains the logic that will be run.
     * @param collections - the collection/s that will have the logic run across each element.
     * @param <R>         - the return type of the Lambda.lambda method and the type contained with the returned
     *                    collection.
     * @param <E>         - the argument type of the Lambda.lambda method and the type contained with the given
     *                    collection/s.
     * @param <RC>        - the type of the returned collection e.g. List, Set, Map... This is set by the returnType
     *                    argument.
     * @param <C>         - the type of the given collection e.g. List, Set, Map...
     * @return - the new collection built from the given collections after the logic has been run over each element.
     */
    public static <R, E, RC extends Collection<R>, C extends Collection<E>> RC map(Class<RC> returnType,
                                                                                   Lambda<R, E> lambda,
                                                                                   C... collections) {
        // TODO properly handle unwanted null arguments..
        if (collections == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collections cannot be null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda cannot be null.");

        RC mappedCollection = collections.length == 0 ? null
                : instantiateCollection(returnType);

        if (mappedCollection != null) {
            runOverCollection(mappedCollection, lambda, collections);
        }

        return mappedCollection;
    }

    /**
     * Run the logic within the lambda method across the provided collection/s that contain objects of type <E>.
     * Then place each processed element into the provided return collection of type <RC>.
     *
     * @param returnCollection - the collection that will have the processed elements added to it.
     * @param lambda           - the Lambda object that contains the logic that will be run.
     * @param collections      - the collection/s that will have the logic run across each element.
     * @param <R>              - the return type of the Lambda.lambda method and the type contained with the returned
     *                         collection.
     * @param <E>              - the argument type of the Lambda.lambda method and the type contained with the given
     *                         collection/s.
     * @param <RC>             - the type of the returned collection e.g. List, Set, Map... This is set by the
     *                         returnType argument.
     * @param <C>              - the type of the given collection e.g. List, Set, Map...
     * @return - the collection that was passed in as the returnCollection.
     */
    public static <R, E, RC extends Collection<R>, C extends Collection<E>> RC map(RC returnCollection,
                                                                                   Lambda<R, E> lambda,
                                                                                   C... collections) {
        // TODO properly handle unwanted null arguments..
        if (returnCollection == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - return collections is null.");
        if (collections == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collections is null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda is null.");

        runOverCollection(returnCollection, lambda, collections);

        return returnCollection;
    }

    /**
     * Run the logic within the lambda method across the provided collection/s that contain objects of type <E>.
     * <p/>
     * The return type for the Lambda class is set strictly to Object. This is because the return type in this method
     * is irrelevant.
     *
     * @param lambda      - the Lambda object that contains the logic that will be run.
     * @param collections - the collection/s that will have the logic run across each element.
     * @param <E>         - the argument type of the Lambda.lambda method and the type contained with the given
     *                    collection/s.
     * @param <C>         - the type of the given collection e.g. List, Set, Map...
     */
    public static <E, C extends Collection<E>> void mapC(Lambda<Object, E> lambda, C collections) {
        if (collections == null)
            throw new NullPointerException("org.lambda.util.LambdaCollections.map - collections is null.");
        if (lambda == null) throw new NullPointerException("org.lambda.util.LambdaCollections.map - lambda is null.");

        runOverCollection(lambda, collections);
    }

    /**
     * Run the logic within the lambda method across the provided collection/s that contain collections with objects of
     * type <E>. Then return a collapsed single dimension collection of all the processed elements.
     *
     * @param returnType  - the Class of the type of collection that should be returned e.g. List.class, Set.class,
     *                    Map.class...
     * @param lambda      - the Lambda object that contains the logic that will be run.
     * @param collections - the 2 dimensional collection/s that will have the logic run across each element.
     * @param <R>         - the return type of the Lambda.lambda method and the type contained with the returned
     *                    collection.
     * @param <E>         - the argument type of the Lambda.lambda method and the type contained with the given
     *                    collections contains within the given collection/s.
     * @param <RC>        - the type of the returned collection e.g. List, Set, Map... This is set by the
     *                    returnType argument.
     * @param <C>         - the type of the given collection e.g. List<Set>, Set<Set>, Map<Collection>...
     *                    lambda method.
     * @return - a single dimension collection containing the lambda results.
     */
    public static <R, E, RC extends Collection<R>, C extends Collection<Collection<E>>> RC mapCan(Class<RC> returnType,
                                                                                                  Lambda<R, E> lambda,
                                                                                                  C... collections) {
        // TODO properly handle unwanted null arguments..
        if (returnType == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - returnType cannot be null.");
        if (collections == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collections cannot be null.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda cannot be null.");

        RC mappedCollection = collections.length == 0 ? null
                : instantiateCollection(returnType);

        if (mappedCollection != null) {
            runOverDeepCollection(mappedCollection, lambda, collections);
        }

        return mappedCollection;
    }

    /**
     * Run the logic within the lambda method across the provided list/s that contain objects of type <E>.
     * The argument for the Lambda.lambda method when used with this method is a list containing the rest of the
     * elements that are yet to be processed e,g. the current element/s that are yet to be process along with the
     * remaining tail of the list/s. Then return a new list contain the result of the processed sub lists.
     *
     * @param lambda - the Lambda object that contains the logic that will be run.
     * @param lists  - the list/s that will have the logic run across each element.
     * @param <R>    - the return type of the Lambda.lambda method and the type contained with the returned list.
     * @param <E>    - the argument type of the Lambda.lambda method and the type contained with the given list/s.
     * @param <RL>   - the type of the returned list e.g. ArrayList, LinkedList, Vector...
     * @param <L>    - The type of the list that will be passed into the lambda method as it's argument. This must be
     *               the same as the list that was passed in to be processed.
     * @return - a list containing all the results of the processed sub lists. This list will be the same length as the
     *         shortest processed list.
     */
    public static <R, E, RL extends List<R>, L extends List<E>> RL mapList(Lambda<R, L> lambda, L... lists) {
        if (lists == null) throw new NullPointerException("org.lambda.util.LambdaCollections.map - lists is null.");
        if (lambda == null) throw new NullPointerException("org.lambda.util.LambdaCollections.map - lambda is null.");

        RL mappedList = lists.length == 0 ? null
                : instantiateCollection((Class<RL>) lists[0].getClass());

        if (mappedList != null) {
            runOverList(mappedList, lambda, lists);
        }

        return mappedList;
    }

    /**
     * Return true if any evaluation of lambda returns true or not null when run across the elements within the provided
     * collection/s.
     * <p/>
     * The lambda method will be iteratively evaluated until it returns a not null or true value where all evaluations
     * will stop.
     *
     * @param lambda      - the Lambda object that contains the logic that will be run.
     * @param collections - the collection/s that will have the logic run across each element.
     * @param <R>         - the return type of the Lambda.lambda method.
     * @param <E>         - the argument type of the Lambda.lambda method and the type contained with the given
     *                    collection/s.
     * @param <C>         - the type of the given collection e.g. List, Set, Map...
     * @return - true if a non null or true value is returned from an evaluation of the lambda method otherwise false.
     */
    public static <R, E, C extends Collection<E>> Boolean some(Lambda<R, E> lambda, C... collections) {
        if (collections == null || collections.length == 0) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collections cannot be null or empty.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda cannot be null.");

        Boolean some = false;
        Iterator<E>[] iterators = getIterators(collections);
        E[] arguments = null;
        R result = null;
        while (haveNext(iterators)) {

            arguments = assignValues(iterators);

            result = lambda.lambda(arguments);
            if (null != result && !Boolean.FALSE.equals(result)) {
                some = true;
                break;
            }
        }

        return some;
    }

    /**
     * Return true if all evaluations of lambda return true or not null when run across the elements within the provided
     * collection/s
     * <p/>
     * The lambda method will be iteratively evaluated until it returns a null or false value where all evaluations will
     * stop.
     *
     * @param lambda      - the Lambda object that contains the logic that will be run.
     * @param collections - the collection/s that will have the logic run across each element.
     * @param <R>         - the return type of the Lambda.lambda method.
     * @param <E>         - the argument type of the Lambda.lambda method and the type contained with the given
     *                    collection/s.
     * @param <C>         - the type of the given collection e.g. List, Set, Map...
     * @return - true if a non null or true value is returned from all evaluations of the lambda method otherwise false.
     */
    public static <R, E, C extends Collection<E>> Boolean every(Lambda<R, E> lambda, C... collections) {
        if (collections == null || collections.length == 0) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - collections cannot be null or empty.");
        if (lambda == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.map - lambda cannot be null.");

        Boolean some = true;
        Iterator<E>[] iterators = getIterators(collections);
        E[] arguments = null;
        R result = null;
        while (haveNext(iterators)) {

            arguments = assignValues(iterators);

            result = lambda.lambda(arguments);
            if (null == result || Boolean.FALSE.equals(result)) {
                some = false;
                break;
            }
        }

        return some;
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
     * Run the given logic in the Lambda class over the provided collection/s.
     * <p/>
     * The return type for the Lambda class is set strictly to Object. This is because the return type in this method
     * is irrelevant.
     *
     * @param lambda      - the Lambda object that contains the logic that will be run.
     * @param collections - the collection/s that will have the logic run across each element.
     * @param <E>         - the argument type of the Lambda.lambda method and the type contained with the provided
     *                    collection/s.
     */
    private static <E> void runOverCollection(Lambda<Object, E> lambda, Collection<E>... collections) {
        E[] arguments = null;
        Iterator<E>[] iterators = getIterators(collections);
        while (haveNext(iterators)) {
            arguments = assignValues(iterators);

            lambda.lambda(arguments);
        }
    }

    /**
     * Run the given logic in the Lambda class over the provided collection/s and place the processed elements into the
     * provided return collection.
     *
     * @param mappedCollection - the collection that will have the lambda results added to it.
     * @param lambda           - the Lambda object that contains the logic that will be run.
     * @param collections      - the collection/s that will have the logic run across each element.
     * @param <R>              - the return type of the Lambda.lambda method and the type contained with the returned
     *                         collection.
     * @param <E>              - the argument type of the Lambda.lambda method and the type contained with the provided
     *                         collection/s.
     */
    private static <R, E> void runOverCollection(Collection<R> mappedCollection, Lambda<R, E> lambda,
                                                 Collection<E>... collections) {
        E[] arguments = null;
        Iterator<E>[] iterators = getIterators(collections);
        while (haveNext(iterators)) {
            arguments = assignValues(iterators);

            mappedCollection.add(lambda.lambda(arguments));
        }
    }

    /**
     * Run the logic within the lambda method across the provided collection/s that contain collections with objects of
     * type <E> and collapse the processed elements into a single dimension collection.
     *
     * @param mappedCollection - the single dimension collection that will have the lambda results added to it.
     * @param lambda           - the Lambda object that contains the logic that will be run.
     * @param collections      - the 2 dimensional collection/s that will have the logic run across each element.
     * @param <R>              - the return type of the Lambda.lambda method and the type contained with the provided
     *                         single dimension collection.
     * @param <E>              - the argument type of the Lambda.lambda method and the type of the collection/s
     *                         contained within the given collection.
     */
    private static <R, E> void runOverDeepCollection(Collection<R> mappedCollection, Lambda<R, E> lambda,
                                                     Collection<Collection<E>>... collections) {
        Collection<E>[] arguments = null;
        Iterator<Collection<E>>[] iterators = getIterators(collections);
        while (haveNext(iterators)) {
            arguments = assignValues(iterators);

            runOverCollection(mappedCollection, lambda, arguments);
        }
    }

    /**
     * Run the logic within the lambda method across the provided list/s that contain objects of type <E>.
     * The argument for the Lambda.lambda method when used with this method is a list/s containing the rest of the
     * elements that are yet to be processed e,g. the current element that is to be process along with the remaining
     * tail of the list/s. The results of the lambda method will be added to the provided mapped list.
     *
     * @param mappedList - the lists that will have the the lambda results added to it.
     * @param lambda     - the Lambda object that contains the logic that will be run.
     * @param lists      - the list/s that will have the logic run across each element.
     * @param <R>        - the return type of the Lambda.lambda method and the type contained with the mapped list.
     * @param <E>        - the argument type of the Lambda.lambda method and the type contained with the provided list/s.
     * @param <L>        - The type of the list that will be passed into the lambda method as it's argument. This must be
     *                   the same as the list/s that was passed in to be processed.
     */
    private static <R, E, L extends List<E>> void runOverList(List<R> mappedList, Lambda<R, L> lambda,
                                                              L... lists) {
        L[] tails = null;
        for (int i = 0; i < lists[0].size(); i++) {
            tails = getTails(i, lists);

            if (null == tails) break;
            else mappedList.add(lambda.lambda(tails));
        }
    }

    private static <E, L extends List<E>> L[] getTails(int nth, L... lists) {
        if (lists == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.getTails - lists must not be null.");

        L tail = null;
        L[] tails = null;
        for (int i = 0; i < lists.length; i++) {
            tail = (L) lists[i].subList(nth, lists[i].size());

            if (tails == null) {
                tails = (L[]) Array.newInstance(tail.getClass(), lists.length);
            }

            tails[i] = tail;
        }

        return tails;
    }

    private static <E> Iterator<E>[] getIterators(Collection<E>... collections) {
        Iterator<E>[] iterators = (Iterator<E>[]) new Iterator[collections.length];

        for (int i = 0; i < iterators.length; i++) {
            iterators[i] = collections[i].iterator();
        }

        return iterators;
    }

    private static Boolean haveNext(Iterator... iterators) {
        for (Iterator itr : iterators) {
            if (!itr.hasNext()) return false;
        }

        return true;
    }

    private static <E> E[] assignValues(Iterator<E>[] iterators) {
        if (iterators == null) throw new NullPointerException(
                "org.lambda.util.LambdaCollections.assignValues - iterators must not be null.");

        E value = null;
        E[] values = null;
        for (int i = 0; i < iterators.length; i++) {
            value = iterators[i].next();

            if (values == null) {
                values = (E[]) Array.newInstance(value.getClass(), iterators.length);
            }

            values[i] = value;
        }

        return values;
    }
}
