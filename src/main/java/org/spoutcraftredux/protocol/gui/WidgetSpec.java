package org.spoutcraftredux.protocol.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * One widget as sent over the wire. Common layout fields are fixed; everything type-specific travels in a small
 * property map so new properties don't need a new message format.
 *
 * @param id          unique within its screen, 1 or more
 * @param type        one of {@link WidgetTypes}
 * @param x           x offset from the anchor point
 * @param y           y offset from the anchor point
 * @param width       width in GUI pixels
 * @param height      height in GUI pixels
 * @param anchor      one of {@link Anchors}
 * @param visible     whether it is drawn
 * @param enabled     whether it reacts to input
 * @param tooltipJson tooltip as a JSON text component, or empty for none
 * @param properties  type-specific values: Boolean, Integer, Float, String or a List of String
 */
public record WidgetSpec(int id, String type, int x, int y, int width, int height, int anchor, boolean visible,
                         boolean enabled, String tooltipJson, Map<String, Object> properties) {
    private static final int BOOLEAN = 0;
    private static final int INT = 1;
    private static final int FLOAT = 2;
    private static final int STRING = 3;
    private static final int STRING_LIST = 4;

    public WidgetSpec {
        if (id < 1) {
            throw new ProtocolException("Widget id must be 1 or more");
        }
        if (anchor < 0 || anchor > Anchors.SCALE) {
            throw new ProtocolException("Bad anchor " + anchor);
        }
        if (properties.size() > Limits.MAX_WIDGET_PROPERTIES) {
            throw new ProtocolException("Too many widget properties");
        }
        properties = Map.copyOf(properties);
    }

    public boolean bool(String key, boolean fallback) {
        return this.properties.get(key) instanceof Boolean value ? value : fallback;
    }

    public int integer(String key, int fallback) {
        return this.properties.get(key) instanceof Integer value ? value : fallback;
    }

    public float number(String key, float fallback) {
        return this.properties.get(key) instanceof Float value ? value : fallback;
    }

    public String string(String key, String fallback) {
        return this.properties.get(key) instanceof String value ? value : fallback;
    }

    @SuppressWarnings("unchecked")
    public List<String> strings(String key) {
        return this.properties.get(key) instanceof List<?> value ? (List<String>) value : List.of();
    }

    public void write(MessageWriter out) {
        out.writeVarInt(this.id)
            .writeString(this.type, Limits.MAX_PROPERTY_KEY_LENGTH)
            .writeVarInt(this.x).writeVarInt(this.y).writeVarInt(this.width).writeVarInt(this.height)
            .writeVarInt(this.anchor)
            .writeBoolean(this.visible).writeBoolean(this.enabled)
            .writeString(this.tooltipJson, Limits.MAX_COMPONENT_JSON_LENGTH)
            .writeVarInt(this.properties.size());
        for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
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

    public static WidgetSpec read(MessageReader in) {
        int id = in.readVarInt();
        String type = in.readString(Limits.MAX_PROPERTY_KEY_LENGTH);
        int x = in.readVarInt();
        int y = in.readVarInt();
        int width = in.readVarInt();
        int height = in.readVarInt();
        int anchor = in.readVarInt();
        boolean visible = in.readBoolean();
        boolean enabled = in.readBoolean();
        String tooltip = in.readString(Limits.MAX_COMPONENT_JSON_LENGTH);
        int count = in.readLength(Limits.MAX_WIDGET_PROPERTIES);
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
        return new WidgetSpec(id, type, x, y, width, height, anchor, visible, enabled, tooltip, properties);
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
