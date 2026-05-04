package matmos.engine;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;

public class MAtmosEventTimed extends MAtmosDescriptible {
	MAtmosMachine machine;
	public String event = "";
	public float volMod;
	public float pitchMod;
	public float delayMin;
	public float delayMax;
	public float delayStart;
	public long nextPlayTime;

	MAtmosEventTimed(MAtmosMachine var1) {
		this.machine = var1;
		this.volMod = 1.0F;
		this.pitchMod = 1.0F;
		this.delayMin = 10.0F;
		this.delayMax = 10.0F;
		this.delayStart = 0.0F;
	}

	void setMachine(MAtmosMachine var1) {
		this.machine = var1;
	}

	public void routine() {
		if(this.machine.knowledge.getTimeMillis() >= this.nextPlayTime) {
			if(this.machine.knowledge.events.containsKey(this.event)) {
				((MAtmosEvent)this.machine.knowledge.events.get(this.event)).playSound(this.volMod, this.pitchMod);
			}

			this.nextPlayTime = this.machine.knowledge.getTimeMillis() + (long)((this.delayMin + this.machine.knowledge.random.nextFloat() * (this.delayMax - this.delayMin)) * 1000.0F);
		}

	}

	public void restart() {
		if(this.delayStart == 0.0F) {
			this.nextPlayTime = this.machine.knowledge.getTimeMillis() + (long)(this.machine.knowledge.random.nextFloat() * this.delayMax * 1000.0F);
		} else {
			this.nextPlayTime = this.machine.knowledge.getTimeMillis() + (long)(this.delayStart * 1000.0F);
		}

	}

	public String serialize(XMLEventWriter var1) throws XMLStreamException {
		XMLEventFactory var2 = XMLEventFactory.newInstance();
		DTD var3 = var2.createDTD("\n");
		DTD var4 = var2.createDTD("\t");
		var1.add(var4);
		var1.add(var2.createStartElement("", "", "eventtimed"));
		var1.add(var3);
		this.createNode(var1, "eventname", this.event, 2);
		this.createNode(var1, "delaymin", "" + this.delayMin, 2);
		this.createNode(var1, "delaymax", "" + this.delayMax, 2);
		this.createNode(var1, "delaystart", "" + this.delayStart, 2);
		this.createNode(var1, "volmod", "" + this.volMod, 2);
		this.createNode(var1, "pitchmod", "" + this.pitchMod, 2);
		var1.add(var4);
		var1.add(var2.createEndElement("", "", "eventtimed"));
		var1.add(var3);
		return "";
	}
}
