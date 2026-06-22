#!/usr/bin/env python3
from pathlib import Path

BASE = Path("fundamentals")
HEADERS = [
"## 1. Why This Concept Matters",
"## 2. Basic Meaning",
"## 3. Real Code / Real Example",
"## 4. What Happens Internally",
"## 5. Tricky Interview Cases",
"## 6. Common Mistakes",
"## 7. Production Usage",
"## 8. Advanced Details",
"## 9. Interview Questions And Answers",
"## 10. Final 30-Second Answer",
]

for p in BASE.rglob("README.md"):
    text = p.read_text(errors="ignore")
    if all(h in text for h in HEADERS):
        continue
    if "PLACEHOLDER" in text:
        new = text.replace("PLACEHOLDER", "TODO: add content")
        p.write_text(new)
        print("fixed", p)
