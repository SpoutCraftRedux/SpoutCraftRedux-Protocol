package org.spoutcraftredux.protocol.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.PropertyMap;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * Server to client, during configuration: every custom registry entry on the server. The client registers each one at
 * its raw id and answers with {@link ContentAck}.
 *
 * @param entries the entries, in raw id order within each registry
 */
public record Content(List<Entry> entries) {
    public static final String CHANNEL = "spoutcraftredux:content";

    /**
     * One registry entry.
     *
     * @param registry   the registry, such as {@code minecraft:item}
     * @param id         the entry id, such as {@code myplugin:ruby}
     * @param rawId      the numeric id the server uses on the wire
     * @param properties the definition, keyed by {@link org.spoutcraftredux.protocol.content.ContentKeys}
     */
    public record Entry(String registry, String id, int rawId, Map<String, Object> properties) {
        public Entry {
            if (rawId < 0) {
                throw new ProtocolException("Bad raw id " + rawId);
            }
            if (properties.size() > Limits.MAX_CONTENT_PROPERTIES) {
                throw new ProtocolException("Too many properties for " + id);
            }
            properties = Map.copyOf(properties);
        }

        public boolean bool(String key, boolean fallback) {
            return this.properties.get(key) instanceof Boolean value ? value : fallback;
        }

        public int integer(String key, int fallback) {
            return this.properties.get(key) instanceof Integer value ? value : fallback;
        }

        public float number(String key, float fallback) {
            return this.properties.get(key) instanceof Float value ? value : fallback;
        }

        public String string(String key, String fallback) {
            return this.properties.get(key) instanceof String value ? value : fallback;
        }
    }

    public Content {
        if (entries.size() > Limits.MAX_CONTENT_ENTRIES) {
            throw new ProtocolException("More than " + Limits.MAX_CONTENT_ENTRIES + " content entries");
        }
        entries = List.copyOf(entries);
    }

    public byte[] encode() {
        MessageWriter out = new MessageWriter().writeVarInt(this.entries.size());
        for (Entry entry : this.entries) {
            out.writeString(entry.registry(), Limits.MAX_IDENTIFIER_LENGTH)
                .writeString(entry.id(), Limits.MAX_IDENTIFIER_LENGTH)
                .writeVarInt(entry.rawId());
            PropertyMap.write(out, entry.properties());
        }
        byte[] bytes = out.toByteArray();
        if (bytes.length > Limits.MAX_CLIENTBOUND_BYTES) {
            throw new ProtocolException("Content too large");
        }
        return bytes;
    }

    public static Content decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        int count = in.readLength(Limits.MAX_CONTENT_ENTRIES);
        List<Entry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entries.add(new Entry(
                in.readString(Limits.MAX_IDENTIFIER_LENGTH),
                in.readString(Limits.MAX_IDENTIFIER_LENGTH),
                in.readVarInt(),
                PropertyMap.read(in, Limits.MAX_CONTENT_PROPERTIES)
            ));
        }
        in.expectEnd();
        return new Content(entries);
    }
}
