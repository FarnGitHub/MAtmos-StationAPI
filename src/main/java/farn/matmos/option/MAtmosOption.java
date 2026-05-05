package farn.matmos.option;

import net.minecraft.MAtmos;

public enum MAtmosOption {
    SOUND_VOLUME("Sound Volume", 1),
    MUSIC_VOLUME("Music Volume", 1),
    ONLINE_SOUND_DATA_BASE("Online Sound Database", 0),
    DONT_DUMP_DATA("Don't Dump Data", 0),
    MC_VOLUME("Use MC Music Volume", 0),
    ENGINE_LOGGING("Action Logging", 0),
    SHOW_MUSIC_HINT("Show Music Hint", 0);

    public String name;
    public int type;

    MAtmosOption(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public static String getDisplayString(MAtmosOption option) {
        String value = switch (option) {
            case SOUND_VOLUME -> percentage(MAtmos.INSTANCE.getSoundManager().getCustomSoundVolume());
            case MUSIC_VOLUME -> percentage(MAtmos.INSTANCE.getSoundManager().getCustomMusicVolume());
            case ONLINE_SOUND_DATA_BASE -> toggleDisplay(MAtmos.INSTANCE.useOnlineDatabase);
            case DONT_DUMP_DATA -> toggleDisplay(MAtmos.INSTANCE.doNotDump);
            case MC_VOLUME -> toggleDisplay(MAtmos.INSTANCE.getSoundManager().getMusicVolumeIsBasedOffMinecraft());
            case ENGINE_LOGGING -> toggleDisplay(MAtmos.INSTANCE.showMAtmosLogger);
            case SHOW_MUSIC_HINT -> toggleDisplay(MAtmos.INSTANCE.shouldShowMusicHint);
            default -> "";
        };
        return option.name + value;
    }

    public static void setValue(MAtmosOption option, float value) {
        switch (option) {
            case SOUND_VOLUME -> MAtmos.INSTANCE.getSoundManager().setCustomSoundVolume(value);
            case MUSIC_VOLUME -> MAtmos.INSTANCE.getSoundManager().setCustomMusicVolume(value);
        }
    }

    public static float getValue(MAtmosOption option) {
        switch (option) {
            case SOUND_VOLUME : return MAtmos.INSTANCE.getSoundManager().getCustomSoundVolume();
            case MUSIC_VOLUME : return MAtmos.INSTANCE.getSoundManager().getCustomMusicVolume();
        }
        return 0.0F;
    }

    public static void toggleValue(MAtmosOption option) {
        switch (option) {
            case ONLINE_SOUND_DATA_BASE -> MAtmos.INSTANCE.useOnlineDatabase = !MAtmos.INSTANCE.useOnlineDatabase;
            case DONT_DUMP_DATA -> MAtmos.INSTANCE.doNotDump = !MAtmos.INSTANCE.doNotDump;
            case MC_VOLUME -> MAtmos.INSTANCE.getSoundManager().setMusicVolumeIsBasedOffMinecraft(!MAtmos.INSTANCE.getSoundManager().getMusicVolumeIsBasedOffMinecraft());
            case ENGINE_LOGGING -> MAtmos.INSTANCE.showMAtmosLogger = !MAtmos.INSTANCE.showMAtmosLogger;
            case SHOW_MUSIC_HINT -> MAtmos.INSTANCE.shouldShowMusicHint = !MAtmos.INSTANCE.shouldShowMusicHint;
        }
    }

    private static String percentage(float value) {
        return " : " + (value == 0.0F ? "OFF" : (int)(value * 100.0F) + "%");
    }

    private static String toggleDisplay(boolean value) {
        return " : " + (value ? "ON" : "OFF");
    }
}
