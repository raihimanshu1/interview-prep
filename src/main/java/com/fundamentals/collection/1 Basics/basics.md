Got it. You are not asking **interface vs class theory**. You want:

* In the **Collection hierarchy**, which ones are interfaces?
* Which ones are actual classes?
* How did Java build common implementation?
* What code is inside each layer?
* How does ArrayDeque finally get these methods?

Let's go layer by layer.

---

# 1. Complete Java Collection Hierarchy (Actual)

```
                         Iterable (Interface)
                              |
                              |
                    Collection (Interface)
                              |
        ------------------------------------------------
        |                     |                        |
    List (Interface)      Set (Interface)        Queue (Interface)
        |                     |                        |
        |                     |                   Deque (Interface)
        |                     |                        |
        |                     |                ------------------
        |                     |                |                |
   ArrayList(Class)      HashSet(Class)   ArrayDeque(Class)  LinkedList(Class)
```

Important:

**The arrows are inheritance relationships.**

But:

* Iterable → Interface
* Collection → Interface
* List → Interface
* Set → Interface
* Queue → Interface
* Deque → Interface

These are NOT implementations.

They only define contracts.

Actual working classes:

* ArrayList
* HashSet
* ArrayDeque
* LinkedList

---

# 2. Iterable Interface

This is the root.

Java code simplified:

```java
public interface Iterable<T> {

    Iterator<T> iterator();

}
```

Why does it exist?

Because Java wants every collection to support:

```java
for(String name : list){

}
```

Any class implementing Iterable must provide:

```java
iterator()
```

---

# 3. Collection Interface

Code:

```java
public interface Collection<E>
extends Iterable<E>{


    boolean add(E e);


    boolean remove(Object o);


    int size();


    void clear();


    boolean contains(Object o);

}
```

Meaning:

Every collection should know:

* add element
* remove element
* count elements
* check element

But Collection does NOT know how.

Example:

ArrayList:

```
how add?
using array

[A][B][C]
```

LinkedList:

```
how add?
using nodes

A -> B -> C
```

Different implementation.

---

# 4. List Interface

Code:

```java
public interface List<E>
extends Collection<E>{


    E get(int index);


    void add(int index,E value);


}
```

List adds:

"Elements have positions"

Example:

```
0 1 2

A B C
```

You can do:

```java
list.get(1)
```

---

## ArrayList Implementation

Actual class:

```java
public class ArrayList<E>
implements List<E>{


    Object[] elementData;


    public boolean add(E e){

        elementData[size++] = e;

    }


    public E get(int index){

        return elementData[index];

    }

}
```

ArrayList says:

"I will implement List using array"

---

# 5. Queue Interface

Code:

```java
public interface Queue<E>
extends Collection<E>{


    boolean offer(E e);


    E poll();


    E peek();

}
```

Queue adds FIFO behavior.

Example:

```
A B C

remove A first
```

Queue does not care HOW.

It only says:

"I need offer/poll/peek"

---

# 6. Deque Interface

Code:

```java
public interface Deque<E>
extends Queue<E>{


    void addFirst(E e);


    void addLast(E e);


    E removeFirst();


    E removeLast();


}
```

Deque means:

Double ended queue.

Queue:

```
Front

A B C

remove only here
```

Deque:

```
Front              Rear

A B C D

remove        remove
add           add
```

---

# 7. ArrayDeque Implementation

Now the real class.

Code:

```java
public class ArrayDeque<E>
extends AbstractCollection<E>
implements Deque<E>{


    transient Object[] elements;


    int head;


    int tail;


}
```

Now understand.

---

## implements Deque

Means:

Deque says:

"I need these methods"

Example:

Deque:

```java
interface Deque{


    void addFirst(E e);


    void addLast(E e);


}
```

ArrayDeque provides:

```java
class ArrayDeque
implements Deque{


    public void addFirst(E e){

        // circular array logic

    }


    public void addLast(E e){

        // circular array logic

    }

}
```

---

# 8. Then where does size(), contains() come from?

Good question.

ArrayDeque does:

```java
extends AbstractCollection
```

not directly Collection.

Hierarchy:

```
Collection Interface

        |
        |
AbstractCollection Class

        |
        |
ArrayDeque Class
```

AbstractCollection provides common implementation.

Example:

AbstractCollection:

```java
public abstract class AbstractCollection<E>
implements Collection<E>{


    public boolean isEmpty(){

        return size()==0;

    }


    public boolean contains(Object o){

        Iterator<E> i=iterator();

        while(i.hasNext()){

        }

    }

}
```

So ArrayDeque gets:

```
size()
isEmpty()
contains()
toString()
```

from AbstractCollection.

---

# 9. Actual ArrayDeque Structure

Simplified:

```java
public class ArrayDeque<E>
extends AbstractCollection<E>
implements Deque<E>{


    Object[] elements;


    int head;


    int tail;



    public void addFirst(E e){

        head--;

        elements[head]=e;

    }



    public void addLast(E e){

        elements[tail]=e;

        tail++;

    }



    public E removeFirst(){

        E value=(E)elements[head];

        head++;

        return value;

    }

}
```

The class only implements Deque-specific behavior.

Common collection behavior comes from AbstractCollection.

---

# 10. Full Flow of Method

When you write:

```java
Deque<String> d =
new ArrayDeque<>();

d.addLast("A");
```

What happens?

Reference type:

```
Deque
```

Method available:

```
addLast()
```

Runtime object:

```
ArrayDeque
```

Java calls:

```
ArrayDeque.addLast()
```

Inside:

```
elements[tail]="A"
tail++
```

---

# 11. Why Java Designed Like This?

Without interfaces:

Every class duplicates code.

Bad:

```
ArrayList

add()
remove()
size()


LinkedList

add()
remove()
size()


ArrayDeque

add()
remove()
size()
```

Duplicate.

Instead:

```
Collection
    |
common rules


AbstractCollection
    |
common implementation


ArrayDeque
    |
specific implementation
```

Reuse.

---

# 12. Final Picture

```
                 Iterable
                 (Interface)
                     |
                     |
              Collection
              (Interface)
                     |
          AbstractCollection
              (Abstract Class)
                     |
              ArrayDeque
                 (Class)


Queue
(Interface)
    |
Deque
(Interface)
    |
ArrayDeque
(Class implements)
```

So ArrayDeque is:

* **Class**
* Implements **Deque interface**
* Deque extends **Queue interface**
* Queue extends **Collection interface**
* Collection extends **Iterable interface**
* Gets common code from **AbstractCollection class**

This is the complete relationship.
