package me.stivendarsi.tigerBeach.itemmanager.groups;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.BlocksAttacks.Builder;
import io.papermc.paper.datacomponent.item.Tool.Rule;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.item.MapPostProcessing;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import me.clip.placeholderapi.PlaceholderAPI;
import me.stivendarsi.tigerBeach.TigerBeach;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl.Variant;
import org.bukkit.entity.Cat.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.entity.Wolf.SoundVariant;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.common.value.qual.IntRange;

import java.util.*;
import java.util.stream.Collectors;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;

@SuppressWarnings("UnstableApiUsage")
public class GroupsItemSection {
    private ItemType itemType;
    private String id;
    private String prefix;
    private final ItemGroup group;
    private ItemStack itemStack;
    private boolean clearPrototypeData;
    private Art painting_variant;
    private Variant axolotl_variant;
    private BannerPatternLayers banner_patterns;
    private BlocksAttacks blocks_attacks;
    private BundleContents bundleContents;
    private Type cat_variant;
    private org.bukkit.entity.Chicken.Variant chicken_variant;
    private ChargedProjectiles charged_projectiles;
    private Consumable consumable;
    private org.bukkit.entity.Cow.Variant cow_variant;
    private CustomModelData customModelData;
    private DeathProtection death_protection;
    private DamageResistant damage_resistant;
    private DyeColor base_color;
    private DyeColor cat_collar;
    private DyeColor sheep_color;
    private DyeColor shulker_color;
    private DyeColor tropical_fish_base_color;
    private DyeColor tropical_fish_pattern_color;
    private DyeColor wolf_collar;
    private DyedItemColor dyed_color;
    private Enchantable enchantable;
    private Equippable equippable;
    private FireworkEffect firework_explosion;
    private Fireworks fireworks;
    private Float potion_duration_scale;
    private FoodProperties food;
    private org.bukkit.entity.Fox.Type fox_variant;
    private org.bukkit.entity.Frog.Variant frog_variant;
    private ItemAdventurePredicate can_break;
    private ItemAdventurePredicate can_place_on;
    private ItemArmorTrim trim;
    private ItemAttributeModifiers attribute_modifiers;
    private ItemContainerContents container;
    private ItemEnchantments enchantments;
    private ItemEnchantments stored_enchantments;
    private List<String> lore;
    private ItemRarity rarity;
    private Key break_sound;
    private Key item_model;
    private Key note_block_sound;
    private Key tooltip_style;
    private List<Key> recipes;
    private Color llama_variant;
    private LodestoneTracker lodestone_tracker;
    private MapDecorations map_decorations;
    private MapId map_id;
    private MapItemColor map_color;
    private MapPostProcessing map_post_processing;
    @IntRange(
            from = 1L,
            to = 99L
    )
    private Integer max_stack_size;
    private org.bukkit.entity.MushroomCow.Variant mooshroom_variant;
    private MusicInstrument instrument;
    @NonNegative
    private Integer damage;
    private OminousBottleAmplifier ominous_bottle_amplifier;
    private org.bukkit.entity.Parrot.Variant parrot_variant;
    private org.bukkit.entity.Pig.Variant pig_variant;
    private PotDecorations pot_decorations;
    private PotionContents potion_contents;
    private RegistryKeySet<PatternType> provides_banner_patterns;
    private TrimMaterial provides_trim_material;
    private org.bukkit.entity.Rabbit.Type rabbit_variant;
    @NonNegative
    private Integer repair_cost;
    private Repairable repairable;
    private ResolvableProfile profile;
    private org.bukkit.entity.Salmon.Variant salmon_size;
    private SeededContainerLoot container_loot;
    private SuspiciousStewEffects suspicious_stew_effects;
    private Tool tool;
    private TooltipDisplay tooltip_display;
    private Pattern tropical_fish_pattern;
    private UseCooldown use_cooldown;
    private UseRemainder use_remainder;
    private org.bukkit.entity.Villager.Type villager_variant;
    private Weapon weapon;
    private SoundVariant wolf_sound_variant;
    private org.bukkit.entity.Wolf.Variant wolf_variant;
    private WritableBookContent writable_book_content;
    private WrittenBookContent written_book_content;
    private Boolean enchantment_glint_override;
    private boolean glider;
    private boolean intangible_projectile;
    private boolean unbreakable;
    private JukeboxPlayable jukebox_playable;
    private String custom_name;
    private String item_name;

    public GroupsItemSection(ItemGroup itemGroup) {
        this.itemType = ItemType.BEDROCK;
        this.clearPrototypeData = false;
        this.glider = false;
        this.intangible_projectile = false;
        this.unbreakable = false;
        this.group = itemGroup;
    }

    public Key itemKey() {
        return Key.key(this.group.getCleanName(), this.id);
    }

    public void load(String id, ConfigurationSection section) {
        if (section == null) {
            return;
        }

        this.id = id;
        this.prefix = id + ".";
        if (!section.contains("type"))
            throw new RuntimeException("Invalid Material");

        String itemTypeName = section.getString("type");
        assert itemTypeName != null;
        this.itemType = Registry.ITEM.get(Key.key(itemTypeName));
        if (section.contains("clear_data")) {
            this.clearPrototypeData = section.getBoolean("clear_data");
        }

        Registry<Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        boolean gilder = section.getBoolean("glider", false);
        boolean unbreakable = section.getBoolean("unbreakable", false);
        boolean intangible_projectile = section.getBoolean("intangible_projectile", false);
        this.unbreakable = unbreakable;
        this.glider = gilder;
        this.intangible_projectile = intangible_projectile;
        if (section.contains("item_name")) {
            this.item_name = section.getString("item_name");
        }

        if (section.contains("custom_name")) {
            this.custom_name = section.getString("custom_name");
        }


        if (section.contains("paint_variant")) {
            String modelName = section.getString("paint_variant");
            if (!Key.parseable(modelName)) {
                throw new RuntimeException("Invalid Paint Variant");
            }

            Registry<Art> artRegistryKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.PAINTING_VARIANT);
            this.painting_variant = artRegistryKey.get(Key.key(modelName));
        }


