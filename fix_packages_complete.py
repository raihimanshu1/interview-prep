#!/usr/bin/env python3
import os
import re

def get_package_from_path(filepath):
    """Extract package name from directory path"""
    # Get directory relative to src/main/java
    rel_path = filepath.replace('src/main/java/', '')
    # Remove filename
    rel_path = os.path.dirname(rel_path)
    # Convert path separators to dots
    package = rel_path.replace('/', '.')
    return package

def fix_all_hyphens_in_package(package):
    """Replace ALL hyphens with appropriate replacements for valid Java package names"""
    # General rule: remove hyphens and capitalize next letter (camelCase)
    # But for numeric prefixes like "07-", we'll remove the hyphen
    parts = package.split('.')
    fixed_parts = []
    for part in parts:
        # Remove hyphens and capitalize the letter after each hyphen
        # e.g., "07-cache-lru-lfu" -> "07CacheLruLfu"
        # Use regex to handle multiple hyphens
        fixed = re.sub(r'-([a-z])', lambda m: m.group(1).upper(), part)
        fixed_parts.append(fixed)
    return '.'.join(fixed_parts)

def fix_package_in_file(filepath):
    """Fix or add package declaration in a Java file"""
    try:
        with open(filepath, 'r') as f:
            lines = f.readlines()
        
        # Find existing package declaration
        package_line_idx = None
        for i, line in enumerate(lines):
            if line.strip().startswith('package '):
                package_line_idx = i
                break
        
        # Get correct package name from directory
        package_name = get_package_from_path(filepath)
        package_name = fix_all_hyphens_in_package(package_name)
        new_package_line = f'package {package_name};\n'
        
        # Check if package declaration exists
        if package_line_idx is not None:
            # Replace existing package declaration
            old_line = lines[package_line_idx]
            if 'package' in old_line:
                lines[package_line_idx] = new_package_line
                action = 'Updated'
            else:
                lines.insert(package_line_idx, new_package_line)
                action = 'Inserted'
        else:
            # No package declaration found, insert before first import or class
            insert_idx = 0
            
            # Skip comments and blank lines at the top
            for i, line in enumerate(lines):
                stripped = line.strip()
                if stripped and not stripped.startswith('//') and not stripped.startswith('/*') and not stripped.startswith('*'):
                    insert_idx = i
                    break
                elif stripped.startswith('import') or stripped.startswith('public') or stripped.startswith('class') or stripped.startswith('interface'):
                    insert_idx = i
                    break
            
            lines.insert(insert_idx, new_package_line)
            action = 'Added'
        
        # Write back
        with open(filepath, 'w') as f:
            f.writelines(lines)
        
        print(f'{action}: {filepath} -> {package_name}')
        return True
    except Exception as e:
        print(f'Error processing {filepath}: {e}')
        return False

def main():
    java_files = []
    
    # Find all Java files
    for root, dirs, files in os.walk('src/main/java'):
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    
    # Sort for consistent output
    java_files.sort()
    
    print(f"Found {len(java_files)} Java files\n")
    
    # Process each file
    success_count = 0
    for filepath in java_files:
        if fix_package_in_file(filepath):
            success_count += 1
    
    print(f"\nSuccessfully processed {success_count}/{len(java_files)} files")

if __name__ == '__main__':
    main()