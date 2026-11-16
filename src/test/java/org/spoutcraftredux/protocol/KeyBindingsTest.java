package org.spoutcraftredux.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.spoutcraftredux.protocol.message.KeyBindingPressed;
import org.spoutcraftredux.protocol.message.KeyBindings;

class KeyBindingsTest {
    @Test
    void bindingsRoundTrip() {
        KeyBindings bindings = new KeyBindings(List.of(
            new KeyBindings.Binding("shop:open", "{\"text\":\"Open shop\"}", "Shop", "key.keyboard.m", 0),
            new KeyBindings.Binding("shop:sell", "\"Sell\"", "Shop", "", KeyBindings.CTRL | KeyBindings.SHIFT)
        ));
        assertEquals(bindings, KeyBindings.decode(bindings.encode()));
    }

    @Test
    void pressRoundTrips() {
        KeyBindingPressed press = new KeyBindingPressed("shop:open", true);
        assertEquals(press, KeyBindingPressed.decode(press.encode()));
    }

    @Test
    void rejectsBadModifiers() {
        assertThrows(ProtocolException.class, () -> new KeyBindings.Binding("a:b", "\"x\"", "P", "", 8));
    }

    @Test
    void rejectsTooManyBindings() {
        byte[] forged = new MessageWriter().writeVarInt(Limits.MAX_KEY_BINDINGS + 1).toByteArray();
        assertThrows(ProtocolException.class, () -> KeyBindings.decode(forged));
    }
}
