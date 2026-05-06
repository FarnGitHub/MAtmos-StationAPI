package farn.matmos;

import matmos.minecraft.MAtmos;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class MatmosStationAPI {

    @SuppressWarnings("UnstableApiUsage")
    @Entrypoint.Namespace
    public static Namespace NAMESPACE = Namespace.resolve();

    @Entrypoint.Logger("MAtmos")
    public static Logger LOGGER = NAMESPACE.getLogger();

    @EventListener
    public void initFinished(InitFinishedEvent event) {
        MAtmos.initialize();
    }
}
