# Comparable vs Comparator — Complete Deep Dive

## 1. Why This Concept Matters

Comparable and Comparator are Java's two mechanisms for defining object ordering. They power sorting in Collections, TreeSet, TreeMap, and stream operations. Understanding when to use each, their contract requirements, and implementation differences is essential. In production, choosing correctly determines whether your objects can be sorted, used in sorted collections, and compared consistently. Interviewers test this because it reveals your understanding of interfaces, generics, and API design.

Misunderstanding Comparable/Comparator causes:
- `ClassCastException` at runtime when putting objects in TreeSet/TreeMap
- Inconsistent ordering between equals and compareTo breaking contracts
- Wrong choice making objects un-sortable in certain contexts

## 2. Basic Meaning

**Comparable**: interface defined ON the class itself. Provides one natural ordering.
**Comparator**: external class/lambda defining alternative orderings. Multiple possible.

**Key vocabulary:**
- **Natural ordering**: ordering defined by `Comparable.compareTo()`
- **External ordering**: ordering defined by `Comparator`
- **`compareTo()`**: returns negative if less, 0 if equal, positive if greater
- **`compare()`**: same semantics, takes two arguments
- **Consistent with equals**: if `compareTo()==0`, objects should be `equals()`
- **`Comparator.nullsFirst/Last`**: null-safe comparison
- **`Comparator.reverseOrder()`**: reverse natural order
- **`Comparator.thenComparing()`**: chain multiple criteria

## 3. Real Code / Real Example

```java
import java.util.*;
import java.util.stream.Collectors;

// === COMPARABLE: natural ordering defined in class ===
class Employee implements Comparable<Employee> {
    int id;
    String name;
    int salary;
    Employee(int id, String n, int s) { this.id = id; this.name = n; this.salary = s; }

    // Natural order: by salary descending
    @Override
    public int compareTo(Employee other) {
        return Integer.compare(other.salary, this.salary); // descending
    }

    @Override
    public String toString() { return name + "($" + salary + ")"; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee e = (Employee) o;
        return id == e.id;
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
}

// === COMPARATOR: external ordering ===
class NameComparator implements Comparator<Employee> {
    @Override
    public int compare(Employee a, Employee b) {
        return a.name.compareTo(b.name); // alphabetical by name
    }
}

public class ComparableDemo {
    public static void main(String[] args) {
        List<Employee> employees = List.of(
            new Employee(1, "Alice", 100_000),
            new Employee(2, "Bob", 80_000),
            new Employee(3, "Charlie", 120_000),
            new Employee(4, "Diana", 80_000)
        );

        // === SORT BY NATURAL ORDER (Comparable) ===
        List<Employee> bySalary = new ArrayList<>(employees);
        bySalary.sort(null); // null = natural order
        System.out.println("By salary (natural): " + bySalary);
        // [Charlie($120000), Alice($100000), Bob($80000), Diana($80000)]

        // === SORT BY COMPARATOR ===
        List<Employee> byName = new ArrayList<>(employees);
        byName.sort(new NameComparator());
        System.out.println("By name: " + byName);
        // [Alice($100000), Bob($80000), Charlie($120000), Diana($80000)]

        // === LAMBDA COMPARATOR ===
        List<Employee> byId = new ArrayList<>(employees);
        byId.sort(Comparator.comparingInt(e -> e.id));
        System.out.println("By ID: " + byId);

        // === CHAINED COMPARATORS ===
        List<Employee> bySalaryThenName = new ArrayList<>(employees);
        bySalaryThenName.sort(
            Comparator.comparingInt((Employee e) -> -e.salary) // salary desc
                      .thenComparing(e -> e.name)          // name asc
        );
        System.out.println("Salary desc, name asc: " + bySalaryThenName);

        // === NULLS FIRST ===
        List<String> list = new ArrayList<>(Arrays.asList("C", null, "A", "B", null));
        list.sort(Comparator.nullsFirst(String::compareTo));
        System.out.println("Nulls first: " + list);
        // [null, null, A, B, C]

        // === REVERSE ORDER ===
        List<Integer> nums = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5));
        nums.sort(Comparator.reverseOrder());
        System.out.println("Reverse: " + nums); // [5, 4, 3, 1, 1]

        // === TREESET WITH COMPARATOR ===
        Set<Employee> sortedSet = new TreeSet<>(Comparator.comparingInt(e -> -e.salary));
        sortedSet.addAll(employees);
        System.out.println("TreeSet by salary: " + sortedSet);

        // === STREAM SORTING ===
        List<Employee> streamSorted = employees.stream()
            .sorted(Comparator.comparing(e -> e.name))
            .collect(Collectors.toList());
        System.out.println("Stream sorted: " + streamSorted);
    }
}
```

