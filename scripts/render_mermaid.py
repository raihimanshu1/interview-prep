#!/usr/bin/env python3
"""
Extract Mermaid diagrams from markdown files, render them as PNG images,
and embed the images in the markdown files.

Usage: python3 scripts/render_mermaid.py [--puppeteer-config /path/to/config.json] [--skip-images] [--fix-only]
  --puppeteer-config: path to puppeteer config JSON
  --skip-images: skip re-rendering images (just fix embedding)
  --fix-only: only fix diagrams that failed (re-render)
"""

import os
import re
import sys
import json
import subprocess
from pathlib import Path

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PUPPETEER_CONFIG = os.path.join(BASE_DIR, "scripts", "puppeteer-config.json")
DEFAULT_PUPPETEER_CONFIG = {
    "executablePath": "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
}

# Save default puppeteer config
os.makedirs(os.path.dirname(PUPPETEER_CONFIG), exist_ok=True)
with open(PUPPETEER_CONFIG, 'w') as f:
    json.dump(DEFAULT_PUPPETEER_CONFIG, f, indent=2)

# Files to process
FILES = [
    "fundamentals/collection/COLLECTIONS-MASTER.md",
    "fundamentals/collection/arraylist/README.md",
    "fundamentals/collection/linkedlist/README.md",
    "fundamentals/collection/hashmap/README.md",
    "fundamentals/collection/linkedhashmap/README.md",
    "fundamentals/collection/hashset/README.md",
    "fundamentals/collection/treeset/README.md",
    "fundamentals/collection/priorityqueue/README.md",
    "fundamentals/collection/arraydeque/README.md",
    "fundamentals/collection/concurrenthashmap/README.md",
    "fundamentals/collection/copyonwritearraylist/README.md",
    "fundamentals/collection/blockingqueue/README.md",
    "fundamentals/collection/iterable-collection-iterator/README.md",
    "fundamentals/collection/special-collections-reference-types/README.md",
    "fundamentals/collection/remaining-collections/README.md",
]


def fix_mermaid_content(content):
    """
    Fix common Mermaid parsing issues:
    1. Replace <br/> with <br> (self-closing tags cause parse errors)
    2. Any other fixes needed
    """
    # Replace <br/> with <br>
    content = content.replace('<br/>', '<br>')
    return content


def find_mermaid_blocks(content):
    """Find all mermaid code blocks in a markdown file."""
    blocks = []
    lines = content.split('\n')
    in_mermaid = False
    mermaid_lines = []
    start_line = None
    diagram_count = 0

    for i, line in enumerate(lines):
        stripped = line.strip()
        if stripped == '```mermaid' and not in_mermaid:
            in_mermaid = True
            mermaid_lines = []
            start_line = i
        elif stripped == '```' and in_mermaid:
            in_mermaid = False
            block_content = '\n'.join(mermaid_lines)
            
            first_line = mermaid_lines[0].strip() if mermaid_lines else "diagram"
            diagram_type = "diagram"
            match = re.match(r'^(graph\s+\w+|classDiagram|sequenceDiagram|stateDiagram|flowchart\s+\w+|gantt|pie|erDiagram)', first_line)
            if match:
                diagram_type = match.group(1).replace(' ', '-')
            
            diagram_count += 1
            blocks.append({
                'start_line': start_line,
                'end_line': i,
                'diagram_index': diagram_count,
                'original_content': block_content,
                'fixed_content': fix_mermaid_content(block_content),
                'diagram_type': diagram_type,
                'first_line': first_line,
            })
        elif in_mermaid:
            mermaid_lines.append(line)

    return blocks


def get_diagram_name(block, md_file_stem):
    """Generate a meaningful filename for the diagram."""
    first_line = block['first_line']
    name = re.sub(r'[^a-zA-Z0-9\-]', '-', first_line)
    name = re.sub(r'-+', '-', name).strip('-')[:50]
    return f"{md_file_stem}_{name}_{block['diagram_index']}"


