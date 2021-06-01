package me.michaeldevc.option;

import java.util.HashMap;
import java.util.Map;

public class Options {
    private final Map<String, Object> options = new HashMap<>();

    public boolean isOption(String id) {
        return this.options.containsKey(id);
    }

    public <T> T getOption(String id, T defaultValue) {
        return (T)this.options.getOrDefault(id, defaultValue);
    }

    public Map<String, Object> getOptions() {
        return this.options;
    }

    public static class Builder {
        public static Options of(String... args) {
            Options options = new Options();
            byte b;
            int i;
            String[] arrayOfString;
            for (i = (arrayOfString = args).length, b = 0; b < i; ) {
                String arg = arrayOfString[b];
                String[] part = arg.split("=");
                if (part.length > 1) {
                    Object value = part[1];
                    try {
                        value = Integer.valueOf(Integer.parseInt((String)value));
                    } catch (Exception exception) {}
                    if ((value instanceof String && value.equals("true")) || value.equals("false"))
                        try {
                            value = Boolean.valueOf(Boolean.parseBoolean((String)value));
                        } catch (Exception exception) {}
                    options.options.put(part[0].replace("=", ""), value);
                }
                b++;
            }
            return options;
        }
    }
}
