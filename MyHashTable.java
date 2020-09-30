package MiniStressTester;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class MyHashTable<K,V> implements Iterable<HashPair<K,V>>{
    // num of entries to the table
    private int numEntries;
    // num of buckets 
    private int numBuckets;
    // load factor needed to check for rehashing 
    private static final double MAX_LOAD_FACTOR = 0.75;
    // ArrayList of buckets. Each bucket is a LinkedList of MiniStressTester.HashPair
    private ArrayList<LinkedList<HashPair<K,V>>> buckets;
    //private double currentLF;
    
    // constructor
    public MyHashTable(int initialCapacity) {
        if (initialCapacity == 0) {
            numBuckets = 10;
        } else {
            numBuckets = initialCapacity;
        }
        numEntries = 0;
        buckets = new ArrayList<>(numBuckets);
        for (int x = 0; x<numBuckets; x++){
            LinkedList<HashPair<K, V>> emptyList = new LinkedList<>();
            buckets.add(emptyList);
        }
    }
    
    public int size() {
        return this.numEntries;
    }
    
    public boolean isEmpty() {
        return this.numEntries == 0;
    }
    
    public int numBuckets() {
        return this.numBuckets;
    }
    
    //Returns the buckets variable. Useful for testing  purposes.
    public ArrayList<LinkedList<HashPair<K,V>>> getBuckets(){
        return this.buckets;
    }
    
    //Given a key, return the bucket position for the key.
    public int hashFunction(K key) {
        int hashValue = Math.abs(key.hashCode())%this.numBuckets; //hashcode will be the same if the strings are identical
        return hashValue;
    }
    
    /**
     * Takes a key and a value as input and adds the corresponding MiniStressTester.HashPair
     * to this HashTable. Expected average run time  O(1)
     */
    public V put(K key, V value) {
        if ((double) numEntries/numBuckets > MAX_LOAD_FACTOR) {
            this.rehash();
        }
        int hValue = hashFunction(key);
        LinkedList<HashPair<K, V>> tempList = buckets.get(hValue);
        if (tempList != null) {
            for (int x=0; x<tempList.size(); x++) {
                HashPair<K,V> entry = tempList.get(x);
                if (entry.getKey().equals(key)) {
                    V oldValue = entry.getValue();
                    entry.setValue(value);
                    return oldValue;
                }
            }
        }
        tempList.add(new HashPair(key, value));
        numEntries ++;
        return null;
    }

    //Get the value corresponding to key. Expected average runtime O(1)
    public V get(K key) {
        int hValue = this.hashFunction(key);
        LinkedList<HashPair<K, V>> tempList = buckets.get(hValue);
        if (tempList != null) {
            for (int x=0; x<tempList.size(); x++) {
                if (tempList.get(x).getKey().equals(key)) {
                    return tempList.get(x).getValue();
                }
            }
        }
    	return null;
    }

    // Remove the MiniStressTester.HashPair corresponding to key . Expected average runtime O(1)
    public V remove(K key) {
        int hValue = this.hashFunction(key);
        LinkedList<HashPair<K, V>> tempList = buckets.get(hValue);
        if (tempList != null) {
            for (int x=0; x<tempList.size(); x++) {
                if (tempList.get(x).getKey().equals(key)) {
                    V oldValue = tempList.get(x).getValue();
                    tempList.remove(x);
                    numEntries--;
                    return oldValue;

                }
            }
        }
    	return null;
    }
    
    /** 
     * Method to double the size of the hashtable if load factor increases
     * beyond MAX_LOAD_FACTOR.
     * Made public for ease of testing.
     * Expected average runtime is O(m), where m is the number of buckets
     */

    //MUST TEST
    public void rehash() {
        numEntries = 0; //resetting the num of entries
        ArrayList<LinkedList<HashPair<K,V>>> oldBuckets = buckets;
        for (int x=0; x<numBuckets; x++){
            oldBuckets.add(buckets.get(x));
        }
        int oldNumBuckets = numBuckets;
        numBuckets *= 2;
        buckets = new ArrayList<>(numBuckets);
        for (int x = 0; x<numBuckets; x++){ //fill up new buckets with empty linked lists
            buckets.add(new LinkedList<>());
        }
        for (int x = 0; x<oldNumBuckets; x++){
            LinkedList<HashPair<K, V>> tempList = oldBuckets.get(x);
            if (tempList != null) {
                for (HashPair<K, V> entry : tempList){
                    this.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }
    
    /**
     * Return a list of all the keys present in this hashtable.
     * Expected average runtime is O(m), where m is the number of buckets
     */
    public ArrayList<K> keys() {
        ArrayList<K> keysList = new ArrayList<K>();
        for (int x=0; x<numBuckets; x++){
            LinkedList<HashPair<K, V>> tempList = buckets.get(x);
            if (tempList != null){
                for (int y=0; y<tempList.size(); y++){
                    keysList.add(tempList.get(y).getKey());
                }
            }
        }
    	return keysList;
    }

    /**
     * Returns an ArrayList of unique values present in this hashtable.
     * Expected average runtime is O(m) where m is the number of buckets
     */
    public ArrayList<V> values() {
        MyHashTable<V, V> valuesTable  = new MyHashTable<V, V>(numBuckets);
        ArrayList<V> valuesList;
        for (int x = 0; x<this.numBuckets; x++){
            LinkedList<HashPair<K, V>> originalList = this.buckets.get(x);
            if (originalList != null){
                for (HashPair<K, V> entry : originalList) {
                    valuesTable.put(entry.getValue(), entry.getValue());
                }
            }
        }
        valuesList = valuesTable.keys();
        return valuesList;
    }

    /**
	 * This method takes as input an object of type MiniStressTester.MyHashTable with values that
	 * are Comparable. It returns an ArrayList containing all the keys from the map, 
	 * ordered in descending order based on the values they mapped to. 
	 * 
	 * The time complexity for this method is O(n^2), where n is the number 
	 * of pairs in the map. 
	 */
	//time: latest to oldest; name: z-a
    public static <K, V extends Comparable<V>> ArrayList<K> slowSort (MyHashTable<K, V> results) {
        ArrayList<K> sortedResults = new ArrayList<>();
        for (HashPair<K, V> entry : results) {
			V element = entry.getValue();
			K toAdd = entry.getKey();
			int i = sortedResults.size() - 1;
			V toCompare = null;
        	while (i >= 0) {
        		toCompare = results.get(sortedResults.get(i));
        		if (element.compareTo(toCompare) <= 0 )
        			break;
        		i--;
        	}
        	sortedResults.add(i+1, toAdd);
        }
        return sortedResults;
    }

	/**
	 * This method takes as input an object of type MiniStressTester.MyHashTable with values that
	 * are Comparable. It returns an ArrayList containing all the keys from the map, 
	 * ordered in descending order based on the values they mapped to.
	 * 
	 * The time complexity for this method is O(n*log(n)), where n is the number 
	 * of pairs in the map. 
	 */
    
    public static <K, V extends Comparable<V>> ArrayList<K> fastSort(MyHashTable<K, V> results) {
        ArrayList<K> keysList = results.keys();
        //System.out.println(keysList.toString());
        ArrayList<V> valuesList = new ArrayList<>();
        for (K key : keysList){
            valuesList.add(results.get(key));
        }
        int leftIndex = 0; int rightIndex = valuesList.size()-1;
        quickSort(valuesList, keysList, leftIndex, rightIndex);
    	return keysList;
    }

    private static <K, V extends Comparable<V>> void quickSort(ArrayList<V> list, ArrayList<K> kList, int left, int right){
        if (left < right) {
            int index = placeAndDivide(list, kList, left, right);
            quickSort(list, kList, left, index-1);
            quickSort(list, kList, index+1, right);
        }
    }

    private static <K, V extends Comparable<V>> int placeAndDivide(ArrayList<V> list, ArrayList<K> kList, int left, int right){
        V pivot = list.get(right);
        int wall = left-1;
        for (int x = left; x < right; x++){
            //if the date is greater than the pivot: move it to the left
            if (list.get(x).compareTo(pivot) > 0) {
                wall++;
                //swap wall and the element x
                V temp = list.get(wall);
                list.set(wall, list.get(x));
                list.set(x, temp);
                K kTemp = kList.get(wall);
                kList.set(wall, kList.get(x));
                kList.set(x, kTemp);
            }
        }
        V temp = list.get(wall+1);
        list.set(wall+1, pivot);
        list.set(right, temp);
        K kTemp = kList.get(wall+1);
        kList.set(wall+1, kList.get(right));
        kList.set(right, kTemp);
        return wall+1;
    }
    
    @Override
    public MyHashIterator iterator() {
        return new MyHashIterator();
    }   
    
    private class MyHashIterator implements Iterator<HashPair<K,V>> {
    	ArrayList<HashPair<K, V>> list = new ArrayList<>();
    	HashPair<K, V> current;
    	int index = 0;

    	/**
    	 * Expected average runtime is O(m) where m is the number of buckets
    	 */
        private MyHashIterator() {
            ArrayList<LinkedList<HashPair<K, V>>> listOfBuckets = getBuckets();
        	for (LinkedList<HashPair<K, V>> linkedList : listOfBuckets){
        	    if (linkedList != null){
        	        for (HashPair<K, V> entry : linkedList){
        	            list.add(entry);
                    }
                }
            }
        	if (list.size() > 0) {
                current = list.get(0);
            }
        }
        public HashPair<K,V> next() {
            HashPair<K,V> temp = new HashPair<K,V>(current.getKey(), current.getValue());
            index ++; //int nextIndex = list.indexOf(current)+1; -> indexOf takes a long time!!!
            if (index >= list.size()){
                current = null;
            } else {
                current = list.get(index);
            }
            return temp;
        }

        public boolean hasNext() {
            return current != null;
        }
    }
}
