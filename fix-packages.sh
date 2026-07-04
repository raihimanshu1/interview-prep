#!/bin/bash

# Find all Java files and fix their package declarations
find src/main/java -name "*.java" -type f | while read -r file; do
    # Get the directory relative to src/main/java
    dir=$(dirname "$file")
    rel_path="${dir#src/main/java/}"
    
    # Convert path to package name (replace / with .)
    package_name=$(echo "$rel_path" | sed 's/\//./g')
    
    # Check if file has a package declaration
    if head -n 1 "$file" | grep -q "^package"; then
        # Replace the package declaration
        sed -i "1s/^package .*/package $package_name;/" "$file"
        echo "Updated: $file -> package $package_name;"
    else
        # Insert package declaration before first line (usually imports or class)
        # Check if first line is an import or class/interface
        first_line=$(head -n 1 "$file")
        if [[ "$first_line" == import* || "$first_line" == public* || "$first_line" == class* || "$first_line" == interface* ]]; then
            sed -i "1i package $package_name;\n" "$file"
            echo "Added: $file -> package $package_name;"
        else
            # For files with comments at the top, find the right place
            # Insert after the last comment block or before first non-comment/non-blank line
            awk -v pkg="package $package_name;" '
                NR==1 && /^\/\// { in_comment=1; lines[NR]=$0; next }
                NR==1 && /^\/\*/ { in_comment=1; lines[NR]=$0; next }
                in_comment && /^\*\// { lines[NR]=$0; in_comment=0; next }
                in_comment { lines[NR]=$0; next }
                !in_comment && /^[[:space:]]*$/ { lines[NR]=$0; next }
                !in_comment && /^package/ { lines[NR]=pkg; print pkg; lines[NR]=$0; next }
                !in_comment {
                    if (!printed_pkg) {
                        print pkg
                        printed_pkg=1
                    }
                    print
                    next
                }
                { lines[NR]=$0 }
                END {
                    for (i=1; i<=NR; i++) {
                        if (lines[i]) print lines[i]
                    }
                }
            ' "$file" > "${file}.tmp" && mv "${file}.tmp" "$file"
            echo "Inserted: $file -> package $package_name;"
        fi
    fi
done
