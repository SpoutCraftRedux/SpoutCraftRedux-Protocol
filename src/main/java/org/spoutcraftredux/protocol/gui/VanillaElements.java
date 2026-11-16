package org.spoutcraftredux.protocol.gui;

import java.util.List;

/** Names of the vanilla HUD elements a server can hide or move. */
public final class VanillaElements {
    public static final String HOTBAR = "hotbar";
    public static final String HEALTH = "health";
    public static final String ARMOR = "armor";
    public static final String FOOD = "food";
    public static final String AIR = "air";
    public static final String MOUNT_HEALTH = "mount_health";
    public static final String EXPERIENCE_BAR = "experience_bar";
    public static final String EXPERIENCE_LEVEL = "experience_level";
    public static final String CROSSHAIR = "crosshair";
    public static final String HELD_ITEM_NAME = "held_item_name";
    public static final String EFFECTS = "effects";
    public static final String BOSS_BAR = "boss_bar";
    public static final String SCOREBOARD = "scoreboard";
    public static final String ACTION_BAR = "action_bar";
    public static final String TITLE = "title";
    public static final String CHAT = "chat";
    public static final String PLAYER_LIST = "player_list";
    public static final String SUBTITLES = "subtitles";

    public static final List<String> ALL = List.of(HOTBAR, HEALTH, ARMOR, FOOD, AIR, MOUNT_HEALTH, EXPERIENCE_BAR,
        EXPERIENCE_LEVEL, CROSSHAIR, HELD_ITEM_NAME, EFFECTS, BOSS_BAR, SCOREBOARD, ACTION_BAR, TITLE, CHAT, PLAYER_LIST,
        SUBTITLES);

    private VanillaElements() {
    }
}
