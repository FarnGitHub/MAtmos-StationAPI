package matmos.minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import matmos.engine.MAtmosData;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

@SuppressWarnings({"unused", "FieldMayBeFinal", "SameParameterValue"})
public class MAtDataGatherer {
	final String INSTANTS = "Instants";
	final String DELTAS = "Deltas";
	final String LARGESCAN = "LargeScan";
	final String SMALLSCAN = "SmallScan";
	final String LARGESCAN_THOUSAND = "LargeScanPerMil";
	final String SMALLSCAN_THOUSAND = "SmallScanPerMil";
	final String SPECIAL_LARGE = "SpecialLarge";
	final String SPECIAL_SMALL = "SpecialSmall";
	final String CONTACTSCAN = "ContactScan";
	MAtmosData data;
	Minecraft mc = Minecraft.INSTANCE;
	private int[] scanSum = new int[256];
	private int[] scanPermile = new int[256];
	private int scanX;
	private int scanY;
	private int scanZ;
	private int scanSpan = 64;
	private int[] smallSum = new int[256];
	private int[] smallPermile = new int[256];
	private int[] contactSum = new int[256];
	private int timeAnalysisRoutine = 0;
	private int lastScanX = -16384;
	private int lastScanY = -16384;
	private int lastScanZ = -16384;
	private int closestSignXdiff = -16384;
	private int closestSignYdiff = -16384;
	private int closestSignZdiff = -16384;
	private int closestSignMagic = 0;
	private int closestSignAOE = -1;
	private int spanTick = 0;
	Random random;
	HashMap<String, Integer> biomeHash;
	int quickInstantSpan = 1;
	ArrayList<Integer> instants;
	ArrayList<Integer> deltas;
	LinkedList<MAtCustomSheet> customsheetsList;
	LinkedList<MAtCustomSheet> customsheetsList_UsingLarge;
	LinkedList<MAtCustomSheet> customsheetsList_UsingSmall;

	public MAtDataGatherer(LinkedList<MAtCustomSheet> var1) {
		this.emptyScan();
		this.random = new Random(System.currentTimeMillis());
		this.data = new MAtmosData();
		this.biomeHash = new HashMap<>();
		this.biomeHash.put("Swampland", 2);
		this.biomeHash.put("Forest", 4);
		this.biomeHash.put("Taiga", 7);
		this.biomeHash.put("Desert", 8);
		this.biomeHash.put("Plains", 9);
		this.biomeHash.put("Hell", 12);
		this.biomeHash.put("Sky", 13);
		this.biomeHash.put("Ocean", 14);
		this.biomeHash.put("Extreme Hills", 15);
		this.biomeHash.put("River", 16);
		this.customsheetsList = var1;
	}

	public void routine() {
		if(this.timeAnalysisRoutine > 255) {
			this.timeAnalysisRoutine = 0;
		}

		if(this.timeAnalysisRoutine == 0) {
			this.initiateScan();
		}

		this.tryScan();
		if(this.timeAnalysisRoutine % 32 == 0) {
			this.performInstantRelaxed();
			this.performSmall();

            for (MAtCustomSheet mAtCustomSheet : this.customsheetsList) {
                mAtCustomSheet.doRelaxed();
            }

			this.data.flagUpdate();
		}

		if(this.timeAnalysisRoutine % this.quickInstantSpan == 0) {
			this.performInstant();
			this.performContact();

            for (MAtCustomSheet mAtCustomSheet : this.customsheetsList) {
                mAtCustomSheet.doFrequent();
            }

			this.data.flagUpdate();
			++this.spanTick;
		}

		++this.timeAnalysisRoutine;
	}

	public void resetSpanTick() {
		this.spanTick = 0;
	}

	public void setInstantSpan(int var1) {
		if(var1 > 0) {
			this.quickInstantSpan = var1;
		}

	}

	public int getInstantSpan() {
		return this.quickInstantSpan;
	}

	private void setInstant(int var1, int var2) {
		int var3 = this.instants.get(var1);
		if(this.spanTick != 0) {
			this.deltas.set(var1, var2 - var3);
		}

		this.instants.set(var1, var2);
	}

