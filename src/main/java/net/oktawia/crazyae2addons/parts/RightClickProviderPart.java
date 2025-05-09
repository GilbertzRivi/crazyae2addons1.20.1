package net.oktawia.crazyae2addons.parts;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.core.definitions.AEItems;
import appeng.items.parts.PartModels;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.automation.UpgradeablePart;
import appeng.parts.p2p.P2PModels;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;
import net.oktawia.crazyae2addons.menus.RightClickProviderMenu;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class RightClickProviderPart extends UpgradeablePart implements
        IUpgradeableObject, IGridTickable, InternalInventoryHost, MenuProvider {

    public AppEngInternalInventory inv = new AppEngInternalInventory(this, 1);
    private static final P2PModels MODELS = new P2PModels(
            new ResourceLocation(CrazyAddons.MODID, "part/right_click_provider"));

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }
    public FakePlayer fakePlayer;
    public int waitingFor = 0;

    public RightClickProviderPart(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(8)
                .addService(IGridTickable.class,this);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
    }

    @Override
    public void readFromNBT(CompoundTag extra) {
        super.readFromNBT(extra);
        var inv = this.inv;
        if (inv != InternalInventory.empty()) {
            var opt = extra.getCompound("inv");
            for (int x = 0; x < inv.size(); x++) {
                var item = opt.getCompound("item" + x);
                inv.setItemDirect(x, ItemStack.of(item));
            }
        }
    }


    @Override
    public void writeToNBT(CompoundTag extra) {
        super.writeToNBT(extra);
        var inv = this.inv;
        if (inv != InternalInventory.empty()) {
            final CompoundTag opt = new CompoundTag();
            for (int x = 0; x < inv.size(); x++) {
                final CompoundTag item = new CompoundTag();
                final ItemStack is = inv.getStackInSlot(x);
                if (!is.isEmpty()) {
                    is.save(item);
                }
                opt.put("item" + x, item);
            }
            extra.put("inv", opt);
        }
    }

    public IPartModel getStaticModels() {
        return MODELS.getModel(this.isPowered(), this.isActive());
    }

    @Override
    public boolean onPartActivate(Player p, InteractionHand hand, Vec3 pos) {
        if (!p.getCommandSenderWorld().isClientSide()) {
            MenuOpener.open(CrazyMenuRegistrar.RIGHT_CLICK_PROVIDER_MENU.get(), p, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RightClickProviderMenu(containerId, playerInventory, this);
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(5, 5, 12, 11, 11, 13);
        bch.addBox(3, 3, 13, 13, 13, 14);
        bch.addBox(2, 2, 14, 14, 14, 16);
    }

    @Override
    public void upgradesChanged() {
        getHost().markForSave();
    }

    @Override
    public boolean hasCustomName() {
        return super.hasCustomName();
    }

    @Override
    public Component getDisplayName() {
        return super.getDisplayName();
    }

    @Override
    protected int getUpgradeSlots() {
        return 4;
    }


    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1,
                1, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        waitingFor ++;
        if (waitingFor < 40 / Math.pow(2, getInstalledUpgrades(AEItems.SPEED_CARD))){
            return TickRateModulation.IDLE;
        }
        waitingFor = 0;
        if (getLevel().getServer() == null) return TickRateModulation.IDLE;
        var world = getLevel().getServer().getLevel(getLevel().dimension());
        if (this.fakePlayer == null){
            this.fakePlayer = FakePlayerFactory.get(Objects.requireNonNull(getLevel().getServer()).getLevel(getLevel().dimension()),
                    new GameProfile(UUID.randomUUID(), "[CrazyAE2Addons]"));
        }
        if (world == null) return TickRateModulation.IDLE;
        simulateUse(getLevel().getServer().getLevel(getLevel().dimension()));
        return TickRateModulation.IDLE;
    }

    public void simulateUse(ServerLevel world) {
        BlockPos basePos = getBlockEntity().getBlockPos();
        Direction side = getSide();
        BlockPos targetPos = basePos.relative(side);
        BlockState targetState = world.getBlockState(targetPos);
        ItemStack stack = this.inv.getStackInSlot(0);

        fakePlayer.absMoveTo(
                targetPos.getX() + 0.5,
                targetPos.getY() + 1.0,
                targetPos.getZ() + 0.5,
                0.0F,
                90.0F
        );

        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        fakePlayer.getInventory().setItem(fakePlayer.getInventory().selected, stack.copy());

        if (stack.isEmpty()) {
            Vec3 hitVec = Vec3.atCenterOf(targetPos);
            BlockHitResult hit = new BlockHitResult(hitVec, side, targetPos, false);
            fakePlayer.gameMode.useItemOn(fakePlayer, world, stack, InteractionHand.MAIN_HAND, hit);

            this.inv.setItemDirect(0, fakePlayer.getMainHandItem().copy());
            return;
        }

        if (stack.getItem() instanceof BucketItem bucket) {
            boolean isEmptyBucket = stack.getItem() == Items.BUCKET;
            if (isEmptyBucket) {
                if (targetState.getFluidState().isEmpty()) {
                    return;
                }
                Vec3 eyePos = fakePlayer.getEyePosition(1.0F);
                Vec3 lookVec = fakePlayer.getLookAngle();
                Vec3 reachVec = eyePos.add(lookVec.scale(5.0D));

                ClipContext context = new ClipContext(eyePos, reachVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, fakePlayer);
                HitResult pickResult = world.clip(context);

                if (pickResult.getType() == HitResult.Type.BLOCK && pickResult instanceof BlockHitResult blockHit) {
                    BlockPos hitPos = blockHit.getBlockPos();
                    BlockState hitState = world.getBlockState(hitPos);
                    if (!hitState.isAir()) {
                        InteractionResultHolder<ItemStack> result = bucket.use(world, fakePlayer, InteractionHand.MAIN_HAND);
                        this.inv.setItemDirect(0, result.getObject().copy());
                    }
                }
                return;
            } else {
                BlockState frontState = world.getBlockState(targetPos);
                BlockState belowFront = world.getBlockState(targetPos.relative(Direction.DOWN));

                if (!frontState.isAir()) {
                    Vec3 frontVec = Vec3.atCenterOf(targetPos).add(Vec3.atLowerCornerOf(side.getOpposite().getNormal()).scale(0.5));
                    BlockHitResult frontHit = new BlockHitResult(frontVec, side.getOpposite(), targetPos, false);

                    fakePlayer.gameMode.useItemOn(fakePlayer, world, stack, InteractionHand.MAIN_HAND, frontHit);

                    this.inv.setItemDirect(0, fakePlayer.getMainHandItem().copy());
                    return;
                }

                Vec3 eyePos = fakePlayer.getEyePosition(1.0F);
                Vec3 lookVec = fakePlayer.getLookAngle();
                Vec3 reachVec = eyePos.add(lookVec.scale(5.0D));

                ClipContext context = new ClipContext(eyePos, reachVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, fakePlayer);
                HitResult pickResult = world.clip(context);

                if (pickResult.getType() == HitResult.Type.BLOCK && !belowFront.isAir() && pickResult instanceof BlockHitResult blockHit) {
                    BlockPos hitPos = blockHit.getBlockPos();
                    BlockState hitState = world.getBlockState(hitPos);
                    if (!hitState.isAir()) {
                        InteractionResultHolder<ItemStack> result = bucket.use(world, fakePlayer, InteractionHand.MAIN_HAND);
                        this.inv.setItemDirect(0, result.getObject().copy());
                        return;
                    }
                }
                return;
            }
        }
        BlockHitResult hit;
        if (targetState.isAir()) {
            BlockPos below = targetPos.below();
            Vec3 hitVec = Vec3.atCenterOf(below).add(0, 0.5, 0);
            hit = new BlockHitResult(hitVec, Direction.UP, below, false);
        } else {
            Vec3 hitVec = Vec3.atCenterOf(targetPos).add(Vec3.atLowerCornerOf(side.getOpposite().getNormal()).scale(0.5));
            hit = new BlockHitResult(hitVec, side.getOpposite(), targetPos, false);
        }
        fakePlayer.gameMode.useItemOn(fakePlayer, world, stack, InteractionHand.MAIN_HAND, hit);
        this.inv.setItemDirect(0, fakePlayer.getMainHandItem().copy());
    }



    @Override
    public void saveChanges() {
        getHost().markForSave();
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        getHost().markForSave();
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(UPGRADES)) {
            return getUpgrades();
        }
        return super.getSubInventory(id);
    }
}