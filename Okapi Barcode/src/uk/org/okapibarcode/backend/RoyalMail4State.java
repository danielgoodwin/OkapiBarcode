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

import java.util.Locale;
import java.awt.Rectangle;

/**
 *
 * @author Robin Stuart <rstuart114@gmail.com>
 */
public class RoyalMail4State extends Symbol {
    /* Handles the 4 State barcodes used in the UK by Royal Mail */

    private String[] RoyalTable = {
        "TTFF", "TDAF", "TDFA", "DTAF", "DTFA", "DDAA", "TADF", "TFTF", "TFDA", 
        "DATF", "DADA", "DFTA", "TAFD", "TFAD", "TFFT", "DAAD", "DAFT", "DFAT", 
        "ATDF", "ADTF", "ADDA", "FTTF", "FTDA", "FDTA", "ATFD", "ADAD", "ADFT", 
        "FTAD", "FTFT", "FDAT", "AADD", "AFTD", "AFDT", "FATD", "FADT", "FFTT"
    };

    private char[] krSet = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 
        'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    @Override
    public boolean encode() {
        String dest;
        int i, top = 0, bottom = 0;
        int row, column;
        int index;

        content = content.toUpperCase(Locale.ENGLISH);
        if(!(content.matches("[0-9A-Z]+?"))) {
            error_msg = "Invalid characters in data";
            return false;
        }
        dest = "A";

        for (i = 0; i < content.length(); i++) {
            index = positionOf(content.charAt(i), krSet);
            dest += RoyalTable[index];
            top += (index + 1) % 6;
            bottom += ((index / 6) + 1) % 6;
        }

        /* calculate check digit */
        row = (top % 6) - 1;
        column = (bottom % 6) - 1;
        if (row == -1) {
            row = 5;
        }
        if (column == -1) {
            column = 5;
        }

        dest += RoyalTable[(6 * row) + column];

        encodeInfo += "Check Digit: " + (int)((6 * row) + column) + "\n";
        
        /* Stop character */
        dest += "F";

        readable = "";
        pattern = new String[1];
        pattern[0] = dest;
        row_count = 1;
        row_height = new int[1];
        row_height[0] = -1;
        plotSymbol();
        return true;
    }

    @Override
    public void plotSymbol() {
        int xBlock;
        int x, y, w, h;

        rect.clear();
        x = 0;
        w = 1;
        y = 0;
        h = 0;
        for (xBlock = 0; xBlock < pattern[0].length(); xBlock++) {
            switch (pattern[0].charAt(xBlock)) {
            case 'A':
                y = 0;
                h = 5;
                break;
            case 'D':
                y = 3;
                h = 5;
                break;
            case 'F':
                y = 0;
                h = 8;
                break;
            case 'T':
                y = 3;
                h = 2;
                break;
            }

            Rectangle thisrect = new Rectangle(x, y, w, h);
            rect.add(thisrect);

            x += 2.0;
        }
        symbol_width = pattern[0].length() * 3;
        symbol_height = 8;
    }
}
