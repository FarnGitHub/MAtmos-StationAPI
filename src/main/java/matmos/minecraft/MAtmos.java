package matmos.minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import matmos.engine.MAtmosException;
import matmos.engine.MAtmosKnowledge;
import matmos.engine.MAtmosUtilityLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.modificationstation.stationapi.api.client.texture.TextureHelper;

@SuppressWarnings("CallToPrintStackTrace")
public class MAtmos {
	public static Minecraft mc = Minecraft.INSTANCE;
	public static MAtmosKnowledge knowledge;
	public static MAtDataGatherer dataGatherer;
	public static MAtSoundManagerBase soundManager;
	public static MAtOnlineDatabaseFetcher onlineDatabaseFetcher;
	public static boolean useOnlineDatabase = true;
	@SuppressWarnings("HttpUrlsUsage")
	private static String onlineDatabase = "http://ha3extra.googlecode.com/svn/trunk/matmos/online_database.xml";
	public static boolean dumpData = false;
	public static boolean showMAtmosLogger = true;
	private static boolean passedFirstTick = false;
	private static long lastWorldTime = 0L;
	private static final LinkedList<MAtCustomSheet> customsheetsList = new LinkedList<>();
	private static final File optionsFile = new File(Minecraft.getRunDirectory(), "matmos_options.txt");
	private static final File expansionFolder = new File(Minecraft.getRunDirectory(), "matmos_expansions/");

    public static void initialize() {
		findSheetClasses();
		initializeKnowledge();
		initSheetClasses();
		loadOptions();
		loadKnowledge(true);
	}

	public static void OnTickInGame() {
		if(mc.options.soundVolume != 0.0F || mc.options.musicVolume != 0.0F) {
			if(mc.world.getProperties().getTime() != lastWorldTime) {
				tickThink();
				lastWorldTime = mc.world.getProperties().getTime();
			}
		}
	}

	private static void initSheetClasses() {
		Iterator<MAtCustomSheet> iterator = customsheetsList.iterator();

		while(true) {
			MAtCustomSheet sheet = null;
			int[] locators = null;
			while(locators == null) {
				if(!iterator.hasNext()) return;
				sheet = iterator.next();
				locators = sheet.getLocators();
			}

            for (int i : locators) {
                soundManager.addLocator(i, sheet);
            }
		}
	}

	private static void findSheetClasses() {
		List<Object> listeners = FabricLoader.getInstance().getEntrypoints("matmos:custom_sheet", Object.class);
		for(Object listener : listeners) {
			if(!(listener instanceof MAtCustomSheet sheet)) continue;
			customsheetsList.add(sheet);
			printMessageSilent("Registered CustomSheet: " + sheet.getName());
		}
	}

	private static void printMessage(String var1) {
		if(mc.player != null) {
			mc.player.sendMessage("(MAtmos) " + var1);
		}

		printMessageSilent(var1);
	}

	public static void printMessageSilent(String var1) {
		System.out.println("(MAtmos) " + var1);
	}

	private static void initializeKnowledge() {
		dataGatherer = new MAtDataGatherer(customsheetsList);
		knowledge = new MAtmosKnowledge();
		soundManager = new MAtSoundManagerDefault();
		dataGatherer.generateData();
		knowledge.setData(dataGatherer.data);
		knowledge.setSoundManager(soundManager);
	}

	public static boolean isKnowledgeTurnedOn() {
		return knowledge.isTurnedOn();
	}

	private static void loadKnowledge(boolean useOnline) {
		if(!useOnlineDatabase || !useOnline) {
			if(useOnlineDatabase) {
				loadOfflineProcedure();
			} else {
				loadDefaultProcedure();
			}
		} else {
			fetchOnlineDatabase();
		}

	}

	private static void fetchOnlineDatabase() {
		try {
			printMessageSilent("Fetching Online from source: " + onlineDatabase);
			onlineDatabaseFetcher = new MAtOnlineDatabaseFetcher();
			onlineDatabaseFetcher.getDatabase(new URL(onlineDatabase));
		} catch (MalformedURLException var2) {
			printMessageSilent("Failure to load Online database because the URL is malformed. Now loading Offline.");
			loadOfflineProcedure();
		}

	}

	@SuppressWarnings("unused")
	public static void beginLoadingKnowledge() {
		knowledge.patchKnowledge();
	}

	public static void takeOnlineDatabase(InputStream var1) {
		printMessageSilent("Got online source.");
		loadOnlineProcedure(var1);
	}

