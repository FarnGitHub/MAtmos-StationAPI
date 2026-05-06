package matmos.engine;

import farn.matmos.MatmosStationAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Objects;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MAtmosUtilityLoader {
	MAtmosKnowledge knowledgeWorkstation;
	static final String NAME = "name";
	static final String DESCRIPTIBLE = "descriptible";
	static final String NICKNAME = "nickname";
	static final String DESCRIPTION = "description";
	static final String ICON = "icon";
	static final String META = "meta";
	static final String LIST = "list";
	static final String CONDITION = "condition";
	static final String SHEET = "sheet";
	static final String KEY = "key";
	static final String DYNAMICKEY = "dynamickey";
	static final String SYMBOL = "symbol";
	static final String CONSTANT = "constant";
	static final String SET = "set";
	static final String TRUEPART = "truepart";
	static final String FALSEPART = "falsepart";
	static final String EVENT = "event";
	static final String VOLMIN = "volmin";
	static final String VOLMAX = "volmax";
	static final String PITCHMIN = "pitchmin";
	static final String PITCHMAX = "pitchmax";
	static final String METASOUND = "metasound";
	static final String PATH = "path";
	static final String MACHINE = "machine";
	static final String ALLOW = "allow";
	static final String RESTRICT = "restrict";
	static final String DYNAMIC = "dynamic";
	static final String ENTRY = "entry";
	static final String EVENTTIMED = "eventtimed";
	static final String EVENTNAME = "eventname";
	static final String VOLMOD = "volmod";
	static final String PITCHMOD = "pitchmod";
	static final String DELAYSTART = "delaystart";
	static final String DELAYMIN = "delaymin";
	static final String DELAYMAX = "delaymax";
	static final String STREAM = "stream";
	static final String VOLUME = "volume";
	static final String PITCH = "pitch";
	static final String FADEINTIME = "fadeintime";
	static final String FADEOUTTIME = "fadeouttime";
	static final String DELAYBEFOREFADEIN = "delaybeforefadein";
	static final String DELAYBEFOREFADEOUT = "delaybeforefadeout";
	static final String ISLOOPING = "islooping";
	static final String ISUSINGPAUSE = "isusingpause";

	public static boolean loadKnowledge(MAtmosKnowledge var0, File var1, boolean var2) throws FileNotFoundException, MAtmosException {
		loadKnowledgeStream(var0, new FileInputStream(var1.getAbsolutePath()), var2);
		return true;
	}

	@SuppressWarnings("CallToPrintStackTrace")
    public static boolean loadKnowledgeStream(MAtmosKnowledge var0, InputStream var1, boolean var2) throws MAtmosException {
		try {
			parseXML(var0, var1, var2);
			var1.close();
		} catch (XMLStreamException | NumberFormatException var7) {
			try {
				var1.close();
			} catch (IOException var6) {
				var6.printStackTrace();
			}

			throw new MAtmosException();
		} catch (IOException var9) {
			var9.printStackTrace();
		}

		return true;
	}

	private static void parseXML(MAtmosKnowledge var0, InputStream var1, boolean var2) throws XMLStreamException {
		XMLInputFactory var3 = XMLInputFactory.newInstance();
		XMLEventReader var4 = var3.createXMLEventReader(var1);

		while(var4.hasNext()) {
			XMLEvent var5 = var4.nextEvent();
			if(var5.isStartElement()) {
				StartElement var6 = var5.asStartElement();
				String var7;
				if(Objects.equals(var6.getName().getLocalPart(), "dynamic")) {
					var7 = parxeXMLextractNameAttribute(var6);
					if(var7 != null) {
						parseXMLdynamic(var0, var4, var7, var2);
					}
				} else if(Objects.equals(var6.getName().getLocalPart(), "list")) {
					var7 = parxeXMLextractNameAttribute(var6);
					if(var7 != null) {
						parseXMLlist(var0, var4, var7, var2);
					}
				} else if(Objects.equals(var6.getName().getLocalPart(), "condition")) {
					var7 = parxeXMLextractNameAttribute(var6);
					if(var7 != null) {
						parseXMLcondition(var0, var4, var7, var2);
					}
				} else if(Objects.equals(var6.getName().getLocalPart(), "set")) {
					var7 = parxeXMLextractNameAttribute(var6);
					if(var7 != null) {
						parseXMLset(var0, var4, var7, var2);
					}
				} else if(Objects.equals(var6.getName().getLocalPart(), "event")) {
					var7 = parxeXMLextractNameAttribute(var6);
					if(var7 != null) {
						parseXMLevent(var0, var4, var7, var2);
					}
				} else if(Objects.equals(var6.getName().getLocalPart(), "machine")) {
					var7 = parxeXMLextractNameAttribute(var6);
					if(var7 != null) {
						parseXMLmachine(var0, var4, var7, var2);
					}
				}
			}
		}

	}

	private static String parxeXMLextractNameAttribute(StartElement var0) {
		String var1 = null;
		Iterator<Attribute> var2 = var0.getAttributes();

		while(var2.hasNext() && var1 == null) {
			Attribute var3 = var2.next();
			if(var3.getName().toString().equals("name")) {
				var1 = var3.getValue();
			}
		}

		return var1;
	}

	private static String pickupNextEventData(XMLEventReader var0) throws XMLStreamException {
		return !var0.peek().isCharacters() ? "" : var0.nextEvent().asCharacters().getData();
	}

	private static void parseXMLdescriptible(MAtmosKnowledge var0, XMLEventReader var1, MAtmosDescriptible var2) throws XMLStreamException {
		while(true) {
			if(var1.hasNext()) {
				XMLEvent var3 = var1.nextEvent();
				if(var3.isStartElement()) {
					StartElement var4 = var3.asStartElement();
					String var5 = var4.getName().getLocalPart();
					if(Objects.equals(var5, "nickname")) {
						var2.nickname = pickupNextEventData(var1);
					} else if(Objects.equals(var5, "description")) {
						var2.description = pickupNextEventData(var1);
					} else if(Objects.equals(var5, "icon")) {
						var2.icon = pickupNextEventData(var1);
					} else if(Objects.equals(var5, "meta")) {
						var2.meta = pickupNextEventData(var1);
					}
				}

				if(!var3.isEndElement()) {
					continue;
				}

				EndElement var6 = var3.asEndElement();
				if(!Objects.equals(var6.getName().getLocalPart(), "descriptible")) {
					continue;
				}

				return;
			}

			return;
		}
	}

	private static boolean parseXMLdynamic(MAtmosKnowledge var0, XMLEventReader var1, String var2, boolean var3) throws XMLStreamException {
		boolean var4 = var0.getDynamic(var2) == null;
		if(!var4 && var3) {
			var0.removeDynamic(var2);
		}

		if(var4 || var3) {
			var0.addDynamic(var2);
		}

		if(!var4 && !var3) {
            MatmosStationAPI.LOGGER.error("Couldn't add Dynamic : {}", var2);
		}

		MAtmosDynamic var5 = var0.getDynamic(var2);

		while(var1.hasNext()) {
			XMLEvent var6 = var1.nextEvent();
			if(!var4 && !var3 && var6.isEndElement() && Objects.equals(var6.asEndElement().getName().getLocalPart(), "dynamic")) {
				return false;
			}

			if(var6.isStartElement()) {
				StartElement var7 = var6.asStartElement();
				String var8 = var7.getName().getLocalPart();
				if(Objects.equals(var8, "descriptible")) {
					parseXMLdescriptible(var0, var1, var5);
				} else if(Objects.equals(var8, "entry")) {
					Iterator<Attribute> var9 = var7.getAttributes();

					while(var9.hasNext()) {
						Attribute var10 = var9.next();
						if(var10.getName().toString().equals("sheet")) {
							var5.addCouple(var10.getValue(), Integer.parseInt(var1.nextEvent().asCharacters().toString()));
						}
					}
				}
			}

			if(var6.isEndElement()) {
				EndElement var11 = var6.asEndElement();
				if(Objects.equals(var11.getName().getLocalPart(), "dynamic")) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean parseXMLlist(MAtmosKnowledge var0, XMLEventReader var1, String var2, boolean var3) throws XMLStreamException {
		boolean var4 = var0.getList(var2) == null;
		if(!var4 && var3) {
			var0.removeList(var2);
		}

		if(var4 || var3) {
			var0.addList(var2);
		}

		if(!var4 && !var3) {
            MatmosStationAPI.LOGGER.error("Couldn't add List : {}", var2);
		}

		MAtmosList var5 = var0.getList(var2);

		while(var1.hasNext()) {
			XMLEvent var6 = var1.nextEvent();
			if(!var4 && !var3 && var6.isEndElement() && Objects.equals(var6.asEndElement().getName().getLocalPart(), "dynamic")) {
				return false;
			}

			if(var6.isStartElement()) {
				StartElement var7 = var6.asStartElement();
				String var8 = var7.getName().getLocalPart();
				if(Objects.equals(var8, "descriptible")) {
					parseXMLdescriptible(var0, var1, var5);
				} else if(Objects.equals(var8, "constant")) {
					String var9 = pickupNextEventData(var1);

					try {
						var5.add(Integer.parseInt(var9));
					} catch (NumberFormatException var11) {
						var5.add((int)Float.parseFloat(var9));
					}
				}
			}

			if(var6.isEndElement()) {
				EndElement var12 = var6.asEndElement();
				if(Objects.equals(var12.getName().getLocalPart(), "list")) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean parseXMLcondition(MAtmosKnowledge var0, XMLEventReader var1, String var2, boolean var3) throws XMLStreamException {
		boolean var4 = var0.getDataCondition(var2) == null;
		if(!var4 && var3) {
			var0.removeDataCondition(var2);
		}

		if(var4 || var3) {
			var0.addDataCondition(var2);
		}

		if(!var4 && !var3) {
            MatmosStationAPI.LOGGER.error("Couldn't add Condition : {}", var2);
		}

		MAtmosCondition var5 = var0.getDataCondition(var2);

		while(var1.hasNext()) {
			XMLEvent var6 = var1.nextEvent();
			if(!var4 && !var3 && var6.isEndElement() && Objects.equals(var6.asEndElement().getName().getLocalPart(), "condition")) {
				return false;
			}

			if(var6.isStartElement()) {
				StartElement var7 = var6.asStartElement();
				String var8 = var7.getName().getLocalPart();
				if(Objects.equals(var8, "descriptible")) {
					parseXMLdescriptible(var0, var1, var5);
				} else if(Objects.equals(var8, "sheet")) {
					var5.setSheet(pickupNextEventData(var1));
				} else if(Objects.equals(var8, "key")) {
					var5.setKey(Integer.parseInt(pickupNextEventData(var1)));
				} else if(Objects.equals(var8, "dynamickey")) {
					var5.setDynamic(pickupNextEventData(var1));
				} else if(Objects.equals(var8, "symbol")) {
					var5.setSymbol(pickupNextEventData(var1));
				} else if(Objects.equals(var8, "constant")) {
					String var9 = pickupNextEventData(var1);

					try {
						var5.setConstant(Integer.parseInt(var9));
					} catch (NumberFormatException var11) {
						var5.setConstant((int)Float.parseFloat(var9));
					}
				} else if(Objects.equals(var8, "list")) {
					var5.setList(pickupNextEventData(var1));
				}
			}

			if(var6.isEndElement()) {
				EndElement var12 = var6.asEndElement();
				if(Objects.equals(var12.getName().getLocalPart(), "condition")) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean parseXMLset(MAtmosKnowledge var0, XMLEventReader var1, String var2, boolean var3) throws XMLStreamException {
		boolean var4 = var0.getConditionSet(var2) == null;
		if(!var4 && var3) {
			var0.removeConditionSet(var2);
		}

		if(var4 || var3) {
			var0.addConditionSet(var2);
		}

		if(!var4 && !var3) {
            MatmosStationAPI.LOGGER.error("Couldn't add Set : {}", var2);
		}

		MAtmosConditionSet var5 = var0.getConditionSet(var2);

		while(var1.hasNext()) {
			XMLEvent var6 = var1.nextEvent();
			if(!var4 && !var3 && var6.isEndElement() && Objects.equals(var6.asEndElement().getName().getLocalPart(), "set")) {
				return false;
			}

			if(var6.isStartElement()) {
				StartElement var7 = var6.asStartElement();
				String var8 = var7.getName().getLocalPart();
				if(Objects.equals(var8, "descriptible")) {
					parseXMLdescriptible(var0, var1, var5);
				} else if(Objects.equals(var8, "truepart")) {
					var5.addCondition(pickupNextEventData(var1), true);
				} else if(Objects.equals(var8, "falsepart")) {
					var5.addCondition(pickupNextEventData(var1), false);
				}
			}

			if(var6.isEndElement()) {
				EndElement var9 = var6.asEndElement();
				if(Objects.equals(var9.getName().getLocalPart(), "set")) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean parseXMLevent(MAtmosKnowledge var0, XMLEventReader var1, String var2, boolean var3) throws XMLStreamException {
		boolean var4 = var0.getEvent(var2) == null;
		if(!var4 && var3) {
			var0.removeEvent(var2);
		}

		if(var4 || var3) {
			var0.addEvent(var2);
		}

		if(!var4 && !var3) {
            MatmosStationAPI.LOGGER.error("Couldn't add Event : {}", var2);
		}

		MAtmosEvent var5 = var0.getEvent(var2);

		while(var1.hasNext()) {
			XMLEvent var6 = var1.nextEvent();
			if(!var4 && !var3 && var6.isEndElement() && Objects.equals(var6.asEndElement().getName().getLocalPart(), "event")) {
				return false;
			}

			if(var6.isStartElement()) {
				StartElement var7 = var6.asStartElement();
				String var8 = var7.getName().getLocalPart();
				if(Objects.equals(var8, "descriptible")) {
					parseXMLdescriptible(var0, var1, var5);
				} else if(Objects.equals(var8, "volmin")) {
					var5.volMin = Float.parseFloat(pickupNextEventData(var1));
				} else if(Objects.equals(var8, "volmax")) {
					var5.volMax = Float.parseFloat(pickupNextEventData(var1));
				} else if(Objects.equals(var8, "pitchmin")) {
					var5.pitchMin = Float.parseFloat(pickupNextEventData(var1));
				} else if(Objects.equals(var8, "pitchmax")) {
					var5.pitchMax = Float.parseFloat(pickupNextEventData(var1));
				} else if(Objects.equals(var8, "metasound")) {
					var5.metaSound = Integer.parseInt(pickupNextEventData(var1));
				} else if(Objects.equals(var8, "path")) {
					var5.paths.add(pickupNextEventData(var1));
				}
			}

			if(var6.isEndElement()) {
				EndElement var9 = var6.asEndElement();
				if(Objects.equals(var9.getName().getLocalPart(), "event")) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean parseXMLmachine(MAtmosKnowledge var0, XMLEventReader var1, String var2, boolean var3) throws XMLStreamException {
		boolean var4 = var0.getMachine(var2) == null;
		if(!var4 && var3) {
			var0.removeMachine(var2);
		}

		if(var4 || var3) {
			var0.addMachine(var2);
		}

		if(!var4 && !var3) {
            MatmosStationAPI.LOGGER.error("Couldn't add Machine : {}", var2);
		}

		MAtmosMachine var5 = var0.getMachine(var2);

		while(var1.hasNext()) {
			XMLEvent var6 = var1.nextEvent();
			if(!var4 && !var3 && var6.isEndElement() && Objects.equals(var6.asEndElement().getName().getLocalPart(), "machine")) {
				return false;
			}

			if(var6.isStartElement()) {
				StartElement var7 = var6.asStartElement();
				String var8 = var7.getName().getLocalPart();
				if(Objects.equals(var8, "descriptible")) {
					parseXMLdescriptible(var0, var1, var5);
				} else if(Objects.equals(var8, "allow")) {
					var5.addAllow(pickupNextEventData(var1));
				} else if(Objects.equals(var8, "restrict")) {
					var5.addRestrict(pickupNextEventData(var1));
				} else {
					int var9;
					if(Objects.equals(var8, "eventtimed")) {
						var9 = var5.addEventTimed();
						parseXMLeventTimed(var5.getEventTimed(var9 - 1), var1);
					} else if(Objects.equals(var8, "stream")) {
						var9 = var5.addStream();
						parseXMLstream(var5.getStream(var9 - 1), var1);
					}
				}
			}

			if(var6.isEndElement()) {
				EndElement var10 = var6.asEndElement();
				if(Objects.equals(var10.getName().getLocalPart(), "machine")) {
					return true;
				}
			}
		}

		return false;
	}

	private static void parseXMLeventTimed(MAtmosEventTimed var0, XMLEventReader var1) throws XMLStreamException {
		while(true) {
			if(var1.hasNext()) {
				XMLEvent var2 = var1.nextEvent();
				if(var2.isStartElement()) {
					StartElement var3 = var2.asStartElement();
					String var4 = var3.getName().getLocalPart();
					if(Objects.equals(var4, "eventname")) {
						var0.event = pickupNextEventData(var1);
					} else if(Objects.equals(var4, "volmod")) {
						var0.volMod = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "pitchmod")) {
						var0.pitchMod = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "delaystart")) {
						var0.delayStart = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "delaymin")) {
						var0.delayMin = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "delaymax")) {
						var0.delayMax = Float.parseFloat(pickupNextEventData(var1));
					}
				}

				if(!var2.isEndElement()) {
					continue;
				}

				EndElement var5 = var2.asEndElement();
				if(!Objects.equals(var5.getName().getLocalPart(), "eventtimed")) {
					continue;
				}

				return;
			}

			return;
		}
	}

	private static void parseXMLstream(MAtmosStream var0, XMLEventReader var1) throws XMLStreamException {
		while(true) {
			if(var1.hasNext()) {
				XMLEvent var2 = var1.nextEvent();
				if(var2.isStartElement()) {
					StartElement var3 = var2.asStartElement();
					String var4 = var3.getName().getLocalPart();
					if(Objects.equals(var4, "path")) {
						var0.path = pickupNextEventData(var1);
					} else if(Objects.equals(var4, "volume")) {
						var0.volume = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "pitch")) {
						var0.pitch = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "fadeintime")) {
						var0.fadeInTime = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "fadeouttime")) {
						var0.fadeOutTime = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "delaybeforefadein")) {
						var0.delayBeforeFadeIn = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "delaybeforefadeout")) {
						var0.delayBeforeFadeOut = Float.parseFloat(pickupNextEventData(var1));
					} else if(Objects.equals(var4, "islooping")) {
						var0.isLooping = Integer.parseInt(pickupNextEventData(var1)) == 1;
					} else if(Objects.equals(var4, "isusingpause")) {
						var0.isUsingPause = Integer.parseInt(pickupNextEventData(var1)) == 1;
					}
				}

				if(!var2.isEndElement()) {
					continue;
				}

				EndElement var5 = var2.asEndElement();
				if(!Objects.equals(var5.getName().getLocalPart(), "stream")) {
					continue;
				}

				return;
			}

			return;
		}
	}
}
