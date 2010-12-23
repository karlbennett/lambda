package org.lambda.util;

import org.junit.Test;
import org.lambda.Lambda;

import java.util.*;

import static junit.framework.Assert.*;
import static org.lambda.util.LambdaCollections.*;

/**
 * @author Karl Bennett
 */
public class LambdaCollectionsTest {

    /**
     * Test map method with collection and lambda.
     *
     * @throws Exception
     */
    @Test
    public void testMapCL() throws Exception {
        Collection<Integer> collection = new Vector<Integer>(Arrays.asList(1, 2, 3, 4));

        Collection<Integer> mappedCollection = map(collection, new Lambda<Integer, Integer>() {
            @Override
            public Integer lambda(Integer integer) {
                return integer + 1;
            }
        });

        assertNotNull("mapped collection created", mappedCollection);
        assertFalse("new mapped collection", collection == mappedCollection);
        assertNotSame("mapped collection different", collection, mappedCollection);
        assertEquals("mapped collection size correct", collection.size(), mappedCollection.size());
        List<Integer> mappedList = new ArrayList<Integer>(mappedCollection);
        assertEquals("mapped elemet 0 correct", new Integer(2), mappedList.get(0));
        assertEquals("mapped elemet 1 correct", new Integer(3), mappedList.get(1));
        assertEquals("mapped elemet 2 correct", new Integer(4), mappedList.get(2));
        assertEquals("mapped elemet 3 correct", new Integer(5), mappedList.get(3));
    }

    /**
     * Test map method with collection and lambda with a different return type.
     *
     * @throws Exception
     */
    @Test
    public void testMapCLDR() throws Exception {
        Collection<Integer> collection = new Vector<Integer>(Arrays.asList(1, 2, 3, 4));

        Collection<String> mappedCollection = map(collection, new Lambda<String, Integer>() {
            @Override
            public String lambda(Integer integer) {
                return Integer.toString(integer + 1);
            }
        });

        assertNotNull("mapped collection created", mappedCollection);
        assertNotSame("mapped collection different", collection, mappedCollection);
        assertEquals("mapped collection size correct", collection.size(), mappedCollection.size());
        List<String> mappedList = new ArrayList<String>(mappedCollection);
        assertEquals("mapped elemet 0 correct", "2", mappedList.get(0));
        assertEquals("mapped elemet 1 correct", "3", mappedList.get(1));
        assertEquals("mapped elemet 2 correct", "4", mappedList.get(2));
        assertEquals("mapped elemet 3 correct", "5", mappedList.get(3));
    }

    /**
     * Test map method with class, collection, and lambda.
     *
     * @throws Exception
     */
    @Test
    public void testMapClCL() throws Exception {
        Collection<Integer> collection = new Vector<Integer>(Arrays.asList(1, 2, 3, 4));

        List<Integer> mappedList = map(List.class, collection, new Lambda<Integer, Integer>() {
            @Override
            public Integer lambda(Integer integer) {
                return integer + 1;
            }
        });

        assertNotNull("mapped collection created", mappedList);
        assertFalse("new mapped collection", collection == mappedList);
        assertNotSame("mapped collection different", collection, mappedList);
        assertEquals("mapped collection size correct", collection.size(), mappedList.size());
        assertEquals("mapped elemet 0 correct", new Integer(2), mappedList.get(0));
        assertEquals("mapped elemet 1 correct", new Integer(3), mappedList.get(1));
        assertEquals("mapped elemet 2 correct", new Integer(4), mappedList.get(2));
        assertEquals("mapped elemet 3 correct", new Integer(5), mappedList.get(3));
    }

    /**
     * Test map method with return collection, collection, and lambda.
     *
     * @throws Exception
     */
    @Test
    public void testMapRCCL() throws Exception {
        Collection<Integer> collection = new Vector<Integer>(Arrays.asList(1, 2, 3, 4));
        List<Integer> preMappedList = new ArrayList<Integer>();

        List<Integer> postMappedList = map(preMappedList, collection, new Lambda<Integer, Integer>() {
            @Override
            public Integer lambda(Integer integer) {
                return integer + 1;
            }
        });

        assertNotNull("mapped collection created", preMappedList);
        assertFalse("new mapped collection", collection == preMappedList);
        assertTrue("post list equals pre list", preMappedList == postMappedList);
        assertNotSame("mapped collection different", collection, preMappedList);
        assertEquals("mapped collection size correct", collection.size(), preMappedList.size());
        assertEquals("mapped elemet 0 correct", new Integer(2), preMappedList.get(0));
        assertEquals("mapped elemet 1 correct", new Integer(3), preMappedList.get(1));
        assertEquals("mapped elemet 2 correct", new Integer(4), preMappedList.get(2));
        assertEquals("mapped elemet 3 correct", new Integer(5), preMappedList.get(3));
    }

