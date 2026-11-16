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
    public static final String SATURATION = "saturation";
    public static final String CAN_ALWAYS_EAT = "can_always_eat";

    private ContentKeys() {
    }
}
