package org.spoutcraftredux.protocol.gui;

import java.util.List;
import java.util.Map;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.PropertyMap;
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
            .writeString(this.tooltipJson, Limits.MAX_COMPONENT_JSON_LENGTH);
        PropertyMap.write(out, this.properties);
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
        Map<String, Object> properties = PropertyMap.read(in, Limits.MAX_WIDGET_PROPERTIES);
        return new WidgetSpec(id, type, x, y, width, height, anchor, visible, enabled, tooltip, properties);
    }
}
