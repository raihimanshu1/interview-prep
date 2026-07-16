# Java Package Structure Guidelines

## Purpose

This document defines the mandatory package naming and directory structure rules for all Java files in this project. Following these rules ensures:
- No compilation errors
- IntelliJ IDEA recognizes files as Java (not plain text)
- Proper package-to-directory mapping
- Clean, professional project structure

---

## Directory Naming Rules

### ✅ DO
- Use **all lowercase** letters only: `ratelimiter`, `parkinglot`, `cabboking`
- Use numbers if needed (but not as prefix): `top50coding` ✓
- Keep names short and meaningful: `ratelimiter` ✓

### ❌ DON'T
- Use hyphens: `rate-limiter` ✗
- Use underscores: `rate_limiter` ✗
- Start with numbers: `08-ratelimiter` ✗
- Use uppercase: `RateLimiter` ✗
- Use mixed case: `rateLimiter` ✗

---

## Package Declaration Rules

### Rule 1: Package Must Be First
The `package` declaration must be the **FIRST statement** in the file (before imports, class declarations, or comments).

✅ **Correct:**
```java
package com.lldtop16.ratelimiter;

import java.util.*;

public class RateLimiter {
    // class code
}
```

❌ **Wrong:**
```java
import java.util.*;

package com.lldtop16.ratelimiter;  // ERROR: package must be first

public class RateLimiter {
    // class code
}
```

❌ **Wrong:**
```java
/* Comment block */
package com.lldtop16.ratelimiter;  // ERROR: package must be before comments

import java.util.*;
```

### Rule 2: Package Names Must Be All Lowercase
Java package names use **all lowercase** letters only.

✅ **Correct:**
```java
package com.lldtop16.ratelimiter;
package com.companywisejavasolutions.ebay.solutions;
package com.patternwisejavasolutions.dynamicprogramming.core;
```

❌ **Wrong:**
```java
package com.lldtop16.RateLimiter;      // ERROR: uppercase
package com.lldtop16.rateLimiter;      // ERROR: mixed case
package com.lldtop16.rate_limiter;     // ERROR: underscore
```

### Rule 3: No Invalid Characters
Package names can only contain:
- Lowercase letters (`a-z`)
- Digits (`0-9`)
- Dots (`.`) as separators

❌ **Invalid:**
```java
package com.lld-top16.ratelimiter;  // ERROR: hyphen
package com.lldtop16.rate_limiter;  // ERROR: underscore
```

### Rule 4: Match Directory Structure
Package name MUST match the directory path from `src/main/java/`.

Example:
- File: `src/main/java/com/lldtop16/ratelimiter/RateLimiter.java`
- Package: `package com.lldtop16.ratelimiter;`

- File: `src/main/java/com/companywisejavasolutions/ebay/solutions/BinaryGap.java`
- Package: `package com.companywisejavasolutions.ebay.solutions;`

---

## Complete Naming Convention

### Directory Name → Package Name Conversion

| Directory Name | Package Name | Rule |
|----------------|--------------|------|
| `ratelimiter` | `ratelimiter` | All lowercase |
| `parkinglot` | `parkinglot` | All lowercase |
| `simpleapproach` | `simpleapproach` | All lowercase |
| `08-ratelimiter` | `ratelimiter` | Remove numeric prefix |
| `top-50-coding` | `top50coding` | Remove prefix, join, lowercase |

### Step-by-Step Conversion

1. **Remove numeric prefix** (if exists): `08-ratelimiter` → `ratelimiter`
2. **Remove hyphens**: `movie-ticket-booking` → `movieticketbooking`
3. **Convert to lowercase**: `MovieTicketBooking` → `movieticketbooking`
4. **Use as package name**: `package com.lldtop16.movieticketbooking;`

---

## Enforcement Rules

### Mandatory Checks Before Committing

1. **File starts with package declaration** (before imports)
2. **Package name is all lowercase** (no uppercase, no hyphens, no underscores)
3. **Package matches directory structure** (verify path alignment)
4. **No compilation errors** (run `javac` to verify)

### IntelliJ IDEA Indicators

