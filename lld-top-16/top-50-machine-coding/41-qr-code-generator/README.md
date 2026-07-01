# 🔲 Problem 41: QR Code Generator

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Payment companies, Any tech company  
> **Est. Time**: 90 min | **Patterns**: Builder, Strategy, Matrix Operations

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Generate QR codes from data."

**What the interviewer tests**:
```
1. Can you encode data into QR format? (Reed-Solomon error correction)
2. Can you render the QR matrix? (Bit manipulation)
3. Can you handle different encoding modes? (Numeric, alphanumeric, byte)
4. Can you optimize for size? (Version selection)
```

### Step 2: The "Aha!" Moment

The key insight: **QR codes are just 2D barcodes with error correction.**

```
QR CODE STRUCTURE:
  [Finder Pattern] [Finder Pattern] [Finder Pattern]
       [Alignment]              [Timing]
  [Format Info]    [Data]    [Format Info]
       [Alignment]              [Timing]
  [Finder Pattern] [Finder Pattern] [Finder Pattern]

FINDER PATTERN: 7x7 black/white squares (helps scanners find corners)
DATA: Encoded with error correction (Reed-Solomon)
FORMAT: Error correction level + mask pattern

VERSIONS:
  Version 1: 21x21 modules (small QR)
  Version 2: 25x25 modules
  ...
  Version 40: 177x177 modules (largest)

More data → larger version.
```

### Step 3: How error correction works?

```
ERROR CORRECTION LEVELS:
  L (Low):  ~7% error correction
  M (Medium): ~15%
  Q (Quartile): ~25%
  H (High): ~30%

ENCODING:
  Original: "HELLO"
  With EC: "HELLO" + [error_correction_bytes]

If scanner reads "HEL_O" (one pixel damaged):
  Reed-Solomon can recover original "HELLO"

This is the SAME technology used in:
  - CDs (scratch recovery)
  - Satellite communications
  - Barcodes
```

---

## 💻 Core Implementation