def extract_and_render(md_path, puppeteer_config_path, skip_images=False, fix_only=False):
    """Extract diagrams from a markdown file and render them."""
    abs_path = os.path.join(BASE_DIR, md_path)
    if not os.path.exists(abs_path):
        print(f"  ❌ File not found: {md_path}")
        return False

    with open(abs_path, 'r') as f:
        content = f.read()

    blocks = find_mermaid_blocks(content)
    if not blocks:
        print(f"  No mermaid blocks found in {md_path}")
        return True

    md_dir = os.path.dirname(abs_path)
    md_stem = os.path.splitext(os.path.basename(md_path))[0]
    diagrams_dir = os.path.join(md_dir, 'diagrams')
    os.makedirs(diagrams_dir, exist_ok=True)

    updated_content = content
    
    for block in reversed(blocks):
        diagram_name = get_diagram_name(block, md_stem)
        mmd_file = os.path.join(diagrams_dir, f"{diagram_name}.mmd")
        png_file = os.path.join(diagrams_dir, f"{diagram_name}.png")
        png_rel = f"./diagrams/{diagram_name}.png"

        # Check if image already rendered
        png_exists = os.path.exists(png_file)
        
        if fix_only and png_exists and not skip_images:
            print(f"    ℹ️  Already rendered, re-rendering {diagram_name}.png...")
        elif not skip_images and (not png_exists or fix_only):
            # Write the fixed .mmd file
            with open(mmd_file, 'w') as f:
                f.write(block['fixed_content'])
            
            # Also check if original differs from fixed - if so, also update the markdown
            if block['original_content'] != block['fixed_content']:
                print(f"    ℹ️  Fixed <br/> to <br> in {diagram_name}")
                # Find and replace in original content
                orig_block = f"```mermaid\n{block['original_content']}\n```"
                fixed_block = f"```mermaid\n{block['fixed_content']}\n```"
                if orig_block in updated_content:
                    updated_content = updated_content.replace(orig_block, fixed_block)
                    print(f"    ✅ Updated mermaid block in markdown to use <br> instead of <br/>")

            # Render with mmdc
            cmd = [
                'mmdc',
                '-i', mmd_file,
                '-o', png_file,
                '-p', puppeteer_config_path,
                '-b', 'white',
            ]
            print(f"    Rendering {diagram_name}.png...")
            result = subprocess.run(cmd, capture_output=True, text=True)
            if result.returncode != 0:
                print(f"    ❌ Failed to render {diagram_name}: {result.stderr[:200]}")
                continue
            if os.path.exists(png_file):
                print(f"    ✅ Rendered {png_file}")
            else:
                print(f"    ⚠️  No output file for {diagram_name}")
                continue
        elif skip_images:
            print(f"    ℹ️  Skipping render (--skip-images)")
        else:
            print(f"    ℹ️  Already rendered {diagram_name}.png")

        # Find the mermaid block in updated_content and embed image if needed
        lines = updated_content.split('\n')
        mermaid_block_start = None
        in_mermaid = False
        current_match = 0
        for i, line in enumerate(lines):
            if line.strip() == '```mermaid' and not in_mermaid:
                current_match += 1
                if current_match == block['diagram_index']:
                    mermaid_block_start = i
                    break

        if mermaid_block_start is not None:
            # Check if image already exists right before the mermaid block
            preceding_lines = lines[max(0, mermaid_block_start-3):mermaid_block_start]
            has_image = any(png_rel in line for line in preceding_lines)

            if has_image:
                print(f"    ℹ️  Image already embedded for block {block['diagram_index']}")
                continue

            # Add image tag before the mermaid block
            image_tag = f"![{diagram_name}]({png_rel})"
            lines.insert(mermaid_block_start, "")
            lines.insert(mermaid_block_start, image_tag)
            lines.insert(mermaid_block_start, "")
            updated_content = '\n'.join(lines)
            print(f"    ✅ Embedded {png_rel} before mermaid block {block['diagram_index']}")

    # Write updated content
    with open(abs_path, 'w') as f:
        f.write(updated_content)

    return True


def main():
    puppeteer_config = PUPPETEER_CONFIG
    skip_images = False
    fix_only = False
    
    for arg in sys.argv[1:]:
        if arg.startswith('--puppeteer-config='):
            puppeteer_config = arg.split('=', 1)[1]
        elif arg == '--skip-images':
            skip_images = True
        elif arg == '--fix-only':
            fix_only = True

    print(f"Puppeteer config: {puppeteer_config}")
    print(f"Skip images: {skip_images}")
    print(f"Fix only: {fix_only}")
    print(f"Base dir: {BASE_DIR}")
    print()

    success_count = 0
    fail_count = 0
    
    for md_file in FILES:
        print(f"Processing: {md_file}")
        if extract_and_render(md_file, puppeteer_config, skip_images, fix_only):
            success_count += 1
        else:
            fail_count += 1
        print()

    print(f"Done! Processed {success_count + fail_count} files ({success_count} OK, {fail_count} failed)")


if __name__ == '__main__':
    main()