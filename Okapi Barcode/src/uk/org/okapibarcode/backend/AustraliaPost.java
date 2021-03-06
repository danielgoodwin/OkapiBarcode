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

import java.awt.Rectangle;
/**
 * Implements Australia Post 4-State Barcode
 * Specified at http://auspost.com.au/media/documents/a-guide-to-printing-the-4state-barcode-v31-mar2012.pdf
 *
 * @author Robin Stuart <rstuart114@gmail.com>
 * @version 0.1
 */
public class AustraliaPost extends Symbol{

    private char[] characterSet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' ', '#'};

    private String[] nEncodingTable = {"00", "01", "02", "10", "11", "12", "20", "21", "22", "30"};

    private String[] cEncodingTable = {"222", "300", "301", "302", "310", "311", "312", "320", "321", "322",
            "000", "001", "002", "010", "011", "012", "020", "021", "022", "100", "101", "102", "110",
            "111", "112", "120", "121", "122", "200", "201", "202", "210", "211", "212", "220", "221",
            "023", "030", "031", "032", "033", "103", "113", "123", "130", "131", "132", "133", "203",
            "213", "223", "230", "231", "232", "233", "303", "313", "323", "330", "331", "332", "333",
            "003", "013"};

    private String[] barValueTable = {"000", "001", "002", "003", "010", "011", "012", "013", "020", "021",
            "022", "023", "030", "031", "032", "033", "100", "101", "102", "103", "110", "111", "112",
            "113", "120", "121", "122", "123", "130", "131", "132", "133", "200", "201", "202", "203",
            "210", "211", "212", "213", "220", "221", "222", "223", "230", "231", "232", "233", "300",
            "301", "302", "303", "310", "311", "312", "313", "320", "321", "322", "323", "330", "331",
            "332", "333"};
    
    private enum ausMode {AUSPOST, AUSREPLY, AUSROUTE, AUSREDIRECT};
    
    private ausMode mode;
    
    public AustraliaPost() {
        mode = ausMode.AUSPOST;
    }
    
    public void setPostMode() {
        mode = ausMode.AUSPOST;
    }
    
    public void setReplyMode() {
        mode = ausMode.AUSREPLY;
    }
    
    public void setRouteMode() {
        mode = ausMode.AUSROUTE;
    }
    
    public void setRedirectMode() {
        mode = ausMode.AUSREDIRECT;
    }
    
    @Override
    public boolean encode() {
        String formatControlCode = "00";
        String deliveryPointId;
        String barStateValues;
        String zeroPaddedInput = "";
        int i;
        
        switch(mode) {
            case AUSPOST:
                switch(content.length()) {
                    case 8: formatControlCode = "11";
                        break;
                    case 13: formatControlCode = "59";
                        break;
                    case 16: formatControlCode = "59";
                        if (!(content.matches("[0-9]+?"))) {
                            error_msg = "Invalid characters in data";
                            return false;
                        }
                        break;
                    case 18: formatControlCode = "62";
                        break;
                    case 23: formatControlCode = "62";
                        if (!(content.matches("[0-9]+?"))) {
                            error_msg = "Invalid characters in data";
                            return false;
                        }                    
                        break;
                    default: error_msg = "Auspost input is wrong length";
                        return false;
                }
                break;
            case AUSREPLY:
                if (content.length() > 8) {
                    error_msg = "Auspost input is too long";
                    return false;
                } else {
                    formatControlCode = "45";
                }
                break;
            case AUSROUTE:
                if (content.length() > 8) {
                    error_msg = "Auspost input is too long";
                    return false;
                } else {
                    formatControlCode = "87";
                }
                break;
            case AUSREDIRECT:
                if (content.length() > 8) {
                    error_msg = "Auspost input is too long";
                    return false;
                } else {
                    formatControlCode = "92";
                }
                break;                
        }
        
        encodeInfo += "FCC: " + formatControlCode + '\n';
        
        if(mode != ausMode.AUSPOST) {
            for (i = content.length(); i < 8; i++) {
                zeroPaddedInput += "0";
            }
        }
        zeroPaddedInput += content;
        
        if (!(content.matches("[0-9A-Za-z #]+?"))) {
            error_msg = "Invalid characters in data";
            return false;
        }
        
        /* Verify that the first 8 characters are numbers */
        deliveryPointId = zeroPaddedInput.substring(0, 8);

        if (!(deliveryPointId.matches("[0-9]+?"))) {
            error_msg = "Invalid characters in DPID";
            return false;
        }
        
        encodeInfo += "DPID: " + deliveryPointId + '\n';
        
        /* Start */
        barStateValues = "13";
        
        /* Encode the FCC */
        for(i = 0; i < 2; i++) {
            barStateValues += nEncodingTable[formatControlCode.charAt(i) - '0'];
        }
        
        /* Delivery Point Identifier (DPID) */
        for(i = 0; i < 8; i++) {
            barStateValues += nEncodingTable[deliveryPointId.charAt(i) - '0'];
        }
        
        /* Customer Information */
        switch(zeroPaddedInput.length()) {
            case 13:
            case 18:
                for(i = 8; i < zeroPaddedInput.length(); i++) {
                    barStateValues += cEncodingTable[positionOf(zeroPaddedInput.charAt(i), characterSet)];
                }
                break;
            case 16:
            case 23:
                for(i = 8; i < zeroPaddedInput.length(); i++) {
                    barStateValues += nEncodingTable[positionOf(zeroPaddedInput.charAt(i), characterSet)];
                }
                break;
        }
    
        /* Filler bar */
        switch(barStateValues.length()) {
            case 22:
            case 37:
            case 52:
                barStateValues += "3";
                break;
        }
        
        /* Reed Solomon error correction */
        barStateValues += calcReedSolomon(barStateValues);
        
        /* Stop character */
        barStateValues += "13";
        
        encodeInfo += "Total length: " + barStateValues.length() + '\n';
        
        readable = "";
        pattern = new String[1];
        pattern[0] = barStateValues;
        row_count = 1;
        row_height = new int[1];
        row_height[0] = -1;
        plotSymbol();
        return true;
    }
    
    private String calcReedSolomon(String oldBarStateValues) {
        ReedSolomon rs = new ReedSolomon();
        String newBarStateValues = "";
        
        /* Adds Reed-Solomon error correction to auspost */
        
        int barStateCount;
        int tripleValueCount = 0;
        int[] tripleValue = new int[31];
        
        for(barStateCount = 2; barStateCount < oldBarStateValues.length(); barStateCount += 3, tripleValueCount++) {
            tripleValue[tripleValueCount] = barStateToDecimal(oldBarStateValues.charAt(barStateCount), 4)
                    + barStateToDecimal(oldBarStateValues.charAt(barStateCount + 1), 2)
                    + barStateToDecimal(oldBarStateValues.charAt(barStateCount + 2), 0);
	}
        
        rs.init_gf(0x43);
        rs.init_code(4, 1);
        rs.encode(tripleValueCount, tripleValue);
        
        for(barStateCount = 4; barStateCount > 0; barStateCount--) {
            newBarStateValues += barValueTable[rs.getResult(barStateCount - 1)];
        }
        
        return newBarStateValues;
    }
    
    private int barStateToDecimal (char oldBarStateValues, int shift) {
        return (oldBarStateValues - '0') << shift;
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
        for(xBlock = 0; xBlock < pattern[0].length(); xBlock++) {
            switch(pattern[0].charAt(xBlock)) {
                case '1':
                    y = 0;
                    h = 5;
                    break;
                case '2':
                    y = 3;
                    h = 5;
                    break;
                case '0':
                    y = 0;
                    h = 8;
                    break;
                case '3':
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
