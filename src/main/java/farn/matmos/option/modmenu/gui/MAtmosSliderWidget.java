package farn.matmos.option.modmenu.gui;

import farn.matmos.option.MAtmosOption;
import matmos.minecraft.MAtmos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

public class MAtmosSliderWidget extends ButtonWidget {
    public boolean dragging = false;
    public MAtmosOption option;
    public float value;

    public MAtmosSliderWidget(int id, int x, int y, MAtmosOption option) {
        super(id, x, y, 150, 20, MAtmosOption.getDisplayString(option));
        this.option = option;
        this.value = MAtmosOption.getValue(option);
    }

    protected int getYImage(boolean hovered) {
        return 0;
    }

    protected void renderBackground(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.value = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                if (value < 0.0F) {
                    this.value = 0.0F;
                }

                if (this.value > 1.0F) {
                    this.value = 1.0F;
                }

                MAtmosOption.setValue(option, value);
                this.text = MAtmosOption.getDisplayString(option);
            }

            if (this.active)
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            else
                GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
            this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexture(this.x + (int)(this.value * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
        }
        this.active = MAtmos.isKnowledgeTurnedOn() && (this.option != MAtmosOption.MUSIC_VOLUME || !MAtmos.soundManager.getMusicVolumeIsBasedOffMinecraft());
    }

    public boolean isMouseOver(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.isMouseOver(minecraft, mouseX, mouseY)) {
            this.value = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
            if (this.value < 0.0F) {
                this.value = 0.0F;
            }

            if (this.value > 1.0F) {
                this.value = 1.0F;
            }

            MAtmosOption.setValue(option, value);
            this.text = MAtmosOption.getDisplayString(option);
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}
