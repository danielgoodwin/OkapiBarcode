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
 * Implements QR Code 2005 bar code symbology
 * According to ISO/IEC 18004:2006
 *
 * @author Robin Stuart <rstuart114@gmail.com>
 */
public class QrCode extends Symbol {
    private enum qrMode {
        NULL, KANJI, BINARY, ALPHANUM, NUMERIC
    }
    private enum eccMode {
        L, M, Q, H
    }
    private qrMode[] inputMode;
    private String binary;
    private int[] datastream;
    private int[] fullstream;
    private byte[] grid;
    private byte[] eval;

    private final char[] rhodium = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '$', '%', '*', '\'', '+', '-', '.',
            '/', ':'
    };

    private final int[] qr_data_codewords_L = {
        19, 34, 55, 80, 108, 136, 156, 194, 232, 274, 324, 370, 428, 461, 523, 589, 647,
        721, 795, 861, 932, 1006, 1094, 1174, 1276, 1370, 1468, 1531, 1631,
        1735, 1843, 1955, 2071, 2191, 2306, 2434, 2566, 2702, 2812, 2956
    };

    private final int[] qr_data_codewords_M = {
        16, 28, 44, 64, 86, 108, 124, 154, 182, 216, 254, 290, 334, 365, 415, 453, 507,
        563, 627, 669, 714, 782, 860, 914, 1000, 1062, 1128, 1193, 1267,
        1373, 1455, 1541, 1631, 1725, 1812, 1914, 1992, 2102, 2216, 2334
    };

    private final int[] qr_data_codewords_Q = {
        13, 22, 34, 48, 62, 76, 88, 110, 132, 154, 180, 206, 244, 261, 295, 325, 367,
        397, 445, 485, 512, 568, 614, 664, 718, 754, 808, 871, 911,
        985, 1033, 1115, 1171, 1231, 1286, 1354, 1426, 1502, 1582, 1666
    };

    private final int[] qr_data_codewords_H = {
        9, 16, 26, 36, 46, 60, 66, 86, 100, 122, 140, 158, 180, 197, 223, 253, 283,
        313, 341, 385, 406, 442, 464, 514, 538, 596, 628, 661, 701,
        745, 793, 845, 901, 961, 986, 1054, 1096, 1142, 1222, 1276
    };

    private final int[] qr_blocks_L = {
        1, 1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 4, 4, 6, 6, 6, 6, 7, 8, 8, 9, 9, 10, 12, 12,
        12, 13, 14, 15, 16, 17, 18, 19, 19, 20, 21, 22, 24, 25
    };

    private final int[] qr_blocks_M = {
        1, 1, 1, 2, 2, 4, 4, 4, 5, 5, 5, 8, 9, 9, 10, 10, 11, 13, 14, 16, 17, 17, 18, 20,
        21, 23, 25, 26, 28, 29, 31, 33, 35, 37, 38, 40, 43, 45, 47, 49
    };

    private final int[] qr_blocks_Q = {
        1, 1, 2, 2, 4, 4, 6, 6, 8, 8, 8, 10, 12, 16, 12, 17, 16, 18, 21, 20, 23, 23, 25,
        27, 29, 34, 34, 35, 38, 40, 43, 45, 48, 51, 53, 56, 59, 62, 65, 68
    };

    private final int[] qr_blocks_H = {
        1, 1, 2, 4, 4, 4, 5, 6, 8, 8, 11, 11, 16, 16, 18, 16, 19, 21, 25, 25, 25, 34, 30,
        32, 35, 37, 40, 42, 45, 48, 51, 54, 57, 60, 63, 66, 70, 74, 77, 81
    };

    private final int[] qr_total_codewords = {
        26, 44, 70, 100, 134, 172, 196, 242, 292, 346, 404, 466, 532, 581, 655, 733, 815,
        901, 991, 1085, 1156, 1258, 1364, 1474, 1588, 1706, 1828, 1921, 2051,
        2185, 2323, 2465, 2611, 2761, 2876, 3034, 3196, 3362, 3532, 3706
    };

    private final int[] qr_sizes = {
        21, 25, 29, 33, 37, 41, 45, 49, 53, 57, 61, 65, 69, 73, 77, 81, 85, 89, 93, 97,
        101, 105, 109, 113, 117, 121, 125, 129, 133, 137, 141, 145, 149, 153, 157, 161, 165, 169, 173, 177
    };

    private final int[] qr_align_loopsize = {
        0, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7
    };

    private final int[] qr_table_e1 = {
        6, 18, 0, 0, 0, 0, 0,
        6, 22, 0, 0, 0, 0, 0,
        6, 26, 0, 0, 0, 0, 0,
        6, 30, 0, 0, 0, 0, 0,
        6, 34, 0, 0, 0, 0, 0,
        6, 22, 38, 0, 0, 0, 0,
        6, 24, 42, 0, 0, 0, 0,
        6, 26, 46, 0, 0, 0, 0,
        6, 28, 50, 0, 0, 0, 0,
        6, 30, 54, 0, 0, 0, 0,
        6, 32, 58, 0, 0, 0, 0,
        6, 34, 62, 0, 0, 0, 0,
        6, 26, 46, 66, 0, 0, 0,
        6, 26, 48, 70, 0, 0, 0,
        6, 26, 50, 74, 0, 0, 0,
        6, 30, 54, 78, 0, 0, 0,
        6, 30, 56, 82, 0, 0, 0,
        6, 30, 58, 86, 0, 0, 0,
        6, 34, 62, 90, 0, 0, 0,
        6, 28, 50, 72, 94, 0, 0,
        6, 26, 50, 74, 98, 0, 0,
        6, 30, 54, 78, 102, 0, 0,
        6, 28, 54, 80, 106, 0, 0,
        6, 32, 58, 84, 110, 0, 0,
        6, 30, 58, 86, 114, 0, 0,
        6, 34, 62, 90, 118, 0, 0,
        6, 26, 50, 74, 98, 122, 0,
        6, 30, 54, 78, 102, 126, 0,
        6, 26, 52, 78, 104, 130, 0,
        6, 30, 56, 82, 108, 134, 0,
        6, 34, 60, 86, 112, 138, 0,
        6, 30, 58, 86, 114, 142, 0,
        6, 34, 62, 90, 118, 146, 0,
        6, 30, 54, 78, 102, 126, 150,
        6, 24, 50, 76, 102, 128, 154,
        6, 28, 54, 80, 106, 132, 158,
        6, 32, 58, 84, 110, 136, 162,
        6, 26, 54, 82, 110, 138, 166,
        6, 30, 58, 86, 114, 142, 170
    };

    private final int[] qr_annex_c = {
        /* Format information bit sequences */
        0x5412, 0x5125, 0x5e7c, 0x5b4b, 0x45f9, 0x40ce, 0x4f97, 0x4aa0, 0x77c4, 0x72f3, 0x7daa, 0x789d,
        0x662f, 0x6318, 0x6c41, 0x6976, 0x1689, 0x13be, 0x1ce7, 0x19d0, 0x0762, 0x0255, 0x0d0c, 0x083b,
        0x355f, 0x3068, 0x3f31, 0x3a06, 0x24b4, 0x2183, 0x2eda, 0x2bed
    };

    private final long[] qr_annex_d = {
        /* Version information bit sequences */
        0x07c94, 0x085bc, 0x09a99, 0x0a4d3, 0x0bbf6, 0x0c762, 0x0d847, 0x0e60d, 0x0f928, 0x10b78,
        0x1145d, 0x12a17, 0x13532, 0x149a6, 0x15683, 0x168c9, 0x177ec, 0x18ec4, 0x191e1, 0x1afab,
        0x1b08e, 0x1cc1a, 0x1d33f, 0x1ed75, 0x1f250, 0x209d5, 0x216f0, 0x228ba, 0x2379f, 0x24b0b,
        0x2542e, 0x26a64, 0x27541, 0x28c69
    };

    @Override
    public boolean encode() {
        int i, j;
        int est_binlen;
        eccMode ecc_level;
        int max_cw;
        int autosize;
        int target_binlen, version, blocks;
        int size;
        int bitmask;
        String bin;

        inputMode = new qrMode[content.length()];
        define_mode();
        est_binlen = estimate_binary_length();

        switch (option1) {
        case 1:
            ecc_level = eccMode.L;
            max_cw = 2956;
            break;
        case 2:
            ecc_level = eccMode.M;
            max_cw = 2334;
            break;
        case 3:
            ecc_level = eccMode.Q;
            max_cw = 1666;
            break;
        case 4:
            ecc_level = eccMode.H;
            max_cw = 1276;
            break;
        default:
            ecc_level = eccMode.L;
            max_cw = 2956;
            break;
        }

        if (est_binlen > (8 * max_cw)) {
            error_msg = "Input too long for selected error correction level";
            return false;
        }

        autosize = 40;
        for (i = 39; i >= 0; i--) {
            switch (ecc_level) {
            case L:
                if ((8 * qr_data_codewords_L[i]) >= est_binlen) {
                    autosize = i + 1;
                }
                break;
            case M:
                if ((8 * qr_data_codewords_M[i]) >= est_binlen) {
                    autosize = i + 1;
                }
                break;
            case Q:
                if ((8 * qr_data_codewords_Q[i]) >= est_binlen) {
                    autosize = i + 1;
                }
                break;
            case H:
                if ((8 * qr_data_codewords_H[i]) >= est_binlen) {
                    autosize = i + 1;
                }
                break;
            }
        }

        version = autosize;
        if((option2 >= 1) && (option2 <= 40)) {
            if (option2 > autosize) {
                    version = option2;
            }
        }

        /* Ensure maxium error correction capacity */
        if (est_binlen <= qr_data_codewords_M[version - 1]) {
            ecc_level = eccMode.M;
        }
        if (est_binlen <= qr_data_codewords_Q[version - 1]) {
            ecc_level = eccMode.Q;
        }
        if (est_binlen <= qr_data_codewords_H[version - 1]) {
            ecc_level = eccMode.H;
        }

        target_binlen = qr_data_codewords_L[version - 1];
        blocks = qr_blocks_L[version - 1];
        switch (ecc_level) {
        case M:
            target_binlen = qr_data_codewords_M[version - 1];
            blocks = qr_blocks_M[version - 1];
            break;
        case Q:
            target_binlen = qr_data_codewords_Q[version - 1];
            blocks = qr_blocks_Q[version - 1];
            break;
        case H:
            target_binlen = qr_data_codewords_H[version - 1];
            blocks = qr_blocks_H[version - 1];
            break;
        }

        datastream = new int[target_binlen + 1];
        fullstream = new int[qr_total_codewords[version - 1] + 1];

        if (!(qr_binary(version, target_binlen, est_binlen))) {
            /* Invalid characters used - stop encoding */
            return false;
        }

        add_ecc(version, target_binlen, blocks);

        size = qr_sizes[version - 1];

        grid = new byte[size * size];
        
        encodeInfo += "Version: " + version + "\n";
        encodeInfo += "ECC Level: ";
        switch (ecc_level) {
            case L:
                encodeInfo += "L\n";
                break;
            case M:
                encodeInfo += "M\n";
                break;
            case Q:
                encodeInfo += "Q\n";
                break;
            case H:
            default:
                encodeInfo += "H\n";
                break;
        }

        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                grid[(i * size) + j] = 0;
            }
        }

        setup_grid(size, version);
        populate_grid(size, qr_total_codewords[version - 1]);
        bitmask = apply_bitmask(size);
        encodeInfo += "Mask Pattern: " + Integer.toBinaryString(bitmask) + "\n";
        add_format_info(size, ecc_level, bitmask);
        if (version >= 7) {
            add_version_info(size, version);
        }

        readable = "";
        pattern = new String[size];
        row_count = size;
        row_height = new int[size];
        for (i = 0; i < size; i++) {
            bin = "";
            for (j = 0; j < size; j++) {
                if ((grid[(i * size) + j] & 0x01) != 0) {
                    bin += "1";
                } else {
                    bin += "0";
                }
            }
            pattern[i] = bin2pat(bin);
            row_height[i] = 1;
        }

        plotSymbol();
        return true;
    }

    private void define_mode() {
        int i, mlen, j;
        int length = content.length();

        for (i = 0; i < length; i++) {
            if (content.charAt(i) > 0xff) {
                inputMode[i] = qrMode.KANJI;
            } else {
                inputMode[i] = qrMode.BINARY;
                if (in_alpha(content.charAt(i))) {
                    inputMode[i] = qrMode.ALPHANUM;
                }
                if (gs1 && (content.charAt(i) == '[')) {
                    inputMode[i] = qrMode.ALPHANUM;
                }
                if ((content.charAt(i) >= '0') && (content.charAt(i) <= '9')) {
                    inputMode[i] = qrMode.NUMERIC;
                }
            }
        }

        /* If less than 6 numeric digits together then don't use numeric mode */
        for (i = 0; i < length; i++) {
            if (inputMode[i] == qrMode.NUMERIC) {
                if (((i != 0) && (inputMode[i - 1] != qrMode.NUMERIC)) || (i == 0)) {
                    mlen = 0;
                    while (((mlen + i) < length) && (inputMode[mlen + i] == qrMode.NUMERIC)) {
                        mlen++;
                    }
                    if (mlen < 6) {
                        for (j = 0; j < mlen; j++) {
                            inputMode[i + j] = qrMode.ALPHANUM;
                        }
                    }
                }
            }
        }

        /* If less than 4 alphanumeric characters together then don't use alphanumeric mode */
        for (i = 0; i < length; i++) {
            if (inputMode[i] == qrMode.ALPHANUM) {
                if (((i != 0) && (inputMode[i - 1] != qrMode.ALPHANUM)) || (i == 0)) {
                    mlen = 0;
                    while (((mlen + i) < length) && (inputMode[mlen + i] == qrMode.ALPHANUM)) {
                        mlen++;
                    }
                    if (mlen < 6) {
                        for (j = 0; j < mlen; j++) {
                            inputMode[i + j] = qrMode.BINARY;
                        }
                    }
                }
            }
        }
    }

    private boolean in_alpha(char cglyph) {
        /* Returns true if input glyph is in the Alphanumeric set */
        boolean retval = false;

        if ((cglyph >= '0') && (cglyph <= '9')) {
            retval = true;
        }
        if ((cglyph >= 'A') && (cglyph <= 'Z')) {
            retval = true;
        }
        switch (cglyph) {
        case ' ':
        case '$':
        case '%':
        case '*':
        case '+':
        case '-':
        case '.':
        case '/':
        case ':':
            retval = true;
            break;
        }

        return retval;
    }

    private int estimate_binary_length() {
        /* Make an estimate (worst case scenario) of how long the binary string will be */
        int i, count = 0;
        qrMode current = qrMode.NULL;
        int a_count = 0;
        int n_count = 0;

        if (gs1) {
            count += 4;
        }

        for (i = 0; i < content.length(); i++) {
            if (inputMode[i] != current) {
                switch (inputMode[i]) {
                case KANJI:
                    count += 12 + 4;
                    current = qrMode.KANJI;
                    break;
                case BINARY:
                    count += 16 + 4;
                    current = qrMode.BINARY;
                    break;
                case ALPHANUM:
                    count += 13 + 4;
                    current = qrMode.ALPHANUM;
                    a_count = 0;
                    break;
                case NUMERIC:
                    count += 14 + 4;
                    current = qrMode.NUMERIC;
                    n_count = 0;
                    break;
                }
            }

            switch (inputMode[i]) {
            case KANJI:
                count += 13;
                break;
            case BINARY:
                count += 8;
                break;
            case ALPHANUM:
                a_count++;
                if ((a_count & 1) == 0) {
                    count += 5; // 11 in total
                    a_count = 0;
                } else
                    count += 6;
                break;
            case NUMERIC:
                n_count++;
                if ((n_count % 3) == 0) {
                    count += 3; // 10 in total
                    n_count = 0;
                } else if ((n_count & 1) == 0)
                    count += 3; // 7 in total
                else
                    count += 4;
                break;
            }
        }

        return count;
    }

    private boolean qr_binary(int version, int target_binlen, int est_binlen) {
        /* Convert input data to a binary stream and add padding */
        int position = 0;
        int short_data_block_length, i, scheme = 1;
        int padbits;
        int current_binlen, current_bytes;
        int toggle, percent;
        String oneChar;
        qrMode data_block;
        int jis;
        byte[] jisBytes;
        int msb, lsb, prod;
        int count, first, second, third;
        int weight;

        binary = "";

        if (gs1) {
            binary += "0101"; /* FNC1 */
        }

        if (version <= 9) {
            scheme = 1;
        } else if ((version >= 10) && (version <= 26)) {
            scheme = 2;
        } else if (version >= 27) {
            scheme = 3;
        }

        if (debug) {
            for (i = 0; i < content.length(); i++) {
                switch (inputMode[i]) {
                case KANJI:
                    System.out.print("K");
                    break;
                case BINARY:
                    System.out.print("B");
                    break;
                case ALPHANUM:
                    System.out.print("A");
                    break;
                case NUMERIC:
                    System.out.print("N");
                    break;
                }
            }
            System.out.printf("\n");
        }

        percent = 0;

        do {
            data_block = inputMode[position];
            short_data_block_length = 0;
            do {
                short_data_block_length++;
            } while (((short_data_block_length + position) < content.length()) &&
                (inputMode[position + short_data_block_length] == data_block));

            switch (data_block) {
            case KANJI:
                /* Kanji mode */
                /* Mode indicator */
                binary += "1000";

                /* Character count indicator */
                qr_bscan(short_data_block_length, 0x20 << (scheme * 2)); /* scheme = 1..3 */

                if (debug) {
                    System.out.printf("Kanji block (length %d)\n", short_data_block_length);
                }

                /* Character representation */
                for (i = 0; i < short_data_block_length; i++) {
                    oneChar = "";
                    oneChar += content.charAt(position + i);

                    /* Convert Unicode input to Shift-JIS */
                    try {
                        jisBytes = oneChar.getBytes("SJIS");
                    } catch (UnsupportedEncodingException e) {
                        error_msg = "Invalid character(s) in input data";
                        return false;
                    }

                    jis = ((jisBytes[0] & 0xFF) << 8) + (jisBytes[1] & 0xFF);

                    if (jis > 0x9fff) {
                        jis -= 0xc140;
                    } else {
                        jis -= 0x8140;
                    }
                    msb = (jis & 0xff00) >> 8;
                    lsb = (jis & 0xff);
                    prod = (msb * 0xc0) + lsb;

                    qr_bscan(prod, 0x1000);

                    if (debug) {
                        System.out.printf("\t0x%4X\n", prod);
                    }
                }

                if (debug) {
                    System.out.printf("\n");
                }

                break;
            case BINARY:
                /* Byte mode */
                /* Mode indicator */
                binary += "0100";

                /* Character count indicator */
                qr_bscan(short_data_block_length, scheme > 1 ? 0x8000 : 0x80); /* scheme = 1..3 */

                if (debug) {
                    System.out.printf("Byte block (length %d)\n\t", short_data_block_length);
                }

                /* Character representation */
                for (i = 0; i < short_data_block_length; i++) {
                    int lbyte = content.charAt(position + i);

                    if (gs1 && (lbyte == '[')) {
                        lbyte = 0x1d; /* FNC1 */
                    }

                    qr_bscan(lbyte, 0x80);

                    if (debug) {
                        System.out.printf("0x%2X(%d) ", lbyte, lbyte);
                    }
                }

                if (debug) {
                    System.out.printf("\n");
                }

                break;
            case ALPHANUM:
                /* Alphanumeric mode */
                /* Mode indicator */
                binary += "0010";

                /* Character count indicator */
                qr_bscan(short_data_block_length, 0x40 << (2 * scheme)); /* scheme = 1..3 */

                if (debug) {
                    System.out.printf("Alpha block (length %d)\n\t", short_data_block_length);
                }

                /* Character representation */
                i = 0;
                while (i < short_data_block_length) {

                    if (percent == 0) {
                        if (gs1 && (content.charAt(position + i) == '%')) {
                            first = positionOf('%', rhodium);
                            second = positionOf('%', rhodium);
                            count = 2;
                            prod = (first * 45) + second;
                            i++;
                        } else {
                            if (gs1 && (content.charAt(position + i) == '[')) {
                                first = positionOf('%', rhodium); /* FNC1 */
                            } else {
                                first = positionOf(content.charAt(position + i), rhodium);
                            }
                            count = 1;
                            i++;
                            prod = first;

                            if (inputMode[position + i] == qrMode.ALPHANUM) {
                                if (gs1 && (content.charAt(position + i) == '%')) {
                                    second = positionOf('%', rhodium);
                                    count = 2;
                                    prod = (first * 45) + second;
                                    percent = 1;
                                } else {
                                    if (gs1 && (content.charAt(position + i) == '[')) {
                                        second = positionOf('%', rhodium); /* FNC1 */
                                    } else {
                                        second = positionOf(content.charAt(position + i), rhodium);
                                    }
                                    count = 2;
                                    i++;
                                    prod = (first * 45) + second;
                                }
                            }
                        }
                    } else {
                        first = positionOf('%', rhodium);
                        count = 1;
                        i++;
                        prod = first;
                        percent = 0;

                        if (inputMode[position + i] == qrMode.ALPHANUM) {
                            if (gs1 && (content.charAt(position + i) == '%')) {
                                second = positionOf('%', rhodium);
                                count = 2;
                                prod = (first * 45) + second;
                                percent = 1;
                            } else {
                                if (gs1 && (content.charAt(position + i) == '[')) {
                                    second = positionOf('%', rhodium); /* FNC1 */
                                } else {
                                    second = positionOf(content.charAt(position + i), rhodium);
                                }
                                count = 2;
                                i++;
                                prod = (first * 45) + second;
                            }
                        }
                    }

                    qr_bscan(prod, count == 2 ? 0x400 : 0x20); /* count = 1..2 */

                    if (debug) {
                        System.out.printf("0x%4X ", prod);
                    }
                };

                if (debug) {
                    System.out.printf("\n");
                }

                break;
            case NUMERIC:
                /* Numeric mode */
                /* Mode indicator */
                binary += "0001";

                /* Character count indicator */
                qr_bscan(short_data_block_length, 0x80 << (2 * scheme)); /* scheme = 1..3 */

                if (debug) {
                    System.out.printf("Number block (length %d)\n\t", short_data_block_length);
                }

                /* Character representation */
                i = 0;
                while (i < short_data_block_length) {

                    first = Character.getNumericValue(content.charAt(position + i));
                    count = 1;
                    prod = first;

                    if ((i + 1) < short_data_block_length) {
                        second = Character.getNumericValue(content.charAt(position + i + 1));
                        count = 2;
                        prod = (prod * 10) + second;

                        if ((i + 2) < short_data_block_length) {
                            third = Character.getNumericValue(content.charAt(position + i + 2));
                            count = 3;
                            prod = (prod * 10) + third;
                        }
                    }

                    qr_bscan(prod, 1 << (3 * count)); /* count = 1..3 */

                    if (debug) {
                        System.out.printf("0x%4X (%d)", prod, prod);
                    }

                    i += count;
                };

                if (debug) {
                    System.out.printf("\n");
                }

                break;
            }
            position += short_data_block_length;
        } while (position < content.length());

        /* Terminator */
        binary += "0000";

        current_binlen = binary.length();
        padbits = 8 - (current_binlen % 8);
        if (padbits == 8) {
            padbits = 0;
        }
        current_bytes = (current_binlen + padbits) / 8;

        /* Padding bits */
        for (i = 0; i < padbits; i++) {
            binary += "0";
        }

        /* Put data into 8-bit codewords */
        for (i = 0; i < current_bytes; i++) {
            datastream[i] = 0x00;
            for(weight = 0; weight < 8; weight++) {
                if (binary.charAt((i * 8) + weight) == '1') {
                    datastream[i] += (0x80 >> weight);
                }
            }
        }

        /* Add pad codewords */
        toggle = 0;
        for (i = current_bytes; i < target_binlen; i++) {
            if (toggle == 0) {
                datastream[i] = 0xec;
                toggle = 1;
            } else {
                datastream[i] = 0x11;
                toggle = 0;
            }
        }

        if (debug) {
            System.out.printf("Resulting codewords:\n\t");
            for (i = 0; i < target_binlen; i++) {
                System.out.printf("0x%2X ", datastream[i]);
            }
            System.out.printf("\n");
        }

        return true;
    }

    private void qr_bscan(int data, int h) {

        for (;
            (h != 0); h >>= 1) {
            if ((data & h) != 0) {
                binary += "1";
            } else {
                binary += "0";
            }
        }

    }

    private void add_ecc(int version, int data_cw, int blocks) {
        /* Split data into blocks, add error correction and then interleave the blocks and error correction data */
        int ecc_cw = qr_total_codewords[version - 1] - data_cw;
        int short_data_block_length = data_cw / blocks;
        int qty_long_blocks = data_cw % blocks;
        int qty_short_blocks = blocks - qty_long_blocks;
        int ecc_block_length = ecc_cw / blocks;
        int i, j, k, length_this_block, posn;

        int[] data_block = new int[short_data_block_length + 2];
        int[] ecc_block = new int[ecc_block_length + 2];
        int[] interleaved_data = new int[data_cw + 2];
        int[] interleaved_ecc = new int[ecc_cw + 2];

        posn = 0;

        for (i = 0; i < blocks; i++) {
            ReedSolomon rs = new ReedSolomon();
            if (i < qty_short_blocks) {
                length_this_block = short_data_block_length;
            } else {
                length_this_block = short_data_block_length + 1;
            }

            for (j = 0; j < ecc_block_length; j++) {
                ecc_block[j] = 0;
            }

            for (j = 0; j < length_this_block; j++) {
                data_block[j] = datastream[posn + j];
            }

            rs.init_gf(0x11d);
            rs.init_code(ecc_block_length, 0);
            rs.encode(length_this_block, data_block);
            for (k = 0; k < ecc_block_length; k++) {
                ecc_block[k] = rs.getResult(k);
            }
            if (debug) {
                System.out.printf("Block %d: ", i + 1);
                for (j = 0; j < length_this_block; j++) {
                    System.out.printf("%2X ", data_block[j]);
                }
                if (i < qty_short_blocks) {
                    System.out.printf("   ");
                }
                System.out.printf(" // ");
                for (j = 0; j < ecc_block_length; j++) {
                    System.out.printf("%2X ", ecc_block[ecc_block_length - j - 1]);
                }
                System.out.printf("\n");
            }

            for (j = 0; j < short_data_block_length; j++) {
                interleaved_data[(j * blocks) + i] = (int) data_block[j];
            }

            if (i >= qty_short_blocks) {
                interleaved_data[(short_data_block_length * blocks) + (i - qty_short_blocks)] = (int) data_block[short_data_block_length];
            }

            for (j = 0; j < ecc_block_length; j++) {
                interleaved_ecc[(j * blocks) + i] = (int) ecc_block[ecc_block_length - j - 1];
            }

            posn += length_this_block;
        }

        for (j = 0; j < data_cw; j++) {
            fullstream[j] = interleaved_data[j];
        }
        for (j = 0; j < ecc_cw; j++) {
            fullstream[j + data_cw] = interleaved_ecc[j];
        }

        if (debug) {
            System.out.printf("\nData Stream: \n");
            for (j = 0; j < (data_cw + ecc_cw); j++) {
                System.out.printf("%2X ", fullstream[j]);
            }
            System.out.printf("\n");
        }
    }

    private void setup_grid(int size, int version) {
        int i;
        int loopsize, x, y, xcoord, ycoord;
        boolean toggle = true;

        /* Add timing patterns */
        for (i = 0; i < size; i++) {
            if (toggle) {
                grid[(6 * size) + i] = 0x21;
                grid[(i * size) + 6] = 0x21;
                toggle = false;
            } else {
                grid[(6 * size) + i] = 0x20;
                grid[(i * size) + 6] = 0x20;
                toggle = true;
            }
        }

        /* Add finder patterns */
        place_finder(size, 0, 0);
        place_finder(size, 0, size - 7);
        place_finder(size, size - 7, 0);

        /* Add separators */
        for (i = 0; i < 7; i++) {
            grid[(7 * size) + i] = 0x10;
            grid[(i * size) + 7] = 0x10;
            grid[(7 * size) + (size - 1 - i)] = 0x10;
            grid[(i * size) + (size - 8)] = 0x10;
            grid[((size - 8) * size) + i] = 0x10;
            grid[((size - 1 - i) * size) + 7] = 0x10;
        }
        grid[(7 * size) + 7] = 0x10;
        grid[(7 * size) + (size - 8)] = 0x10;
        grid[((size - 8) * size) + 7] = 0x10;

        /* Add alignment patterns */
        if (version != 1) {
            /* Version 1 does not have alignment patterns */

            loopsize = qr_align_loopsize[version - 1];
            for (x = 0; x < loopsize; x++) {
                for (y = 0; y < loopsize; y++) {
                    xcoord = qr_table_e1[((version - 2) * 7) + x];
                    ycoord = qr_table_e1[((version - 2) * 7) + y];

                    if ((grid[(ycoord * size) + xcoord] & 0x10) == 0) {
                        place_align(size, xcoord, ycoord);
                    }
                }
            }
        }

        /* Reserve space for format information */
        for (i = 0; i < 8; i++) {
            grid[(8 * size) + i] += 0x20;
            grid[(i * size) + 8] += 0x20;
            grid[(8 * size) + (size - 1 - i)] = 0x20;
            grid[((size - 1 - i) * size) + 8] = 0x20;
        }
        grid[(8 * size) + 8] += 20;
        grid[((size - 1 - 7) * size) + 8] = 0x21; /* Dark Module from Figure 25 */

        /* Reserve space for version information */
        if (version >= 7) {
            for (i = 0; i < 6; i++) {
                grid[((size - 9) * size) + i] = 0x20;
                grid[((size - 10) * size) + i] = 0x20;
                grid[((size - 11) * size) + i] = 0x20;
                grid[(i * size) + (size - 9)] = 0x20;
                grid[(i * size) + (size - 10)] = 0x20;
                grid[(i * size) + (size - 11)] = 0x20;
            }
        }
    }

    private void place_finder(int size, int x, int y) {
        int xp, yp;

        int finder[] = {
            1, 1, 1, 1, 1, 1, 1,
            1, 0, 0, 0, 0, 0, 1,
            1, 0, 1, 1, 1, 0, 1,
            1, 0, 1, 1, 1, 0, 1,
            1, 0, 1, 1, 1, 0, 1,
            1, 0, 0, 0, 0, 0, 1,
            1, 1, 1, 1, 1, 1, 1
        };

        for (xp = 0; xp < 7; xp++) {
            for (yp = 0; yp < 7; yp++) {
                if (finder[xp + (7 * yp)] == 1) {
                    grid[((yp + y) * size) + (xp + x)] = 0x11;
                } else {
                    grid[((yp + y) * size) + (xp + x)] = 0x10;
                }
            }
        }
    }

    private void place_align(int size, int x, int y) {
        int xp, yp;

        int alignment[] = {
            1, 1, 1, 1, 1,
            1, 0, 0, 0, 1,
            1, 0, 1, 0, 1,
            1, 0, 0, 0, 1,
            1, 1, 1, 1, 1
        };

        x -= 2;
        y -= 2; /* Input values represent centre of pattern */

        for (xp = 0; xp < 5; xp++) {
            for (yp = 0; yp < 5; yp++) {
                if (alignment[xp + (5 * yp)] == 1) {
                    grid[((yp + y) * size) + (xp + x)] = 0x11;
                } else {
                    grid[((yp + y) * size) + (xp + x)] = 0x10;
                }
            }
        }
    }

    private void populate_grid(int size, int cw) {
        boolean goingUp = true;
        int row = 0; /* right hand side */

        int i, n, x, y;

        n = cw * 8;
        y = size - 1;
        i = 0;
        do {
            x = (size - 2) - (row * 2);
            if (x < 6)
                x--; /* skip over vertical timing pattern */

            if ((grid[(y * size) + (x + 1)] & 0xf0) == 0) {
                if (cwbit(i)) {
                    grid[(y * size) + (x + 1)] = 0x01;
                } else {
                    grid[(y * size) + (x + 1)] = 0x00;
                }
                i++;
            }

            if (i < n) {
                if ((grid[(y * size) + x] & 0xf0) == 0) {
                    if (cwbit(i)) {
                        grid[(y * size) + x] = 0x01;
                    } else {
                        grid[(y * size) + x] = 0x00;
                    }
                    i++;
                }
            }

            if (goingUp) {
                y--;
            } else {
                y++;
            }
            if (y == -1) {
                /* reached the top */
                row++;
                y = 0;
                goingUp = false;
            }
            if (y == size) {
                /* reached the bottom */
                row++;
                y = size - 1;
                goingUp = true;
            }
        } while (i < n);
    }

    private boolean cwbit(int i) {
        boolean resultant = false;

        if ((fullstream[i / 8] & (0x80 >> (i % 8))) != 0) {
            resultant = true;
        }

        return resultant;
    }

    private int apply_bitmask(int size) {
        int x, y;
        char p;
        int local_pattern;
        int best_val, best_pattern;
        int[] penalty = new int[8];
        byte[] mask = new byte[size * size];
        eval = new byte[size * size];


        /* Perform data masking */
        for (x = 0; x < size; x++) {
            for (y = 0; y < size; y++) {
                mask[(y * size) + x] = 0x00;

                if ((grid[(y * size) + x] & 0xf0) == 0) {
                    if (((y + x) & 1) == 0) {
                        mask[(y * size) + x] += 0x01;
                    }
                    if ((y & 1) == 0) {
                        mask[(y * size) + x] += 0x02;
                    }
                    if ((x % 3) == 0) {
                        mask[(y * size) + x] += 0x04;
                    }
                    if (((y + x) % 3) == 0) {
                        mask[(y * size) + x] += 0x08;
                    }
                    if ((((y / 2) + (x / 3)) & 1) == 0) {
                        mask[(y * size) + x] += 0x10;
                    }
                    if ((((y * x) & 1) + ((y * x) % 3)) == 0) {
                        mask[(y * size) + x] += 0x20;
                    }
                    if (((((y * x) & 1) + ((y * x) % 3)) & 1) == 0) {
                        mask[(y * size) + x] += 0x40;
                    }
                    if (((((y + x) & 1) + ((y * x) % 3)) & 1) == 0) {
                        mask[(y * size) + x] += 0x80;
                    }
                }
            }
        }

        for (x = 0; x < size; x++) {
            for (y = 0; y < size; y++) {
                if ((grid[(y * size) + x] & 0x01) != 0) {
                    p = 0xff;
                } else {
                    p = 0x00;
                }

                eval[(y * size) + x] = (byte)(mask[(y * size) + x] ^ p);
            }
        }


        /* Evaluate result */
        for (local_pattern = 0; local_pattern < 8; local_pattern++) {
            penalty[local_pattern] = evaluate(size, local_pattern);
        }

        best_pattern = 0;
        best_val = penalty[0];
        for (local_pattern = 1; local_pattern < 8; local_pattern++) {
            if (penalty[local_pattern] < best_val) {
                best_pattern = local_pattern;
                best_val = penalty[local_pattern];
            }
        }

        /* Apply mask */
        for (x = 0; x < size; x++) {
            for (y = 0; y < size; y++) {
                if ((mask[(y * size) + x] & (0x01 << best_pattern)) != 0) {
                    if ((grid[(y * size) + x] & 0x01) != 0) {
                        grid[(y * size) + x] = 0x00;
                    } else {
                        grid[(y * size) + x] = 0x01;
                    }
                }
            }
        }

        return best_pattern;
    }

    private int evaluate(int size, int pattern) {
        int x, y, block;
        int result = 0;
        int state;
        int p;
        int weight;
        int dark_mods;
        int percentage, k;
        byte[] local = new byte[size * size];


        for (x = 0; x < size; x++) {
            for (y = 0; y < size; y++) {
                if ((eval[(y * size) + x] & (0x01 << pattern)) != 0) {
                    local[(y * size) + x] = '1';
                } else {
                    local[(y * size) + x] = '0';
                }
            }
        }

        /* Test 1: Adjacent modules in row/column in same colour */
        /* Vertical */
        for (x = 0; x < size; x++) {
            state = local[x];
            block = 0;
            for (y = 0; y < size; y++) {
                if (local[(y * size) + x] == state) {
                    block++;
                } else {
                    if (block > 5) {
                        result += (3 + block);
                    }
                    block = 0;
                    state = local[(y * size) + x];
                }
            }
            if (block > 5) {
                result += (3 + block);
            }
        }

        /* Horizontal */
        for (y = 0; y < size; y++) {
            state = local[y * size];
            block = 0;
            for (x = 0; x < size; x++) {
                if (local[(y * size) + x] == state) {
                    block++;
                } else {
                    if (block > 5) {
                        result += (3 + block);
                    }
                    block = 0;
                    state = local[(y * size) + x];
                }
            }
            if (block > 5) {
                result += (3 + block);
            }
        }

        /* Test 2 is not implimented */

        /* Test 3: 1:1:3:1:1 ratio pattern in row/column */
        /* Vertical */
        for (x = 0; x < size; x++) {
            for (y = 0; y < (size - 7); y++) {
                p = 0;
                for(weight = 0; weight < 7; weight++) {
                    if (local[((y + weight) * size) + x] == '1') {
                        p += (0x40 >> weight);
                    }
                }
                if (p == 0x5d) {
                    result += 40;
                }
            }
        }

        /* Horizontal */
        for (y = 0; y < size; y++) {
            for (x = 0; x < (size - 7); x++) {
                p = 0;
                for(weight = 0; weight < 7; weight++) {
                    if (local[(y * size) + x + weight] == '1') {
                        p += (0x40 >> weight);
                    }
                }
                if (p == 0x5d) {
                    result += 40;
                }
            }
        }

        /* Test 4: Proportion of dark modules in entire symbol */
        dark_mods = 0;
        for (x = 0; x < size; x++) {
            for (y = 0; y < size; y++) {
                if (local[(y * size) + x] == '1') {
                    dark_mods++;
                }
            }
        }
        percentage = 100 * (dark_mods / (size * size));
        if (percentage <= 50) {
            k = ((100 - percentage) - 50) / 5;
        } else {
            k = (percentage - 50) / 5;
        }

        result += 10 * k;

        return result;
    }

    private void add_format_info(int size, eccMode ecc_level, int pattern) {
        /* Add format information to grid */

        int format = pattern;
        int seq;
        int i;

        switch (ecc_level) {
        case L:
            format += 0x08;
            break;
        case Q:
            format += 0x18;
            break;
        case H:
            format += 0x10;
            break;
        }

        seq = qr_annex_c[format];

        for (i = 0; i < 6; i++) {
            grid[(i * size) + 8] += (seq >> i) & 0x01;
        }

        for (i = 0; i < 8; i++) {
            grid[(8 * size) + (size - i - 1)] += (seq >> i) & 0x01;
        }

        for (i = 0; i < 6; i++) {
            grid[(8 * size) + (5 - i)] += (seq >> (i + 9)) & 0x01;
        }

        for (i = 0; i < 7; i++) {
            grid[(((size - 7) + i) * size) + 8] += (seq >> (i + 8)) & 0x01;
        }

        grid[(7 * size) + 8] += (seq >> 6) & 0x01;
        grid[(8 * size) + 8] += (seq >> 7) & 0x01;
        grid[(8 * size) + 7] += (seq >> 8) & 0x01;
    }

    private void add_version_info(int size, int version) {
        /* Add version information */
        int i;

        long version_data = qr_annex_d[version - 7];
        for (i = 0; i < 6; i++) {
            grid[((size - 11) * size) + i] += (version_data >> (i * 3)) & 0x01;
            grid[((size - 10) * size) + i] += (version_data >> ((i * 3) + 1)) & 0x01;
            grid[((size - 9) * size) + i] += (version_data >> ((i * 3) + 2)) & 0x01;
            grid[(i * size) + (size - 11)] += (version_data >> (i * 3)) & 0x01;
            grid[(i * size) + (size - 10)] += (version_data >> ((i * 3) + 1)) & 0x01;
            grid[(i * size) + (size - 9)] += (version_data >> ((i * 3) + 2)) & 0x01;
        }
    }
}
