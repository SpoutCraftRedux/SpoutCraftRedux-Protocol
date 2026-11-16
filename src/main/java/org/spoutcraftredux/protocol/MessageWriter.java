package org.spoutcraftredux.protocol;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

/** Growable byte sink for encoding messages. */
public final class MessageWriter {
    private byte[] buf = new byte[64];
    private int size;

    public MessageWriter writeByte(int value) {
        ensure(1);
        buf[size++] = (byte) value;
        return this;
    }

    public MessageWriter writeBoolean(boolean value) {
        return writeByte(value ? 1 : 0);
    }

    public MessageWriter writeVarInt(int value) {
        while ((value & ~0x7F) != 0) {
            writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        return writeByte(value);
    }

    public MessageWriter writeString(String value, int maxLength) {
        if (value.length() > maxLength) {
            throw new ProtocolException("String longer than " + maxLength);
        }
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length);
        return writeBytes(bytes);
    }

    public MessageWriter writeStrings(Collection<String> values, int maxCount, int maxLength) {
        if (values.size() > maxCount) {
            throw new ProtocolException("More than " + maxCount + " entries");
        }
        writeVarInt(values.size());
        for (String value : values) {
            writeString(value, maxLength);
        }
        return this;
    }

    public MessageWriter writeBytes(byte[] bytes) {
        ensure(bytes.length);
        System.arraycopy(bytes, 0, buf, size, bytes.length);
        size += bytes.length;
        return this;
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(buf, size);
    }

    private void ensure(int extra) {
        if (size + extra > Limits.MAX_CLIENTBOUND_BYTES) {
            throw new ProtocolException("Message exceeds " + Limits.MAX_CLIENTBOUND_BYTES + " bytes");
        }
        if (size + extra > buf.length) {
            buf = Arrays.copyOf(buf, Math.max(buf.length * 2, size + extra));
        }
    }
}