Expected output:
```
By salary (natural): [Charlie($120000), Alice($100000), Bob($80000), Diana($80000)]
By name: [Alice($100000), Bob($80000), Charlie($120000), Diana($80000)]
By ID: [Alice($100000), Bob($80000), Charlie($120000), Diana($80000)]
Salary desc, name asc: [Charlie($120000), Alice($100000), Bob($80000), Diana($80000)]
Nulls first: [null, null, A, B, C]
Reverse: [5, 4, 3, 1, 1]
TreeSet by salary: [Charlie($120000), Alice($100000), Bob($80000), Diana($80000)]
Stream sorted: [Alice($100000), Bob($80000), Charlie($120000), Diana($80000)]
```

## 4. What Happens Internally

**Comparable dispatch:**
When `Collections.sort(list)` is called on objects implementing Comparable:
1. Check if list elements implement Comparable
2. Cast to Comparable
3. Call `compareTo()` during sort algorithm
4. Sort algorithm (TimSort in Java 7+) uses compareTo for ordering

**Comparator dispatch:**
When `list.sort(comparator)` is called:
1. Use provided Comparator
2. Call `comparator.compare(a, b)` during sort
3. Sort algorithm uses comparator for all comparisons

**Treeset/Treemap usage:**
```java
TreeSet<Employee> set = new TreeSet<>(); // uses natural ordering (Comparable)
TreeSet<Employee> set2 = new TreeSet<>(new NameComparator()); // uses comparator
```
TreeMap/Set calls `compareTo()` or `compare()` to place entries in Red-Black tree.

**Sort algorithm (TimSort):**
Java 7+ uses TimSort (hybrid merge-insertion sort). Calls compare ~O(n log n) times. Each comparison delegates to `compareTo()` or `compare()`.

## 5. Tricky Interview Cases

**Case 1 — compareTo broken: not antisymmetric**
```java
class Bad implements Comparable<Bad> {
    int val;
    Bad(int v) { val = v; }
    @Override public int compareTo(Bad o) { return Integer.compare(o.val, this.val); } // reversed
}
Bad a = new Bad(1), b = new Bad(2);
System.out.println(a.compareTo(b)); // 1 (a > b in this reversed impl)
System.out.println(b.compareTo(a)); // 1 (b > a too!)
System.out.println(a.compareTo(a)); // 0 (same)
```
Output: Both return positive — violates antisymmetry.
Explanation: `compareTo` should return negative if a<b, positive if a>b. This returns positive both ways. Sort treats them as equal — unstable sort.

**Case 2 — Comparator inconsistent with equals**
```java
class Person {
    String name; int age;
    Person(String n, int a) { name = n; age = a; }
    @Override public boolean equals(Object o) {
        return (o instanceof Person) && name.equals(((Person)o).name) && age == ((Person)o).age;
    }
    @Override public int hashCode() { return Objects.hash(name, age); }
}
Set<Person> set = new TreeSet<>(Comparator.comparingInt(p -> p.age));
set.add(new Person("Alice", 30));
set.add(new Person("Bob", 30)); // same age — comparator returns 0
set.add(new Person("Charlie", 30)); // ALSO returns 0
System.out.println(set.size()); // 1 — Charlie never added!
```
Output: `1` — only one person stored.
Explanation: TreeSet uses comparator for uniqueness, not equals. Comparator returns 0 for same age → considered duplicate even if names differ.

**Case 3 — null handling in Comparator**
```java
List<String> list = Arrays.asList("C", null, "A");
// list.sort(Comparator.naturalOrder()); // NPE on null!
list.sort(Comparator.nullsFirst(String::compareTo)); // safe
System.out.println(list); // [null, A, C]
```
Output: NPE without nullsFirst.
Explanation: `Comparator.naturalOrder()` doesn't handle null. `nullsFirst` places null first without NPE.

**Case 4 — reverseOrder vs reversed()**
```java
Comparator<String> comp = String::compareTo;
Comparator<String> rev = comp.reversed();
Comparator<String> rev2 = Comparator.reverseOrder(); // same for natural order
System.out.println(rev.compare("A", "B")); // positive (B before A)
```
Output: Both reverse.

**Case 5 — multiple sorting criteria**
```java
List<Employee> list = ...;
list.sort(Comparator
    .comparing(Employee::getSalary) // primary
    .thenComparing(Employee::getName) // secondary
    .thenComparingInt(Employee::getId) // tertiary
);
```
Output: Multi-level sort.

