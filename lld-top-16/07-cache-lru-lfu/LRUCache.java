/*Below are **two separate runnable Java files**:

        1. `LRUCache.java`
        2. `LFUCache.java`

        Everything (design explanation + approach + code comments) is inside the Java file so you can directly paste into IntelliJ.

        ---

        # 1. LRU Cache

        Create:

        ```
        LRUCache.java
        ```

        ```java*/
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.*;


/*

===========================================================
LRU CACHE (Least Recently Used)

===========================================================


PROBLEM:

Cache has limited capacity.

Example:

Capacity = 3


put(1,100)

Cache:

1


put(2,200)

Cache:

1 -> 2


put(3,300)

Cache:

1 -> 2 -> 3



Now:

put(4,400)


Capacity is full.

Which item should remove?

The item that was used least recently.


If 1 was not accessed:

Remove 1


Final:

2 -> 3 -> 4



===========================================================


WHY HASHMAP + DOUBLY LINKED LIST?


We need two operations:

1. Find key quickly

Example:

get(5)

Need O(1)



HashMap gives:

key -> node


2. Maintain usage order


Example:


HEAD

 |
 v

Most Recently Used


A

B

C


 |

TAIL


Least Recently Used



When item is accessed:

Move it to HEAD


When cache full:

Remove TAIL



===========================================================


TIME COMPLEXITY:


get():

HashMap lookup = O(1)

Move node = O(1)


Total = O(1)



put():

Insert = O(1)

Remove = O(1)


Total = O(1)



===========================================================


DATA STRUCTURE:


HashMap<Integer, Node>


Node contains:


key

value

previous

next



===========================================================

*/


public class LRUCache {


    /*
    =======================================================
    NODE

    Doubly linked list node

    =======================================================
    */


    static class Node {


        int key;


        int value;


        Node prev;


        Node next;


        Node(
                int key,
                int value
        ) {

            this.key = key;

            this.value = value;

        }

    }








    /*
    =======================================================
    CACHE CLASS
    =======================================================
    */


    static class Cache {


        private final int capacity;


        private final Map<Integer, Node> map;


        private final Node head;


        private final Node tail;


        Cache(int capacity) {


            this.capacity = capacity;


            map = new HashMap<>();


            /*

            Dummy nodes

            head = most recent

            tail = least recent

            */


            head = new Node(0, 0);


            tail = new Node(0, 0);


            head.next = tail;


            tail.prev = head;

        }









        /*
        ===================================================
        GET

        ===================================================


        If key exists:


        1. Find node from hashmap

        2. Move node to front

        3. Return value



        ===================================================
        */


        public int get(int key) {


            if (!map.containsKey(key)) {


                return -1;


            }


            Node node =
                    map.get(key);


            remove(node);


            insertAtHead(node);


            return node.value;


        }










        /*
        ===================================================
        PUT

        ===================================================


        Case 1:

        Key already exists


        Update value

        Move to front



        Case 2:

        New key


        Add node

        If capacity exceeded:

        Remove least recently used


        ===================================================
        */


        public void put(
                int key,
                int value
        ) {


            if (map.containsKey(key)) {


                Node node =
                        map.get(key);


                node.value = value;


                remove(node);


                insertAtHead(node);


                return;


            }


            Node node =
                    new Node(
                            key,
                            value
                    );


            map.put(
                    key,
                    node
            );


            insertAtHead(node);


            if (map.size() > capacity) {


                Node lru =
                        tail.prev;


                remove(lru);


                map.remove(
                        lru.key
                );


            }


        }


        private void remove(Node node) {


            node.prev.next =
                    node.next;


            node.next.prev =
                    node.prev;


        }


        private void insertAtHead(Node node) {


            node.next =
                    head.next;


            node.prev =
                    head;


            head.next.prev =
                    node;


            head.next =
                    node;


        }


        public void print() {


            Node current =
                    head.next;


            while (current != tail) {


                System.out.print(
                        current.key
                                +
                                "="
                                +
                                current.value
                                +
                                " "
                );


                current =
                        current.next;

            }


            System.out.println();


        }


    }


