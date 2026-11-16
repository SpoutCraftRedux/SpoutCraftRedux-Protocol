package org.spoutcraftredux.protocol.message;

import java.util.ArrayList;
import java.util.List;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;
import org.spoutcraftredux.protocol.gui.WidgetSpec;

/**
 * Server to client: open a popup screen, replacing any popup the server opened before.
 *
 * @param screenId  the server's id for this screen, 1 or more
 * @param titleJson the title as a JSON text component, used for narration
 * @param widgets   the widgets on the screen
 */
public record ScreenOpen(int screenId, String titleJson, List<WidgetSpec> widgets) {
    public static final String CHANNEL = "spoutcraftredux:screen_open";

    public ScreenOpen {
        if (screenId < 1) {
            throw new ProtocolException("Screen id must be 1 or more");
        }
        if (widgets.size() > Limits.MAX_WIDGETS) {
            throw new ProtocolException("Too many widgets");
        }
        widgets = List.copyOf(widgets);
    }

    public byte[] encode() {
        MessageWriter out = new MessageWriter().writeVarInt(this.screenId)
            .writeString(this.titleJson, Limits.MAX_COMPONENT_JSON_LENGTH)
            .writeVarInt(this.widgets.size());
        this.widgets.forEach(widget -> widget.write(out));
        return out.toByteArray();
    }

    public static ScreenOpen decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        int screenId = in.readVarInt();
        String title = in.readString(Limits.MAX_COMPONENT_JSON_LENGTH);
        int count = in.readLength(Limits.MAX_WIDGETS);
        List<WidgetSpec> widgets = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            widgets.add(WidgetSpec.read(in));
        }
        in.expectEnd();
        return new ScreenOpen(screenId, title, widgets);
    }
}
