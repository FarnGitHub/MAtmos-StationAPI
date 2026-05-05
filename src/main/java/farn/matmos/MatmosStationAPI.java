package farn.matmos;

import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.MAtmos;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public class MatmosStationAPI {

    @EventListener
    public void initFinished(InitFinishedEvent event) {
        MAtmos.INSTANCE.initialize();
    }

    @EventListener
    public void registerKey(KeyBindingRegisterEvent event) {
        event.keyBindings.add(MAtmos.INSTANCE.matmosKeyBinding);
    }

    @EventListener
    public void handleKeyboardEvent(KeyStateChangedEvent event) {
        if(Minecraft.INSTANCE.world == null) return;
        int eventKey = Keyboard.getEventKey();
        if(Keyboard.getEventKeyState()) {
            for(KeyBinding keybind : MAtmos.INSTANCE.keyManager.keys.keySet()) {
                if(keybind.code == eventKey) {
                    MAtmos.INSTANCE.KeyboardEvent(keybind);
                    return;
                }
            }
        }
    }
}
