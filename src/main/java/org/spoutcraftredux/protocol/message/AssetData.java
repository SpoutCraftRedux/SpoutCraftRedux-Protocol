package org.spoutcraftredux.protocol.message;

import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * Server to client: one piece of an asset. Pieces arrive in order; the client checks the SHA-256 of the whole asset
 * before using it.
 *
 * @param sha256    the asset's SHA-256 as 64 lowercase hex characters
 * @param totalSize the size of the whole asset in bytes
 * @param offset    where this piece starts
 * @param bytes     this piece
 */
public record AssetData(String sha256, int totalSize, int offset, byte[] bytes) {
    public static final String CHANNEL = "spoutcraftredux:asset_data";
    public static final int MAX_ASSET_BYTES = 4 * 1024 * 1024;
    public static final int MAX_CHUNK_BYTES = 512 * 1024;

    public AssetData {
        if (!sha256.matches("[0-9a-f]{64}")) {
            throw new ProtocolException("Not a SHA-256 hash");
        }
        if (totalSize < 1 || totalSize > MAX_ASSET_BYTES || offset < 0 || bytes.length > MAX_CHUNK_BYTES
            || (long) offset + bytes.length > totalSize) {
            throw new ProtocolException("Bad asset piece");
        }
    }

    public byte[] encode() {
        return new MessageWriter().writeString(this.sha256, 64).writeVarInt(this.totalSize).writeVarInt(this.offset)
            .writeVarInt(this.bytes.length).writeBytes(this.bytes).toByteArray();
    }

    public static AssetData decode(byte[] data) {
        if (data.length > Limits.MAX_CLIENTBOUND_BYTES) {
            throw new ProtocolException("Asset piece too large");
        }
        MessageReader in = new MessageReader(data);
        String sha256 = in.readString(64);
        int totalSize = in.readVarInt();
        int offset = in.readVarInt();
        int length = in.readLength(MAX_CHUNK_BYTES);
        byte[] bytes = in.readBytes(length);
        in.expectEnd();
        return new AssetData(sha256, totalSize, offset, bytes);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AssetData that && this.sha256.equals(that.sha256) && this.totalSize == that.totalSize
            && this.offset == that.offset && java.util.Arrays.equals(this.bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return this.sha256.hashCode() * 31 + this.offset;
    }
}
