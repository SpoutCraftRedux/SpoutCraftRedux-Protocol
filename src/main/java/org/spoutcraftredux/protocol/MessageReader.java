package org.spoutcraftredux.protocol;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

/** Bounds-checked reader over a received message. */
public final class MessageReader {
    private final byte[] buf;
    private int pos;

    public MessageReader(byte[] buf) {
        this.buf = buf;
    }

    public int readByte() {
        if (pos >= buf.length) {
            throw new ProtocolException("Unexpected end of message");
        }
        return buf[pos++] & 0xFF;
    }

    public boolean readBoolean() {
        int value = readByte();
        if (value > 1) {
            throw new ProtocolException("Invalid boolean " + value);
        }
        return value == 1;
    }

    public int readVarInt() {
        int value = 0;
        for (int shift = 0; shift < 35; shift += 7) {
            int b = readByte();
            value |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return value;
            }
        }
        throw new ProtocolException("VarInt too long");
    }

    public int readLength(int max) {
        int length = readVarInt();
        if (length < 0 || length > max) {
            throw new ProtocolException("Length " + length + " outside 0.." + max);
        }
        return length;
    }

    public String readString(int maxLength) {
        int bytes = readLength(maxLength * 3);
        if (bytes > remaining()) {
            throw new ProtocolException("String runs past end of message");
        }
        String value = new String(buf, pos, bytes, StandardCharsets.UTF_8);
        pos += bytes;
        if (value.length() > maxLength) {
            throw new ProtocolException("String longer than " + maxLength);
        }
        return value;
    }

    public Set<String> readStrings(int maxCount, int maxLength) {
        int count = readLength(maxCount);
        Set<String> values = new LinkedHashSet<>(count);
        for (int i = 0; i < count; i++) {
            values.add(readString(maxLength));
        }
        return values;
    }

    public byte[] readBytes(int length) {
        if (length < 0 || length > remaining()) {
            throw new ProtocolException("Byte array runs past end of message");
        }
        byte[] bytes = java.util.Arrays.copyOfRange(this.buf, this.pos, this.pos + length);
        this.pos += length;
        return bytes;
    }

    public int remaining() {
        return buf.length - pos;
    }

    /** Fails if bytes are left over, which means the sender and receiver disagree on the format. */
    public void expectEnd() {
        if (remaining() != 0) {
            throw new ProtocolException(remaining() + " trailing bytes");
        }
    }
}
