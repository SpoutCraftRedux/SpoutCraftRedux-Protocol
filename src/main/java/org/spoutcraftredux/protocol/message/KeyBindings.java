package org.spoutcraftredux.protocol.message;

import java.util.ArrayList;
import java.util.List;
import org.spoutcraftredux.protocol.Limits;
import org.spoutcraftredux.protocol.MessageReader;
import org.spoutcraftredux.protocol.MessageWriter;
import org.spoutcraftredux.protocol.ProtocolException;

/**
 * Server to client: every key binding the server's plugins declared. Replaces the previous list.
 *
 * @param bindings the bindings, in display order
 */
public record KeyBindings(List<Binding> bindings) {
    public static final String CHANNEL = "spoutcraftredux:key_bindings";

    /** Modifier bits, the same in both directions. */
    public static final int CTRL = 1;
    public static final int SHIFT = 2;
    public static final int ALT = 4;

    /**
     * One binding.
     *
     * @param id               unique id, such as {@code myplugin:open_menu}
     * @param nameJson         the name shown in the controls screen, as a JSON text component
     * @param pluginName       the plugin that declared it, shown under the name
     * @param defaultKey       the default key, such as {@code key.keyboard.m}; empty for unbound
     * @param defaultModifiers Ctrl, Shift and Alt bits needed with the default key
     */
    public record Binding(String id, String nameJson, String pluginName, String defaultKey, int defaultModifiers) {
        public Binding {
            if (defaultModifiers < 0 || defaultModifiers > (CTRL | SHIFT | ALT)) {
                throw new ProtocolException("Bad modifiers " + defaultModifiers);
            }
        }
    }

    public KeyBindings {
        if (bindings.size() > Limits.MAX_KEY_BINDINGS) {
            throw new ProtocolException("More than " + Limits.MAX_KEY_BINDINGS + " key bindings");
        }
        bindings = List.copyOf(bindings);
    }

    public byte[] encode() {
        MessageWriter out = new MessageWriter().writeVarInt(this.bindings.size());
        for (Binding binding : this.bindings) {
            out.writeString(binding.id(), Limits.MAX_IDENTIFIER_LENGTH)
                .writeString(binding.nameJson(), Limits.MAX_COMPONENT_JSON_LENGTH)
                .writeString(binding.pluginName(), Limits.MAX_PLUGIN_NAME_LENGTH)
                .writeString(binding.defaultKey(), Limits.MAX_IDENTIFIER_LENGTH)
                .writeVarInt(binding.defaultModifiers());
        }
        return out.toByteArray();
    }

    public static KeyBindings decode(byte[] data) {
        MessageReader in = new MessageReader(data);
        int count = in.readLength(Limits.MAX_KEY_BINDINGS);
        List<Binding> bindings = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            bindings.add(new Binding(
                in.readString(Limits.MAX_IDENTIFIER_LENGTH),
                in.readString(Limits.MAX_COMPONENT_JSON_LENGTH),
                in.readString(Limits.MAX_PLUGIN_NAME_LENGTH),
                in.readString(Limits.MAX_IDENTIFIER_LENGTH),
                in.readVarInt()
            ));
        }
        in.expectEnd();
        return new KeyBindings(bindings);
    }
}
