package farn.matmos.option;

import matmos.minecraft.MAtmos;

public enum MAtmosOption {
    SOUND_VOLUME("Sound Volume", 1),
    MUSIC_VOLUME("Music Volume", 1),
    ONLINE_SOUND_DATA_BASE("Online Sound Database", 0),
    DUMP_DATA("Dump Data", 0),
    MC_VOLUME("Use MC Music Volume", 0),
    ENGINE_LOGGING("Engine Logging", 0);

    public final String name;
    public final int type;

    MAtmosOption(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public static String getDisplayString(MAtmosOption option) {
        String value = switch (option) {
            case SOUND_VOLUME -> percentage(MAtmos.soundManager.getCustomSoundVolume());
            case MUSIC_VOLUME -> percentage(MAtmos.soundManager.getCustomMusicVolume());
            case ONLINE_SOUND_DATA_BASE -> toggleDisplay(MAtmos.useOnlineDatabase);
            case DUMP_DATA -> toggleDisplay(MAtmos.dumpData);
            case MC_VOLUME -> toggleDisplay(MAtmos.soundManager.getMusicVolumeIsBasedOffMinecraft());
            case ENGINE_LOGGING -> toggleDisplay(MAtmos.showMAtmosLogger);
        };
        return option.name + value;
    }

    public static void setValue(MAtmosOption option, float value) {
        switch (option) {
            case SOUND_VOLUME -> MAtmos.soundManager.setCustomSoundVolume(value);
            case MUSIC_VOLUME -> MAtmos.soundManager.setCustomMusicVolume(value);
        }
    }

    public static float getValue(MAtmosOption option) {
        return switch (option) {
            case SOUND_VOLUME -> MAtmos.soundManager.getCustomSoundVolume();
            case MUSIC_VOLUME -> MAtmos.soundManager.getCustomMusicVolume();
            default -> 0.0F;
        };
    }

    public static void toggleValue(MAtmosOption option) {
        switch (option) {
            case ONLINE_SOUND_DATA_BASE -> MAtmos.useOnlineDatabase = !MAtmos.useOnlineDatabase;
            case DUMP_DATA -> MAtmos.dumpData = !MAtmos.dumpData;
            case MC_VOLUME -> MAtmos.soundManager.setMusicVolumeIsBasedOffMinecraft(!MAtmos.soundManager.getMusicVolumeIsBasedOffMinecraft());
            case ENGINE_LOGGING -> MAtmos.showMAtmosLogger = !MAtmos.showMAtmosLogger;
        }
    }

    private static String percentage(float value) {
        return " : " + (value == 0.0F ? "OFF" : (int)(value * 100.0F) + "%");
    }

    private static String toggleDisplay(boolean value) {
        return " : " + (value ? "ON" : "OFF");
    }
}
