package org.spoutcraftredux.protocol.message;

import java.util.ArrayList;
import java.util.List;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * Server to client: remove widgets.
 *
 * @param screenId  {@link WidgetUpdate#HUD} or a popup's id
 * @param widgetIds the widgets to remove
 */
public record WidgetRemove(int screenId, List<Integer> widgetIds) {
    public static final String CHANNEL = "spoutcraftredux:widget_remove";

    public WidgetRemove {
        if (screenId < 0 || widgetIds.size() > Limits.MAX_WIDGETS) {
            throw new ProtocolException("Bad widget removal");
        }
        widgetIds = List.copyOf(widgetIds);
    }

    public byte[] encode() {
        MessageWriter out = new MessageWriter().writeVarInt(this.screenId).writeVarInt(this.widgetIds.size());
        this.widgetIds.forEach(out::writeVarInt);
        return out.toByteArray();
    }

    public static WidgetRemove decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        int screenId = in.readVarInt();
        int count = in.readLength(Limits.MAX_WIDGETS);
        List<Integer> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(in.readVarInt());
        }
        in.expectEnd();
        return new WidgetRemove(screenId, ids);
    }
}
