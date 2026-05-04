package net.minecraft;

import farn.matmos.MatmosMod;
import farn.matmos.MatmosStationAPI;
import ha3.Ha3KeyManager;
import ha3.Ha3Scroller;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.stream.XMLStreamException;
import matmos.engine.MAtmosException;
import matmos.engine.MAtmosKnowledge;
import matmos.engine.MAtmosLogger;
import matmos.engine.MAtmosUtilityLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.texture.TextureHelper;

public class MAtmos implements MatmosMod {
	final String VERSION = "1.8.1 r6";
	Minecraft mc;
	MAtmosKnowledge knowledge;
	MAtDataGatherer dataGatherer;
	MAtSoundManagerBase soundManager;
	MAtOnlineDatabaseFetcher onlineDatabaseFetcher;
	private Ha3KeyManager keyManager;
	public KeyBinding matmosKeyBinding = new KeyBinding("key.matmos", 65);
	public Ha3Scroller scroller;
	private boolean useOnlineDatabase;
	private String onlineDatabase;
	boolean doNotDump;
	boolean showMAtmosLogger;
	private boolean passedFirstTick;
	private short knowsHowToChangeVolume;
	private boolean shouldShowMusicHint;
	private boolean knowsHowToChangeMusicVolume;
	private long lastWorldTime;
	boolean errorWhileLoadingKnowledge;
	boolean errorWhileLoadingOnline;
	private final LinkedList<MAtCustomSheet> customsheetsList = new LinkedList<>();
	private final File modDir = new File(Minecraft.getRunDirectory(), "/mods/");
	private final File optionsFile;
	private final File expansionFolder;
	public static final MAtmos INSTANCE = new MAtmos();

	private MAtmos() {
		this.optionsFile = new File(Minecraft.getRunDirectory(), "matmos_options.txt");
		this.expansionFolder = new File(Minecraft.getRunDirectory(), "matmos_expansions/");
	}

