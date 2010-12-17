package org.lambda.util;

import org.junit.Test;
import org.lambda.Lambda;

import java.util.*;

import static junit.framework.Assert.*;
import static org.lambda.util.Collections.map;
import static org.lambda.util.Collections.mapC;
import static org.lambda.util.Collections.mapList;

/**
 * @author Karl Bennett
 */
public class CollectionsTest {

    @Test
    public void testMap() throws Exception {
        Collection<Integer> collection = new Vector<Integer>(Arrays.asList(1, 2, 3, 4));

        Collection mappedCollection = map(collection, new Lambda<Integer, Integer>(){
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

    @Test
    public void testMapC() throws Exception {
        Collection<Integer> collection = new Vector<Integer>(Arrays.asList(1, 2, 3, 4));
        final Collection<Integer> collectionCopy = new Vector<Integer>();

        mapC(collection, new Lambda<Integer, Integer>(){
            @Override
            public Integer lambda(Integer integer) {
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

        List mappedList = mapList(list, new Lambda<Integer, List<Integer>>(){
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
}