    @Test
    public void testMapCan() throws Exception {
        Collection<Collection<Integer>> collection = new Vector<Collection<Integer>>();
        collection.add(new Vector<Integer>(Arrays.asList(1, 2)));
        collection.add(new Vector<Integer>(Arrays.asList(3, 4)));
        collection.add(new Vector<Integer>(Arrays.asList(5, 6)));

        List<Integer> mappedList = mapCan(List.class, collection,
                new Lambda<Integer, Integer>() {
                    @Override
                    public Integer lambda(Integer integer) {
                        return integer + 1;
                    }
                });

        assertNotNull("mapped collection created", mappedList);
        assertNotSame("mapped collection different", collection, mappedList);
        assertFalse("mapped collection size correct", collection.size() == mappedList.size());
        assertEquals("mapped elemet 0 correct", new Integer(2), mappedList.get(0));
        assertEquals("mapped elemet 1 correct", new Integer(3), mappedList.get(1));
        assertEquals("mapped elemet 2 correct", new Integer(4), mappedList.get(2));
        assertEquals("mapped elemet 3 correct", new Integer(5), mappedList.get(3));
        assertEquals("mapped elemet 4 correct", new Integer(6), mappedList.get(4));
        assertEquals("mapped elemet 5 correct", new Integer(7), mappedList.get(5));
    }

    @Test
    public void testMapC() throws Exception {
        Collection<Integer> collection = new Vector<Integer>(Arrays.asList(1, 2, 3, 4));
        final Collection<Integer> collectionCopy = new Vector<Integer>();

        mapC(collection, new Lambda<Object, Integer>() {
            @Override
            public Object lambda(Integer integer) {
                collectionCopy.add(integer);
                return null;
            }
        });

        assertEquals("copied collection size correct", collection.size(), collectionCopy.size());
        assertEquals("copied collection equal", collection, collectionCopy);
    }

    @Test
    public void testMapList() throws Exception {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));

        List<Integer> mappedList = mapList(list, new Lambda<Integer, List<Integer>>() {
            @Override
            public Integer lambda(List<Integer> tail) {
                int sum = 0;

                for (int n : tail) {
                    sum += n;
                }

                return sum;
            }
        });

        assertNotNull("mapped list created", mappedList);
        assertFalse("new mapped list", list == mappedList);
        assertNotSame("mapped list different", list, mappedList);
        assertEquals("mapped list size correct", list.size(), mappedList.size());
        assertEquals("mapped elemet 0 correct", new Integer(10), mappedList.get(0));
        assertEquals("mapped elemet 1 correct", new Integer(9), mappedList.get(1));
        assertEquals("mapped elemet 2 correct", new Integer(7), mappedList.get(2));
        assertEquals("mapped elemet 3 correct", new Integer(4), mappedList.get(3));
    }

    /**
     * Map list with different return type.
     * @throws Exception
     */
    @Test
    public void testMapListDR() throws Exception {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));

        List<String> mappedList = mapList(list, new Lambda<String, List<Integer>>() {
            @Override
            public String lambda(List<Integer> tail) {
                int sum = 0;

                for (int n : tail) {
                    sum += n;
                }

                return Integer.toString(sum);
            }
        });

        assertNotNull("mapped list created", mappedList);
        assertNotSame("mapped list different", list, mappedList);
        assertEquals("mapped list size correct", list.size(), mappedList.size());
        assertEquals("mapped elemet 0 correct", "10", mappedList.get(0));
        assertEquals("mapped elemet 1 correct", "9", mappedList.get(1));
        assertEquals("mapped elemet 2 correct", "7", mappedList.get(2));
        assertEquals("mapped elemet 3 correct", "4", mappedList.get(3));
    }
}