        if (section.contains("blocks_attacks")) {
            Builder builder = BlocksAttacks.blocksAttacks();
            if (section.contains("blocks_attacks.block_sound")) {
                String blockSound = section.getString("blocks_attacks.block_sound");
                if (!Key.parseable(blockSound)) {
                    throw new RuntimeException("Invalid Sound");
                }

                builder.blockSound(Key.key(blockSound));
            }

            if (section.contains("blocks_attacks.item_damage")) {
                ItemDamageFunction.Builder itemDamageBuilder = ItemDamageFunction.itemDamageFunction();
                float threshold = (float) section.getDouble("blocks_attacks.item_damage.threshold", 0.0D);
                float base = (float) section.getDouble("blocks_attacks.item_damage.base", 0.0D);
                float factor = (float) section.getDouble("blocks_attacks.item_damage.factor", 0.0D);
                itemDamageBuilder.threshold(threshold).factor(factor).base(base);
                builder.itemDamage(itemDamageBuilder.build());
            }

            if (section.contains("blocks_attacks.disabled_sound")) {
                String disabledSound = section.getString("blocks_attacks.disabled_sound");
                if (!Key.parseable(disabledSound)) {
                    throw new RuntimeException("Invalid Sound");
                }

                builder.disableSound(Key.key(disabledSound));
            }

            if (section.contains("blocks_attacks.damage_reductions")) {
                List<Map<?, ?>> damageReductionsMap = section.getMapList("blocks_attacks.damage_reductions");
                for (Map<?, ?> rawDamageReduction : damageReductionsMap) {
                    YamlConfiguration damageReduactionSection = new YamlConfiguration();
                    damageReduactionSection.addDefaults((Map<String, Object>) rawDamageReduction);
                    damageReduactionSection.options().copyDefaults(true);
                    builder.addDamageReduction(loadDamageReduction(damageReduactionSection));
                }
            }

            if (section.contains("disable_cooldown_scale")) {
                float disableCooldownScale = (float) section.getDouble("disable_cooldown_scale", 1.0D);
                builder.disableCooldownScale(disableCooldownScale);
            }

            if (section.contains("block_delay_seconds")) {
                float blockDelaySeconds = (float) section.getDouble("block_delay_seconds", 0.0D);
                builder.blockDelaySeconds(blockDelaySeconds);
            }

            this.blocks_attacks = builder.build();
        }

        if (section.contains("potion_contents")) {
            ConfigurationSection potionSection = section.getConfigurationSection("potion_contents");
            if (potionSection == null) return;

            PotionContents.Builder builder = PotionContents.potionContents();

            // Load potion type
            if (potionSection.contains("type")) {
                String potionTypeKey = potionSection.getString("type");
                PotionType potionType = Registry.POTION.get(NamespacedKey.fromString(potionTypeKey));
                if (potionType != null) {
                    builder.potion(potionType);
                }
            }

            // Load custom color
            if (potionSection.contains("color")) {
                ConfigurationSection colorSection = potionSection.getConfigurationSection("color");
                if (colorSection != null) {
                    Color color = loadColor(colorSection);
                    builder.customColor(color);
                }
            }

            // Load custom name
            if (potionSection.contains("custom_name")) {
                String customPotionName = potionSection.getString("custom_name");
                builder.customName(customPotionName);
            }

            // Load potion effects
            if (potionSection.contains("effects")) {
                ConfigurationSection effectsSection = potionSection.getConfigurationSection("effects");
                if (effectsSection != null) {
                    for (String effectKey : effectsSection.getKeys(false)) {
                        ConfigurationSection effectSection = effectsSection.getConfigurationSection(effectKey);
                        if (effectSection != null) {
                            PotionEffect effect = loadEffect(effectSection, effectKey);
                            builder.addCustomEffect(effect);
                        }
                    }
                }
            }

            this.potion_contents = builder.build();
        }


        if (section.contains("potion_duration_scale")) {
            this.potion_duration_scale = (float) section.getDouble("potion_duration_scale", 1.0D);
        }

        if (section.contains("profile")) {
            ResolvableProfile.Builder builder = ResolvableProfile.resolvableProfile();
            String name = section.getString("profile");
            builder.name(name);
            this.profile = builder.build();
        }

        if (section.contains("rarity")) {
            String rarity = section.getString("rarity");
            this.rarity = ItemRarity.valueOf(rarity);
        }

        if (section.contains("repairable")) {
            List<ItemType> itemTypeList = section.getStringList("repairable").stream().map((s) -> Registry.ITEM.get(Key.key(s))).filter(Objects::nonNull).toList();
            RegistryKeySet<ItemType> itemTypeRegistryKeySet = RegistrySet.keySetFromValues(RegistryKey.ITEM, itemTypeList);
            Repairable.repairable(itemTypeRegistryKeySet);
        }

        if (section.contains("repair_cost")) {
            this.repair_cost = section.getInt("repair_cost", 0);
        }

        if (section.contains("damage")) {
            this.damage = section.getInt("damage", 0);
        }

        if (section.contains("max_stack_size")) {
            int size = section.getInt("max_stack_size", 1);
            if (size < 1) {
                size = 1;
            } else if (size > 99) {
                size = 99;
            }

            this.max_stack_size = size;
        }

        if (section.contains("stored_enchantments")) {
            ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();

            for (String enchantmentName : section.getConfigurationSection("stored_enchantments").getKeys(false)) {
                Enchantment enchantment = (Enchantment) enchantmentRegistry.getOrThrow(TypedKey.create(RegistryKey.ENCHANTMENT, Key.key(enchantmentName)));
                int lvl = section.getInt("stored_enchantments." + enchantmentName);
                builder.add(enchantment, lvl);
            }
            this.stored_enchantments = builder.build();
        }


        if (section.contains("tool")) {
            Tool.Builder builder = Tool.tool();
            float defaultMiningSpeed = (float) section.getDouble("tool.default_mining_speed", 1.0D);
            int damage_per_block = section.getInt("tool.damage_per_block", 1);
            builder.damagePerBlock(damage_per_block);
            builder.defaultMiningSpeed(defaultMiningSpeed);
            boolean canDestroyBlocksInCreative = section.getBoolean("tool.can_destroy_blocks_in_creative", true);
            builder.canDestroyBlocksInCreative(canDestroyBlocksInCreative);

            if (section.contains("tool.rules")) {
                List<Map<?, ?>> rules = section.getMapList("tool.rules");
                for (Map<?, ?> rawRule : rules) {
                    YamlConfiguration ruleSection = new YamlConfiguration();
                    ruleSection.addDefaults((Map<String, Object>) rawRule);
                    ruleSection.options().copyDefaults(true);
                    builder.addRule(loadRule(ruleSection));
                }
            }

            this.tool = builder.build();
        }

