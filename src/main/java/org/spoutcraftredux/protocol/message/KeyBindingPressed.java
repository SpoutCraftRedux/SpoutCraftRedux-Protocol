package org.spoutcraftredux.protocol.message;

import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * Client to server: the player pressed or released one of the server's key bindings.
 *
 * @param id      the binding's id
 * @param pressed true when pressed, false when released
 */
public record KeyBindingPressed(String id, boolean pressed) {
    public static final String CHANNEL = "spoutcraftredux:key_binding_pressed";

    public byte[] encode() {
        return new MessageWriter()
            .writeString(this.id, Limits.MAX_IDENTIFIER_LENGTH)
            .writeBoolean(this.pressed)
            .toByteArray();
    }

    public static KeyBindingPressed decode(byte[] data) {
        if (data.length > Limits.MAX_SERVERBOUND_BYTES) {
            throw new ProtocolException("KeyBindingPressed too large");
        }
        MessageReader in = new MessageReader(data);
        KeyBindingPressed message = new KeyBindingPressed(in.readString(Limits.MAX_IDENTIFIER_LENGTH), in.readBoolean());
        in.expectEnd();
        return message;
    }
}
