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
    """Fix package name to be a valid Java identifier - NO HYPHENS ALLOWED"""
    parts = package.split('.')
    fixed_parts = []
    
    for part in parts:
        if part == 'com':
            fixed_parts.append(part)
            continue
        
        # Remove ALL hyphens and convert to camelCase by capitalizing after each hyphen
        # Also handle numeric parts properly
        # e.g., "lld-top-16" -> "lldTop16"
        # e.g., "parking-lot" -> "parkingLot"
        
        # Split by hyphen and capitalize each part (except first)
        subparts = part.split('-')
        if len(subparts) > 1:
            # First part stays as is, rest are capitalized
            fixed = subparts[0]
            for sub in subparts[1:]:
                if sub:
                    fixed += sub[0].upper() + sub[1:] if sub else ''
            part = fixed
        
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
        
        # Remove any existing package declarations
        new_lines = []
        found_package = False
        
        for i, line in enumerate(lines):
            stripped = line.strip()
            
            # Remove lines that are package declarations
            if stripped.startswith('package ') and not found_package:
                found_package = True
                continue
            
            new_lines.append(line)
        
        # Now find where to insert (after any initial comment block, before imports)
        final_lines = []
        
        # Check if file starts with a comment
        if new_lines and new_lines[0].strip().startswith('/*'):
            # Find the end of initial comment block
            for i, line in enumerate(new_lines):
                if '*/' in line:
                    # Insert package after the comment block
                    final_lines = new_lines[:i+1]
                    final_lines.append('\n')
                    final_lines.append(package_line)
                    final_lines.extend(new_lines[i+1:])
                    break
        else:
            # No initial comment, insert before first non-blank line
            insert_idx = 0
            for i, line in enumerate(new_lines):
                stripped = line.strip()
                if stripped and not stripped.startswith('//'):
                    insert_idx = i
                    break
            final_lines = new_lines[:insert_idx]
            final_lines.append(package_line)
            final_lines.extend(new_lines[insert_idx:])
        
        # Write back
        with open(filepath, 'w') as f:
            f.writelines(final_lines)
        
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