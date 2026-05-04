package net.minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import matmos.engine.MAtmosData;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

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

	public MAtDataGatherer(LinkedList var1) {
		this.emptyScan();
		this.random = new Random(System.currentTimeMillis());
		this.data = new MAtmosData();
		this.biomeHash = new HashMap();
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
		World var1 = this.mc.world;
		WorldProperties var2 = var1.getProperties();
		ClientPlayerEntity var3 = this.mc.player;
		int var4 = (int)Math.floor(var3.x);
		int var5 = (int)Math.floor(var3.y);
		int var6 = (int)Math.floor(var3.z);
		int var7 = (int)Math.round(var3.velocityX * 1000.0D);
		int var8 = (int)Math.round(var3.velocityY * 1000.0D);
		int var9 = (int)Math.round(var3.velocityZ * 1000.0D);
		this.setInstant(0, var1.getBrightness(LightType.SKY, var4, var5, var6));
		this.setInstant(1, var1.getBrightness(LightType.BLOCK, var4, var5, var6));
		this.setInstant(2, var1.getBrightness(var4, var5, var6));
		this.setInstant(3, (int)(var2.getTime() % 24000L));
		this.setInstant(4, var5);
		this.setInstant(6, var3.isSubmergedInWater() ? 1 : 0);
		this.setInstant(7, var2.getRaining() ? 1 : 0);
		this.setInstant(8, var2.getThundering() ? 1 : 0);
		this.setInstant(9, var1.hasSkyLight(var4, var5, var6) ? 1 : 0);
		this.setInstant(10, var1.dimension.isNether ? 1 : 0);
		this.setInstant(11, var1.ambientDarkness);
		this.setInstant(19, var3.isWet() ? 1 : 0);
		this.setInstant(20, var4);
		this.setInstant(21, var6);
		this.setInstant(22, var3.onGround ? 1 : 0);
		this.setInstant(23, var3.air);
		this.setInstant(24, var3.health);
		this.setInstant(25, var3.dimensionId);
		this.setInstant(26, var1.hasSkyLight(var4, var5, var6) && var1.getTopSolidBlockY(var4, var6) <= var5 ? 1 : 0);
		this.setInstant(27, var1.getTopSolidBlockY(var4, var6));
		this.setInstant(28, var1.getTopSolidBlockY(var4, var6) - var5);
		this.setInstant(32, var3.inventory.getSelectedItem() != null ? var3.inventory.getSelectedItem().itemId : -1);
		this.setInstant(33, var7);
		this.setInstant(34, var8);
		this.setInstant(35, var9);
		this.setInstant(36, var5 >= 0 && var5 < 255 ? this.getTranslatedBlockId(this.mc.world.getBlockId(var4, var5 - 1, var6)) : -1);
		this.setInstant(37, var5 >= 1 && var5 < 256 ? this.getTranslatedBlockId(this.mc.world.getBlockId(var4, var5 - 2, var6)) : -1);
		this.setInstant(38, this.spanTick);
		this.setInstant(39, var3.fireTicks);
		this.setInstant(40, var3.handSwingTicks);
		this.setInstant(41, var3.handSwinging ? 1 : 0);
		this.setInstant(42, var3.jumping ? 1 : 0);
		this.setInstant(43, (int)(var3.fallDistance * 1000.0F));
		this.setInstant(44, var3.slowed ? 1 : 0);
		this.setInstant(45, (int)Math.floor(Math.sqrt(var7 * var7 + var9 * var9)));
		this.setInstant(46, var3.inventory.selectedSlot);
		this.setInstant(47, this.mc.crosshairTarget != null ? 1 : 0);
		this.setInstant(48, this.mc.crosshairTarget != null ? this.mc.crosshairTarget.type.ordinal() : -1);
		this.setInstant(49, var3.isOnFire()? 1 : 0);
		this.setInstant(50, var3.getTotalArmorDurability());
		this.setInstant(53, 0);
		this.setInstant(57, var3.isOnLadder() ? 1 : 0);
		this.setInstant(59, 0);
	}

	private void performInstantRelaxed() {
		World var1 = this.mc.world;
		WorldProperties var2 = var1.getProperties();
		ClientPlayerEntity var3 = this.mc.player;
		int var4 = (int)Math.floor(var3.x);
		int var5 = (int)Math.floor(var3.z);
		Integer var6 = this.biomeHash.get(var1.method_1781().getBiome(var4, var5).name);
		if(var6 == null) {
			var6 = -1;
		}

		this.setInstant(5, var2.getDimensionId());
		this.setInstant(12, var1.isRemote ? 1 : 0);
		this.setInstant(13, 1 + this.random.nextInt(100));
		this.setInstant(14, 1 + this.random.nextInt(100));
		this.setInstant(15, 1 + this.random.nextInt(100));
		this.setInstant(16, 1 + this.random.nextInt(100));
		this.setInstant(17, 1 + this.random.nextInt(100));
		this.setInstant(18, 1 + this.random.nextInt(100));
		this.setInstant(29, var6.intValue());
		this.setInstant(30, (int)(var1.getSeed() >> 32));
		this.setInstant(31, (int)(var1.getSeed() & -1L));
	}

	private void normalizeAndStore(int var1, String var2, String var3, int[] var4, int[] var5) {
		ArrayList var6 = this.data.sheets.get(var2);
		ArrayList var7 = this.data.sheets.get(var3);

		for(int var8 = 0; var8 < 256; ++var8) {
			var6.set(var8, var4[var8]);
			var5[var8] = var4[var8] * 1000 / var1;
			if(var5[var8] == 0 && var4[var8] != 0) {
				var5[var8] = 1;
			}

			var7.set(var8, var5[var8]);
		}

	}

	private void normalizeAndStore(int var1, String var2, int[] var3) {
		ArrayList var4 = this.data.sheets.get(var2);

		for(int var5 = 0; var5 < 256; ++var5) {
			var4.set(var5, var3[var5]);
		}

	}

	private void performSmall() {
		int var1 = (int)Math.floor(this.mc.player.x);
		int var2 = (int)Math.floor(this.mc.player.y);
		int var3 = (int)Math.floor(this.mc.player.z);
		int var4 = var1 - 7;
		int var5 = var2 - 3;
		int var6 = var3 - 7;
		if(var5 > 238) {
			var5 = 238;
		} else if(var5 < 0) {
			var5 = 0;
		}

		this.emptySmall();

        for (MAtCustomSheet atCustomSheet : this.customsheetsList_UsingSmall) {
            atCustomSheet.doStartSmall();
        }

		for(int var8 = 0; var8 < 2048; ++var8) {
			int var9 = this.getTranslatedBlockId(this.mc.world.getBlockId(var4 + var8 % 16, var5 + var8 / 16 % 8, var6 + var8 / 256));
			++this.smallSum[var9];

            for (MAtCustomSheet mAtCustomSheet : this.customsheetsList_UsingSmall) {
                mAtCustomSheet.doBlockSmall(var4, var5, var6);
            }
		}

        for (MAtCustomSheet mAtCustomSheet : this.customsheetsList_UsingSmall) {
            mAtCustomSheet.doEndSmall();
        }

		this.normalizeAndStore(2048, "SmallScan", "SmallScanPerMil", this.smallSum, this.smallPermile);
	}

	private void performContact() {
		int var1 = (int)Math.floor(this.mc.player.x);
		int var2 = (int)Math.floor(this.mc.player.y) - 1;
		int var3 = (int)Math.floor(this.mc.player.z);
		this.emptyContact();

		for(int var4 = 0; var4 < 12; ++var4) {
			int var5 = var2 + (var4 > 7 ? var4 - 9 : var4 % 2);
			if(var5 >= 0 && var5 < 255) {
				int var6 = var1 + (var4 < 4 ? (var4 < 2 ? -1 : 1) : 0);
				int var7 = var3 + (var4 > 3 && var4 < 8 ? (var4 < 6 ? -1 : 1) : 0);
				int var8 = this.getTranslatedBlockId(this.mc.world.getBlockId(var6, var5, var7));
				++this.contactSum[var8];
			}
		}

		this.normalizeAndStore(2048, "ContactScan", this.contactSum);
	}

	private void initiateScan() {
		int var1 = (int)Math.floor(this.mc.player.x);
		int var2 = (int)Math.floor(this.mc.player.y);
		int var3 = (int)Math.floor(this.mc.player.z);
		int var4 = var1 - this.lastScanX;
		int var5 = var2 - this.lastScanY;
		int var6 = var3 - this.lastScanZ;
		if(var4 < 0) {
			var4 = -var4;
		}

		if(var5 < 0) {
			var5 = -var5;
		}

		if(var6 < 0) {
			var6 = -var6;
		}

		if(var4 >= 16 || var5 >= 8 || var6 >= 16) {

            for (MAtCustomSheet mAtCustomSheet : this.customsheetsList_UsingLarge) {
                mAtCustomSheet.doStartLarge();
            }

			this.closestSignXdiff = 16384;
			this.closestSignYdiff = 16384;
			this.closestSignZdiff = 16384;
			this.closestSignMagic = 0;
			this.closestSignAOE = -1;
			this.lastScanX = var1;
			this.lastScanY = var2;
			this.lastScanZ = var3;
			this.scanX = var1 - 31;
			this.scanY = var2 - 15;
			this.scanZ = var3 - 31;
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
			for(int var1 = 0; var1 < 2048; ++var1) {
				int var2 = this.scanX + var1 % 64;
				int var3 = this.scanY + var1 / 64;
				int var4 = this.scanZ + this.scanSpan;
				int var5 = this.getTranslatedBlockId(this.mc.world.getBlockId(var2, var3, var4));
				++this.scanSum[var5];
				Iterator var6 = this.customsheetsList_UsingLarge.iterator();

				while(var6.hasNext()) {
					((MAtCustomSheet)var6.next()).doBlockLarge(var2, var3, var4);
				}

				if(var5 == 68 || var5 == 63) {
					SignBlockEntity var7 = (SignBlockEntity)this.mc.world.getBlockEntity(var2, var3, var4);
					if(var7.texts[0].contains("matmos")) {
						try {
							int var8 = Math.abs(var1 % 64 - 31);
							int var9 = Math.abs(var1 / 64 - 15);
							int var10 = Math.abs(this.scanSpan - 31);
							if(var8 < this.closestSignXdiff && var9 < this.closestSignYdiff && var10 < this.closestSignZdiff) {
								int var11 = Integer.parseInt(var7.texts[1]);
								int var12 = Integer.parseInt(var7.texts[2]);
								int var13;
								if(!var7.texts[3].equals("")) {
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
						} catch (Exception var14) {
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
		for(int var1 = 0; var1 < 256; ++var1) {
			this.scanSum[var1] = 0;
		}

	}

	private void emptySmall() {
		for(int var1 = 0; var1 < 256; ++var1) {
			this.smallSum[var1] = 0;
		}

	}

	private void emptyContact() {
		for(int var1 = 0; var1 < 256; ++var1) {
			this.contactSum[var1] = 0;
		}

	}

	public void generateData() {
		this.data = new MAtmosData();
		this.data.sheets.put("LargeScan", new ArrayList());
		this.data.sheets.put("LargeScanPerMil", new ArrayList());
		this.data.sheets.put("SmallScan", new ArrayList());
		this.data.sheets.put("SmallScanPerMil", new ArrayList());
		this.data.sheets.put("Instants", new ArrayList());
		this.data.sheets.put("SpecialLarge", new ArrayList());
		this.data.sheets.put("SpecialSmall", new ArrayList());
		this.data.sheets.put("ContactScan", new ArrayList());
		this.data.sheets.put("Deltas", new ArrayList());

		int var1;
		for(var1 = 0; var1 < 256; ++var1) {
			this.data.sheets.get("LargeScan").add(0);
			this.data.sheets.get("LargeScanPerMil").add(0);
			this.data.sheets.get("SmallScan").add(0);
			this.data.sheets.get("SmallScanPerMil").add(0);
			this.data.sheets.get("ContactScan").add(0);
		}

		for(var1 = 0; var1 < 62; ++var1) {
			this.data.sheets.get("Instants").add(0);
			this.data.sheets.get("Deltas").add(0);
		}

		for(var1 = 0; var1 < 2; ++var1) {
			this.data.sheets.get("SpecialLarge").add(0);
		}

		for(var1 = 0; var1 < 1; ++var1) {
			this.data.sheets.get("SpecialSmall").add(0);
		}

		this.instants = this.data.sheets.get("Instants");
		this.deltas = this.data.sheets.get("Deltas");
		this.customsheetsList_UsingSmall = new LinkedList();
		this.customsheetsList_UsingLarge = new LinkedList();

        for (MAtCustomSheet var3 : this.customsheetsList) {
            this.data.sheets.put(var3.getName(), var3.getData());
            if (var3.isUsingDelta()) {
                this.data.sheets.put(var3.getName() + "Delta", var3.getDelta());
            }

            if (var3.isUsingSmall()) {
                this.customsheetsList_UsingSmall.add(var3);
            }

            if (var3.isUsingLarge()) {
                this.customsheetsList_UsingLarge.add(var3);
            }
        }

	}
}