	public void initialize() {
		this.errorWhileLoadingKnowledge = false;
		this.errorWhileLoadingOnline = false;
		this.mc = Minecraft.INSTANCE;
		this.matmosKeyBinding = new KeyBinding("key.matmos", 65);
		this.keyManager = new Ha3KeyManager(this);
		this.keyManager.addKeyBinding(new MAtKey(this.matmosKeyBinding));
		this.useOnlineDatabase = true;
		this.shouldShowMusicHint = true;
		this.showMAtmosLogger = true;
		this.knowsHowToChangeMusicVolume = false;
		this.doNotDump = false;
		this.onlineDatabase = "http://ha3extra.googlecode.com/svn/trunk/matmos/online_database.xml";
		this.knowsHowToChangeVolume = 0;
		this.lastWorldTime = 0L;
		this.passedFirstTick = false;
		try {
			this.findSheetClasses();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.initializeKnowledge();
		this.initSheetClasses();
		this.loadOptions();
		this.loadKnowledge(true, false);
		this.scroller = new MAtScroller(this);
	}

	public String Version() {
		return "1.8.1 r6";
	}

	public void KeyboardEvent(KeyBinding var1) {
		this.keyManager.handleKeyDown(var1);
	}

	public void OnTickInGame() {
		if(this.mc.options.soundVolume != 0.0F || this.mc.options.musicVolume != 0.0F) {
			this.frameThink();
			if(this.mc.world.getProperties().getTime() != this.lastWorldTime) {
				this.tickThink();
				this.lastWorldTime = this.mc.world.getProperties().getTime();
			}
		}
	}

	private void initSheetClasses() {
		Iterator<MAtCustomSheet> var1 = this.customsheetsList.iterator();

		while(true) {
			MAtCustomSheet var2 = null;
			int[] var3 = null;
			while(var3 == null) {
				if(!var1.hasNext()) return;
				var2 = var1.next();
				var3 = var2.getLocators();
			}

			for(int var4 = 0; var4 < var3.length; ++var4) {
				this.soundManager.addLocator(var3[var4], var2);
			}
		}
	}

	private void findSheetClasses() throws IOException {
		ClassLoader var2 = Minecraft.class.getClassLoader();
		if (!this.modDir.isDirectory()) {
			throw new IllegalArgumentException("folder must be a Directory.");
		} else {
			File[] files = this.modDir.listFiles();

			for(int index = 0; index < files.length; ++index) {
				File file = files[index];
				if (file.isDirectory() || file.isFile() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))) {
					if (!file.isFile()) {
						if (file.isDirectory()) {
							File[] modsFile = file.listFiles();
							if (modsFile != null) {
								for(int index2 = 0; index2 < modsFile.length; ++index2) {
									String var13 = modsFile[index2].getName();
									if (modsFile[index2].isFile() && var13.startsWith("matcs_") && var13.endsWith(".class")) {
										this.addSheetClass(var2, var13);
									}
								}
							}
						}
					} else {
						FileInputStream var8 = new FileInputStream(file);
						ZipInputStream var9 = new ZipInputStream(var8);

						while(true) {
							ZipEntry var10 = var9.getNextEntry();
							if (var10 == null) {
								var9.close();
								var8.close();
								break;
							}

							String var7 = var10.getName();
							if (!var10.isDirectory() && var7.startsWith("matcs_") && var7.endsWith(".class")) {
								this.addSheetClass(var2, var7);
							}
						}
					}
				}
			}

		}
	}

	private void addSheetClass(ClassLoader loader, String path) {
		try {
			path = MatmosStationAPI.apron$fixSheetPackage(path);
			String newPath = path.split("\\.")[0];
			if(newPath.contains("$")) {
				return;
			}

			Package packag = MAtmos.class.getPackage();
			if(packag != null) {
				newPath = packag.getName() + "." + newPath;
			}

			Class<?> var5 = loader.loadClass(newPath);
			if(!MAtCustomSheet.class.isAssignableFrom(var5)) {
				return;
			}

			MAtCustomSheet var6 = (MAtCustomSheet)var5.newInstance();
			this.customsheetsList.add(var6);
			this.printMessageSilent("Registered CustomSheet: " + path);
		} catch (Throwable var7) {
			this.printMessageSilent("Failed to register CustomSheet: " + path);
		}

	}


	private void printMessage(String var1) {
		if(this.mc.player != null) {
			this.mc.player.sendChatMessage("(MAtmos) " + var1);
		}

		this.printMessageSilent(var1);
	}

	public void printMessageSilent(String var1) {
		System.out.println("(MAtmos) " + var1);
	}

	private void initializeKnowledge() {
		this.dataGatherer = new MAtDataGatherer(this.customsheetsList);
		this.knowledge = new MAtmosKnowledge();
		this.soundManager = new MAtSoundManagerDefault();
		this.dataGatherer.generateData();
		this.knowledge.setData(this.dataGatherer.data);
		this.knowledge.setSoundManager(this.soundManager);
	}

	public boolean isKnowledgeTurnedOn() {
		return this.knowledge.isTurnedOn();
	}

	private void loadKnowledge(boolean var1, boolean var2) {
		if(!var2 && (!this.useOnlineDatabase || !var1)) {
			if(this.useOnlineDatabase) {
				this.loadOfflineProcedure();
			} else {
				this.loadDefaultProcedure();
			}
		} else {
			this.fetchOnlineDatabase();
		}

	}

	private void fetchOnlineDatabase() {
		try {
			this.printMessageSilent("Fetching Online from source: " + this.onlineDatabase);
			this.onlineDatabaseFetcher = new MAtOnlineDatabaseFetcher(this);
			this.onlineDatabaseFetcher.getDatabase(new URL(this.onlineDatabase));
		} catch (MalformedURLException var2) {
			this.printMessageSilent("Failure to load Online database because the URL is malformed. Now loading Offline.");
			this.loadOfflineProcedure();
		}

	}

	@SuppressWarnings("unused")
	public void beginLoadingKnowledge() {
		this.knowledge.patchKnowledge();
	}

	public void takeOnlineDatabase(InputStream var1) {
		this.printMessageSilent("Got online source.");
		this.loadOnlineProcedure(var1);
	}

	public void failOnlineDatabase() {
		this.printMessageSilent("The Online database failed to be fetched. Now loading Offline.");
		this.loadOfflineProcedure();
	}

	public void loadOnlineProcedure(InputStream var1) {
		Minecraft var2 = this.mc;
		String var3 = Minecraft.getRunDirectory().toString();
		this.knowledge.patchKnowledge();

		try {
			MAtmosKnowledge var4 = new MAtmosKnowledge();
			var4.retreiveKeyring(this.knowledge);
			this.loadKnowledgeStream(var4, var1);
			this.knowledge.retreiveKeyring(var4);
			File var5 = new File(var3, "online_knowledge.xmlo");
			if(!var5.exists()) {
				var5.createNewFile();
			}

			FileWriter var6 = new FileWriter(var5);
			var6.write(this.knowledge.createXML());
			var6.close();
			this.tryAddOverrideKnowledge();
			this.finishLoadingKnowledge();
			this.printMessageSilent("Online loaded.");
		} catch (MAtmosException var7) {
			var7.printStackTrace();
			this.printMessageSilent("The Online database is corrupt. Now loading Offline.");
			this.loadOfflineProcedure();
		} catch (IOException var8) {
			this.printMessage("Error while saving the Offline file.");
			this.knowledge.reclaimKeyring();
		} catch (XMLStreamException var9) {
			this.printMessage("FATAL ERROR while creating the XML of a database.");
			var9.printStackTrace();
		}

	}

	public void loadOfflineProcedure() {
		String mcDir = Minecraft.getRunDirectory().toString();
		File onlineDatabase = new File(mcDir, "offline_knowledge.xmlo");
		this.knowledge.patchKnowledge();

		try {
			MAtmosKnowledge var3 = new MAtmosKnowledge();
			var3.retreiveKeyring(this.knowledge);
			this.loadKnowledgeStream(var3, new FileInputStream(onlineDatabase));
			this.knowledge.retreiveKeyring(var3);
			this.tryAddOverrideKnowledge();
			this.finishLoadingKnowledge();
			this.printMessageSilent("Offline loaded.");
		} catch (FileNotFoundException var4) {
			this.printMessageSilent("The Offline database doesn\'t exist. Now loading default.");
			this.loadDefaultProcedure();
		} catch (MAtmosException var5) {
			this.printMessageSilent("The Offline database is corrupt. Now loading Default.");
			this.loadDefaultProcedure();
		}

	}

	private void loadDefaultProcedure() {
		String databasePath = "/assets/matmos/database/";
		this.knowledge.patchKnowledge();

		try {
			MAtmosKnowledge var3 = new MAtmosKnowledge();
			var3.retreiveKeyring(this.knowledge);
			this.loadKnowledgeStream(var3, TextureHelper.getTextureStream(databasePath + "default_database.xml"));
			this.knowledge.retreiveKeyring(var3);
			this.tryAddOverrideKnowledge();
			this.finishLoadingKnowledge();
			this.printMessageSilent("Default loaded.");
		} catch (MAtmosException var5) {
			this.printMessage("The Default database is corrupt.");
		}

	}

	private void loadKnowledgeStream(MAtmosKnowledge var1, InputStream var2) throws MAtmosException {
		MAtmosUtilityLoader.loadKnowledgeStream(var1, var2, true);
	}

	private void tryAddExpansionKnowledges() {
		this.loadKnowledgeRecursive(this.expansionFolder, "");
	}

	private void loadKnowledgeRecursive(File var1, String var2) {
		if(var1.exists()) {
			File[] var3 = var1.listFiles();

			for(int var4 = 0; var4 < var3.length; ++var4) {
				if(var3[var4].isDirectory()) {
					this.loadKnowledgeRecursive(var3[var4], var2 + var3[var4].getName() + "/");
				} else {
					this.loadOptionalKnowledgeFromFile(var3[var4]);
				}
			}
		}

	}

	private void tryAddOverrideKnowledge() {
		String databasePath = "/assets/matmos/database/";
		this.tryAddExpansionKnowledges();

		try {
			MAtmosKnowledge var3 = new MAtmosKnowledge();
			var3.retreiveKeyring(this.knowledge);
			this.loadKnowledgeStream(var3, TextureHelper.getTextureStream(databasePath + "override_database.xml"));
			this.knowledge.retreiveKeyring(var3);
			this.printMessageSilent("Override loaded.");
		} catch (MAtmosException var5) {
			this.printMessage("The Override database is corrupt.");
			this.knowledge.reclaimKeyring();
		}

	}

	private void loadOptionalKnowledgeFromFile(File var1) {
		try {
			MAtmosKnowledge var2 = new MAtmosKnowledge();
			var2.retreiveKeyring(this.knowledge);
			this.loadKnowledgeStream(var2, new FileInputStream(var1));
			this.knowledge.retreiveKeyring(var2);
			this.printMessageSilent("Expansion Database " + var1.getName() + " loaded.");
		} catch (FileNotFoundException var3) {
			this.printMessageSilent("Expansion Database " + var1.getName() + " missing?");
			this.knowledge.reclaimKeyring();
		} catch (MAtmosException var4) {
			this.printMessageSilent("Expansion Database " + var1.getName() + " is corrupt.");
			this.knowledge.reclaimKeyring();
		}

	}

	private void finishLoadingKnowledge() {

        for (MAtCustomSheet mAtCustomSheet : this.customsheetsList) {
            mAtCustomSheet.load();
        }

		this.dataGatherer.resetSpanTick();
		this.knowledge.cacheSounds();
		this.knowledge.turnOn();
	}

	private void errorMessageIfNotLoaded() {
		if(!this.knowledge.isTurnedOn()) {
			this.printMessage("An error has occured while loading the Knowledge files.");
		}

	}

	public void tickThink() {
		if(!this.passedFirstTick) {
			this.errorMessageIfNotLoaded();
			this.soundManager.initialize();
			this.passedFirstTick = true;
		}

		this.keyManager.handleRuntime();
		if(this.knowledge.isTurnedOn()) {
			this.soundManager.routine();
			this.dataGatherer.routine();
			this.knowledge.routine();
		}

	}

	public void frameThink() {
		this.scroller.routine();
		this.scroller.draw();
	}

	public void loadOptions() {
		try {
			if(!this.optionsFile.exists()) {
				saveOptions();
			}

			BufferedReader var1 = new BufferedReader(new FileReader(this.optionsFile));
			String var2 = "";

			while(true) {
				var2 = var1.readLine();
				if(var2 == null) {
					var1.close();
					break;
				}

				try {
					String[] var3 = var2.split("::");
					if(var3[0].equals("sound_volume")) {
						this.soundManager.setCustomSoundVolume(this.optionToFloat(var3[1]));
					}

					if(var3[0].equals("use_online_database")) {
						this.useOnlineDatabase = this.optionToBool(var3[1]);
					}

					if(var3[0].equals("online_database_url")) {
						this.onlineDatabase = var3[1];
					}

					if(var3[0].equals("never_dump_data")) {
						this.doNotDump = this.optionToBool(var3[1]);
					}

					if(var3[0].equals("music_volume_uses_minecraft_music_volume")) {
						this.soundManager.setMusicVolumeIsBasedOffMinecraft(this.optionToBool(var3[1]));
					}

					if(var3[0].equals("music_volume")) {
						this.soundManager.setCustomMusicVolume(this.optionToFloat(var3[1]));
					}

					if(var3[0].equals("show_music_hint")) {
						this.shouldShowMusicHint = this.optionToBool(var3[1]);
					}

					if(var3[0].equals("instant_span_time")) {
						this.dataGatherer.setInstantSpan((int)Math.floor((double)this.optionToFloat(var3[1])));
					}

					if(var3[0].equals("show_engine_logger")) {
						this.showMAtmosLogger = this.optionToBool(var3[1]);
						MAtmosLogger.setActive(this.showMAtmosLogger);
					}
				} catch (Exception var4) {
					System.out.println("Skipping bad option: " + var2);
				}
			}
		} catch (Exception var5) {
			var5.printStackTrace();
		}

	}

	private float optionToFloat(String var1) {
		return Float.parseFloat(var1);
	}

	private boolean optionToBool(String var1) {
		return var1.equals("true");
	}

	public void saveOptions() {
		try {
			PrintWriter var1 = new PrintWriter(new FileWriter(this.optionsFile));
			var1.println("sound_volume::" + this.soundManager.getCustomSoundVolume());
			var1.println("music_volume::" + this.soundManager.getCustomMusicVolume());
			var1.println("music_volume_uses_minecraft_music_volume::" + this.soundManager.getMusicVolumeIsBasedOffMinecraft());
			var1.println("use_online_database::" + this.useOnlineDatabase);
			var1.println("online_database_url::" + this.onlineDatabase);
			var1.println("never_dump_data::" + this.doNotDump);
			var1.println("show_music_hint::" + this.shouldShowMusicHint);
			var1.println("instant_span_time::" + this.dataGatherer.getInstantSpan());
			var1.println("show_engine_logger::" + this.showMAtmosLogger);
			var1.close();
		} catch (Exception var2) {
			var2.printStackTrace();
		}

	}

	public void userToggle(boolean var1) {
		if(this.knowledge.isTurnedOn()) {
			if(!this.tutorial_shouldShowVolumeExistenceHint()) {
				this.knowledge.patchKnowledge();

                for (MAtCustomSheet mAtCustomSheet : this.customsheetsList) {
                    mAtCustomSheet.unload();
                }

				this.printMessage("Disabled MAtmos. Press F7 again to re-enable.");
				this.createDump();
			} else {
				this.tutorial_signalKnowsAboutVolumeExistence();
				this.printMessage("Press F7 promptly to turn MAtmos on/off.");
				this.printMessage("Hold F7 down to tweak the volume.");
				this.printMessage("Open your inventory and Hold F7 down to tweak MAtmos music volume.");
			}
		} else if(var1) {
			this.loadOptions();
			this.printMessage("Retrieving online knowledge...");
			this.loadKnowledge(false, true);
		} else {
			this.printMessage("Reloading knowledge files...");
			this.loadKnowledge(false, false);
			this.errorMessageIfNotLoaded();
		}

	}

	public void createDump() {
		if(!this.doNotDump) {
			try {
				Minecraft var1 = this.mc;
				File var2 = new File(Minecraft.getRunDirectory(), "data_dump.xml");
				if(!var2.exists()) {
					var2.createNewFile();
				}

				if(var2.canWrite()) {
					String var3 = this.dataGatherer.data.createXML();
					FileWriter var4 = new FileWriter(var2);
					var4.write(var3);
					var4.close();
				}
			} catch (XMLStreamException | IOException ignored) {
			}
		}

	}

	public boolean tutorial_shouldShowVolumeExistenceHint() {
		return this.knowsHowToChangeVolume == 0;
	}

	public boolean tutorial_shouldShowUseVolumeHint() {
		return this.knowsHowToChangeVolume < 5;
	}

	public void tutorial_signalKnowsAboutVolumeExistence() {
		if(this.knowsHowToChangeVolume == 0) {
			this.knowsHowToChangeVolume = 1;
		}

	}

	public void tutorial_signalKnowsHowToUseVolume() {
		if(this.knowsHowToChangeVolume <= 4) {
			if(this.knowsHowToChangeVolume == 0) {
				this.knowsHowToChangeVolume = 4;
			} else {
				++this.knowsHowToChangeVolume;
			}
		}

	}

	public void tutorial_signalKnowsHowToChangeMusicVolume() {
		this.knowsHowToChangeMusicVolume = true;
	}

	public boolean tutorial_shouldShowMusicHint() {
		return !this.knowsHowToChangeMusicVolume && this.shouldShowMusicHint;
	}

	public MAtSoundManagerBase getSoundManager() {
		return this.soundManager;
	}
}
