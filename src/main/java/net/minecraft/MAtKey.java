package net.minecraft;

import ha3.Ha3KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;

public class MAtKey extends Ha3KeyBinding {
	MAtKey(KeyBinding key) {
		super(key);
	}

	protected void doBefore() {
	}

	protected void doDuring(int var1) {
		if(MAtmos.INSTANCE.isKnowledgeTurnedOn() && var1 == 7) {
			MAtmos.INSTANCE.tutorial_signalKnowsHowToUseVolume();
			if(!(Minecraft.INSTANCE.currentScreen instanceof InventoryScreen)) {
				((MAtScroller)MAtmos.INSTANCE.scroller).start(false);
			} else {
				Minecraft.INSTANCE.setScreen(null);
				MAtmos.INSTANCE.tutorial_signalKnowsHowToChangeMusicVolume();
				((MAtScroller)MAtmos.INSTANCE.scroller).start(true);
			}
		}

	}

	protected void doAfter(int var1) {
		if(var1 < 6) {
			MAtmos.INSTANCE.userToggle(false);
		} else if(var1 > 7) {
			MAtmos.INSTANCE.scroller.stop();
			MAtmos.INSTANCE.saveOptions();
			if(!MAtmos.INSTANCE.isKnowledgeTurnedOn()) {
				MAtmos.INSTANCE.userToggle(true);
			}
		}

	}
}