If IntelliJ shows your file as **plain text** (not Java):
- ❌ Missing or incorrect package declaration
- ❌ Package declaration not at the top of file
- ❌ Package name doesn't match directory structure

**Fix:** Ensure package declaration is first, all lowercase, and matches path.

---

## Examples of Valid Packages

```java
// File: src/main/java/com/lldtop16/ratelimiter/RateLimiter.java
package com.lldtop16.ratelimiter;

// File: src/main/java/com/lldtop16/parkinglot/simpleapproach/ParkingLot.java
package com.lldtop16.parkinglot.simpleapproach;

// File: src/main/java/com/companywisejavasolutions/amazon/solutions/TwoSum.java
package com.companywisejavasolutions.amazon.solutions;

// File: src/main/java/com/patternwisejavasolutions/arrays/BinarySearch.java
package com.patternwisejavasolutions.arrays.binarysearch;

// File: src/main/java/com/fundamentals/corejava/streams/StreamExamples.java
package com.fundamentals.corejava.streams;
```

---

## Quick Reference

| What | Correct | Wrong |
|------|---------|-------|
| Package position | First line | After imports/comments |
| Package case | `ratelimiter` | `RateLimiter`, `rateLimiter` |
| Package separators | `.` (dots) | `-`, `_`, spaces |
| Directory names | `ratelimiter` | `rate-limiter`, `RateLimiter` |
| Numeric prefix | Remove it | `08-ratelimiter` |
| Compilation | `javac` passes | Errors on `package` line |

---

## Common Errors and Fixes

### Error 1: `';' expected` on package line
**Cause:** Package declaration is not the first statement
**Fix:** Move package to the very top of the file

### Error 2: `illegal start of type`
**Cause:** Package declaration is in the middle of the file (after imports/class)
**Fix:** Place package before all imports and class declarations

### Error 3: IntelliJ shows file as plain text
**Cause:** Missing package or incorrect package name
**Fix:** Add correct package declaration at top

### Error 4: `package does not exist`
**Cause:** Package name doesn't match directory structure
**Fix:** Verify package matches the folder hierarchy under `src/main/java/`

---

## File Structure Template

```
src/main/java/
└── com/
    ├── lldtop16/
    │   ├── ratelimiter/
    │   │   └── RateLimiter.java          → package com.lldtop16.ratelimiter;
    │   ├── parkinglot/
    │   │   └── simpleapproach/
    │   │       └── ParkingLot.java       → package com.lldtop16.parkinglot.simpleapproach;
    │   └── cabbooking/
    │       └── CabBooking.java            → package com.lldtop16.cabbooking;
    ├── companywisejavasolutions/
    │   ├── ebay/
    │   │   └── solutions/
    │   │       └── BinaryGap.java        → package com.companywisejavasolutions.ebay.solutions;
    │   └── amazon/
    │       └── solutions/
    │           └── TwoSum.java           → package com.companywisejavasolutions.amazon.solutions;
    └── patternwisejavasolutions/
        ├── arrays/
        │   └── binarysearch/
        │       └── BinarySearch.java     → package com.patternwisejavasolutions.arrays.binarysearch;
        └── dynamicprogramming/
            └── core/
                └── CoinChange.java       → package com.patternwisejavasolutions.dynamicprogramming.core;
```

---

## Automated Fix Script

Use `fix_packages_complete.py` to automatically fix all package declarations:

```bash
python3 fix_packages_complete.py
```

This script will:
1. Rename directories with hyphens/numeric prefixes
2. Convert all names to lowercase
3. Fix package declarations to be first line
4. Ensure packages match directory structure

---

## Verification Checklist

Before marking any Java file as complete, verify:

- [ ] Package declaration is the FIRST line (before imports)
- [ ] Package name is all lowercase
- [ ] Package name has no hyphens, underscores, or spaces
- [ ] Package name matches the directory path
- [ ] File compiles without errors: `javac <filename>`
- [ ] IntelliJ recognizes file as Java (not plain text)

---

## Summary

**Remember:** 
- Package = First line, all lowercase, matches directory
- Directory = All lowercase, no hyphens, no numeric prefix
- Compilation = Run `javac` to verify

When in doubt, run: `javac src/main/java/com/your/package/YourFile.java`