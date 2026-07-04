#!/usr/bin/env python3
import os
import re

def get_package_from_path(filepath):
    """Extract package name from directory path"""
    rel_path = filepath.replace('src/main/java/', '')
    rel_path = os.path.dirname(rel_path)
    package = rel_path.replace('/', '.')
    return package

def fix_package_name(package):
    """Fix package name to be a valid Java identifier"""
    parts = package.split('.')
    fixed_parts = []
    
    for part in parts:
        if part == 'com':
            fixed_parts.append(part)
            continue
            
        # Remove hyphens and capitalize next letter
        part = re.sub(r'-([a-z])', lambda m: m.group(1).upper(), part)
        fixed_parts.append(part)
    
    return '.'.join(fixed_parts)

def fix_package_in_file(filepath):
    """Fix package declaration properly"""
    try:
        with open(filepath, 'r') as f:
            lines = f.readlines()
        
        # Get correct package name
        package_name = get_package_from_path(filepath)
        package_name = fix_package_name(package_name)
        package_line = f'package {package_name};\n'
        
        # Find if there's already a package declaration
        package_idx = None
        for i, line in enumerate(lines):
            if line.strip().startswith('package '):
                package_idx = i
                break
        
        # Remove existing package declaration if found
        if package_idx is not None:
            lines.pop(package_idx)
        
        # Find where to insert (before any comments, imports, or class declarations)
        insert_idx = 0
        for i, line in enumerate(lines):
            stripped = line.strip()
            # Skip empty lines and comment blocks at the beginning
            if stripped and not stripped.startswith('//') and not stripped.startswith('/*') and not stripped.startswith('*'):
                insert_idx = i
                break
        
        # Insert package declaration
        lines.insert(insert_idx, package_line)
        
        # Write back
        with open(filepath, 'w') as f:
            f.writelines(lines)
        
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
    
    java_files.sort()
    
    print(f"Found {len(java_files)} Java files")
    
    success_count = 0
    for filepath in java_files:
        if fix_package_in_file(filepath):
            success_count += 1
    
    print(f"Successfully updated {success_count}/{len(java_files)} files")

if __name__ == '__main__':
    main()