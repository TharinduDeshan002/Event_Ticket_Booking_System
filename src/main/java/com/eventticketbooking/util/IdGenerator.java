package com.eventticketbooking.util;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String nextId(String prefix, List<String> existingIds) {
        int max = 0;
        Pattern pattern = Pattern.compile("^" + prefix + "(\\d+)$");
        for (String id : existingIds) {
            Matcher matcher = pattern.matcher(id);
            if (matcher.matches()) {
                max = Math.max(max, Integer.parseInt(matcher.group(1)));
            }
        }
        return prefix + String.format("%03d", max + 1);
    }

    public static <T> String nextId(String prefix, List<T> items, Function<T, String> idExtractor) {
        return nextId(prefix, items.stream().map(idExtractor).toList());
    }
}
