package com.patternwithdepth.phase00_foundation.basic_arrays;

/**
 * PROBLEM: Rotate Array
 *
 * DESCRIPTION:
 * Given an array, rotate the array to the right by k steps.
 *
 * Example 1:
 * Input: nums = [1,2,3,4,5,6,7], k = 3
 * Output: [5,6,7,1,2,3,4]
 * Explanation: Rotated right by 3 steps.
 *
 * Example 2:
 * Input: nums = [-1,-100,3,99], k = 2
 * Output: [3,99,-1,-100]
 * Explanation: Rotated right by 2 steps.
 *
 * CONSTRAINTS:
 * - 1 <= nums.length <= 10^5
 * - 0 <= k <= 10^5
 *
 * APPROACH:
 * 1. k = k % n (handle k > n)
 * 2. Reverse entire array
 * 3. Reverse first k elements
 * 4. Reverse remaining n-k elements
 * Time: O(n), Space: O(1)
 */



/*
*
* Perfect! This is actually the best way to understand rotation. Let's **shift one by one** instead of jumping directly to the answer.

We'll use:

```text
nums = [1,2,3,4,5,6,7]
```

---

# Right Rotation by 3

## Initial Array

```text
1 2 3 4 5 6 7
```

---

## Shift Right - 1 Time

Every element moves **one position to the right**.

```text
Before

1 2 3 4 5 6 7

Move:

7 -> first
1 -> second
2 -> third
3 -> fourth
4 -> fifth
5 -> sixth
6 -> seventh
```

Result:

```text
7 1 2 3 4 5 6
```

---

## Shift Right - 2 Times

Again, move everything one step right.

```text
Before

7 1 2 3 4 5 6

Move:

6 -> first
7 -> second
1 -> third
2 -> fourth
3 -> fifth
4 -> sixth
5 -> seventh
```

Result:

```text
6 7 1 2 3 4 5
```

---

## Shift Right - 3 Times

Again,

```text
Before

6 7 1 2 3 4 5

Move:

5 -> first
6 -> second
7 -> third
1 -> fourth
2 -> fifth
3 -> sixth
4 -> seventh
```

Final:

```text
5 6 7 1 2 3 4
```

✅ This is **Right Rotate by 3**.

---

# Left Rotation by 3

Start again from the original.

```text
1 2 3 4 5 6 7
```

---

## Shift Left - 1 Time

Move every element one position left.

```text
Before

1 2 3 4 5 6 7

Move:

2 -> first
3 -> second
4 -> third
5 -> fourth
6 -> fifth
7 -> sixth
1 -> last
```

Result:

```text
2 3 4 5 6 7 1
```

---

## Shift Left - 2 Times

```text
Before

2 3 4 5 6 7 1
```

Result:

```text
3 4 5 6 7 1 2
```

---

## Shift Left - 3 Times

```text
Before

3 4 5 6 7 1 2
```

Result:

```text
4 5 6 7 1 2 3
```

✅ This is **Left Rotate by 3**.

---

# Notice the Pattern

### Right Rotation

```text
Original

1 2 3 4 5 6 7

↓ Shift Right 1

7 1 2 3 4 5 6

↓ Shift Right 2

6 7 1 2 3 4 5

↓ Shift Right 3

5 6 7 1 2 3 4
```

The **last 3 elements** came to the front.

---

### Left Rotation

```text
Original

1 2 3 4 5 6 7

↓ Shift Left 1

2 3 4 5 6 7 1

↓ Shift Left 2

3 4 5 6 7 1 2

↓ Shift Left 3

4 5 6 7 1 2 3
```

The **first 3 elements** went to the back.

---

## Interview Trick ⭐

Whenever you hear:

* **Right Rotate by `k`** → Think: **"The last `k` elements will come to the front."**
* **Left Rotate by `k`** → Think: **"The first `k` elements will go to the back."**

Once this picture is clear in your mind, the reversal algorithm becomes much easier to derive and remember rather than memorize.

*
* RIGHT ROTATE by k

reverse(all)
reverse(first k)
reverse(rest)
LEFT ROTATE by k

reverse(first k)
reverse(rest)
reverse(all)
*
*
* */
import java.util.Arrays;

public class A12_RotateArray {

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5};
        int k = 2; // Number of steps to rotate to the right

        // 1. Handle edge cases where k is greater than the array size.
        // For example, if an array has length 5, rotating it 5 times brings it back to the start.
        // Rotating it 7 times is exactly the same as rotating it 2 times (7 % 5 = 2).
        k = k % nums.length;

        // Step 1: Reverse the entire array from start to finish.
        reverse(nums, 0, nums.length - 1);

        // Step 2: Reverse the first 'k' elements (from index 0 to k - 1).
        reverse(nums, 0, k - 1);

        // Step 3: Reverse the rest of the elements (from index k to the very end).
        reverse(nums, k, nums.length - 1);

        // Print out the final rotated array.
        System.out.println("Rotated Array: " + Arrays.toString(nums));
    }

    // A simple, reusable helper function to reverse a specific segment of an array in-place.
    // It uses the classic two-pointer approach.
    private static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            // Swap elements at 'start' and 'end' using a temporary holding variable
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;

            // Move the pointers closer to each other
            start++;
            end--;
        }
    }
}

