package net.oktawia.crazyae2addons.defs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import appeng.core.definitions.BlockDefinition;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.oktawia.crazyae2addons.blocks.*;
import net.oktawia.crazyae2addons.items.*;
import net.oktawia.crazyae2addons.CrazyAddons;

public class Blocks {

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();
    public static final BlockDefinition<CraftingCancelerBlock> CRAFTING_CANCELER_BLOCK = block(
            "Crafting Canceler",
            "crafting_canceler_block",
            CraftingCancelerBlock::new,
            CraftingCancelerBlockItem::new
    );
    public static final BlockDefinition<AutoEnchanterBlock> AUTO_ENCHANTER_BLOCK = block(
            "Auto Enchanter",
            "auto_enchanter_block",
            AutoEnchanterBlock::new,
            AutoEnchanterBlockItem::new
    );
    public static final BlockDefinition<MEDataControllerBlock> ME_DATA_CONTROLLER_BLOCK = block(
            "ME Data Controller",
            "me_data_controller_block",
            MEDataControllerBlock::new,
            MEDataControllerBlockItem::new
    );
    public static final BlockDefinition<DataProcessorBlock> DATA_PROCESSOR_BLOCK = block(
            "Data Processor",
            "data_processor_block",
            DataProcessorBlock::new,
            DataProcessorBlockItem::new
    );
    public static final BlockDefinition<DataTrackerBlock> DATA_TRACKER_BLOCK = block(
            "Data Tracker",
            "data_tracker_block",
            DataTrackerBlock::new,
            DataTrackerBlockItem::new
    );
    public static final BlockDefinition<CircuitedPatternProviderBlock> CIRCUITED_PATTERN_PROVIDER_BLOCK = block(
            "Circuited Pattern Provider",
            "circuited_pp",
            CircuitedPatternProviderBlock::new,
            CircuitedPatternProviderBlockItem::new
    );
    public static final BlockDefinition<AmpereMeterBlock> AMPERE_METER_BLOCK = block(
            "Ampere Meter",
            "ampere_meter",
            AmpereMeterBlock::new,
            AmpereMeterBlockItem::new
    );
    public static final BlockDefinition<IsolatedDataProcessorBlock> ISOLATED_DATA_PROCESSOR_BLOCK = block(
            "Isolated Data Processor",
            "isolated_data_processor_block",
            IsolatedDataProcessorBlock::new,
            IsolatedDataProcessorBlockItem::new
    );
    public static final BlockDefinition<ImpulsedPatternProviderBlock> IMPULSED_PATTERN_PROVIDER_BLOCK = block(
            "Impulsed Pattern Provider",
            "impulsed_pp",
            ImpulsedPatternProviderBlock::new,
            ImpulsedPatternProviderBlockItem::new
    );
    public static final BlockDefinition<SignallingInterfaceBlock> SIGNALLING_INTERFACE_BLOCK = block(
            "Signalling Interface",
            "signalling_interface_block",
            SignallingInterfaceBlock::new,
            SignallingInterfaceBlockItem::new
    );

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }


    public static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var block = blockSupplier.get();
        var item = itemFactory.apply(block, new Item.Properties());

        var definition = new BlockDefinition<>(englishName, CrazyAddons.makeId(id), block, item);
        BLOCKS.add(definition);
        return definition;
    }
}