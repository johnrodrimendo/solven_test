package com.affirm.common.util;

import java.io.IOException;
import java.util.List;

public class CSVutils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTES = '\'';

    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains(",")) {
            result = result.replaceAll(",", " ");
        }
        return result.trim();

    }

    public static String writeLine(List<String> values) throws IOException {

        boolean first = true;
        char separators = DEFAULT_SEPARATOR;


        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            sb.append(followCVSformat(value));
            first = false;
        }
        sb.append("\n");
        return sb.toString();

    }

}