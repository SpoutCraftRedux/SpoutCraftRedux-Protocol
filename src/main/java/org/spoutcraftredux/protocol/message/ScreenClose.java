package org.spoutcraftredux.protocol.message;

import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;

/**
 * Close a popup. Sent by the server to close it, and by the client when the player closes it.
 *
 * @param screenId the popup's id
 */
public record ScreenClose(int screenId) {
    public static final String CLIENTBOUND = "spoutcraftredux:screen_close";
    public static final String SERVERBOUND = "spoutcraftredux:screen_closed";

    public byte[] encode() {
        return new MessageWriter().writeVarInt(this.screenId).toByteArray();
    }

    public static ScreenClose decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        ScreenClose message = new ScreenClose(in.readVarInt());
        in.expectEnd();
        return message;
    }
}
