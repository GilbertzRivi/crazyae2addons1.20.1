package net.oktawia.crazyae2addons.menus;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.parts.NBTExportBusPart;
import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;

public class NBTExportBusMenu extends AEBaseMenu {
    public static final String SEND_MATCH_MODE = "SendMatchMode";
    public static final String SEND_DATA = "SendData";

    @GuiSync(18)
    public boolean mode;

    @GuiSync(31)
    public String data;

    public NBTExportBusPart host;

    public NBTExportBusMenu(int id, Inventory playerInventory, NBTExportBusPart host) {
        super(CrazyMenuRegistrar.NBT_EXPORT_BUS_MENU.get(), id, playerInventory, host);
        registerClientAction(SEND_MATCH_MODE, Boolean.class, this::updateMatchMode);
        registerClientAction(SEND_DATA, String.class, this::updateData);
        this.host = host;
        this.mode = host.matchmode;
        this.data = host.data;
        this.host.setMenu(this);
    }

    public void updateMatchMode(boolean mode){
        this.mode = mode;
        this.host.matchmode = mode;
        this.host.getHost().markForSave();
        if (isClientSide()) {
            sendClientAction(SEND_MATCH_MODE, mode);
        }
    }

    public void updateData(String data){
        this.data = data;
        this.host.data = data;
        this.host.getHost().markForSave();
        if (isClientSide()){
            sendClientAction(SEND_DATA, data);
        }
    }
}