        if (section.contains("tooltip_display")) {
            boolean hide_tooltip = section.getBoolean("tooltip_display.hide_tooltip", false);
            TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();
            builder.hideTooltip(hide_tooltip);
            List<String> a = section.getStringList("tooltip_display.hidden_components");
            Set<DataComponentType> components = a.stream().map((s) -> Registry.DATA_COMPONENT_TYPE.get(Key.key(s))).filter(Objects::nonNull).collect(Collectors.toSet());
            builder.hiddenComponents(components);
            this.tooltip_display = builder.build();
        }

        if (section.contains("tooltip_style")) {
            String style = section.getString("tooltip_style");
            assert style != null;
            this.tooltip_style = Key.key(style);
        }

        if (section.contains("break_sound")) {
            String breakSound = section.getString("break_sound");
            assert breakSound != null;
            this.break_sound = Key.key(breakSound);
        }

        if (section.contains("equippable")) {
            String slotName = section.getString("equippable.slot");
            EquipmentSlot slot = EquipmentSlot.valueOf(slotName.toUpperCase());
            Equippable.Builder builder = Equippable.equippable(slot);
            if (section.contains("equippable.sound")) {
                String sound = section.getString("equippable.sound");
                assert sound != null;
                builder.equipSound(Key.key(sound));
            }
            if (section.contains("equippable.asset")) {
                String asset = section.getString("equippable.asset");
                assert asset != null;
                builder.assetId(Key.key(asset));
            }

            if (section.contains("equippable.allowed_entities")) {
                List<EntityType> itemTypeList = section.getStringList("equippable.allowed_entities").stream().map((s) -> {
                    assert s != null;
                    return Registry.ENTITY_TYPE.get(Key.key(s));
                }).filter(Objects::nonNull).toList();
                RegistryKeySet<EntityType> entities = RegistrySet.keySetFromValues(RegistryKey.ENTITY_TYPE, itemTypeList);
                builder.allowedEntities(entities);
            }

            if (section.contains("equippable.dispensable")) {
                boolean dispensable = section.getBoolean("equippable.dispensable", true);
                builder.dispensable(dispensable);
            }

            if (section.contains("equippable.swappable")) {
                boolean swappable = section.getBoolean("equippable.swappable", true);
                builder.swappable(swappable);
            }

            if (section.contains("equippable.damage_on_hurt")) {
                boolean damageOnHurt = section.getBoolean("equippable.damage_on_hurt", true);
                builder.damageOnHurt(damageOnHurt);
            }

            if (section.contains("equippable.equip_on_interact")) {
                boolean equipOnInteract = section.getBoolean("equippable.equip_on_interact", true);
                builder.equipOnInteract(equipOnInteract);
            }

            if (section.contains("equippable.camera_overlay")) {
                String cameraOverlay = section.getString("equippable.camera_overlay");
                assert cameraOverlay != null;
                builder.cameraOverlay(Key.key(cameraOverlay));
            }

            this.equippable = builder.build();
        }


        if (section.contains("food")) {
            FoodProperties.Builder builder = FoodProperties.food();
            if (section.contains("food.nutrition")) {
                int nutrition = section.getInt("food.nutrition");
                builder.nutrition(nutrition);
            }
            if (section.contains("food.can_always_eat")) {
                boolean canAlwaysEat = section.getBoolean("food.can_always_eat");
                builder.canAlwaysEat(canAlwaysEat);
            }
            if (section.contains("food.saturation")) {
                float saturation = (float) section.getDouble("food.saturation");
                builder.saturation(saturation);
            }
            this.food = builder.build();
        }

        if (section.contains("item_model")) {
            String modelName = section.getString("item_model");
            assert modelName != null;
            this.item_model = Key.key(modelName);
        }

        if (section.contains("lore")) {
            this.lore = section.getStringList("lore");
        }

        if (section.contains("dyed_color")) {
            ConfigurationSection colorSection = section.getConfigurationSection("dyed_color");
            assert colorSection != null;
            this.dyed_color = DyedItemColor.dyedItemColor(loadColor(colorSection));
        }

        if (section.contains("death_protection")) {
            DeathProtection.Builder builder = DeathProtection.deathProtection();
            if (section.isConfigurationSection("death_protection")) {
                List<ConsumeEffect> consumeEffects = new ArrayList<>();
                for (String deathType : section.getConfigurationSection("death_protection").getKeys(false)) {
                    ConfigurationSection typeSection = section.getConfigurationSection("death_protection." + deathType);
                    consumeEffects.add(loadConsumeEffect(typeSection, deathType));
                }
                builder.addEffects(consumeEffects);
            }

            this.death_protection = builder.build();
        }

        if (section.contains("use_cooldown")) {
            float cooldownSeconds = (float) section.getDouble("use_cooldown", 0);
            UseCooldown.Builder builder = UseCooldown.useCooldown(cooldownSeconds);
            use_cooldown = builder.build();
        }

        if (section.contains("consumable")) {
            Consumable.Builder builder = Consumable.consumable();
            if (section.contains("consumable.consume_seconds")) {
                float consumeSeconds = (float) section.getDouble("consumable.consume_seconds", 1);
                builder.consumeSeconds(consumeSeconds);
            }

            if (section.contains("consumable.animation")) {
                String animationName = section.getString("consumable.animation");
                ItemUseAnimation animation = ItemUseAnimation.valueOf(animationName);
                builder.animation(animation);
            }

            if (section.contains("consumable.sound")) {
                String consumeSound = section.getString("consumable.sound");
                assert consumeSound != null;
                builder.sound(Key.key(consumeSound));
            }

            if (section.contains("consumable.has_consume_particles")) {
                boolean consumeParticles = section.getBoolean("consumable.has_consume_particles", true);
                builder.hasConsumeParticles(consumeParticles);
            }

            if (section.contains("consumable.effects")) {
                List<ConsumeEffect> consumeEffects = new ArrayList<>();
                for (String type : section.getConfigurationSection("consumable.effects").getKeys(false)) {
                    ConfigurationSection typeSection = section.getConfigurationSection("consumable.effects." + type);
                    ConsumeEffect consumeEffect = loadConsumeEffect(typeSection, type);
                    consumeEffects.add(consumeEffect);
                }
                builder.addEffects(consumeEffects);
            }

            this.consumable = builder.build();
        }