	private void performInstant() {
		World world = this.mc.world;
		WorldProperties prop = world.getProperties();
		ClientPlayerEntity player = this.mc.player;
		int x = (int)Math.floor(player.x);
		int y = (int)Math.floor(player.y);
		int z = (int)Math.floor(player.z);
		int veloX = (int)Math.round(player.velocityX * 1000.0D);
		int veloY = (int)Math.round(player.velocityY * 1000.0D);
		int veloZ = (int)Math.round(player.velocityZ * 1000.0D);
		this.setInstant(0, world.getBrightness(LightType.SKY, x, y, z));
		this.setInstant(1, world.getBrightness(LightType.BLOCK, x, y, z));
		this.setInstant(2, world.getBrightness(x, y, z));
		this.setInstant(3, (int)(prop.getTime() % 24000L));
		this.setInstant(4, y);
		this.setInstant(6, player.isSubmergedInWater() ? 1 : 0);
		this.setInstant(7, prop.getRaining() ? 1 : 0);
		this.setInstant(8, prop.getThundering() ? 1 : 0);
		this.setInstant(9, world.hasSkyLight(x, y, z) ? 1 : 0);
		this.setInstant(10, world.dimension.isNether ? 1 : 0);
		this.setInstant(11, world.ambientDarkness);
		this.setInstant(19, player.isWet() ? 1 : 0);
		this.setInstant(20, x);
		this.setInstant(21, z);
		this.setInstant(22, player.onGround ? 1 : 0);
		this.setInstant(23, player.air);
		this.setInstant(24, player.health);
		this.setInstant(25, player.dimensionId);
		this.setInstant(26, world.hasSkyLight(x, y, z) && world.getTopSolidBlockY(x, z) <= y ? 1 : 0);
		this.setInstant(27, world.getTopSolidBlockY(x, z));
		this.setInstant(28, world.getTopSolidBlockY(x, z) - y);
		this.setInstant(32, player.inventory.getSelectedItem() != null ? player.inventory.getSelectedItem().itemId : -1);
		this.setInstant(33, veloX);
		this.setInstant(34, veloY);
		this.setInstant(35, veloZ);
		this.setInstant(36, y >= 0 && y < 255 ? this.getTranslatedBlockId(this.mc.world.getBlockId(x, y - 1, z)) : -1);
		this.setInstant(37, y >= 1 && y < 256 ? this.getTranslatedBlockId(this.mc.world.getBlockId(x, y - 2, z)) : -1);
		this.setInstant(38, this.spanTick);
		this.setInstant(39, player.fireTicks);
		this.setInstant(40, player.handSwingTicks);
		this.setInstant(41, player.handSwinging ? 1 : 0);
		this.setInstant(42, player.jumping ? 1 : 0);
		this.setInstant(43, (int)(player.fallDistance * 1000.0F));
		this.setInstant(44, player.slowed ? 1 : 0);
		this.setInstant(45, (int)Math.floor(Math.sqrt(veloX * veloX + veloZ * veloZ)));
		this.setInstant(46, player.inventory.selectedSlot);
		this.setInstant(47, this.mc.crosshairTarget != null ? 1 : 0);
		this.setInstant(48, this.mc.crosshairTarget != null ? this.mc.crosshairTarget.type.ordinal() : -1);
		this.setInstant(49, player.isOnFire()? 1 : 0);
		this.setInstant(50, player.getTotalArmorDurability());
		this.setInstant(53, 0);
		this.setInstant(57, player.isOnLadder() ? 1 : 0);
		this.setInstant(59, 0);
	}

	private void performInstantRelaxed() {
		World world = this.mc.world;
		WorldProperties prop = world.getProperties();
		ClientPlayerEntity var3 = this.mc.player;
		int x = (int)Math.floor(var3.x);
		int z = (int)Math.floor(var3.z);
		Integer biome = this.biomeHash.get(world.method_1781().getBiome(x, z).name);
		if(biome == null) {
			biome = -1;
		}

		this.setInstant(5, prop.getDimensionId());
		this.setInstant(12, world.isRemote ? 1 : 0);
		this.setInstant(13, 1 + this.random.nextInt(100));
		this.setInstant(14, 1 + this.random.nextInt(100));
		this.setInstant(15, 1 + this.random.nextInt(100));
		this.setInstant(16, 1 + this.random.nextInt(100));
		this.setInstant(17, 1 + this.random.nextInt(100));
		this.setInstant(18, 1 + this.random.nextInt(100));
		this.setInstant(29, biome);
		this.setInstant(30, (int)(world.getSeed() >> 32));
		this.setInstant(31, (int)(world.getSeed()));
	}

	private void normalizeAndStore(int size, String scan, String sum, int[] scansData, int[] sumsData) {
		ArrayList<Integer> scans = this.data.sheets.get(scan);
		ArrayList<Integer> sums = this.data.sheets.get(sum);

		for(int index = 0; index < 256; ++index) {
			scans.set(index, scansData[index]);
			sumsData[index] = scansData[index] * 1000 / size;
			if(sumsData[index] == 0 && scansData[index] != 0) {
				sumsData[index] = 1;
			}

			sums.set(index, sumsData[index]);
		}

	}