    public static void main(String[] args) {


        Cache cache =
                new Cache(3);


        cache.put(1, 100);

        cache.put(2, 200);

        cache.put(3, 300);


        cache.print();




        /*

        Access 1

        1 becomes most recent


        */

        cache.get(1);


        cache.print();





        /*

        Add 4


        Remove least recently used


        */
/*
        cache.put(4,400);



        cache.print();



    }


}
```

        ---

        # 2. LFU Cache

Create:

        ```
LFUCache.java
```

        ```java*/






/*

===========================================================
LFU CACHE

Least Frequently Used

===========================================================


DIFFERENCE FROM LRU:



LRU:

Remove item based on TIME


Example:


A used recently
B used long ago


Remove B



-----------------------------------------------------------


LFU:

Remove item based on FREQUENCY


Example:


A accessed 10 times

B accessed 2 times


Remove B



===========================================================


PROBLEM:


Capacity = 3



put(A)

frequency:

A = 1



get(A)

frequency:

A = 2



put(B)

frequency:

B = 1




When cache full:


Remove key with smallest frequency



===========================================================


DATA STRUCTURE:



1. HashMap


key -> Node



2. Frequency Map



frequency -> LinkedHashSet<Node>



Why LinkedHashSet?



Because if two keys have same frequency:


A = 2

B = 2


Need remove oldest one.


===========================================================


TIME COMPLEXITY:


get:

O(1)



put:

O(1)



===========================================================


*/


        class LFUCache {


            static class Node {


                int key;


                int value;


                int frequency;


                Node(
                        int key,
                        int value
                ) {

                    this.key = key;


                    this.value = value;


                    this.frequency = 1;


                }


            }


            static class Cache {


                private final int capacity;


                private int minFrequency;


                private final Map<Integer, Node> nodes;


                private final Map<Integer,
                        LinkedHashSet<Node>> frequencyMap;


                Cache(int capacity) {


                    this.capacity = capacity;


                    nodes = new HashMap<>();


                    frequencyMap =
                            new HashMap<>();


                }


                public int get(int key) {


                    if (!nodes.containsKey(key)) {


                        return -1;


                    }


                    Node node =
                            nodes.get(key);


                    increaseFrequency(node);


                    return node.value;


                }


                public void put(
                        int key,
                        int value
                ) {


                    if (capacity == 0) {

                        return;

                    }


                    if (nodes.containsKey(key)) {


                        Node node =
                                nodes.get(key);


                        node.value = value;


                        increaseFrequency(node);


                        return;


                    }


                    if (nodes.size() == capacity) {


                        LinkedHashSet<Node> set =
                                frequencyMap
                                        .get(minFrequency);


                        Node remove =
                                set.iterator()
                                        .next();


                        set.remove(remove);


                        nodes.remove(
                                remove.key
                        );


                    }


                    Node node =
                            new Node(
                                    key,
                                    value
                            );


                    nodes.put(
                            key,
                            node
                    );


                    frequencyMap
                            .computeIfAbsent(
                                    1,
                                    x -> new LinkedHashSet<>()
                            )
                            .add(node);


                    minFrequency = 1;


                }


                private void increaseFrequency(
                        Node node
                ) {


                    int oldFrequency =
                            node.frequency;


                    frequencyMap
                            .get(oldFrequency)
                            .remove(node);


                    node.frequency++;


                    frequencyMap
                            .computeIfAbsent(
                                    node.frequency,
                                    x -> new LinkedHashSet<>()
                            )
                            .add(node);


                    if (
                            oldFrequency == minFrequency
                                    &&
                                    frequencyMap
                                            .get(oldFrequency)
                                            .isEmpty()
                    ) {

                        minFrequency++;

                    }


                }


                public void print() {


                    for (Node node : nodes.values()) {


                        System.out.println(
                                node.key
                                        +
                                        " value="
                                        +
                                        node.value
                                        +
                                        " freq="
                                        +
                                        node.frequency
                        );


                    }


                }


            }


            public static void main(String[] args) {


                Cache cache =
                        new Cache(3);


                cache.put(1, 100);

                cache.put(2, 200);

                cache.put(3, 300);


                cache.get(1);

                cache.get(1);



        /*

        Frequency:


        1 -> 3

        2 -> 1

        3 -> 1


        Add 4


        Remove among frequency 1


        */

                cache.put(4, 400);


                cache.print();


            }
        }

    }
}
/*
```

        ---

        ## LRU vs LFU Quick Interview Difference

|                   | LRU                           | LFU                     |
        | ----------------- | ----------------------------- | ----------------------- |
        | Full form         | Least Recently Used           | Least Frequently Used   |
        | Removes based on  | Time                          | Usage count             |
        | Question answered | "What was not used recently?" | "What is rarely used?"  |
        | Main DS           | HashMap + Doubly LinkedList   | HashMap + Frequency Map |
        | Complexity        | O(1)                          | O(1)                    |

        ---

For interviews:

        * Redis-style cache → usually discuss **LRU**
        * High traffic systems → discuss **LFU**
        * Production systems often combine **LRU + TTL + eviction policy**.
*/
