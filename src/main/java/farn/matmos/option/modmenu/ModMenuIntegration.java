package farn.matmos.option.modmenu;

import farn.matmos.option.modmenu.gui.MAtmosScreen;
import net.danygames2014.modmenu.api.ConfigScreenFactory;
import net.danygames2014.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {

    public ConfigScreenFactory<MAtmosScreen> getModConfigScreenFactory() {
        return MAtmosScreen::new;
    }
}
