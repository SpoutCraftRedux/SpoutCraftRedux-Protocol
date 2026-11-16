package org.spoutcraftredux.protocol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Encodes small maps of Boolean, Integer, Float, String or List of String values. */
public final class PropertyMap {
    private static final int BOOLEAN = 0;
    private static final int INT = 1;
    private static final int FLOAT = 2;
    private static final int STRING = 3;
    private static final int STRING_LIST = 4;

    private PropertyMap() {
    }

    public static void write(MessageWriter out, Map<String, Object> properties) {
        out.writeVarInt(properties.size());
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            out.writeString(entry.getKey(), Limits.MAX_PROPERTY_KEY_LENGTH);
            switch (entry.getValue()) {
                case Boolean value -> out.writeByte(BOOLEAN).writeBoolean(value);
                case Integer value -> out.writeByte(INT).writeVarInt(value);
                case Float value -> out.writeByte(FLOAT).writeVarInt(Float.floatToIntBits(value));
                case String value -> out.writeByte(STRING).writeString(value, Limits.MAX_PROPERTY_STRING_LENGTH);
                case List<?> value -> {
                    out.writeByte(STRING_LIST);
                    List<String> strings = new ArrayList<>(value.size());
                    for (Object item : value) {
                        strings.add((String) item);
                    }
                    out.writeStrings(strings, Limits.MAX_LIST_ENTRIES, Limits.MAX_COMPONENT_JSON_LENGTH);
                }
                default -> throw new ProtocolException("Unsupported property type for " + entry.getKey());
            }
        }
    }

    public static Map<String, Object> read(MessageReader in, int maxCount) {
        int count = in.readLength(maxCount);
        Map<String, Object> properties = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            String key = in.readString(Limits.MAX_PROPERTY_KEY_LENGTH);
            Object value = switch (in.readByte()) {
                case BOOLEAN -> in.readBoolean();
                case INT -> in.readVarInt();
                case FLOAT -> Float.intBitsToFloat(in.readVarInt());
                case STRING -> in.readString(Limits.MAX_PROPERTY_STRING_LENGTH);
                case STRING_LIST -> List.copyOf(readOrderedStrings(in));
                default -> throw new ProtocolException("Unknown property type");
            };
            properties.put(key, value);
        }
        return properties;
    }

    // Lists keep their order and duplicates, unlike readStrings
    private static List<String> readOrderedStrings(MessageReader in) {
        int count = in.readLength(Limits.MAX_LIST_ENTRIES);
        List<String> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            values.add(in.readString(Limits.MAX_COMPONENT_JSON_LENGTH));
        }
        return values;
    }
}
