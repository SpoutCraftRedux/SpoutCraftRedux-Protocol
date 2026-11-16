package org.spoutcraftredux.protocol.content;

/** Registry names and definition property keys used in {@link org.spoutcraftredux.protocol.message.Content}. */
public final class ContentKeys {
    public static final String ITEM = "minecraft:item";

    /** Display name as a JSON text component. */
    public static final String NAME = "name";
    /** Item model id; defaults to the item id. */
    public static final String MODEL = "model";
    public static final String MAX_STACK_SIZE = "max_stack_size";
    public static final String MAX_DAMAGE = "max_damage";
    /** One of common, uncommon, rare or epic. */
    public static final String RARITY = "rarity";
    public static final String FIRE_RESISTANT = "fire_resistant";
    /** Present when the item is food. */
    public static final String NUTRITION = "nutrition";
    /** Saturation modifier, as vanilla's food builder takes it. */
    public static final String SATURATION = "saturation";
    public static final String CAN_ALWAYS_EAT = "can_always_eat";
    /** Present when the item is a tool: sword, pickaxe, axe, shovel or hoe. */
    public static final String TOOL = "tool";
    /** Tool tier: wood, stone, copper, iron, diamond, gold or netherite. */
    public static final String TOOL_MATERIAL = "tool_material";
    public static final String ATTACK_DAMAGE = "attack_damage";
    public static final String ATTACK_SPEED = "attack_speed";
    /** Present when the item is armor: leather, copper, chainmail, iron, gold, diamond, turtle_scute or netherite. */
    public static final String ARMOR_MATERIAL = "armor_material";
    /** Armor piece: helmet, chestplate, leggings or boots. */
    public static final String ARMOR_PIECE = "armor_piece";
    /** Equipment asset id for the worn look; defaults to the material's. */
    public static final String EQUIPMENT_ASSET = "equipment_asset";
    /** Slot a non-armor item can be worn in: head, chest, legs, feet or body. */
    public static final String EQUIP_SLOT = "equip_slot";

    private ContentKeys() {
    }
}