	private void normalizeAndStore(int size, String biome, int[] table) {
		ArrayList<Integer> lits = this.data.sheets.get(biome);

		for(int index = 0; index < 256; ++index) {
			lits.set(index, table[index]);
		}

	}

	private void performSmall() {
		int x = (int)Math.floor(this.mc.player.x);
		int y = (int)Math.floor(this.mc.player.y);
		int z = (int)Math.floor(this.mc.player.z);
		int xDis = x - 7;
		int yDis = y - 3;
		int zDis = z - 7;
		if(yDis > 238) {
			yDis = 238;
		} else if(yDis < 0) {
			yDis = 0;
		}

		this.emptySmall();

        for (MAtCustomSheet atCustomSheet : this.customsheetsList_UsingSmall) {
            atCustomSheet.doStartSmall();
        }

		for(int index = 0; index < 2048; ++index) {
			int blockId = this.getTranslatedBlockId(this.mc.world.getBlockId(xDis + index % 16, yDis + index / 16 % 8, zDis + index / 256));
			++this.smallSum[blockId];

            for (MAtCustomSheet mAtCustomSheet : this.customsheetsList_UsingSmall) {
                mAtCustomSheet.doBlockSmall(xDis, yDis, zDis);
            }
		}

        for (MAtCustomSheet mAtCustomSheet : this.customsheetsList_UsingSmall) {
            mAtCustomSheet.doEndSmall();
        }

		this.normalizeAndStore(2048, "SmallScan", "SmallScanPerMil", this.smallSum, this.smallPermile);
	}

	private void performContact() {
		int x = (int)Math.floor(this.mc.player.x);
		int y = (int)Math.floor(this.mc.player.y) - 1;
		int z = (int)Math.floor(this.mc.player.z);
		this.emptyContact();

		for(int radius = 0; radius < 12; ++radius) {
			int yBlock = y + (radius > 7 ? radius - 9 : radius % 2);
			if(yBlock >= 0 && yBlock < 255) {
				int xBlock = x + (radius < 4 ? (radius < 2 ? -1 : 1) : 0);
				int zBlock = z + (radius > 3 && radius < 8 ? (radius < 6 ? -1 : 1) : 0);
				int var8 = this.getTranslatedBlockId(this.mc.world.getBlockId(xBlock, yBlock, zBlock));
				++this.contactSum[var8];
			}
		}

		this.normalizeAndStore(2048, "ContactScan", this.contactSum);
	}

	private void initiateScan() {
		int x = (int)Math.floor(this.mc.player.x);
		int y = (int)Math.floor(this.mc.player.y);
		int z = (int)Math.floor(this.mc.player.z);
		int xOffset = x - this.lastScanX;
		int yOffset = y - this.lastScanY;
		int zOffset = z - this.lastScanZ;
		if(xOffset < 0) {
			xOffset = -xOffset;
		}

		if(yOffset < 0) {
			yOffset = -yOffset;
		}

		if(zOffset < 0) {
			zOffset = -zOffset;
		}

		if(xOffset >= 16 || yOffset >= 8 || zOffset >= 16) {

            for (MAtCustomSheet mAtCustomSheet : this.customsheetsList_UsingLarge) {
                mAtCustomSheet.doStartLarge();
            }

			this.closestSignXdiff = 16384;
			this.closestSignYdiff = 16384;
			this.closestSignZdiff = 16384;
			this.closestSignMagic = 0;
			this.closestSignAOE = -1;
			this.lastScanX = x;
			this.lastScanY = y;
			this.lastScanZ = z;
			this.scanX = x - 31;
			this.scanY = y - 15;
			this.scanZ = z - 31;
			if(this.scanY > 190) {
				this.scanY = 190;
			} else if(this.scanY < 0) {
				this.scanY = 0;
			}

			this.scanSpan = 0;
			this.emptyScan();
		}

	}

	private void tryScan() {
		if(this.scanSpan < 64) {
			this.performScan();
			if(this.scanSpan == 64) {

                for (MAtCustomSheet mAtCustomSheet : this.customsheetsList_UsingLarge) {
                    mAtCustomSheet.doEndLarge();
                }

				this.normalizeAndStore(131072, "LargeScan", "LargeScanPerMil", this.scanSum, this.scanPermile);
				this.data.sheets.get("SpecialLarge").set(0, this.closestSignMagic);
				this.data.sheets.get("SpecialLarge").set(1, this.closestSignAOE);
			}
		}

	}

