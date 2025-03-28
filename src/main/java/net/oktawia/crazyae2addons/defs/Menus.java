package net.oktawia.crazyae2addons.defs;

import appeng.core.AppEng;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.oktawia.crazyae2addons.Parts.DisplayPart;
import net.oktawia.crazyae2addons.Parts.NBTExportBusPart;
import net.oktawia.crazyae2addons.entities.AutoEnchanterBE;
import net.oktawia.crazyae2addons.entities.CraftingCancelerBE;
import net.oktawia.crazyae2addons.Parts.EntityTickerPart;
import net.oktawia.crazyae2addons.entities.PatternModifierBE;
import net.oktawia.crazyae2addons.menus.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Menus {

    private static final Map<ResourceLocation, MenuType<?>> MENU_TYPES = new HashMap<>();

    public static Map<ResourceLocation, MenuType<?>> getMenuTypes() {
        return Collections.unmodifiableMap(MENU_TYPES);
    }

    public static final MenuType<CraftingCancelerMenu> CRAFTING_CANCELER_MENU = create(
            "crafting_canceler",
            CraftingCancelerMenu::new,
            CraftingCancelerBE.class

    );
    public static final MenuType<EntityTickerMenu> ENTITY_TICKER_MENU = create(
            "entity_ticker",
            EntityTickerMenu::new,
            EntityTickerPart.class
    );
    public static final MenuType<NBTExportBusMenu> NBT_EXPORT_BUS_MENU = create(
            "nbt_export_bus",
            NBTExportBusMenu::new,
            NBTExportBusPart.class
    );
    public static final MenuType<PatternModifierMenu> PATTERN_MODIFIER_MENU = create(
            "nbt_pattern_modifier",
            PatternModifierMenu::new,
            PatternModifierBE.class
    );
    public static final MenuType<AutoEnchanterMenu> AUTO_ENCHANTER_MENU = create(
            "auto_enchanter",
            AutoEnchanterMenu::new,
            AutoEnchanterBE.class
    );
    public static final MenuType<DisplayMenu> DISPLAY_MENU = create(
            "display",
            DisplayMenu::new,
            DisplayPart.class
    );


    public static <C extends AEBaseMenu, I> MenuType<C> create(
            String id, MenuTypeBuilder.MenuFactory<C, I> factory, Class<I> host) {
        var menu = MenuTypeBuilder.create(factory, host).build(id);
        MENU_TYPES.put(AppEng.makeId(id), menu);
        return menu;
    }
}