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
        # Handle numeric prefixes like "01-", "02-", etc.
        # Move the number to the end: "01-parking-lot" -> "parkingLot01"
        if re.match(r'^\d+-', part):
            # Extract number and rest
            match = re.match(r'^(\d+)-(.*)$', part)
            if match:
                number = match.group(1)
                rest = match.group(2)
                # Convert rest to camelCase
                rest_fixed = re.sub(r'-([a-z])', lambda m: m.group(1).upper(), rest)
                # Put number at the end: parkingLot01
                part = rest_fixed + number
        
        # Remove remaining hyphens and capitalize next letter
        part = re.sub(r'-([a-z])', lambda m: m.group(1).upper(), part)
        
        fixed_parts.append(part)
    
    return '.'.join(fixed_parts)

def fix_package_in_file(filepath):
    """Fix package declaration in a Java file"""
    try:
        with open(filepath, 'r') as f:
            lines = f.readlines()
        
        # Find existing package declaration
        package_line_idx = None
        for i, line in enumerate(lines):
            if line.strip().startswith('package '):
                package_line_idx = i
                break
        
        if package_line_idx is not None:
            # Get correct package name
            package_name = get_package_from_path(filepath)
            package_name = fix_package_name(package_name)
            new_package_line = f'package {package_name};\n'
            
            # Replace existing package declaration
            lines[package_line_idx] = new_package_line
            
            # Write back
            with open(filepath, 'w') as f:
                f.writelines(lines)
            
            print(f'Updated: {filepath} -> {package_name}')
            return True
        return False
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
    
    print(f"Found {len(java_files)} Java files\n")
    
    success_count = 0
    for filepath in java_files:
        if fix_package_in_file(filepath):
            success_count += 1
    
    print(f"\nSuccessfully processed {success_count} files")

if __name__ == '__main__':
    main()