package org.spoutcraftredux.protocol.message;

import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * Server to client: show the notification popup.
 *
 * @param titleJson     title as a JSON text component
 * @param messageJson   message as a JSON text component
 * @param itemId        item drawn as the icon, such as {@code minecraft:diamond}
 * @param displayMillis how long it stays up; 0 means the default of 7.5 seconds
 */
public record Notification(String titleJson, String messageJson, String itemId, int displayMillis) {
    public static final String CHANNEL = "spoutcraftredux:notification";
    public static final int DEFAULT_DISPLAY_MILLIS = 7500;
    public static final int MAX_DISPLAY_MILLIS = 60_000;

    public Notification {
        if (displayMillis < 0 || displayMillis > MAX_DISPLAY_MILLIS) {
            throw new ProtocolException("Display time " + displayMillis + " outside 0.." + MAX_DISPLAY_MILLIS);
        }
    }

    public int effectiveDisplayMillis() {
        return displayMillis == 0 ? DEFAULT_DISPLAY_MILLIS : displayMillis;
    }

    public byte[] encode() {
        return new MessageWriter()
            .writeString(titleJson, Limits.MAX_COMPONENT_JSON_LENGTH)
            .writeString(messageJson, Limits.MAX_COMPONENT_JSON_LENGTH)
            .writeString(itemId, Limits.MAX_IDENTIFIER_LENGTH)
            .writeVarInt(displayMillis)
            .toByteArray();
    }

    public static Notification decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        Notification notification = new Notification(
            in.readString(Limits.MAX_COMPONENT_JSON_LENGTH),
            in.readString(Limits.MAX_COMPONENT_JSON_LENGTH),
            in.readString(Limits.MAX_IDENTIFIER_LENGTH),
            in.readVarInt()
        );
        in.expectEnd();
        return notification;
    }
}
