package org.spoutcraftredux.protocol.message;

import java.util.ArrayList;
import java.util.List;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;
import org.spoutcraftredux.protocol.gui.WidgetSpec;

/**
 * Server to client: add widgets, or replace widgets with the same ids.
 *
 * @param screenId {@link #HUD} for the in-game HUD, otherwise an open popup's id
 * @param widgets  the widgets to add or replace
 */
public record WidgetUpdate(int screenId, List<WidgetSpec> widgets) {
    public static final String CHANNEL = "spoutcraftredux:widget_update";
    public static final int HUD = 0;

    public WidgetUpdate {
        if (screenId < 0 || widgets.size() > Limits.MAX_WIDGETS) {
            throw new ProtocolException("Bad widget update");
        }
        widgets = List.copyOf(widgets);
    }

    public byte[] encode() {
        MessageWriter out = new MessageWriter().writeVarInt(this.screenId).writeVarInt(this.widgets.size());
        this.widgets.forEach(widget -> widget.write(out));
        return out.toByteArray();
    }

    public static WidgetUpdate decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        int screenId = in.readVarInt();
        int count = in.readLength(Limits.MAX_WIDGETS);
        List<WidgetSpec> widgets = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            widgets.add(WidgetSpec.read(in));
        }
        in.expectEnd();
        return new WidgetUpdate(screenId, widgets);
    }
}