## 6. Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Broken compareTo (not antisymmetric) | Unstable sort, infinite loop in TreeSet | Follow contract: neg(a,b) = -neg(b,a) |
| Comparator inconsistent with equals | TreeSet loses "equal" objects | Ensure compare==0 iff equals |
| `compareTo` and equals disagree | HashSet + TreeSet behave differently | Keep them consistent |
| `sort(null)` on non-Comparable | ClassCastException | Use Comparator instead |
| Using `==` in compareTo | Wrong for objects | Use `compareTo()` or field comparison |
| Not handling nulls in Comparator | NPE at runtime | Use `nullsFirst`/`nullsLast` or filter nulls |

## 7. Production Usage

**Spring Data JPA sorting:**
```java
// Spring Data uses JpaSort.unsafe() for native SQL ORDER BY
Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
List<User> users = userRepo.findAll(pageable).getContent();
```

**Multi-field database sorting:**
```java
// Composite comparator for complex objects
Comparator<Order> orderComp = Comparator
    .comparing(Order::getPriority).reversed()
    .thenComparing(Order::getCreatedAt).reversed()
    .thenComparing(Order::getId);
orders.sort(orderComp);
```

**Streams and Collectors:**
```java
List<Employee> top5 = employees.stream()
    .sorted(Comparator.comparing(Employee::getSalary).reversed())
    .limit(5)
    .toList();
```

## 8. Advanced Details

- **`Comparable` vs `Comparator` contract:** Both require antisymmetry: `sgn(compare(a,b)) == -sgn(compare(b,a))`. Transitivity: if `a>b` and `b>c`, then `a>c`.
- **`Comparator` composition:** `thenComparing()`, `thenComparingInt()`, `thenComparingDouble()`. `comparing()` for single key.
- **`Collator` (locale-sensitive):** `Collator.getInstance(locale)` for string comparison respecting locale rules.
- **`ComparisonChain` (Guava):** `ComparisonChain.start().compare(a, b).compare(c, d).result()` — short-circuiting multi-field comparison.
- **`naturalOrder()` vs `reverseOrder()`:** `Comparator.naturalOrder()` returns Comparable's natural ordering. `Comparator.reverseOrder()` inverts it.

## 9. Interview Questions And Answers

### Beginner
Q: What is the difference between Comparable and Comparator?
A: Comparable is defined in the class (natural ordering, one possible). Comparator is external (alternative orderings, many possible). `Comparable` has `compareTo(T o)` (compare this with other). `Comparator` has `compare(T a, T b)` (compare two given objects).

### Intermediate
Q: You have a `List<String>`. Which sort methods are available? Show code examples.
A:
```java
// 1. Natural order (String implements Comparable)
list.sort(null); // null = natural order
Collections.sort(list); // equivalent

// 2. Reverse order
list.sort(Comparator.reverseOrder());

// 3. Custom comparator (lambda)
list.sort((a, b) -> a.length() - b.length()); // by length

// 4. Comparator methods
list.sort(Comparator.comparingInt(String::length));
```

### Senior
Q: A `TreeSet<Person>` uses `Comparator.comparingInt(Person::getAge)`. Two people with same age are considered equal. You need both age and name for uniqueness. How do you fix?
A: Chain comparators:
```java
Comparator<Person> byAgeThenName = Comparator
    .comparingInt(Person::getAge)
    .thenComparing(Person::getName);

TreeSet<Person> set = new TreeSet<>(byAgeThenName);
```
Now two people with same age and name are duplicates; different names with same age are distinct.

### Tricky
Q: You sort `List<Employee>` with `Comparator.comparing(Employee::getSalary)`. Some employees have salary 0 (e.g., interns). Sorting is unstable — employees with same salary appear in different orders. How do you make it stable? Do you need to modify Employee?
A: No need to modify Employee. Add secondary comparator:
```java
list.sort(Comparator
    .comparing(Employee::getSalary)
    .thenComparing(Employee::getId) // secondary key ensures stability
);
```
Or: `Collections.sort()` and `List.sort()` are ALREADY stable (TimSort in Java 7+). If stability broken, comparator is inconsistent (returns 0 for unequal objects).

## 10. Final 30-Second Answer

`Comparable`: class defines its own natural order (`compareTo`). ONE ordering per class. `Comparator`: external, multiple possible (`compare(a,b)`). Use `Comparable` for natural ordering. Use `Comparator` for alternative sorts (lambda, method ref). **Comparator must be consistent with equals** (compare==0 iff equals), else TreeSet loses objects. Chain with `thenComparing()`. `nullsFirst`/`nullsLast` for nulls. `reverseOrder()` / `.reversed()` for descending.