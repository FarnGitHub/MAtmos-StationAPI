package matmos.engine;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MAtmosDynamic extends MAtmosSwitchable {
	public ArrayList<String> sheets = new ArrayList<>();
	public ArrayList<Integer> keys = new ArrayList<>();
	public int value = 0;

	MAtmosDynamic(MAtmosKnowledge var1) {
		super(var1);
	}

	public void addCouple(String var1, int var2) {
		this.sheets.add(var1);
		this.keys.add(var2);
		this.flagNeedsTesting();
	}

	public void removeCouple(int var1) {
		this.sheets.remove(var1);
		this.keys.remove(var1);
		this.flagNeedsTesting();
	}

	public void setSheet(int var1, String var2) {
		this.sheets.set(var1, var2);
		this.flagNeedsTesting();
	}

	public void setKey(int var1, int var2) {
		this.keys.set(var1, var2);
		this.flagNeedsTesting();
	}

	public ArrayList<String> getSheets() {
		return this.sheets;
	}

	public ArrayList<Integer> getKeys() {
		return this.keys;
	}

	public String getSheet(int var1) {
		return this.sheets.get(var1);
	}

	public int getKey(int var1) {
		return this.keys.get(var1);
	}

	public boolean isActive() {
		return false;
	}

	public void evaluate() {
		this.value = 0;
		if(this.isValid()) {
			Iterator<String> itSheet = this.sheets.iterator();

			String sheet;
			Integer key;
			for(Iterator<Integer> itKey = this.keys.iterator(); itSheet.hasNext(); this.value += (this.knowledge.data.sheets.get(sheet).get(key))) {
				sheet = itSheet.next();
				key = itKey.next();
			}
		}

	}

	protected boolean testIfValid() {
		Iterator<String> itSheet = this.sheets.iterator();
		Iterator<Integer> itString = this.keys.iterator();

		String sheet;
		Integer key;
		do {
			if(!itSheet.hasNext()) {
				return true;
			}

			sheet = itSheet.next();
			key = itString.next();
			if(!this.knowledge.data.sheets.containsKey(sheet)) {
				return false;
			}
		} while(key >= 0 && key < this.knowledge.data.sheets.get(sheet).size());

		return false;
	}

	public String serialize(XMLEventWriter var1) throws XMLStreamException {
		this.buildDescriptibleSerialized(var1);
		XMLEventFactory var2 = XMLEventFactory.newInstance();
		DTD var3 = var2.createDTD("\t");
		DTD var4 = var2.createDTD("\n");

		for(int var5 = 0; var5 < this.sheets.size(); ++var5) {
			var1.add(var3);
			var1.add(var2.createStartElement("", "", "entry"));
			var1.add(var2.createAttribute("sheet", (String)this.sheets.get(var5)));
			var1.add(var2.createCharacters(this.keys.get(var5) + ""));
			var1.add(var2.createEndElement("", "", "entry"));
			var1.add(var4);
		}

		return null;
	}
}
