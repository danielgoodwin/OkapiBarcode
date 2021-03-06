/*
 * Copyright 2014 Robin Stuart
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.org.okapibarcode.backend;

import java.io.UnsupportedEncodingException;

/**
 * Implements Data Matrix ECC 200 bar code symbology
 * According to ISO/IEC 16022:2006
 * 
 * @author Robin Stuart <rstuart114@gmail.com>
 * @version 0.2
 */
public class DataMatrix extends Symbol {

    static int[] c40_shift = {
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 
        3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 
        3, 3, 3, 3, 3, 3, 3, 3
    };

    static int[] c40_value = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 
        20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 3, 0, 1, 2, 3, 4, 5, 6, 
        7, 8, 9, 10, 11, 12, 13, 14, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16, 
        17, 18, 19, 20, 21, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 
        27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 22, 23, 24, 25, 26, 
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 
        20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31
    };

    static int[] text_shift = {
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 
        3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 
        3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 3, 3, 3, 3, 3
    };

    static int[] text_value = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 
        20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 3, 0, 1, 2, 3, 4, 5, 6, 
        7, 8, 9, 10, 11, 12, 13, 14, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16, 
        17, 18, 19, 20, 21, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 
        16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 22, 23, 24, 25, 26, 0, 14, 
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 
        33, 34, 35, 36, 37, 38, 39, 27, 28, 29, 30, 31
    };

    static int[] intsymbol = {
        0, 1, 3, 5, 7, 8, 10, 12, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
        25, 26, 27, 28, 29, 2, 4, 6, 9, 11, 14
    };

    static int[] matrixH = {
        10, 12, 8, 14, 8, 16, 12, 18, 20, 12, 22, 16, 24, 26, 16, 32, 36, 40, 
        44, 48, 52, 64, 72, 80, 88, 96, 104, 120, 132, 144
    };

    static int[] matrixW = {
        10, 12, 18, 14, 32, 16, 26, 18, 20, 36, 22, 36, 24, 26, 48, 32, 36, 40, 
        44, 48, 52, 64, 72, 80, 88, 96, 104, 120, 132, 144
    };

    static int[] matrixFH = {
        10, 12, 8, 14, 8, 16, 12, 18, 20, 12, 22, 16, 24, 26, 16, 16, 18, 20, 
        22, 24, 26, 16, 18, 20, 22, 24, 26, 20, 22, 24
    };

    static int[] matrixFW = {
        10, 12, 18, 14, 16, 16, 26, 18, 20, 18, 22, 18, 24, 26, 24, 16, 18, 20, 
        22, 24, 26, 16, 18, 20, 22, 24, 26, 20, 22, 24
    };

    static int[] matrixbytes = {
        3, 5, 5, 8, 10, 12, 16, 18, 22, 22, 30, 32, 36, 44, 49, 62, 86, 114, 
        144, 174, 204, 280, 368, 456, 576, 696, 816, 1050, 1304, 1558
    };

    static int[] matrixdatablock = {
        3, 5, 5, 8, 10, 12, 16, 18, 22, 22, 30, 32, 36, 44, 49, 62, 86, 114, 
        144, 174, 102, 140, 92, 114, 144, 174, 136, 175, 163, 156
    };

    static int[] matrixrsblock = {
        5, 7, 7, 10, 11, 12, 14, 14, 18, 18, 20, 24, 24, 28, 28, 36, 42, 48, 56, 
        68, 42, 56, 36, 48, 56, 68, 56, 68, 62, 62
    };

    private enum dm_mode {
        DM_ASCII, DM_C40, DM_TEXT, DM_X12, DM_EDIFACT, DM_BASE256
    }
    private int[] target = new int[2200];
    private int[] binary = new int[2200];
    private int binary_length;
    private dm_mode last_mode;
    private int[] places;
    private boolean isSquare;
    int[] inputData;

    public DataMatrix() {
        isSquare = true;
    }

    public void forceSquare(boolean input) {
        isSquare = input;
    }

    @Override
    public boolean encode() {
        int i, binlen, skew = 0;
        int symbolsize, optionsize, calcsize;
        int taillength;
        int H, W, FH, FW, datablock, bytes, rsblock;
        int x, y, NC, NR, v;
        int[] grid;
        String bin;
        byte[] inputBytes;

        try {
            inputBytes = content.getBytes("ISO8859_1");
        } catch (UnsupportedEncodingException e) {
            error_msg = "Invalid character in input data";
            return false;
        }

        inputData = new int[content.length()];
        for (i = 0; i < content.length(); i++) {
            inputData[i] = inputBytes[i] & 0xFF;
        }

        binlen = generateCodewords();

        if (binlen == 0) {
            error_msg = "Data too long to fit in symbol";
            return false;
        }
        
        if ((option2 >= 1) && (option2 <= 30)) {
            optionsize = intsymbol[option2 - 1];
        } else {
            optionsize = -1;
        }
        
        calcsize = 29;
        for (i = 29; i > -1; i--) {
            if (matrixbytes[i] >= binlen) {
                calcsize = i;
            }
        }

        if (isSquare) {
            // Force to use square symbol
            switch (calcsize) {
            case 2:
            case 4:
            case 6:
            case 9:
            case 11:
            case 14:
                calcsize++;
                break;
            default:
                break;
            }
        }

        symbolsize = optionsize;
        if (calcsize > optionsize) {
            symbolsize = calcsize;
            if (optionsize != -1) {
                /* flag an error */
                error_msg = "Data does not fit in selected symbol size";
                return false;
            }
        }

        H = matrixH[symbolsize];
        W = matrixW[symbolsize];
        FH = matrixFH[symbolsize];
        FW = matrixFW[symbolsize];
        bytes = matrixbytes[symbolsize];
        datablock = matrixdatablock[symbolsize];
        rsblock = matrixrsblock[symbolsize];

        taillength = bytes - binlen;

        if (taillength != 0) {
            addPadBits(binlen, taillength);
        }

        // ecc code
        if (symbolsize == 29) {
            skew = 1;
        }
        calculateErrorCorrection(bytes, datablock, rsblock, skew);
        NC = W - 2 * (W / FW);
        NR = H - 2 * (H / FH);
        places = new int[NC * NR];
        placeData(NR, NC);
        grid = new int[W * H];
        for (i = 0; i < (W * H); i++) {
            grid[i] = 0;
        }
        for (y = 0; y < H; y += FH) {
            for (x = 0; x < W; x++) {
                grid[y * W + x] = 1;
            }
            for (x = 0; x < W; x += 2) {
                grid[(y + FH - 1) * W + x] = 1;
            }
        }
        for (x = 0; x < W; x += FW) {
            for (y = 0; y < H; y++) {
                grid[y * W + x] = 1;
            }
            for (y = 0; y < H; y += 2) {
                grid[y * W + x + FW - 1] = 1;
            }
        }
        for (y = 0; y < NR; y++) {
            for (x = 0; x < NC; x++) {
                v = places[(NR - y - 1) * NC + x];
                if (v == 1 || (v > 7 && (target[(v >> 3) - 1] & (1 << (v & 7))) != 0)) {
                    grid[(1 + y + 2 * (y / (FH - 2))) * W + 1 + x + 2 * (x / (FW - 2))] = 1;
                }
            }
        }

        readable = "";
        pattern = new String[H];
        row_count = H;
        row_height = new int[H];
        for (y = H - 1; y >= 0; y--) {
            bin = "";
            for (x = 0; x < W; x++) {
                if (grid[W * y + x] == 1) {
                    bin += "1";
                } else {
                    bin += "0";
                }
            }
            pattern[(H - y) - 1] = bin2pat(bin);
            row_height[(H - y) - 1] = 1;
        }
        
        encodeInfo += "Grid Size: " + W + " X " + H + "\n";
        encodeInfo += "Data Codewords: " + datablock + "\n";
        encodeInfo += "ECC Codewords: " + rsblock + "\n";

        plotSymbol();
        return true;
    }

    private int generateCodewords() {
        /* Encodes data using ASCII, C40, Text, X12, EDIFACT or Base 256 modes as appropriate */
        /* Supports encoding FNC1 in supporting systems */

        int sp, tp, i;
        dm_mode current_mode, next_mode;
        int inputlen = content.length();
        int c40_p, text_p, x12_p, edifact_p;
        int[] c40_buffer = new int[6];
        int[] text_buffer = new int[6];
        int[] x12_buffer = new int[6];
        int[] edifact_buffer = new int[8];

        sp = 0;
        tp = 0;
        c40_p = 0;
        text_p = 0;
        x12_p = 0;
        edifact_p = 0;
        for (i = 0; i < 6; i++) {
            c40_buffer[i] = 0;
            text_buffer[i] = 0;
            x12_buffer[i] = 0;
            edifact_buffer[i] = 0;
        }
        edifact_buffer[6] = 0;
        edifact_buffer[7] = 0;
        binary_length = 0;

        /* step (a) */
        current_mode = dm_mode.DM_ASCII;
        next_mode = dm_mode.DM_ASCII;

        if(gs1) {
            target[tp] = 232; tp++;
            binary[binary_length] = ' ';
            binary_length++;
            if(debug) System.out.printf("FN1 ");
        } /* FNC1 */

        if(readerInit) {
            if(gs1) {
                error_msg = "Cannot encode in GS1 mode and Reader Initialisation at the same time";
                return 0;
            } else {
                target[tp] = 234; tp++; /* Reader Programming */
                binary[binary_length] = ' ';
                binary_length++;
                if(debug) System.out.printf("RP ");
            }
        }
        
        /* Check for Macro05/Macro06 */
        /* "[)>[RS]05[GS]...[RS][EOT]" -> CW 236 */
        /* "[)>[RS]06[GS]...[RS][EOT]" -> CW 237 */
        
        if (tp == 0 & sp == 0 && inputlen >= 9) {
            if (inputData[0] == '[' && inputData[1] == ')' && inputData[2] == '>'
                    && inputData[3] == '\u001e' && inputData[4] == '0'
                    && (inputData[5] == '5' || inputData[5] == '6')
                    && inputData[6] == '\u001d'
                    && inputData[inputlen - 2] == '\u001e'
                    && inputData[inputlen - 1] == '\u0004') {
                /* Output macro Codeword */
		if (inputData[5] == '5') {
			target[tp] = 236;
			if (debug) System.out.printf("Macro05 ");
		} else {
			target[tp] = 237;
			if (debug) System.out.printf("Macro06 ");
		}
		tp++;
		binary[binary_length] = ' ';
                binary_length++;
		/* Remove macro characters from input string */
		sp = 7;
		inputlen -= 2;
            }
        }
        
        while (sp < inputlen) {

            current_mode = next_mode;

            /* step (b) - ASCII encodation */
            if (current_mode == dm_mode.DM_ASCII) {
                next_mode = dm_mode.DM_ASCII;

                if (isTwoDigits(sp)) {
                    target[tp] = (10 * Character.getNumericValue(inputData[sp])) 
                            + Character.getNumericValue(inputData[sp + 1]) + 130;
                    if (debug) System.out.printf("N%d ", target[tp] - 130);
                    tp++;
                    binary[binary_length] = ' ';
                    binary_length++;
                    sp += 2;
                } else {
                    next_mode = lookAheadTest(sp, current_mode);

                    if (next_mode != dm_mode.DM_ASCII) {
                        switch (next_mode) {
                        case DM_C40:
                            target[tp] = 230;
                            tp++;
                            binary[binary_length] = ' ';
                            binary_length++;
                            if (debug) System.out.printf("C40 ");
                            break;
                        case DM_TEXT:
                            target[tp] = 239;
                            tp++;
                            binary[binary_length] = ' ';
                            binary_length++;
                            if (debug) System.out.printf("TEX ");
                            break;
                        case DM_X12:
                            target[tp] = 238;
                            tp++;
                            binary[binary_length] = ' ';
                            binary_length++;
                            if (debug) System.out.printf("X12 ");
                            break;
                        case DM_EDIFACT:
                            target[tp] = 240;
                            tp++;
                            binary[binary_length] = ' ';
                            binary_length++;
                            if (debug) System.out.printf("EDI ");
                            break;
                        case DM_BASE256:
                            target[tp] = 231;
                            tp++;
                            binary[binary_length] = ' ';
                            binary_length++;
                            if (debug) System.out.printf("BAS ");
                            break;
                        }
                    } else {
                        if (inputData[sp] > 127) {
                            target[tp] = 235; /* FNC4 */
                            if (debug) System.out.printf("FN4 ");
                            tp++;
                            target[tp] = (inputData[sp] - 128) + 1;
                            if (debug) System.out.printf("A%02X ", target[tp] - 1);
                            tp++;
                            binary[binary_length] = ' ';
                            binary_length++;
                            binary[binary_length] = ' ';
                            binary_length++;
                        } else {
                            if (gs1 && (inputData[sp] == '[')) {
                                target[tp] = 232; /* FNC1 */
                                if (debug) System.out.printf("FN1 ");
                            } else {
                                target[tp] = inputData[sp] + 1;
                                if (debug) System.out.printf("A%02X ", target[tp] - 1);
                            }
                            tp++;
                            binary[binary_length] = ' ';
                            binary_length++;
                        }
                        sp++;
                    }
                }

            }

            /* step (c) C40 encodation */
            if (current_mode == dm_mode.DM_C40) {
                int shift_set, value;

                next_mode = dm_mode.DM_C40;
                if (c40_p == 0) {
                    next_mode = lookAheadTest(sp, current_mode);
                }

                if (next_mode != dm_mode.DM_C40) {
                    target[tp] = 254;
                    tp++;
                    binary[binary_length] = ' ';
                    binary_length++; /* Unlatch */
                    next_mode = dm_mode.DM_ASCII;
                    if (debug) System.out.printf("ASC ");
                } else {
                    if (inputData[sp] > 127) {
                        c40_buffer[c40_p] = 1;
                        c40_p++;
                        c40_buffer[c40_p] = 30;
                        c40_p++; /* Upper Shift */
                        shift_set = c40_shift[inputData[sp] - 128];
                        value = c40_value[inputData[sp] - 128];
                    } else {
                        shift_set = c40_shift[inputData[sp]];
                        value = c40_value[inputData[sp]];
                    }

                    if (gs1 && (inputData[sp] == '[')) {
                        shift_set = 2;
                        value = 27; /* FNC1 */
                    }

                    if (shift_set != 0) {
                        c40_buffer[c40_p] = shift_set - 1;
                        c40_p++;
                    }
                    c40_buffer[c40_p] = value;
                    c40_p++;

                    if (c40_p >= 3) {
                        int iv;

                        iv = (1600 * c40_buffer[0]) + (40 * c40_buffer[1]) + 
                                (c40_buffer[2]) + 1;
                        target[tp] = iv / 256;
                        tp++;
                        target[tp] = iv % 256;
                        tp++;
                        binary[binary_length] = ' ';
                        binary_length++;
                        binary[binary_length] = ' ';
                        binary_length++;
                        if (debug) System.out.printf("[%d %d %d] ", c40_buffer[0], 
                                c40_buffer[1], c40_buffer[2]);

                        c40_buffer[0] = c40_buffer[3];
                        c40_buffer[1] = c40_buffer[4];
                        c40_buffer[2] = c40_buffer[5];
                        c40_buffer[3] = 0;
                        c40_buffer[4] = 0;
                        c40_buffer[5] = 0;
                        c40_p -= 3;
                    }
                    sp++;
                }
            }

            /* step (d) Text encodation */
            if (current_mode == dm_mode.DM_TEXT) {
                int shift_set, value;

                next_mode = dm_mode.DM_TEXT;
                if (text_p == 0) {
                    next_mode = lookAheadTest(sp, current_mode);
                }

                if (next_mode != dm_mode.DM_TEXT) {
                    target[tp] = 254;
                    tp++;
                    binary[binary_length] = ' ';
                    binary_length++; /* Unlatch */
                    next_mode = dm_mode.DM_ASCII;
                    if (debug) System.out.printf("ASC ");
                } else {
                    if (inputData[sp] > 127) {
                        text_buffer[text_p] = 1;
                        text_p++;
                        text_buffer[text_p] = 30;
                        text_p++; /* Upper Shift */
                        shift_set = text_shift[inputData[sp] - 128];
                        value = text_value[inputData[sp] - 128];
                    } else {
                        shift_set = text_shift[inputData[sp]];
                        value = text_value[inputData[sp]];
                    }

                    if (gs1 && (inputData[sp] == '[')) {
                        shift_set = 2;
                        value = 27; /* FNC1 */
                    }

                    if (shift_set != 0) {
                        text_buffer[text_p] = shift_set - 1;
                        text_p++;
                    }
                    text_buffer[text_p] = value;
                    text_p++;

                    if (text_p >= 3) {
                        int iv;

                        iv = (1600 * text_buffer[0]) + (40 * text_buffer[1]) + 
                                (text_buffer[2]) + 1;
                        target[tp] = iv / 256;
                        tp++;
                        target[tp] = iv % 256;
                        tp++;
                        binary[binary_length] = ' ';
                        binary_length++;
                        binary[binary_length] = ' ';
                        binary_length++;
                        if (debug) System.out.printf("[%d %d %d] ", 
                                text_buffer[0], text_buffer[1], text_buffer[2]);

                        text_buffer[0] = text_buffer[3];
                        text_buffer[1] = text_buffer[4];
                        text_buffer[2] = text_buffer[5];
                        text_buffer[3] = 0;
                        text_buffer[4] = 0;
                        text_buffer[5] = 0;
                        text_p -= 3;
                    }
                    sp++;
                }
            }

            /* step (e) X12 encodation */
            if (current_mode == dm_mode.DM_X12) {
                int value = 0;

                next_mode = dm_mode.DM_X12;
                if (x12_p == 0) {
                    next_mode = lookAheadTest(sp, current_mode);
                }

                if (next_mode != dm_mode.DM_X12) {
                    target[tp] = 254;
                    tp++;
                    binary[binary_length] = ' ';
                    binary_length++; /* Unlatch */
                    next_mode = dm_mode.DM_ASCII;
                    if (debug) System.out.printf("ASC ");
                } else {
                    if (inputData[sp] == 13) {
                        value = 0;
                    }
                    if (inputData[sp] == '*') {
                        value = 1;
                    }
                    if (inputData[sp] == '>') {
                        value = 2;
                    }
                    if (inputData[sp] == ' ') {
                        value = 3;
                    }
                    if ((inputData[sp] >= '0') && (inputData[sp] <= '9')) {
                        value = (inputData[sp] - '0') + 4;
                    }
                    if ((inputData[sp] >= 'A') && (inputData[sp] <= 'Z')) {
                        value = (inputData[sp] - 'A') + 14;
                    }

                    x12_buffer[x12_p] = value;
                    x12_p++;

                    if (x12_p >= 3) {
                        int iv;

                        iv = (1600 * x12_buffer[0]) + (40 * x12_buffer[1]) 
                                + (x12_buffer[2]) + 1;
                        target[tp] = iv / 256;
                        tp++;
                        target[tp] = iv % 256;
                        tp++;
                        binary[binary_length] = ' ';
                        binary_length++;
                        binary[binary_length] = ' ';
                        binary_length++;
                        if (debug) System.out.printf("[%d %d %d] ", 
                                x12_buffer[0], x12_buffer[1], x12_buffer[2]);

                        x12_buffer[0] = x12_buffer[3];
                        x12_buffer[1] = x12_buffer[4];
                        x12_buffer[2] = x12_buffer[5];
                        x12_buffer[3] = 0;
                        x12_buffer[4] = 0;
                        x12_buffer[5] = 0;
                        x12_p -= 3;
                    }
                    sp++;
                }
            }

            /* step (f) EDIFACT encodation */
            if (current_mode == dm_mode.DM_EDIFACT) {
                int value = 0;

                next_mode = dm_mode.DM_EDIFACT;
                if (edifact_p == 3) {
                    next_mode = lookAheadTest(sp, current_mode);
                }

                if (next_mode != dm_mode.DM_EDIFACT) {
                    edifact_buffer[edifact_p] = 31;
                    edifact_p++;
                    next_mode = dm_mode.DM_ASCII;
                } else {
                    if ((inputData[sp] >= '@') && (inputData[sp] <= '^')) {
                        value = inputData[sp] - '@';
                    }
                    if ((inputData[sp] >= ' ') && (inputData[sp] <= '?')) {
                        value = inputData[sp];
                    }

                    edifact_buffer[edifact_p] = value;
                    edifact_p++;
                    sp++;
                }

                if (edifact_p >= 4) {
                    target[tp] = (edifact_buffer[0] << 2) 
                            + ((edifact_buffer[1] & 0x30) >> 4);
                    tp++;
                    target[tp] = ((edifact_buffer[1] & 0x0f) << 4) 
                            + ((edifact_buffer[2] & 0x3c) >> 2);
                    tp++;
                    target[tp] = ((edifact_buffer[2] & 0x03) << 6) 
                            + edifact_buffer[3];
                    tp++;
                    binary[binary_length] = ' ';
                    binary_length++;
                    binary[binary_length] = ' ';
                    binary_length++;
                    binary[binary_length] = ' ';
                    binary_length++;
                    if (debug) System.out.printf("[%d %d %d %d] ", 
                            edifact_buffer[0], edifact_buffer[1], 
                            edifact_buffer[2], edifact_buffer[3]);

                    edifact_buffer[0] = edifact_buffer[4];
                    edifact_buffer[1] = edifact_buffer[5];
                    edifact_buffer[2] = edifact_buffer[6];
                    edifact_buffer[3] = edifact_buffer[7];
                    edifact_buffer[4] = 0;
                    edifact_buffer[5] = 0;
                    edifact_buffer[6] = 0;
                    edifact_buffer[7] = 0;
                    edifact_p -= 4;
                }
            }

            /* step (g) Base 256 encodation */
            if (current_mode == dm_mode.DM_BASE256) {
                next_mode = lookAheadTest(sp, current_mode);

                if (next_mode == dm_mode.DM_BASE256) {
                    target[tp] = inputData[sp];
                    if (debug) System.out.printf("B%02X ", target[tp]);
                    tp++;
                    sp++;
                    binary[binary_length] = 'b';
                    binary_length++;
                } else {
                    next_mode = dm_mode.DM_ASCII;
                    if (debug) System.out.printf("ASC ");
                }
            }

            if (tp > 1558) {
                return 0;
            }

        } /* while */

        /* Empty buffers */
        if (c40_p == 2) {
            target[tp] = 254;
            tp++; /* unlatch */
            target[tp] = inputData[inputlen - 2] + 1;
            tp++;
            target[tp] = inputData[inputlen - 1] + 1;
            tp++;
            binary[binary_length] = ' ';
            binary_length++;
            if (debug) System.out.printf("ASC A%02X A%02X ", target[tp - 2] - 1, 
                    target[tp - 1] - 1);
            current_mode = dm_mode.DM_ASCII;
        }
        if (c40_p == 1) {
            target[tp] = 254;
            tp++; /* unlatch */
            target[tp] = inputData[inputlen - 1] + 1;
            tp++;
            binary[binary_length] = ' ';
            binary_length++;
            binary[binary_length] = ' ';
            binary_length++;
            if (debug) System.out.printf("ASC A%02X ", target[tp - 1] - 1);
            current_mode = dm_mode.DM_ASCII;
        }

        if (text_p == 2) {
            target[tp] = 254;
            tp++; /* unlatch */
            target[tp] = inputData[inputlen - 2] + 1;
            tp++;
            target[tp] = inputData[inputlen - 1] + 1;
            tp++;
            binary[binary_length] = ' ';
            binary_length++;
            binary[binary_length] = ' ';
            binary_length++;
            binary[binary_length] = ' ';
            binary_length++;
            if (debug) System.out.printf("ASC A%02X A%02X ", target[tp - 2] - 1, 
                    target[tp - 1] - 1);
            current_mode = dm_mode.DM_ASCII;
        }
        if (text_p == 1) {
            target[tp] = 254;
            tp++; /* unlatch */
            target[tp] = inputData[inputlen - 1] + 1;
            tp++;
            binary[binary_length] = ' ';
            binary_length++;
            binary[binary_length] = ' ';
            binary_length++;
            if (debug) System.out.printf("ASC A%02X ", target[tp - 1] - 1);
            current_mode = dm_mode.DM_ASCII;
        }

        if (x12_p == 2) {
            target[tp] = 254;
            tp++; /* unlatch */
            target[tp] = inputData[inputlen - 2] + 1;
            tp++;
            target[tp] = inputData[inputlen - 1] + 1;
            tp++;
            binary[binary_length] = ' ';
            binary_length++;
            binary[binary_length] = ' ';
            binary_length++;
            binary[binary_length] = ' ';
            binary_length++;
            if (debug) System.out.printf("ASC A%02X A%02X ", target[tp - 2] - 1, 
                    target[tp - 1] - 1);
            current_mode = dm_mode.DM_ASCII;
        }
        if (x12_p == 1) {
            target[tp] = 254;
            tp++; /* unlatch */
            target[tp] = inputData[inputlen - 1] + 1;
            tp++;
            binary[binary_length] = ' ';
            binary_length++;
            binary[binary_length] = ' ';
            binary_length++;
            if (debug) System.out.printf("ASC A%02X ", target[tp - 1] - 1);
            current_mode = dm_mode.DM_ASCII;
        }

        /* Add length and randomising algorithm to b256 */
        i = 0;
        while (i < tp) {
            if (binary[i] == 'b') {
                if ((i == 0) || ((i != 0) && (binary[i - 1] != 'b'))) {
                    /* start of binary data */
                    int binary_count; /* length of b256 data */

                    for (binary_count = 0; binary[binary_count + i] == 'b'; 
                            binary_count++);

                    if (binary_count <= 249) {
                        insertAt(i, 'b');
                        insertValueAt(i, tp, (char) binary_count);
                        tp++;
                    } else {
                        insertAt(i, 'b');
                        insertAt(i + 1, 'b');
                        insertValueAt(i, tp, (char)((binary_count / 250) + 249));
                        tp++;
                        insertValueAt(i + 1, tp, (char)(binary_count % 250));
                        tp++;
                    }
                }
            }
            i++;
        }

        for (i = 0; i < tp; i++) {
            if (binary[i] == 'b') {
                int prn, temp;

                prn = ((149 * (i + 1)) % 255) + 1;
                temp = target[i] + prn;
                if (temp <= 255) {
                    target[i] = temp;
                } else {
                    target[i] = temp - 256;
                }
            }
        }

        if (debug) {
            System.out.printf("\n\n");
            for (i = 0; i < tp; i++) {
                System.out.printf("%02X ", target[i]);
            }
            System.out.printf("\n");
        }

        last_mode = current_mode;
        return tp;
    }

    private boolean isTwoDigits(int pos) {
        if (Character.isDigit((char) inputData[pos])) {
            if (pos + 1 >= content.length()) {
                return false;
            }
            if (Character.isDigit((char) inputData[pos + 1])) {
                return true;
            }
            return false;
        }
        return false;
    }

    private dm_mode lookAheadTest(int position, dm_mode current_mode) {
        /* A custom version of the 'look ahead test' from Annex P */
        /* This version is deliberately very reluctant to end a data stream with EDIFACT encoding */

        double ascii_count, c40_count, text_count, x12_count, edf_count, b256_count, best_count;
        int sp, done;
        char reduced_char;
        int sourcelen = content.length();
        dm_mode best_scheme;

        /* step (j) */
        if (current_mode == dm_mode.DM_ASCII) {
            ascii_count = 0.0;
            c40_count = 1.0;
            text_count = 1.0;
            x12_count = 1.0;
            edf_count = 1.0;
            b256_count = 1.25;
        } else {
            ascii_count = 1.0;
            c40_count = 2.0;
            text_count = 2.0;
            x12_count = 2.0;
            edf_count = 2.0;
            b256_count = 2.25;
        }

        switch (current_mode) {
        case DM_C40:
            c40_count = 0.0;
            break;
        case DM_TEXT:
            text_count = 0.0;
            break;
        case DM_X12:
            x12_count = 0.0;
            break;
        case DM_EDIFACT:
            edf_count = 0.0;
            break;
        case DM_BASE256:
            b256_count = 0.0;
            break;
        }

        for (sp = position;
        (sp < sourcelen) && (sp <= (position + 8)); sp++) {

            if (inputData[sp] <= 127) {
                reduced_char = (char) inputData[sp];
            } else {
                reduced_char = (char)(inputData[sp] - 127);
            }

            if ((inputData[sp] >= '0') && (inputData[sp] <= '9')) {
                ascii_count += 0.5;
            } else {
                ascii_count += 1.0;
            }
            if (inputData[sp] > 127) {
                ascii_count += 1.0;
            }

            done = 0;
            if (reduced_char == ' ') {
                c40_count += (2.0 / 3.0);
                done = 1;
            }
            if ((reduced_char >= '0') && (reduced_char <= '9')) {
                c40_count += (2.0 / 3.0);
                done = 1;
            }
            if ((reduced_char >= 'A') && (reduced_char <= 'Z')) {
                c40_count += (2.0 / 3.0);
                done = 1;
            }
            if (inputData[sp] > 127) {
                c40_count += (4.0 / 3.0);
            }
            if (done == 0) {
                c40_count += (4.0 / 3.0);
            }

            done = 0;
            if (reduced_char == ' ') {
                text_count += (2.0 / 3.0);
                done = 1;
            }
            if ((reduced_char >= '0') && (reduced_char <= '9')) {
                text_count += (2.0 / 3.0);
                done = 1;
            }
            if ((reduced_char >= 'a') && (reduced_char <= 'z')) {
                text_count += (2.0 / 3.0);
                done = 1;
            }
            if (inputData[sp] > 127) {
                text_count += (4.0 / 3.0);
            }
            if (done == 0) {
                text_count += (4.0 / 3.0);
            }

            if (isX12(inputData[sp])) {
                x12_count += (2.0 / 3.0);
            } else {
                x12_count += 4.0;
            }

            /* step (p) */
            if ((inputData[sp] >= ' ') && (inputData[sp] <= '^')) {
                edf_count += (3.0 / 4.0);
            } else {
                edf_count += 6.0;
            }
            if (gs1 && (inputData[sp] == '[')) {
                edf_count += 6.0;
            }
            if (sp >= (sourcelen - 5)) {
                edf_count += 6.0;
            } /* MMmmm fudge! */

            /* step (q) */
            if (gs1 && (inputData[sp] == '[')) {
                b256_count += 4.0;
            } else {
                b256_count += 1.0;
            }
        }

        best_count = ascii_count;
        best_scheme = dm_mode.DM_ASCII;

        if (b256_count <= best_count) {
            best_count = b256_count;
            best_scheme = dm_mode.DM_BASE256;
        }

        if (edf_count <= best_count) {
            best_count = edf_count;
            best_scheme = dm_mode.DM_EDIFACT;
        }

        if (text_count <= best_count) {
            best_count = text_count;
            best_scheme = dm_mode.DM_TEXT;
        }

        if (x12_count <= best_count) {
            best_count = x12_count;
            best_scheme = dm_mode.DM_X12;
        }

        if (c40_count <= best_count) {
            best_scheme = dm_mode.DM_C40;
        }

        return best_scheme;
    }

    private boolean isX12(int source) {
        if (source == 13) {
            return true;
        }
        if (source == 42) {
            return true;
        }
        if (source == 62) {
            return true;
        }
        if (source == 32) {
            return true;
        }
        if ((source >= '0') && (source <= '9')) {
            return true;
        }
        if ((source >= 'A') && (source <= 'Z')) {
            return true;
        }

        return false;
    }

    private void calculateErrorCorrection(int bytes, int datablock, int rsblock, int skew) {
        // calculate and append ecc code, and if necessary interleave
        int blocks = (bytes + 2) / datablock, b;
        int n, p;
        ReedSolomon rs = new ReedSolomon();

        rs.init_gf(0x12d);
        rs.init_code(rsblock, 1);

        for (b = 0; b < blocks; b++) {
            int[] buf = new int[256];
            int[] ecc = new int[256];

            p = 0;
            for (n = b; n < bytes; n += blocks) {
                buf[p++] = target[n];
            }
            rs.encode(p, buf);
            for (n = 0; n < rsblock; n++) {
                ecc[n] = rs.getResult(n);
            }
            p = rsblock - 1; // comes back reversed
            for (n = b; n < rsblock * blocks; n += blocks) {
                if (skew == 1) {
                    /* Rotate ecc data to make 144x144 size symbols acceptable */
                    /* See http://groups.google.com/group/postscriptbarcode/msg/5ae8fda7757477da */
                    if (b < 8) {
                        target[bytes + n + 2] = ecc[p--];
                    } else {
                        target[bytes + n - 8] = ecc[p--];
                    }
                } else {
                    target[bytes + n] = ecc[p--];
                }
            }
        }
    }

    private void insertAt(int pos, char newbit) {
        /* Insert a character into the middle of a string at position posn */
        int i;

        for (i = binary_length; i > pos; i--) {
            binary[i] = binary[i - 1];
        }
        binary[pos] = newbit;
    }

    private void insertValueAt(int posn, int streamlen, char newbit) {
        int i;

        for (i = streamlen; i > posn; i--) {
            target[i] = target[i - 1];
        }
        target[posn] = newbit;
    }

    private void addPadBits(int tp, int tail_length) {
        /* adds unlatch and pad bits */
        int i, prn, temp;

        switch (last_mode) {
        case DM_C40:
        case DM_TEXT:
        case DM_X12:
            target[tp] = 254;
            tp++; /* Unlatch */
            tail_length--;
        }

        for (i = tail_length; i > 0; i--) {
            if (i == tail_length) {
                target[tp] = 129;
                tp++; /* Pad */
            } else {
                prn = ((149 * (tp + 1)) % 253) + 1;
                temp = 129 + prn;
                if (temp <= 254) {
                    target[tp] = temp;
                    tp++;
                } else {
                    target[tp] = temp - 254;
                    tp++;
                }
            }
        }
    }

    private void placeData(int NR, int NC) {
        int r, c, p;
        // invalidate
        for (r = 0; r < NR; r++) {
            for (c = 0; c < NC; c++) {
                places[r * NC + c] = 0;
            }
        }
        // start
        p = 1;
        r = 4;
        c = 0;
        do {
            // check corner
            if (r == NR && (c == 0)) { 
                placeCornerA(NR, NC, p++);
            }
            if (r == NR - 2 && (c == 0) && ((NC % 4) != 0)) {
                placeCornerB(NR, NC, p++);
            }
            if (r == NR - 2 && (c == 0) && (NC % 8) == 4) {
                placeCornerC(NR, NC, p++);
            }
            if (r == NR + 4 && c == 2 && ((NC % 8) == 0)) {
                placeCornerD(NR, NC, p++);
            }
            // up/right
            do {
                if (r < NR && c >= 0 && (places[r * NC + c] == 0)) {
                    placeBlock(NR, NC, r, c, p++);
                }
                r -= 2;
                c += 2;
            }
            while (r >= 0 && c < NC);
            r++;
            c += 3;
            // down/left
            do {
                if (r >= 0 && c < NC && (places[r * NC + c] == 0)) {
                    placeBlock(NR, NC, r, c, p++);
                }
                r += 2;
                c -= 2;
            }
            while (r < NR && c >= 0);
            r += 3;
            c++;
        }
        while (r < NR || c < NC);
        // unfilled corner
        if (places[NR * NC - 1] == 0)  {
            places[NR * NC - 1] = places[NR * NC - NC - 2] = 1;
        }
    }

    private void placeCornerA(int NR, int NC, int p) {
        placeBit(NR, NC, NR - 1, 0, p, 7);
        placeBit(NR, NC, NR - 1, 1, p, 6);
        placeBit(NR, NC, NR - 1, 2, p, 5);
        placeBit(NR, NC, 0, NC - 2, p, 4);
        placeBit(NR, NC, 0, NC - 1, p, 3);
        placeBit(NR, NC, 1, NC - 1, p, 2);
        placeBit(NR, NC, 2, NC - 1, p, 1);
        placeBit(NR, NC, 3, NC - 1, p, 0);
    }

    private void placeCornerB(int NR, int NC, int p) {
        placeBit(NR, NC, NR - 3, 0, p, 7);
        placeBit(NR, NC, NR - 2, 0, p, 6);
        placeBit(NR, NC, NR - 1, 0, p, 5);
        placeBit(NR, NC, 0, NC - 4, p, 4);
        placeBit(NR, NC, 0, NC - 3, p, 3);
        placeBit(NR, NC, 0, NC - 2, p, 2);
        placeBit(NR, NC, 0, NC - 1, p, 1);
        placeBit(NR, NC, 1, NC - 1, p, 0);
    }

    private void placeCornerC(int NR, int NC, int p) {
        placeBit(NR, NC, NR - 3, 0, p, 7);
        placeBit(NR, NC, NR - 2, 0, p, 6);
        placeBit(NR, NC, NR - 1, 0, p, 5);
        placeBit(NR, NC, 0, NC - 2, p, 4);
        placeBit(NR, NC, 0, NC - 1, p, 3);
        placeBit(NR, NC, 1, NC - 1, p, 2);
        placeBit(NR, NC, 2, NC - 1, p, 1);
        placeBit(NR, NC, 3, NC - 1, p, 0);
    }

    private void placeCornerD(int NR, int NC, int p) {
        placeBit(NR, NC, NR - 1, 0, p, 7);
        placeBit(NR, NC, NR - 1, NC - 1, p, 6);
        placeBit(NR, NC, 0, NC - 3, p, 5);
        placeBit(NR, NC, 0, NC - 2, p, 4);
        placeBit(NR, NC, 0, NC - 1, p, 3);
        placeBit(NR, NC, 1, NC - 3, p, 2);
        placeBit(NR, NC, 1, NC - 2, p, 1);
        placeBit(NR, NC, 1, NC - 1, p, 0);
    }

    private void placeBlock(int NR, int NC, int r, int c, int p) {
        placeBit(NR, NC, r - 2, c - 2, p, 7);
        placeBit(NR, NC, r - 2, c - 1, p, 6);
        placeBit(NR, NC, r - 1, c - 2, p, 5);
        placeBit(NR, NC, r - 1, c - 1, p, 4);
        placeBit(NR, NC, r - 1, c - 0, p, 3);
        placeBit(NR, NC, r - 0, c - 2, p, 2);
        placeBit(NR, NC, r - 0, c - 1, p, 1);
        placeBit(NR, NC, r - 0, c - 0, p, 0);
    }

    private void placeBit(int NR, int NC, int r, int c, int p, int b) {
        if (r < 0) {
            r += NR;
            c += 4 - ((NR + 4) % 8);
        }
        if (c < 0) {
            c += NC;
            r += 4 - ((NC + 4) % 8);
        }
        places[r * NC + c] = (p << 3) + b;
    }
}
