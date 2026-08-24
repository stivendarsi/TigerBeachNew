package me.stivendarsi.tigerBeach.shop;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.stivendarsi.tigerBeach.utility.PriceVariable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.stivendarsi.tigerBeach.TigerBeach.mainHandler;
import static me.stivendarsi.tigerBeach.TigerBeach.plugin;

public class AisleMenuHolder implements InventoryHolder {

    private final Inventory inventory;
    private final @NotNull Aisle aisle;

    public AisleMenuHolder(@NotNull Aisle aisle) {
        this.inventory = plugin().getServer().createInventory(this, 27, MiniMessage.miniMessage().deserialize("<font:tiger_beach:beach><!shadow><white>\ue002\ue003"));
        this.aisle = aisle;

        List<Product> products = new ArrayList<>(aisle.productMap().values());

        for (int i = 9; i < 18 && !products.isEmpty(); i++) {
            Product product = products.getFirst(); // safe because we check isEmpty()
            ItemStack itemStack = product.getItem();
            products.removeFirst();
            if (itemStack == null) continue;
            Key key = Key.key(aisle.getCleanName(), product.productId());
            itemStack.editPersistentDataContainer(pdc ->
                    pdc.set(mainHandler().constants().itemAisleKey(), PersistentDataType.STRING, key.asString())
            );

            List<Component> lore = new ArrayList<>();
            if (itemStack.hasData(DataComponentTypes.LORE)) {
                ItemLore itemLore = itemStack.getData(DataComponentTypes.LORE);
                if (itemLore != null) {
                    lore.addAll(itemLore.lines());
                    lore.add(Component.empty());
                }
            }
            MiniMessage miniMessage = MiniMessage.miniMessage();
            lore.add(miniMessage.deserialize("<!i><gray><u>        מחיר        "));
            for (PriceVariable priceVariable : product.price()) {
                ItemStack productItem = priceVariable.getItem();
                if (productItem == null) continue;
                TagResolver priceResolver = TagResolver.builder()
                        .tag("item_name", Tag.inserting(productItem.effectiveName()))
                        .tag("amount", Tag.inserting(Component.text(priceVariable.amount())))
                        .build();
                lore.add(miniMessage.deserialize("<!i><item_name> <#99ff2b><amount>x", priceResolver));
            }

            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));

            this.inventory.setItem(i, itemStack);
        }


        ItemStack close = ItemType.BARRIER.createItemStack();
        close.setData(DataComponentTypes.ITEM_NAME, MiniMessage.miniMessage().deserialize("<red>חזרה"));
        close.setData(DataComponentTypes.ITEM_MODEL, net.kyori.adventure.key.Key.key("tiger_beach:gui/close"));
        this.inventory.setItem(22, close);

    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public Aisle aisle() {
        return aisle;
    }


}