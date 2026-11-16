package org.spoutcraftredux.protocol.message;

import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * Client to server: the player used a widget on a popup.
 *
 * @param screenId the popup's id
 * @param widgetId the widget's id
 * @param action   one of the action constants
 * @param flag     the new state for CHECK
 * @param index    the selected row for SELECT, -1 for none
 * @param number   the new position for SLIDE, 0 to 1
 * @param text     the text for TEXT and SUBMIT
 */
public record ControlEvent(int screenId, int widgetId, int action, boolean flag, int index, float number, String text) {
    public static final String CHANNEL = "spoutcraftredux:control_event";
    public static final int CLICK = 0;
    public static final int CHECK = 1;
    public static final int TEXT = 2;
    public static final int SUBMIT = 3;
    public static final int SLIDE = 4;
    public static final int SELECT = 5;

    public ControlEvent {
        if (action < CLICK || action > SELECT) {
            throw new ProtocolException("Unknown control action " + action);
        }
        if (!(number >= 0.0F && number <= 1.0F)) {
            throw new ProtocolException("Slider position outside 0 to 1");
        }
    }

    public static ControlEvent click(int screenId, int widgetId) {
        return new ControlEvent(screenId, widgetId, CLICK, false, -1, 0.0F, "");
    }

    public byte[] encode() {
        byte[] bytes = new MessageWriter().writeVarInt(this.screenId).writeVarInt(this.widgetId).writeVarInt(this.action)
            .writeBoolean(this.flag).writeVarInt(this.index).writeVarInt(Float.floatToIntBits(this.number))
            .writeString(this.text, 4096)
            .toByteArray();
        if (bytes.length > Limits.MAX_SERVERBOUND_BYTES) {
            throw new ProtocolException("Control event too large");
        }
        return bytes;
    }

    public static ControlEvent decode(byte[] data) {
        if (data.length > Limits.MAX_SERVERBOUND_BYTES) {
            throw new ProtocolException("Control event too large");
        }
        MessageReader in = new MessageReader(data);
        ControlEvent event = new ControlEvent(in.readVarInt(), in.readVarInt(), in.readVarInt(), in.readBoolean(),
            in.readVarInt(), Float.intBitsToFloat(in.readVarInt()), in.readString(4096));
        in.expectEnd();
        return event;
    }
}
