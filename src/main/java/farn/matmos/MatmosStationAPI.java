package farn.matmos;

import net.minecraft.MAtmos;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingEvent;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;

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
    public void handleKeyboardEvent(KeyBindingEvent event) {
        MAtmos.INSTANCE.KeyboardEvent(event.keyBinding);
    }

    public static String apron$fixSheetPackage(String originalName) {
        String newName = originalName.split("\\.")[0];
        return "net.minecraft." + newName;
    }
}
