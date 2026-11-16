package org.spoutcraftredux.protocol.message;

import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;

/**
 * Client to server: whether the client registered all of {@link Content}.
 *
 * @param error empty on success, otherwise why the client could not register the content
 */
public record ContentAck(String error) {
    public static final String CHANNEL = "spoutcraftredux:content_ack";
    public static final int MAX_ERROR_LENGTH = 512;

    public boolean ok() {
        return this.error.isEmpty();
    }

    public byte[] encode() {
        return new MessageWriter().writeString(this.error, MAX_ERROR_LENGTH).toByteArray();
    }

    public static ContentAck decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        ContentAck ack = new ContentAck(in.readString(MAX_ERROR_LENGTH));
        in.expectEnd();
        return ack;
    }
}