        if (section.contains("enchantment_glint_override")) {
            this.enchantment_glint_override = section.getBoolean("enchantment_glint_override");
        }

        if (section.contains("enchantments")) {
            ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();
            for (String enchantmentName : section.getConfigurationSection("enchantments").getKeys(false)) {
                Enchantment enchantment = enchantmentRegistry.getOrThrow(TypedKey.create(RegistryKey.ENCHANTMENT, Key.key(enchantmentName)));
                int lvl = section.getInt("enchantments." + enchantmentName);
                builder.add(enchantment, lvl);
            }

            this.enchantments = builder.build();
        }

        if (section.contains("attribute") && section.isConfigurationSection("attribute")) {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
            for (String attributeName : section.getConfigurationSection("attribute").getKeys(false)) {
                Attribute attribute = Registry.ATTRIBUTE.get(Key.key(attributeName));
                if (attribute != null) {
                    double amount = section.getDouble("attribute." + attributeName + ".amount");
                    String idName = section.getString("attribute." + attributeName + ".id");
                    NamespacedKey key = new NamespacedKey(TigerBeach.plugin(), idName);
                    String operationName = section.getString("attribute." + attributeName + ".operation");
                    Operation operation = Operation.valueOf(operationName.toUpperCase(Locale.ROOT));
                    String slotName = section.getString("attribute." + attributeName + ".slot");
                    EquipmentSlotGroup slot = EquipmentSlotGroup.getByName(slotName);

                    assert slot != null;

                    AttributeModifier attributeModifier = new AttributeModifier(key, amount, operation, slot);
                    builder.addModifier(attribute, attributeModifier, slot);
                }
            }

            this.attribute_modifiers = builder.build();
        }

    }


    public GroupsItemSection makeItem() {
        ItemStack itemStack = this.itemType.createItemStack();

        if (this.clearPrototypeData) itemStack.getDataTypes().forEach(itemStack::unsetData);
        if (this.painting_variant != null)
            itemStack.setData(DataComponentTypes.PAINTING_VARIANT, this.painting_variant);
        if (this.axolotl_variant != null) itemStack.setData(DataComponentTypes.AXOLOTL_VARIANT, this.axolotl_variant);
        if (this.banner_patterns != null) itemStack.setData(DataComponentTypes.BANNER_PATTERNS, this.banner_patterns);
        if (this.blocks_attacks != null) itemStack.setData(DataComponentTypes.BLOCKS_ATTACKS, this.blocks_attacks);
        if (this.bundleContents != null) itemStack.setData(DataComponentTypes.BUNDLE_CONTENTS, this.bundleContents);
        if (this.cat_variant != null) itemStack.setData(DataComponentTypes.CAT_VARIANT, this.cat_variant);
        if (this.chicken_variant != null) itemStack.setData(DataComponentTypes.CHICKEN_VARIANT, this.chicken_variant);
        if (this.charged_projectiles != null)
            itemStack.setData(DataComponentTypes.CHARGED_PROJECTILES, this.charged_projectiles);
        if (this.consumable != null) itemStack.setData(DataComponentTypes.CONSUMABLE, this.consumable);
        if (this.cow_variant != null) itemStack.setData(DataComponentTypes.COW_VARIANT, this.cow_variant);
        if (this.customModelData != null) itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, this.customModelData);
        if (this.death_protection != null)
            itemStack.setData(DataComponentTypes.DEATH_PROTECTION, this.death_protection);
        if (this.damage_resistant != null)
            itemStack.setData(DataComponentTypes.DAMAGE_RESISTANT, this.damage_resistant);
        if (this.base_color != null) itemStack.setData(DataComponentTypes.BASE_COLOR, this.base_color);
        if (this.cat_collar != null) itemStack.setData(DataComponentTypes.CAT_COLLAR, this.cat_collar);
        if (this.sheep_color != null) itemStack.setData(DataComponentTypes.SHEEP_COLOR, this.sheep_color);
        if (this.shulker_color != null) itemStack.setData(DataComponentTypes.SHULKER_COLOR, this.shulker_color);
        if (this.tropical_fish_base_color != null)
            itemStack.setData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, this.tropical_fish_base_color);
        if (this.tropical_fish_pattern_color != null)
            itemStack.setData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, this.tropical_fish_pattern_color);
        if (this.wolf_collar != null) itemStack.setData(DataComponentTypes.WOLF_COLLAR, this.wolf_collar);
        if (this.dyed_color != null) itemStack.setData(DataComponentTypes.DYED_COLOR, this.dyed_color);
        if (this.enchantable != null) itemStack.setData(DataComponentTypes.ENCHANTABLE, this.enchantable);
        if (this.equippable != null) itemStack.setData(DataComponentTypes.EQUIPPABLE, this.equippable);
        if (this.firework_explosion != null)
            itemStack.setData(DataComponentTypes.FIREWORK_EXPLOSION, this.firework_explosion);
        if (this.fireworks != null) itemStack.setData(DataComponentTypes.FIREWORKS, this.fireworks);
        if (this.potion_duration_scale != null)
            itemStack.setData(DataComponentTypes.POTION_DURATION_SCALE, this.potion_duration_scale);
        if (this.food != null) itemStack.setData(DataComponentTypes.FOOD, this.food);
        if (this.fox_variant != null) itemStack.setData(DataComponentTypes.FOX_VARIANT, this.fox_variant);
        if (this.frog_variant != null) itemStack.setData(DataComponentTypes.FROG_VARIANT, this.frog_variant);
        if (this.can_break != null) itemStack.setData(DataComponentTypes.CAN_BREAK, this.can_break);
        if (this.can_place_on != null) itemStack.setData(DataComponentTypes.CAN_PLACE_ON, this.can_place_on);
        if (this.trim != null) itemStack.setData(DataComponentTypes.TRIM, this.trim);
        if (this.attribute_modifiers != null)
            itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, this.attribute_modifiers);
        if (this.container != null) itemStack.setData(DataComponentTypes.CONTAINER, this.container);
        if (this.enchantments != null) itemStack.setData(DataComponentTypes.ENCHANTMENTS, this.enchantments);
        if (this.stored_enchantments != null)
            itemStack.setData(DataComponentTypes.STORED_ENCHANTMENTS, this.stored_enchantments);

        if (this.lore != null) {
            List<Component> components = this.lore.stream()
                    .map(s -> {
                        String afterPlaceholders = PlaceholderAPI.setPlaceholders(null, s);
                        return MiniMessage.miniMessage().deserialize("<!i><white>" + afterPlaceholders);
                    })
                    .toList();
            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(components));
        }

        if (this.rarity != null) itemStack.setData(DataComponentTypes.RARITY, this.rarity);
        if (this.break_sound != null) itemStack.setData(DataComponentTypes.BREAK_SOUND, this.break_sound);
        if (this.item_model != null) itemStack.setData(DataComponentTypes.ITEM_MODEL, this.item_model);
        if (this.note_block_sound != null)
            itemStack.setData(DataComponentTypes.NOTE_BLOCK_SOUND, this.note_block_sound);
        if (this.tooltip_style != null) itemStack.setData(DataComponentTypes.TOOLTIP_STYLE, this.tooltip_style);
        if (this.recipes != null) itemStack.setData(DataComponentTypes.RECIPES, this.recipes);
        //    if (this.llama_variant != null) itemStack.setData(DataComponentTypes.LLAMA_VARIANT, Llama.Color);
        if (this.lodestone_tracker != null)
            itemStack.setData(DataComponentTypes.LODESTONE_TRACKER, this.lodestone_tracker);
        if (this.map_decorations != null) itemStack.setData(DataComponentTypes.MAP_DECORATIONS, this.map_decorations);
        if (this.map_id != null) itemStack.setData(DataComponentTypes.MAP_ID, this.map_id);
        if (this.map_color != null) itemStack.setData(DataComponentTypes.MAP_COLOR, this.map_color);
        if (this.map_post_processing != null)
            itemStack.setData(DataComponentTypes.MAP_POST_PROCESSING, this.map_post_processing);
        if (this.max_stack_size != null) itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, this.max_stack_size);
        if (this.mooshroom_variant != null)
            itemStack.setData(DataComponentTypes.MOOSHROOM_VARIANT, this.mooshroom_variant);
        if (this.instrument != null) itemStack.setData(DataComponentTypes.INSTRUMENT, this.instrument);
        if (this.damage != null) itemStack.setData(DataComponentTypes.DAMAGE, this.damage);
        if (this.ominous_bottle_amplifier != null)
            itemStack.setData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, this.ominous_bottle_amplifier);
        if (this.parrot_variant != null) itemStack.setData(DataComponentTypes.PARROT_VARIANT, this.parrot_variant);
        if (this.pig_variant != null) itemStack.setData(DataComponentTypes.PIG_VARIANT, this.pig_variant);
        if (this.pot_decorations != null) itemStack.setData(DataComponentTypes.POT_DECORATIONS, this.pot_decorations);
        if (this.potion_contents != null) itemStack.setData(DataComponentTypes.POTION_CONTENTS, this.potion_contents);
        if (this.provides_banner_patterns != null) itemStack.setData(DataComponentTypes.PROVIDES_BANNER_PATTERNS, this.provides_banner_patterns);
        if (this.provides_trim_material != null)
            itemStack.setData(DataComponentTypes.PROVIDES_TRIM_MATERIAL, this.provides_trim_material);
        if (this.rabbit_variant != null) itemStack.setData(DataComponentTypes.RABBIT_VARIANT, this.rabbit_variant);
        if (this.repair_cost != null) itemStack.setData(DataComponentTypes.REPAIR_COST, this.repair_cost);
        if (this.repairable != null) itemStack.setData(DataComponentTypes.REPAIRABLE, this.repairable);
        if (this.profile != null) itemStack.setData(DataComponentTypes.PROFILE, this.profile);
        if (this.salmon_size != null) itemStack.setData(DataComponentTypes.SALMON_SIZE, this.salmon_size);
        if (this.container_loot != null) itemStack.setData(DataComponentTypes.CONTAINER_LOOT, this.container_loot);
        if (this.suspicious_stew_effects != null)
            itemStack.setData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, this.suspicious_stew_effects);
        if (this.tool != null) itemStack.setData(DataComponentTypes.TOOL, this.tool);
        if (this.tooltip_display != null) itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, this.tooltip_display);
        if (this.tropical_fish_pattern != null)
            itemStack.setData(DataComponentTypes.TROPICAL_FISH_PATTERN, this.tropical_fish_pattern);
        if (this.use_cooldown != null) itemStack.setData(DataComponentTypes.USE_COOLDOWN, this.use_cooldown);
        if (this.use_remainder != null) itemStack.setData(DataComponentTypes.USE_REMAINDER, this.use_remainder);
        if (this.villager_variant != null)
            itemStack.setData(DataComponentTypes.VILLAGER_VARIANT, this.villager_variant);
        if (this.weapon != null) itemStack.setData(DataComponentTypes.WEAPON, this.weapon);
        if (this.wolf_sound_variant != null)
            itemStack.setData(DataComponentTypes.WOLF_SOUND_VARIANT, this.wolf_sound_variant);
        if (this.wolf_variant != null) itemStack.setData(DataComponentTypes.WOLF_VARIANT, this.wolf_variant);
        if (this.writable_book_content != null)
            itemStack.setData(DataComponentTypes.WRITABLE_BOOK_CONTENT, this.writable_book_content);
        if (this.written_book_content != null)
            itemStack.setData(DataComponentTypes.WRITTEN_BOOK_CONTENT, this.written_book_content);
        if (this.jukebox_playable != null)
            itemStack.setData(DataComponentTypes.JUKEBOX_PLAYABLE, this.jukebox_playable);
        if (this.unbreakable) itemStack.setData(DataComponentTypes.UNBREAKABLE);
        if (this.glider) itemStack.setData(DataComponentTypes.GLIDER);
        if (this.intangible_projectile) itemStack.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
        if (this.enchantment_glint_override != null)
            itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, this.enchantment_glint_override);
        if (this.custom_name != null)
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, MiniMessage.miniMessage().deserialize(this.custom_name));
        if (this.item_name != null)
            itemStack.setData(DataComponentTypes.ITEM_NAME, MiniMessage.miniMessage().deserialize(this.item_name));

        this.itemStack = itemStack;
        return this;
    }

    public ItemStack combing(ItemStack itemToOverride) {
        itemToOverride.copyDataFrom(this.itemStack, (dataComponentType) -> this.itemStack.hasData(dataComponentType));
        return itemToOverride;
    }

    public ItemStack asItemStack() {
        return this.itemStack.clone();
    }


    public String id() {
        return this.id;
    }

    private Color loadColor(ConfigurationSection colorSection) {
        int alpha = colorSection.getInt("alpha", 255);
        int red = colorSection.getInt("red", 255);
        int green = colorSection.getInt("green", 255);
        int blue = colorSection.getInt("blue", 255);
        return Color.fromARGB(alpha, red, green, blue);
    }

    private ConsumeEffect loadConsumeEffect(ConfigurationSection section, String type) {
        ConsumeEffectType consumeEffectType = ConsumeEffectType.valueOf(type);
        switch (consumeEffectType) {
            case APPLY_EFFECTS -> {
                List<PotionEffect> potionEffects = new ArrayList<>();
                for (String effectType : section.getConfigurationSection("effects").getKeys(false)) {
                    ConfigurationSection potionSection = section.getConfigurationSection("effects." + effectType);
                    potionEffects.add(this.loadEffect(potionSection, effectType));
                }

                float probability = (float) section.getDouble("probability", 1);
                return ConsumeEffect.applyStatusEffects(potionEffects, probability);
            }

            case REMOVE_EFFECTS -> {
                List<PotionEffectType> itemTypeList = section.getStringList("effects").stream().map((s) -> Registry.MOB_EFFECT.get(Key.key(s))).filter(Objects::nonNull).toList();
                RegistryKeySet<PotionEffectType> potionEffects = RegistrySet.keySetFromValues(RegistryKey.MOB_EFFECT, itemTypeList);
                return ConsumeEffect.removeEffects(potionEffects);
            }
            case CLEAR_ALL_EFFECTS -> {
                return ConsumeEffect.clearAllStatusEffects();
            }
            case TELEPORT_RANDOMLY -> {
                float diameter = (float) section.getDouble("diameter", 16.0D);
                return ConsumeEffect.teleportRandomlyEffect(diameter);
            }
            case PLAY_SOUND -> {
                String soundName = section.getString("sound");
                assert soundName != null;
                return ConsumeEffect.playSoundConsumeEffect(Key.key(soundName));
            }
            default -> {
                return null;
            }
        }

    }

    private PotionEffect loadEffect(ConfigurationSection section, String type) {
        PotionEffectType potionEffectType = Registry.POTION_EFFECT_TYPE.get(Key.key(type));
        if (potionEffectType == null) {
            throw new RuntimeException("Invalid Potion Effect Type");
        } else {
            int duration = section.getInt("duration", 1);
            int amplifier = section.getInt("amplifier", 0);
            boolean ambient = section.getBoolean("ambient", false);
            boolean show_icon = section.getBoolean("show_icon", true);
            boolean show_particles = section.getBoolean("show_particles", true);
            return new PotionEffect(potionEffectType, duration, amplifier, ambient, show_particles, show_icon);
        }
    }

    private Rule loadRule(ConfigurationSection section) {
        RegistrySet.keySet(RegistryKey.BLOCK);
        RegistryKeySet<BlockType> blocks;
        if (section.isConfigurationSection("blocks")) {
            List<BlockType> blockTypeList = section.getStringList("blocks").stream().map((s) -> Registry.BLOCK.get(Key.key(s))).filter(Objects::nonNull).toList();
            blocks = RegistrySet.keySetFromValues(RegistryKey.BLOCK, blockTypeList);
        } else {
            String tag = section.getString("blocks");
            blocks = mainHandler().utilityManager().getBlockSet(tag);
        }

            Float speed = section.contains("speed") ? (float) section.getDouble("speed") : null;
        TriState correct = section.contains("correct_for_drops") ? TriState.byBoolean(section.getBoolean("correct_for_drops")) : TriState.NOT_SET;
        return Tool.rule(blocks, speed, correct);
    }

    private DamageReduction loadDamageReduction(ConfigurationSection section) {
        Registry<DamageType> damageTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
        DamageReduction.Builder builder = DamageReduction.damageReduction();
        List<DamageType> damageTypes = section.getStringList("type").stream().map((s) -> (DamageType) damageTypeRegistry.get(Key.key(s))).toList();
        RegistryKeySet<DamageType> damageTypeRegistryKeySet = RegistrySet.keySetFromValues(RegistryKey.DAMAGE_TYPE, damageTypes);
        builder.type(damageTypeRegistryKeySet);

        if (section.contains("base")) {
            float base = (float) section.getDouble("base");
            builder.base(base);
        }

        if (section.contains("factor")) {
            float factor = (float) section.getDouble("factor");
            builder.factor(factor);
        }

        float horizontalBlockingAngle = (float) section.getDouble("horizontal_blocking_angle", 90.0D);
        builder.factor(horizontalBlockingAngle);
        return builder.build();
    }

    public GroupsItemSection setPainting_variant(Art painting_variant) {
        this.painting_variant = painting_variant;
        this.group.get().set(this.prefix + "paint_variant", painting_variant.assetId().asString());
        this.group.save();
        return this;
    }

    public GroupsItemSection setItemType(ItemType itemType) {
        this.itemType = itemType;
        this.group.get().set(this.prefix + "type", itemType.key().asString());
        this.group.save();
        return this;
    }

    public GroupsItemSection setAxolotl_variant(Variant axolotl_variant) {
        this.axolotl_variant = axolotl_variant;
        return this;
    }

    public GroupsItemSection setBanner_patterns(BannerPatternLayers banner_patterns) {
        this.banner_patterns = banner_patterns;
        return this;
    }

    public GroupsItemSection setBlocks_attacks(BlocksAttacks blocks_attacks) {
        this.blocks_attacks = blocks_attacks;
        String blocksAttacksPrefix = this.prefix + "blocks_attacks.";
        Key blockSound = blocks_attacks.blockSound();
        if (blockSound != null) {
            this.group.get().set(blocksAttacksPrefix + "block_sound", blockSound.asString());
        }

        Key disableSound = blocks_attacks.disableSound();
        if (disableSound != null) {
            this.group.get().set(blocksAttacksPrefix + "disable_sound", disableSound.asString());
        }

        this.group.get().set(blocksAttacksPrefix + "disable_cooldown_scale", blocks_attacks.disableCooldownScale());
        this.group.get().set(blocksAttacksPrefix + "block_delay_seconds", blocks_attacks.blockDelaySeconds());
        ItemDamageFunction damageFunction = blocks_attacks.itemDamage();
        this.group.get().set(blocksAttacksPrefix + "item_damage.threshold", damageFunction.threshold());
        this.group.get().set(blocksAttacksPrefix + "item_damage.base", damageFunction.base());
        this.group.get().set(blocksAttacksPrefix + "item_damage.factor", damageFunction.factor());
        Registry<DamageType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE);
        List<Map<String, Object>> list = new ArrayList<>();
        blocks_attacks.damageReductions().forEach((damageReduction) -> {
            if (damageReduction.type() != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", damageReduction.type().resolve(registry));
                map.put("factor", damageReduction.factor());
                map.put("horizontal_blocking_angle", damageReduction.horizontalBlockingAngle());
                list.add(map);
            }

        });
        if (!list.isEmpty()) {
            this.group.get().set(blocksAttacksPrefix + "damage_reductions", list);
        }

        this.group.save();
        return this;
    }

    public GroupsItemSection setBundleContents(BundleContents bundleContents) {
        this.bundleContents = bundleContents;
        return this;
    }

    public GroupsItemSection setCat_variant(Type cat_variant) {
        this.cat_variant = cat_variant;
        return this;
    }

    public GroupsItemSection setChicken_variant(org.bukkit.entity.Chicken.Variant chicken_variant) {
        this.chicken_variant = chicken_variant;
        return this;
    }

    public GroupsItemSection setCharged_projectiles(ChargedProjectiles charged_projectiles) {
        this.charged_projectiles = charged_projectiles;
        return this;
    }

    public GroupsItemSection setConsumable(Consumable consumable) {
        this.consumable = consumable;
        return this;
    }

    public GroupsItemSection setCow_variant(org.bukkit.entity.Cow.Variant cow_variant) {
        this.cow_variant = cow_variant;
        return this;
    }

    public GroupsItemSection setCustomModelData(CustomModelData customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public GroupsItemSection setDeath_protection(DeathProtection death_protection) {
        this.death_protection = death_protection;
        return this;
    }

    public GroupsItemSection setDamage_resistant(DamageResistant damage_resistant) {
        this.damage_resistant = damage_resistant;
        return this;
    }

    public GroupsItemSection setBase_color(DyeColor base_color) {
        this.base_color = base_color;
        return this;
    }

    public GroupsItemSection setCat_collar(DyeColor cat_collar) {
        this.cat_collar = cat_collar;
        return this;
    }

    public GroupsItemSection setSheep_color(DyeColor sheep_color) {
        this.sheep_color = sheep_color;
        return this;
    }

    public GroupsItemSection setShulker_color(DyeColor shulker_color) {
        this.shulker_color = shulker_color;
        return this;
    }

    public GroupsItemSection setTropical_fish_base_color(DyeColor tropical_fish_base_color) {
        this.tropical_fish_base_color = tropical_fish_base_color;
        return this;
    }

    public GroupsItemSection setTropical_fish_pattern_color(DyeColor tropical_fish_pattern_color) {
        this.tropical_fish_pattern_color = tropical_fish_pattern_color;
        return this;
    }

    public GroupsItemSection setWolf_collar(DyeColor wolf_collar) {
        this.wolf_collar = wolf_collar;
        return this;
    }

    public GroupsItemSection setDyed_color(DyedItemColor dyed_color) {
        this.dyed_color = dyed_color;
        return this;
    }

    public GroupsItemSection setEnchantable(Enchantable enchantable) {
        this.enchantable = enchantable;
        return this;
    }

    public GroupsItemSection setEquippable(Equippable equippable) {
        this.equippable = equippable;
        return this;
    }

    public GroupsItemSection setFirework_explosion(FireworkEffect firework_explosion) {
        this.firework_explosion = firework_explosion;
        return this;
    }

    public GroupsItemSection setFireworks(Fireworks fireworks) {
        this.fireworks = fireworks;
        return this;
    }

    public GroupsItemSection setPotion_duration_scale(Float potion_duration_scale) {
        this.potion_duration_scale = potion_duration_scale;
        return this;
    }

    public GroupsItemSection setFood(FoodProperties food) {
        this.food = food;
        return this;
    }

    public GroupsItemSection setFox_variant(org.bukkit.entity.Fox.Type fox_variant) {
        this.fox_variant = fox_variant;
        return this;
    }

    public GroupsItemSection setFrog_variant(org.bukkit.entity.Frog.Variant frog_variant) {
        this.frog_variant = frog_variant;
        return this;
    }

    public GroupsItemSection setCan_break(ItemAdventurePredicate can_break) {
        this.can_break = can_break;
        return this;
    }

    public GroupsItemSection setCan_place_on(ItemAdventurePredicate can_place_on) {
        this.can_place_on = can_place_on;
        return this;
    }

    public GroupsItemSection setTrim(ItemArmorTrim trim) {
        this.trim = trim;
        return this;
    }

    public GroupsItemSection setAttribute_modifiers(ItemAttributeModifiers attribute_modifiers) {
        this.attribute_modifiers = attribute_modifiers;
        return this;
    }

    public GroupsItemSection setContainer(ItemContainerContents container) {
        this.container = container;
        return this;
    }

    public GroupsItemSection setEnchantments(ItemEnchantments enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public GroupsItemSection setStored_enchantments(ItemEnchantments stored_enchantments) {
        this.stored_enchantments = stored_enchantments;
        return this;
    }

    public GroupsItemSection setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public GroupsItemSection setRarity(ItemRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public GroupsItemSection setBreak_sound(Key break_sound) {
        this.break_sound = break_sound;
        return this;
    }

    public GroupsItemSection setItem_model(Key item_model) {
        this.item_model = item_model;
        return this;
    }

    public GroupsItemSection setNote_block_sound(Key note_block_sound) {
        this.note_block_sound = note_block_sound;
        return this;
    }

    public GroupsItemSection setTooltip_style(Key tooltip_style) {
        this.tooltip_style = tooltip_style;
        return this;
    }

    public GroupsItemSection setRecipes(List<Key> recipes) {
        this.recipes = recipes;
        return this;
    }

    public GroupsItemSection setLlama_variant(Color llama_variant) {
        this.llama_variant = llama_variant;
        return this;
    }

    public GroupsItemSection setLodestone_tracker(LodestoneTracker lodestone_tracker) {
        this.lodestone_tracker = lodestone_tracker;
        return this;
    }

    public GroupsItemSection setMap_decorations(MapDecorations map_decorations) {
        this.map_decorations = map_decorations;
        return this;
    }

    public GroupsItemSection setMap_id(MapId map_id) {
        this.map_id = map_id;
        return this;
    }

    public GroupsItemSection setMap_color(MapItemColor map_color) {
        this.map_color = map_color;
        return this;
    }

    public GroupsItemSection setMap_post_processing(MapPostProcessing map_post_processing) {
        this.map_post_processing = map_post_processing;
        return this;
    }

    public GroupsItemSection setMax_stack_size(Integer max_stack_size) {
        this.max_stack_size = max_stack_size;
        return this;
    }

    public GroupsItemSection setMooshroom_variant(org.bukkit.entity.MushroomCow.Variant mooshroom_variant) {
        this.mooshroom_variant = mooshroom_variant;
        return this;
    }

    public GroupsItemSection setInstrument(MusicInstrument instrument) {
        this.instrument = instrument;
        return this;
    }

    public GroupsItemSection setDamage(Integer damage) {
        this.damage = damage;
        return this;
    }

    public GroupsItemSection setOminous_bottle_amplifier(OminousBottleAmplifier ominous_bottle_amplifier) {
        this.ominous_bottle_amplifier = ominous_bottle_amplifier;
        return this;
    }

    public GroupsItemSection setParrot_variant(org.bukkit.entity.Parrot.Variant parrot_variant) {
        this.parrot_variant = parrot_variant;
        return this;
    }

    public GroupsItemSection setPig_variant(org.bukkit.entity.Pig.Variant pig_variant) {
        this.pig_variant = pig_variant;
        return this;
    }

    public GroupsItemSection setPot_decorations(PotDecorations pot_decorations) {
        this.pot_decorations = pot_decorations;
        return this;
    }

    public GroupsItemSection setPotion_contents(PotionContents potion_contents) {
        this.potion_contents = potion_contents;
        return this;
    }

    public GroupsItemSection setProvides_banner_patterns(RegistryKeySet<PatternType> provides_banner_patterns) {
        this.provides_banner_patterns = provides_banner_patterns;
        return this;
    }

    public GroupsItemSection setProvides_trim_material(TrimMaterial provides_trim_material) {
        this.provides_trim_material = provides_trim_material;
        return this;
    }

    public GroupsItemSection setRabbit_variant(org.bukkit.entity.Rabbit.Type rabbit_variant) {
        this.rabbit_variant = rabbit_variant;
        return this;
    }

    public GroupsItemSection setRepair_cost(Integer repair_cost) {
        this.repair_cost = repair_cost;
        return this;
    }

    public GroupsItemSection setRepairable(Repairable repairable) {
        this.repairable = repairable;
        return this;
    }

    public GroupsItemSection setProfile(ResolvableProfile profile) {
        this.profile = profile;
        return this;
    }

    public GroupsItemSection setSalmon_size(org.bukkit.entity.Salmon.Variant salmon_size) {
        this.salmon_size = salmon_size;
        return this;
    }

    public GroupsItemSection setContainer_loot(SeededContainerLoot container_loot) {
        this.container_loot = container_loot;
        return this;
    }

    public GroupsItemSection setSuspicious_stew_effects(SuspiciousStewEffects suspicious_stew_effects) {
        this.suspicious_stew_effects = suspicious_stew_effects;
        return this;
    }

    public GroupsItemSection setTool(Tool tool) {
        this.tool = tool;
        return this;
    }

    public GroupsItemSection setTooltip_display(TooltipDisplay tooltip_display) {
        this.tooltip_display = tooltip_display;
        return this;
    }

    public GroupsItemSection setTropical_fish_pattern(Pattern tropical_fish_pattern) {
        this.tropical_fish_pattern = tropical_fish_pattern;
        return this;
    }

    public GroupsItemSection setUse_cooldown(UseCooldown use_cooldown) {
        this.use_cooldown = use_cooldown;
        return this;
    }

    public GroupsItemSection setUse_remainder(UseRemainder use_remainder) {
        this.use_remainder = use_remainder;
        return this;
    }

    public GroupsItemSection setVillager_variant(org.bukkit.entity.Villager.Type villager_variant) {
        this.villager_variant = villager_variant;
        return this;
    }

    public GroupsItemSection setWeapon(Weapon weapon) {
        this.weapon = weapon;
        return this;
    }

    public GroupsItemSection setWolf_sound_variant(SoundVariant wolf_sound_variant) {
        this.wolf_sound_variant = wolf_sound_variant;
        return this;
    }

    public GroupsItemSection setWolf_variant(org.bukkit.entity.Wolf.Variant wolf_variant) {
        this.wolf_variant = wolf_variant;
        return this;
    }

    public GroupsItemSection setWritable_book_content(WritableBookContent writable_book_content) {
        this.writable_book_content = writable_book_content;
        return this;
    }

    public GroupsItemSection setWritten_book_content(WrittenBookContent written_book_content) {
        this.written_book_content = written_book_content;
        return this;
    }

    public GroupsItemSection setEnchantment_glint_override(Boolean enchantment_glint_override) {
        this.enchantment_glint_override = enchantment_glint_override;
        return this;
    }

    public GroupsItemSection setGlider(boolean glider) {
        this.glider = glider;
        return this;
    }

    public GroupsItemSection setIntangible_projectile(boolean intangible_projectile) {
        this.intangible_projectile = intangible_projectile;
        return this;
    }

    public GroupsItemSection setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public GroupsItemSection setJukebox_playable(JukeboxPlayable jukebox_playable) {
        this.jukebox_playable = jukebox_playable;
        return this;
    }

    public GroupsItemSection setCustom_name(String custom_name) {
        this.custom_name = custom_name;
        return this;
    }

    public GroupsItemSection setItem_name(String item_name) {
        this.item_name = item_name;
        return this;
    }

    private static enum ConsumeEffectType {
        APPLY_EFFECTS,
        REMOVE_EFFECTS,
        CLEAR_ALL_EFFECTS,
        TELEPORT_RANDOMLY,
        PLAY_SOUND;

        // $FF: synthetic method
        private static ConsumeEffectType[] $values() {
            return new ConsumeEffectType[]{APPLY_EFFECTS, REMOVE_EFFECTS, CLEAR_ALL_EFFECTS, TELEPORT_RANDOMLY, PLAY_SOUND};
        }
    }

    public List<String> getLore(){
        return this.lore;
    }
}
