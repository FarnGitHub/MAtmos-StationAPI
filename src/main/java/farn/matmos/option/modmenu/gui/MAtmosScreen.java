package farn.matmos.option.modmenu.gui;

import farn.matmos.option.MAtmosOption;
import net.minecraft.MAtmos;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

public class MAtmosScreen extends Screen {
    public Screen parent;

    public MAtmosScreen(Screen parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        int index = 0;
        for(MAtmosOption option : MAtmosOption.values()) {
            if(option.type == 1)
                this.buttons.add(new MAtmosSliderWidget(index, this.width / 2 - 155 + index % 2 * 160, this.height / 6 + 24 * (index >> 1), option));
            else
                this.buttons.add(new MAtmosButtonWidget(index, this.width / 2 - 155 + index % 2 * 160, this.height / 6 + 24 * (index >> 1), option));
            ++index;

        }
        this.buttons.add(new ButtonWidget(100, this.width / 2 - 100, this.height / 6 + 168, I18n.getTranslation("gui.done")));
        this.buttons.add(new ButtonWidget(101, this.width / 2 - 100, this.height / 6 + 144, matmosEnabledDisplayString()));
    }


    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
    }

    protected void buttonClicked(ButtonWidget button) {
        if(!button.active) return;

        if(button instanceof MAtmosButtonWidget boolButton) {
            boolButton.updateButton();
        } else if(button.id == 101) {
            if(MAtmos.INSTANCE.isKnowledgeTurnedOn())
                MAtmos.INSTANCE.knowledge.turnOff();
            else
                MAtmos.INSTANCE.knowledge.turnOn();
            button.text = matmosEnabledDisplayString();
        } else if(button.id == 100) {
            this.minecraft.setScreen(this.parent);
        }
    }

    public void removed() {
        MAtmos.INSTANCE.saveOptions();
        super.removed();
    }

    private String matmosEnabledDisplayString() {
        return "MAtmos : " + (MAtmos.INSTANCE.isKnowledgeTurnedOn() ? "Enabled" : "Disabled");
    }
}
