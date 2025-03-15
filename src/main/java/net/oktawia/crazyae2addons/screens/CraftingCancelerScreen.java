package net.oktawia.crazyae2addons.screens;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.TabButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.PlainTextButton;
import appeng.menu.implementations.UpgradeableMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.Utils;
import net.oktawia.crazyae2addons.entities.CraftingCancelerBE;
import net.oktawia.crazyae2addons.menus.CraftingCancelerMenu;

import java.util.logging.Logger;

public class CraftingCancelerScreen<C extends CraftingCancelerMenu> extends AEBaseScreen<C> {
    private static AETextField duration;
    private static AECheckbox onoffbutton;
    private static PlainTextButton confirm;
    public static boolean initialized;

    @Override
    protected void updateBeforeRender(){
        super.updateBeforeRender();
        if (!initialized){
            duration.setValue(String.valueOf(getMenu().dur));
            onoffbutton.setSelected(getMenu().en);
            initialized = true;
        }
    }

    public CraftingCancelerScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
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
        confirm = new PlainTextButton(
                0,0,0,0, Component.literal("Save"), btn -> {validateInput();}, Minecraft.getInstance().font);
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
}