```java
package com.qr;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * INTUITION: QRGenerator creates QR codes.
 * 
 * Flow:
 * 1. Analyze data (length, type)
 * 2. Choose version and encoding mode
 * 3. Encode data with error correction
 * 4. Place modules in matrix
 * 5. Apply mask
 * 6. Render as image
 */
public class QRGenerator {
    
    // Error correction levels
    public enum ECC { L, M, Q, H }
    
    // Encoding modes
    private enum Mode { NUMERIC, ALPHANUMERIC, BYTE, KANJI }

    /**
     * INTUITION: Generate QR code as 2D boolean array.
     * 
     * @param data Data to encode
     * @param ecc Error correction level
     * @return 2D array (true = black, false = white)
     */
    public boolean[][] generate(String data, ECC ecc) {
        // Step 1: Analyze data
        Mode mode = detectMode(data);
        int version = chooseVersion(data.length(), mode, ecc);
        
        // Step 2: Encode data
        BitList bits = encodeData(data, mode, version);
        
        // Step 3: Add error correction
        BitList ecBits = addErrorCorrection(bits, version, ecc);
        
        // Step 4: Create matrix
        int size = 21 + (version - 1) * 4;
        boolean[][] matrix = new boolean[size][size];
        
        // Step 5: Place finder patterns
        placeFinderPatterns(matrix);
        
        // Step 6: Place alignment patterns
        placeAlignmentPatterns(matrix, version);
        
        // Step 7: Place timing patterns
        placeTimingPatterns(matrix);
        
        // Step 8: Reserve format info area
        reserveFormatInfo(matrix);
        
        // Step 9: Place data bits
        placeData(matrix, ecBits);
        
        // Step 10: Apply mask
        applyMask(matrix, 0);  // Mask pattern 0
        
        // Step 11: Add format info
        addFormatInfo(matrix, ecc, 0);
        
        return matrix;
    }

    /**
     * INTUITION: Detect best encoding mode.
     */
    private Mode detectMode(String data) {
        if (data.matches("\\d+")) {
            return Mode.NUMERIC;
        } else if (data.matches("[A-Za-z0-9 $%*+\\-./:]+")) {
            return Mode.ALPHANUMERIC;
        } else {
            return Mode.BYTE;
        }
    }

    /**
     * INTUITION: Choose QR version based on data length.
     * 
     * Version 1: max 17 bytes (with ECC L)
     * Version 2: max 32 bytes
     * Version 3: max 53 bytes
     * ...
     * Version 40: max 2953 bytes
     */
    private int chooseVersion(int dataLength, Mode mode, ECC ecc) {
        // Simplified: return version based on length
        if (dataLength <= 17) return 1;
        if (dataLength <= 32) return 2;
        if (dataLength <= 53) return 3;
        if (dataLength <= 78) return 4;
        // ... up to version 40
        return 10;  // Default for longer data
    }

    /**
     * INTUITION: Encode data into bit stream.
     * 
     * Mode indicator (4 bits):
     *   Numeric: 0001
     *   Alphanumeric: 0010
     *   Byte: 0100
     * 
     * Character count (8-16 bits depending on version)
     * 
     * Data (encoded per mode)
     * 
     * Terminator (up to 4 bits of zeros)
     */
    private BitList encodeData(String data, Mode mode, int version) {
        BitList bits = new BitList();
        
        // Mode indicator
        int modeBits;
        switch (mode) {
            case NUMERIC: modeBits = 0x1; break;
            case ALPHANUMERIC: modeBits = 0x2; break;
            case BYTE: modeBits = 0x4; break;
            default: modeBits = 0x4;
        }
        bits.add(modeBits, 4);
        
        // Character count
        int countBits = version <= 9 ? 8 : 16;
        bits.add(data.length(), countBits);
        
        // Encode data
        switch (mode) {
            case NUMERIC:
                encodeNumeric(bits, data);
                break;
            case ALPHANUMERIC:
                encodeAlphanumeric(bits, data);
                break;
            case BYTE:
                encodeByte(bits, data);
                break;
        }
        
        // Terminator
        int availableBits = getAvailableCodewords(version) * 8;
        int terminatorLength = Math.min(4, availableBits - bits.size());
        bits.add(0, terminatorLength);
        
        // Pad to byte boundary
        while (bits.size() % 8 != 0) {
            bits.add(false);
        }
        
        // Pad bytes if needed
        int padBytes = (availableBits - bits.size()) / 8;
        for (int i = 0; i < padBytes; i++) {
            byte padByte = (i % 2 == 0) ? (byte) 0xEC : (byte) 0x11;
            bits.add(padByte, 8);
        }
        
        return bits;
    }

    private void encodeNumeric(BitList bits, String data) {
        for (int i = 0; i < data.length(); i += 3) {
            int end = Math.min(i + 3, data.length());
            String chunk = data.substring(i, end);
            int value = Integer.parseInt(chunk);
            int bitsCount = chunk.length() == 3 ? 10 : (chunk.length() == 2 ? 7 : 4);
            bits.add(value, bitsCount);
        }
    }

    private void encodeAlphanumeric(BitList bits, String data) {
        String alphanum = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:";
        for (int i = 0; i < data.length(); i += 2) {
            if (i + 1 < data.length()) {
                int first = alphanum.indexOf(data.charAt(i));
                int second = alphanum.indexOf(data.charAt(i + 1));
                int value = first * 45 + second;
                bits.add(value, 11);
            } else {
                int value = alphanum.indexOf(data.charAt(i));
                bits.add(value, 6);
            }
        }
    }

    private void encodeByte(BitList bits, String data) {
        for (byte b : data.getBytes()) {
            bits.add(b, 8);
        }
    }

    private int getAvailableCodewords(int version) {
        // Simplified: return total codewords for version
        return 26 + (version - 1) * 2;  // Approximate
    }

    /**
     * INTUITION: Reed-Solomon error correction.
     * 
     * This is complex - in production, use a library.
     * Simplified here.
     */
    private BitList addErrorCorrection(BitList dataBits, int version, ECC ecc) {
        // Simplified: just return data bits
        // Real implementation would generate EC codewords using Reed-Solomon
        return dataBits;
    }

    /**
     * INTUITION: Place finder patterns (3 corners).
     * 
     * Finder pattern is 7x7:
     *   [B B B B B B B]
     *   [B W B B B W B]
     *   [B B B B B B B]
     *   [B B B B B B B]
     *   [B B B B B B B]
     *   [B W B B B W B]
     *   [B B B B B B B]
     */
    private void placeFinderPatterns(boolean[][] matrix) {
        // Top-left
        placeFinderPattern(matrix, 0, 0);
        // Top-right
        placeFinderPattern(matrix, matrix.length - 7, 0);
        // Bottom-left
        placeFinderPattern(matrix, 0, matrix.length - 7);
    }

    private void placeFinderPattern(boolean[][] matrix, int row, int col) {
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                boolean black = (r == 0 || r == 6 || c == 0 || c == 6 ||
                                (r >= 2 && r <= 4 && c >= 2 && c <= 4));
                matrix[row + r][col + c] = black;
            }
        }
    }

    private void placeAlignmentPatterns(boolean[][] matrix, int version) {
        if (version < 2) return;
        
        int pos = 6 + (version - 1) * 4;
        for (int i = 0; i < version - 1; i++) {
            placeAlignmentPattern(matrix, pos, i * (version - 1) + 6);
            placeAlignmentPattern(matrix, i * (version - 1) + 6, pos);
        }
    }

    private void placeAlignmentPattern(boolean[][] matrix, int row, int col) {
        for (int r = -2; r <= 2; r++) {
            for (int c = -2; c <= 2; c++) {
                int rIdx = row + r;
                int cIdx = col + c;
                if (rIdx >= 0 && rIdx < matrix.length && cIdx >= 0 && cIdx < matrix.length) {
                    matrix[rIdx][cIdx] = (Math.abs(r) == 2 || Math.abs(c) == 2 || (r == 0 && c == 0));
                }
            }
        }
    }

    private void placeTimingPatterns(boolean[][] matrix) {
        for (int i = 8; i < matrix.length - 8; i++) {
            matrix[6][i] = i % 2 == 0;
            matrix[i][6] = i % 2 == 0;
        }
    }

    private void reserveFormatInfo(boolean[][] matrix) {
        // Reserve format info around finder patterns
        for (int i = 0; i < 9; i++) {
            matrix[8][i] = false;  // Top horizontal
            matrix[i][8] = false;  // Left vertical
        }
    }

    /**
     * INTUITION: Place data bits in matrix.
     * 
     * QR code data flows in a zigzag pattern from bottom-right.
     */
    private void placeData(boolean[][] matrix, BitList data) {
        int size = matrix.length;
        int bitIndex = 0;
        boolean upward = true;
        
        for (int col = size - 1; col >= 1; col -= 2) {
            if (col == 6) col = 5;  // Skip timing column
            
            for (int row = upward ? size - 1 : 0; upward ? row >= 0 : row < size; upward ? row-- : row++) {
                for (int c = 0; c < 2; c++) {
                    int currentCol = col - c;
                    if (!matrix[row][currentCol]) {  // Not reserved
                        if (bitIndex < data.size()) {
                            matrix[row][currentCol] = data.get(bitIndex++);
                        }
                    }
                }
            }
            upward = !upward;
        }
    }

    /**
     * INTUITION: Apply mask pattern.
     * 
     * Masking prevents large uniform areas (hard to scan).
     * XOR data with mask pattern.
     */
    private void applyMask(boolean[][] matrix, int maskPattern) {
        int size = matrix.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (isDataArea(row, col)) {
                    boolean mask = shouldInvert(maskPattern, row, col);
                    matrix[row][col] ^= mask;
                }
            }
        }
    }

    private boolean isDataArea(int row, int col) {
        // Simplified: not in finder patterns or timing
        return !(row < 9 && col < 9) && !(row < 9 && col >= matrix.length - 8) &&
               !(row >= matrix.length - 8 && col < 9);
    }

    private boolean shouldInvert(int pattern, int row, int col) {
        switch (pattern) {
            case 0: return (row + col) % 2 == 0;
            case 1: return row % 2 == 0;
            case 2: return col % 3 == 0;
            // ... more patterns
            default: return false;
        }
    }

    private void addFormatInfo(boolean[][] matrix, ECC ecc, int maskPattern) {
        // Simplified: would add 15 bits of format info
    }

    /**
     * INTUITION: Convert boolean matrix to image.
     */
    public BufferedImage toImage(boolean[][] matrix, int scale) {
        int size = matrix.length;
        BufferedImage image = new BufferedImage(size * scale, size * scale, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        g.setColor(Color.BLACK);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (matrix[row][col]) {
                    g.fillRect(col * scale, row * scale, scale, scale);
                }
            }
        }
        
        g.dispose();
        return image;
    }
}

/**
 * Bit list utility.
 */
class BitList {
    private final List<Boolean> bits = new ArrayList<>();
    
    void add(boolean bit) {
        bits.add(bit);
    }
    
    void add(int value, int numBits) {
        for (int i = numBits - 1; i >= 0; i--) {
            bits.add((value & (1 << i)) != 0);
        }
    }
    
    int size() {
        return bits.size();
    }
    
    boolean get(int index) {
        return bits.get(index);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to generate QR code without libraries?"
> "Implement Reed-Solomon encoder manually. Use Galois field arithmetic. Generate generator polynomial. Divide message by polynomial to get ECC."

### Q2: "How to handle Unicode (Chinese, emoji)?"
> "Use UTF-8 byte encoding. Switch to Kanji mode for Japanese. More error correction needed."

### Q3: "How to make QR code scannable from far away?"
> "Print larger modules (higher scale). Add quiet zone (white border). Use high contrast."

### Q4: "How to embed logo in QR code?"
> "Mask logo area (make it white). Increase error correction to H. Test scan with phone camera."