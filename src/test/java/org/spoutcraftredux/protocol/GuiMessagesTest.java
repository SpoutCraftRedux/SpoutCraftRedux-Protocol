package org.spoutcraftredux.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.spoutcraftredux.protocol.gui.Anchors;
import org.spoutcraftredux.protocol.gui.WidgetSpec;
import org.spoutcraftredux.protocol.gui.WidgetTypes;
import org.spoutcraftredux.protocol.message.ControlEvent;
import org.spoutcraftredux.protocol.message.ScreenClose;
import org.spoutcraftredux.protocol.message.ScreenOpen;
import org.spoutcraftredux.protocol.message.WidgetRemove;
import org.spoutcraftredux.protocol.message.WidgetUpdate;

class GuiMessagesTest {
    private static WidgetSpec button() {
        return new WidgetSpec(1, WidgetTypes.BUTTON, -40, 10, 80, 20, Anchors.CENTER_CENTER, true, true, "",
            Map.of("text", "{\"text\":\"Buy\"}", "align", 1));
    }

    @Test
    void screenRoundTrips() {
        WidgetSpec list = new WidgetSpec(2, WidgetTypes.LIST, 0, 0, 100, 60, Anchors.SCALE, true, false, "\"tip\"",
            Map.of("titles", List.of("\"a\"", "\"a\""), "selected", -1, "position", 0.25F, "wrap", true));
        ScreenOpen open = new ScreenOpen(7, "\"Shop\"", List.of(button(), list));
        ScreenOpen decoded = ScreenOpen.decode(open.encode());
        assertEquals(open, decoded);
        assertEquals(List.of("\"a\"", "\"a\""), decoded.widgets().get(1).strings("titles"));
        assertEquals(-40, decoded.widgets().get(0).x());
    }

    @Test
    void updatesAndRemovalsRoundTrip() {
        WidgetUpdate update = new WidgetUpdate(WidgetUpdate.HUD, List.of(button()));
        assertEquals(update, WidgetUpdate.decode(update.encode()));
        WidgetRemove remove = new WidgetRemove(3, List.of(1, 2, 3));
        assertEquals(remove, WidgetRemove.decode(remove.encode()));
        assertEquals(new ScreenClose(5), ScreenClose.decode(new ScreenClose(5).encode()));
    }

    @Test
    void controlEventsRoundTrip() {
        ControlEvent slide = new ControlEvent(1, 2, ControlEvent.SLIDE, false, -1, 0.75F, "");
        assertEquals(slide, ControlEvent.decode(slide.encode()));
        ControlEvent text = new ControlEvent(1, 3, ControlEvent.SUBMIT, false, -1, 0.0F, "hello");
        assertEquals(text, ControlEvent.decode(text.encode()));
    }

    @Test
    void rejectsBadInput() {
        assertThrows(ProtocolException.class, () -> new ControlEvent(1, 1, 99, false, -1, 0, ""));
        assertThrows(ProtocolException.class, () -> new ControlEvent(1, 1, ControlEvent.SLIDE, false, -1, Float.NaN, ""));
        assertThrows(ProtocolException.class, () -> new WidgetSpec(0, WidgetTypes.LABEL, 0, 0, 0, 0, 0, true, true, "", Map.of()));
        byte[] forged = new MessageWriter().writeVarInt(1).writeString("\"t\"", 10).writeVarInt(Limits.MAX_WIDGETS + 1).toByteArray();
        assertThrows(ProtocolException.class, () -> ScreenOpen.decode(forged));
    }
}