	private void performScan() {
		if(this.scanSpan < 64) {
			for(int index = 0; index < 2048; ++index) {
				int sX = this.scanX + index % 64;
				int sY = this.scanY + index / 64;
				int sZ = this.scanZ + this.scanSpan;
				int blockId = this.getTranslatedBlockId(this.mc.world.getBlockId(sX, sY, sZ));
				++this.scanSum[blockId];

                for (MAtCustomSheet sheet : this.customsheetsList_UsingLarge) {
                    sheet.doBlockLarge(sX, sY, sZ);
                }

				if(blockId == 68 || blockId == 63) {
					SignBlockEntity var7 = (SignBlockEntity)this.mc.world.getBlockEntity(sX, sY, sZ);
					if(var7.texts[0].contains("matmos")) {
						try {
							int var8 = Math.abs(index % 64 - 31);
							int var9 = Math.abs(index / 64 - 15);
							int var10 = Math.abs(this.scanSpan - 31);
							if(var8 < this.closestSignXdiff && var9 < this.closestSignYdiff && var10 < this.closestSignZdiff) {
								int var11 = Integer.parseInt(var7.texts[1]);
								int var12 = Integer.parseInt(var7.texts[2]);
								int var13;
								if(!var7.texts[3].isEmpty()) {
									var13 = Integer.parseInt(var7.texts[3]);
								} else {
									var13 = var12;
								}

								if(var8 <= var12 && var9 <= var13 && var10 <= var12) {
									this.closestSignMagic = var11;
									this.closestSignAOE = var12;
									this.closestSignXdiff = var8;
									this.closestSignYdiff = var9;
									this.closestSignZdiff = var10;
								}
							}
						} catch (Exception ignored) {
						}
					}
				}
			}

			++this.scanSpan;
		}

	}

	private int getTranslatedBlockId(int var1) {
		return var1 < 0 ? 0 : (var1 > 255 ? 0 : var1);
	}

	private void emptyScan() {
		for(int index = 0; index < 256; ++index) {
			this.scanSum[index] = 0;
		}

	}

	private void emptySmall() {
		for(int index = 0; index < 256; ++index) {
			this.smallSum[index] = 0;
		}

	}

	private void emptyContact() {
		for(int index = 0; index < 256; ++index) {
			this.contactSum[index] = 0;
		}

	}

	public void generateData() {
		this.data = new MAtmosData();
		this.data.sheets.put("LargeScan", new ArrayList<>());
		this.data.sheets.put("LargeScanPerMil", new ArrayList<>());
		this.data.sheets.put("SmallScan", new ArrayList<>());
		this.data.sheets.put("SmallScanPerMil", new ArrayList<>());
		this.data.sheets.put("Instants", new ArrayList<>());
		this.data.sheets.put("SpecialLarge", new ArrayList<>());
		this.data.sheets.put("SpecialSmall", new ArrayList<>());
		this.data.sheets.put("ContactScan", new ArrayList<>());
		this.data.sheets.put("Deltas", new ArrayList<>());

		int index;
		for(index = 0; index < 256; ++index) {
			this.data.sheets.get("LargeScan").add(0);
			this.data.sheets.get("LargeScanPerMil").add(0);
			this.data.sheets.get("SmallScan").add(0);
			this.data.sheets.get("SmallScanPerMil").add(0);
			this.data.sheets.get("ContactScan").add(0);
		}

		for(index = 0; index < 62; ++index) {
			this.data.sheets.get("Instants").add(0);
			this.data.sheets.get("Deltas").add(0);
		}

		for(index = 0; index < 2; ++index) {
			this.data.sheets.get("SpecialLarge").add(0);
		}

		for(index = 0; index < 1; ++index) {
			this.data.sheets.get("SpecialSmall").add(0);
		}

		this.instants = this.data.sheets.get("Instants");
		this.deltas = this.data.sheets.get("Deltas");
		this.customsheetsList_UsingSmall = new LinkedList<>();
		this.customsheetsList_UsingLarge = new LinkedList<>();

        for (MAtCustomSheet sheet : this.customsheetsList) {
            this.data.sheets.put(sheet.getName(), sheet.getData());
            if (sheet.isUsingDelta()) {
                this.data.sheets.put(sheet.getName() + "Delta", sheet.getDelta());
            }

            if (sheet.isUsingSmall()) {
                this.customsheetsList_UsingSmall.add(sheet);
            }

            if (sheet.isUsingLarge()) {
                this.customsheetsList_UsingLarge.add(sheet);
            }
        }

	}
}
