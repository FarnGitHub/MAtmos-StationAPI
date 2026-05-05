package farn.matmos.option.modmenu.gui;

import farn.matmos.option.MAtmosOption;
import net.minecraft.MAtmos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.OptionButtonWidget;

public class MAtmosButtonWidget extends OptionButtonWidget {
    public MAtmosOption option;

    public MAtmosButtonWidget(int id, int x, int y, MAtmosOption option) {
        super(id, x, y, MAtmosOption.getDisplayString(option));
        this.option = option;
    }

    public void updateButton() {
        MAtmosOption.toggleValue(option);
        this.text = MAtmosOption.getDisplayString(option);
    }

    protected void renderBackground(Minecraft minecraft, int mouseX, int mouseY) {
        super.renderBackground(minecraft, mouseX, mouseY);
        this.active = MAtmos.INSTANCE.isKnowledgeTurnedOn();
    }
}
