package matmos.engine;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;

public class MAtmosDynamic extends MAtmosSwitchable {
	public ArrayList sheets = new ArrayList();
	public ArrayList keys = new ArrayList();
	public int value = 0;

	MAtmosDynamic(MAtmosKnowledge var1) {
		super(var1);
	}

	public void addCouple(String var1, int var2) {
		this.sheets.add(var1);
		this.keys.add(Integer.valueOf(var2));
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
		this.keys.set(var1, Integer.valueOf(var2));
		this.flagNeedsTesting();
	}

	public ArrayList getSheets() {
		return this.sheets;
	}

	public ArrayList getKeys() {
		return this.keys;
	}

	public String getSheet(int var1) {
		return (String)this.sheets.get(var1);
	}

	public int getKey(int var1) {
		return ((Integer)this.keys.get(var1)).intValue();
	}

	public boolean isActive() {
		return false;
	}

	public void evaluate() {
		this.value = 0;
		if(this.isValid()) {
			Iterator var1 = this.sheets.iterator();

			String var2;
			Integer var3;
			for(Iterator var4 = this.keys.iterator(); var1.hasNext(); this.value += ((Integer)((ArrayList)this.knowledge.data.sheets.get(var2)).get(var3.intValue())).intValue()) {
				var2 = (String)var1.next();
				var3 = (Integer)var4.next();
			}
		}

	}

	protected boolean testIfValid() {
		Iterator var1 = this.sheets.iterator();
		Iterator var2 = this.keys.iterator();

		String var3;
		Integer var4;
		do {
			if(!var1.hasNext()) {
				return true;
			}

			var3 = (String)var1.next();
			var4 = (Integer)var2.next();
			if(!this.knowledge.data.sheets.containsKey(var3)) {
				return false;
			}
		} while(var4.intValue() >= 0 && var4.intValue() < ((ArrayList)this.knowledge.data.sheets.get(var3)).size());

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
