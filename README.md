# Fibonacci-Heap
Implementation of Fibonacci heap

**What is Fibonacci-Heap?**
a data structure for priority queue operations, consisting of a collection of heap-ordered trees. It has a better amortized running time than many other priority queue data structures including the binary heap and binomial heap
![image](https://user-images.githubusercontent.com/69398302/114304160-62703180-9ada-11eb-9f12-b71fc346a1bf.png)

# Functions <br/>

**insert** :
Creates a node (of type HeapNode) which contains the given key, and insert it into the heap <br/>
Time complexity(Average) :O(1)


**deleteMin** :
Delete the node containing the minimum key.<br/>
Time complexity(Average) :O(log n)


**findMin** :
 Return the node of the heap whose key is minimal.<br/>
Time complexity(WC) :O(1)

**meld(heap2)** :
Meld current heap with heap2<br/>
Time complexity(Average) :O(1)


**countersRep()** :
Return a counters array, where the value of the i-th entry is the number of
trees of order i in the heap.<br/>
Time complexity(WC) :O(n)

**decreaseKey** :
The function decreases the key of the node x by delta. The structure of the
heap should be updated to reflect this chage (for example, the cascading cuts
procedure should be applied if needed).<br/>
Time complexity(Average) :O(1)

**potential** :
This function returns the current potential of the heap, which is: Potential = #trees + 2*#marked<br/>
Time complexity(WC) :O(1)

**delete (x)** : 
 Deletes the node x from the heap.<br/>
Time complexity(Average) :O(log n)


# Example <br/>
```
 FibonacciHeap f = new FibonacciHeap();
 f.insert(2);
 f.insert(5);
 f.insert(1);
 f.insert(30);
		
 int m = f.findMin().getKey();
 System.out.print(m + "\n"); //1
		
 f. deleteMin();
		
 int m2 = f.findMin().getKey();
 System.out.print(m2 + "\n"); //2
			
 HeapNode node = f.insert(10);
 f.decreaseKey(node,3);	
			
 FibonacciHeap f2 = new FibonacciHeap();
		
 f2.insert(23);
 f2.insert(51);
 f2.insert(1);
 f2.insert(6);
		
 int m3 = f2.findMin().getKey();
 System.out.print(m3 + "\n"); //1
 f2. deleteMin();
		
 f.meld(f2);
		
 int m4 = f.findMin().getKey();
 System.out.print(m4 + "\n"); //2
		
 int [] counter = f.countersRep();
		
 System.out.print(Arrays.toString(counter));  //[3,2]		
```

