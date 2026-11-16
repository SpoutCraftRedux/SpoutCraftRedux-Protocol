package org.spoutcraftredux.protocol.message;

import java.util.ArrayList;
import java.util.List;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * Server to client: how vanilla HUD elements are shown. Replaces everything sent before; elements not listed are
 * drawn normally.
 *
 * @param elements the changed elements
 */
public record VanillaHud(List<Element> elements) {
    public static final String CHANNEL = "spoutcraftredux:vanilla_hud";
    private static final int MAX_ELEMENTS = 64;

    /**
     * One element's settings.
     *
     * @param name    one of {@link org.spoutcraftredux.protocol.gui.VanillaElements}
     * @param visible whether it is drawn
     * @param x       x offset in GUI pixels
     * @param y       y offset in GUI pixels
     */
    public record Element(String name, boolean visible, int x, int y) {
    }

    public VanillaHud {
        if (elements.size() > MAX_ELEMENTS) {
            throw new ProtocolException("Too many HUD elements");
        }
        elements = List.copyOf(elements);
    }

    public byte[] encode() {
        MessageWriter out = new MessageWriter().writeVarInt(this.elements.size());
        for (Element element : this.elements) {
            out.writeString(element.name(), Limits.MAX_PROPERTY_KEY_LENGTH).writeBoolean(element.visible())
                .writeVarInt(element.x()).writeVarInt(element.y());
        }
        return out.toByteArray();
    }

    public static VanillaHud decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        int count = in.readLength(MAX_ELEMENTS);
        List<Element> elements = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            elements.add(new Element(in.readString(Limits.MAX_PROPERTY_KEY_LENGTH), in.readBoolean(), in.readVarInt(), in.readVarInt()));
        }
        in.expectEnd();
        return new VanillaHud(elements);
    }
}
