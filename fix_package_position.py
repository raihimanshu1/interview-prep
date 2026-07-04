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
    """Fix package declaration position in a Java file"""
    try:
        with open(filepath, 'r') as f:
            content = f.read()
        
        # Get correct package name
        package_name = get_package_from_path(filepath)
        package_name = fix_package_name(package_name)
        package_line = f'package {package_name};\n'
        
        # Check if package declaration exists
        if 'package ' in content:
            # Remove existing package line (even if inside comments)
            lines = content.split('\n')
            new_lines = []
            has_package = False
            
            for i, line in enumerate(lines):
                stripped = line.strip()
                if stripped.startswith('package ') and not has_package:
                    has_package = True
                    continue  # Skip this line
                new_lines.append(line)
            
            content = '\n'.join(new_lines)
        
        # Now insert package declaration at the correct position
        # It should be before any imports or class/interface declarations
        lines = content.split('\n')
        insert_idx = 0
        
        # Find the right insertion point (before imports, class, interface)
        found_content = False
        for i, line in enumerate(lines):
            stripped = line.strip()
            # Skip blank lines and comments at the top
            if not found_content:
                if stripped and not stripped.startswith('//') and not stripped.startswith('/*') and not stripped.startswith('*'):
                    insert_idx = i
                    found_content = True
                    break
        
        # Insert package declaration
        lines.insert(insert_idx, package_line)
        content = '\n'.join(lines)
        
        # Write back
        with open(filepath, 'w') as f:
            f.write(content)
        
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