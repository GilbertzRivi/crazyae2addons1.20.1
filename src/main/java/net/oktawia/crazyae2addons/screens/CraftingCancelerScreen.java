package net.oktawia.crazyae2addons.screens;

import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.TabButton;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.Utils;
import net.oktawia.crazyae2addons.menus.CraftingCancelerMenu;

public class CraftingCancelerScreen extends UpgradeableScreen<CraftingCancelerMenu> {
    private static AETextField duration;
    private static AECheckbox onoffbutton;
    private static TabButton confirm;
    public static Integer dur = 0;
    public static boolean en = false;
    public static boolean initialized;

    @Override
    protected void updateBeforeRender(){
        super.updateBeforeRender();
        if (!initialized){
            duration.setValue(String.valueOf(dur));
            onoffbutton.setSelected(en);
            initialized = true;
        }
    }

    public CraftingCancelerScreen(CraftingCancelerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, new ScreenStyle());
        setupGui();
        this.widgets.add("onoffbutton", onoffbutton);
        this.widgets.add("duration", duration);
        this.widgets.add("confirm", confirm);
        initialized = false;
    }

    private void setupGui(){
        onoffbutton = new AECheckbox(
                0, 0, 300, 10, style, Component.empty()
        );
        duration = new AETextField(
                style, Minecraft.getInstance().font, 0, 0, 0, 0
        );
        duration.setBordered(false);
        confirm = new TabButton(
                Icon.VALID, Component.literal(CrazyAddons.checkmark), btn -> {
            validateInput();
        }
        );
    }

    private void validateInput(){
        String input = duration.getValue();
        boolean valid = Utils.checkNumber(input);
        if (valid && Integer.parseInt(duration.getValue()) >= 15 && Integer.parseInt(duration.getValue()) <= 360){
            valid = true;
        } else {
            valid = false;
        }
        boolean en = false;
        int dur = 0;
        if (!valid){
            onoffbutton.setSelected(false);
            duration.setTextColor(0xFF0000);
            Runnable setColorFunction = () -> duration.setTextColor(0xFFFFFF);
            Runnable clearInput = () -> duration.setValue("");
            Utils.asyncDelay(setColorFunction, 1);
            Utils.asyncDelay(clearInput, 1);
        }
        else{
            en = true;
            dur = Integer.parseInt(duration.getValue());
            onoffbutton.setSelected(true);
            duration.setTextColor(0x00FF00);
            Runnable setColorFunction = () -> duration.setTextColor(0xFFFFFF);
            Utils.asyncDelay(setColorFunction, 1);
        }
        menu.sendState(en);
        menu.sendDuration(dur);
    }

    public void updateCraftingCancellerStatus(Boolean state, Integer duration){
        en = state;
        dur = duration;
    }
}