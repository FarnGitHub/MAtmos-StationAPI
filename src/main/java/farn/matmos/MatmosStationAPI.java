package farn.matmos;

import net.minecraft.MAtmos;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;

@SuppressWarnings("unused")
public class MatmosStationAPI {

    @EventListener
    public void initFinished(InitFinishedEvent event) {
        MAtmos.INSTANCE.initialize();
    }
}