	public static void failOnlineDatabase() {
		printMessageSilent("The Online database failed to be fetched. Now loading Offline.");
		loadOfflineProcedure();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadOnlineProcedure(InputStream var1) {
		String var3 = Minecraft.getRunDirectory().toString();
		knowledge.patchKnowledge();

		try {
			MAtmosKnowledge var4 = new MAtmosKnowledge();
			var4.retreiveKeyring(knowledge);
			loadKnowledgeStream(var4, var1);
			knowledge.retreiveKeyring(var4);
			File var5 = new File(var3, "online_knowledge.xmlo");
			if(!var5.exists()) {
				var5.createNewFile();
			}

			FileWriter var6 = new FileWriter(var5);
			var6.write(knowledge.createXML());
			var6.close();
			tryAddOverrideKnowledge();
			finishLoadingKnowledge();
			printMessageSilent("Online loaded.");
		} catch (MAtmosException var7) {
			var7.printStackTrace();
			printMessageSilent("The Online database is corrupt. Now loading Offline.");
			loadOfflineProcedure();
		} catch (IOException var8) {
			printMessage("Error while saving the Offline file.");
			knowledge.reclaimKeyring();
		} catch (XMLStreamException var9) {
			printMessage("FATAL ERROR while creating the XML of a database.");
			var9.printStackTrace();
		}

	}

	public static void loadOfflineProcedure() {
		String mcDir = Minecraft.getRunDirectory().toString();
		File onlineDatabase = new File(mcDir, "offline_knowledge.xmlo");
		knowledge.patchKnowledge();

		try {
			MAtmosKnowledge var3 = new MAtmosKnowledge();
			var3.retreiveKeyring(knowledge);
			loadKnowledgeStream(var3, new FileInputStream(onlineDatabase));
			knowledge.retreiveKeyring(var3);
			tryAddOverrideKnowledge();
			finishLoadingKnowledge();
			printMessageSilent("Offline loaded.");
		} catch (FileNotFoundException var4) {
			printMessageSilent("The Offline database doesn't exist. Now loading default.");
			loadDefaultProcedure();
		} catch (MAtmosException var5) {
			printMessageSilent("The Offline database is corrupt. Now loading Default.");
			loadDefaultProcedure();
		}

	}

	private static void loadDefaultProcedure() {
		String databasePath = "/assets/matmos/database/";
		knowledge.patchKnowledge();

		try {
			MAtmosKnowledge var3 = new MAtmosKnowledge();
			var3.retreiveKeyring(knowledge);
			loadKnowledgeStream(var3, TextureHelper.getTextureStream(databasePath + "default_database.xml"));
			knowledge.retreiveKeyring(var3);
			tryAddOverrideKnowledge();
			finishLoadingKnowledge();
			printMessageSilent("Default loaded.");
		} catch (MAtmosException var5) {
			printMessage("The Default database is corrupt.");
		}

	}

	private static void loadKnowledgeStream(MAtmosKnowledge var1, InputStream var2) throws MAtmosException {
		MAtmosUtilityLoader.loadKnowledgeStream(var1, var2, true);
	}

	private static void tryAddExpansionKnowledges() {
		loadKnowledgeRecursive(expansionFolder, "");
	}

	private static void loadKnowledgeRecursive(File var1, String var2) {
		if(var1.exists()) {
			File[] fileList = var1.listFiles();

			if(fileList == null) return;

            for (File file : fileList) {
                if (file.isDirectory()) {
                    loadKnowledgeRecursive(file, var2 + file.getName() + "/");
                } else {
                    loadOptionalKnowledgeFromFile(file);
                }
            }
		}

	}

	private static void tryAddOverrideKnowledge() {
		String databasePath = "/assets/matmos/database/";
		tryAddExpansionKnowledges();

		try {
			MAtmosKnowledge var3 = new MAtmosKnowledge();
			var3.retreiveKeyring(knowledge);
			loadKnowledgeStream(var3, TextureHelper.getTextureStream(databasePath + "override_database.xml"));
			knowledge.retreiveKeyring(var3);
			printMessageSilent("Override loaded.");
		} catch (MAtmosException var5) {
			printMessage("The Override database is corrupt.");
			knowledge.reclaimKeyring();
		}

	}

	private static void loadOptionalKnowledgeFromFile(File var1) {
		try {
			MAtmosKnowledge var2 = new MAtmosKnowledge();
			var2.retreiveKeyring(knowledge);
			loadKnowledgeStream(var2, new FileInputStream(var1));
			knowledge.retreiveKeyring(var2);
			printMessageSilent("Expansion Database " + var1.getName() + " loaded.");
		} catch (FileNotFoundException var3) {
			printMessageSilent("Expansion Database " + var1.getName() + " missing?");
			knowledge.reclaimKeyring();
		} catch (MAtmosException var4) {
			printMessageSilent("Expansion Database " + var1.getName() + " is corrupt.");
			knowledge.reclaimKeyring();
		}

	}

	private static void finishLoadingKnowledge() {

        for (MAtCustomSheet mAtCustomSheet : customsheetsList) {
            mAtCustomSheet.load();
        }

		dataGatherer.resetSpanTick();
		knowledge.cacheSounds();
		knowledge.turnOn();
	}

	private static void errorMessageIfNotLoaded() {
		if(!knowledge.isTurnedOn()) {
			printMessage("An error has occured while loading the Knowledge files.");
		}

	}

	public static void tickThink() {
		if(!passedFirstTick) {
			errorMessageIfNotLoaded();
			soundManager.initialize();
			passedFirstTick = true;
		}

		if(knowledge.isTurnedOn()) {
			soundManager.routine();
			dataGatherer.routine();
			knowledge.routine();
		}

	}

	public static void loadOptions() {
		try {
			if(!optionsFile.exists()) {
				saveOptions();
			}

			BufferedReader reader = new BufferedReader(new FileReader(optionsFile));
			String line;

			while((line = reader.readLine()) != null) {
				try {
					String[] lines = line.split("::");
					if(lines[0].equals("sound_volume")) {
						soundManager.setCustomSoundVolume(optionToFloat(lines[1]));
					}

					if(lines[0].equals("use_online_database")) {
						useOnlineDatabase = optionToBool(lines[1]);
					}

					if(lines[0].equals("online_database_url")) {
						onlineDatabase = lines[1];
					}

					if(lines[0].equals("dump_data")) {
						dumpData = optionToBool(lines[1]);
					}

					if(lines[0].equals("music_volume_uses_minecraft_music_volume")) {
						soundManager.setMusicVolumeIsBasedOffMinecraft(optionToBool(lines[1]));
					}

					if(lines[0].equals("music_volume")) {
						soundManager.setCustomMusicVolume(optionToFloat(lines[1]));
					}

					if(lines[0].equals("instant_span_time")) {
						dataGatherer.setInstantSpan((int)Math.floor(optionToFloat(lines[1])));
					}

					if(lines[0].equals("show_engine_logger")) {
						showMAtmosLogger = optionToBool(lines[1]);
					}
				} catch (Exception var4) {
					System.out.println("Skipping bad option: " + line);
				}
			}
			reader.close();
		} catch (Exception var5) {
			var5.printStackTrace();
		}

	}

	private static float optionToFloat(String var1) {
		return Float.parseFloat(var1);
	}

	private static boolean optionToBool(String var1) {
		return var1.equals("true");
	}

	public static void saveOptions() {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(optionsFile));
			writer.println("sound_volume::" + soundManager.getCustomSoundVolume());
			writer.println("music_volume::" + soundManager.getCustomMusicVolume());
			writer.println("music_volume_uses_minecraft_music_volume::" + soundManager.getMusicVolumeIsBasedOffMinecraft());
			writer.println("use_online_database::" + useOnlineDatabase);
			writer.println("online_database_url::" + onlineDatabase);
			writer.println("dump_data::" + dumpData);
			writer.println("instant_span_time::" + dataGatherer.getInstantSpan());
			writer.println("show_engine_logger::" + showMAtmosLogger);
			writer.close();
		} catch (Exception var2) {
			var2.printStackTrace();
		}

	}

	public static void userToggle() {
		if(knowledge.isTurnedOn()) {
			knowledge.turnOff();
			knowledge.patchKnowledge();

			for (MAtCustomSheet mAtCustomSheet : customsheetsList) {
				mAtCustomSheet.unload();
			}
			createDump();
		} else {
			knowledge.turnOn();
			loadKnowledge(false);
		}

	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createDump() {
		if(dumpData) {
			try {
				File var2 = new File(Minecraft.getRunDirectory(), "data_dump.xml");
				if(!var2.exists()) {
					var2.createNewFile();
				}

				if(var2.canWrite()) {
					String var3 = dataGatherer.data.createXML();
					FileWriter var4 = new FileWriter(var2);
					var4.write(var3);
					var4.close();
				}
			} catch (XMLStreamException | IOException ignored) {
			}
		}

	}
}
