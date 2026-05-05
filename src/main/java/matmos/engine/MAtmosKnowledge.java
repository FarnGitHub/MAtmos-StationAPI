package matmos.engine;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.transform.stream.StreamResult;

public class MAtmosKnowledge {
	HashMap dynamics;
	HashMap lists;
	HashMap conditions;
	HashMap csets;
	HashMap machines;
	HashMap events;
	MAtmosData data = new MAtmosData();
	MAtmosSoundManager soundManager = null;
	MAtmosClock clock = new MAtmosClock();
	public boolean isRunning = false;
	int dataLastVersion = 0;
	Random random = new Random(System.currentTimeMillis());

	public MAtmosKnowledge() {
		this.patchKnowledge();
	}

	public void patchKnowledge() {
		this.turnOff();
		this.dynamics = new HashMap();
		this.lists = new HashMap();
		this.conditions = new HashMap();
		this.csets = new HashMap();
		this.machines = new HashMap();
		this.events = new HashMap();
	}

	public void turnOn() {
		if(this.soundManager != null && !this.isRunning) {
			this.reclaimKeyring();
			this.isRunning = true;

            for (Object o : this.machines.values()) {
                ((MAtmosMachine) o).powerOn();
            }
		}

	}

	public void turnOff() {
		if(this.isRunning) {
			this.isRunning = false;
			Iterator var1 = this.machines.values().iterator();

			while(var1.hasNext()) {
				((MAtmosMachine)var1.next()).powerOff();
			}
		}

	}

	public boolean isTurnedOn() {
		return this.isRunning;
	}

	public Set getDynamicsKeySet() {
		return this.dynamics.keySet();
	}

	public Set getListsKeySet() {
		return this.lists.keySet();
	}

	public Set getConditionsKeySet() {
		return this.conditions.keySet();
	}

	public Set getConditionSetsKeySet() {
		return this.csets.keySet();
	}

	public Set getMachinesKeySet() {
		return this.machines.keySet();
	}

	public Set getEventsKeySet() {
		return this.events.keySet();
	}

	public void reclaimKeyring() {
		this.turnOff();
		Iterator var1 = this.dynamics.values().iterator();

		while(var1.hasNext()) {
			((MAtmosDynamic)var1.next()).setKnowledge(this);
		}

		var1 = this.conditions.values().iterator();

		while(var1.hasNext()) {
			((MAtmosCondition)var1.next()).setKnowledge(this);
		}

		var1 = this.csets.values().iterator();

		while(var1.hasNext()) {
			((MAtmosConditionSet)var1.next()).setKnowledge(this);
		}

		var1 = this.machines.values().iterator();

		while(var1.hasNext()) {
			((MAtmosMachine)var1.next()).setKnowledge(this);
		}

		var1 = this.events.values().iterator();

		while(var1.hasNext()) {
			((MAtmosEvent)var1.next()).setKnowledge(this);
		}

	}

	public void retreiveKeyring(MAtmosKnowledge var1) {
		if(!var1.isRunning) {
			this.dynamics = (HashMap)var1.dynamics.clone();
			this.lists = (HashMap)var1.lists.clone();
			this.conditions = (HashMap)var1.conditions.clone();
			this.csets = (HashMap)var1.csets.clone();
			this.machines = (HashMap)var1.machines.clone();
			this.events = (HashMap)var1.events.clone();
			this.reclaimKeyring();
		}

	}

	public void setSoundManager(MAtmosSoundManager var1) {
		this.soundManager = var1;
	}

	public void cacheSounds() {
		Iterator var1 = this.events.values().iterator();

		while(var1.hasNext()) {
			((MAtmosEvent)var1.next()).cacheSounds();
		}

	}

	public void setClock(MAtmosClock var1) {
		this.clock = var1;
	}

	public void setData(MAtmosData var1) {
		this.data = var1;
		this.applySheetFlagNeedsTesting();
	}

	public long getTimeMillis() {
		return this.clock.getTimeMillis();
	}

	void applySheetFlagNeedsTesting() {
		Iterator var1 = this.conditions.values().iterator();

		while(var1.hasNext()) {
			((MAtmosCondition)var1.next()).flagNeedsTesting();
		}

		var1 = this.dynamics.values().iterator();

		while(var1.hasNext()) {
			((MAtmosDynamic)var1.next()).flagNeedsTesting();
		}

	}

	public MAtmosEvent getEvent(String var1) {
		return (MAtmosEvent)this.events.get(var1);
	}

	public boolean addEvent(String var1) {
		if(this.events.containsKey(var1)) {
			return false;
		} else {
			this.events.put(var1, new MAtmosEvent(this));
			((MAtmosEvent)this.events.get(var1)).nickname = var1;
			return true;
		}
	}

	public boolean removeEvent(String var1) {
		if(!this.events.containsKey(var1)) {
			return false;
		} else {
			this.events.remove(var1);
			return true;
		}
	}

	public boolean renameEvent(String var1, String var2) {
		if(!this.events.containsKey(var1)) {
			return false;
		} else if(this.events.containsKey(var2)) {
			return false;
		} else {
			this.events.put(var2, this.events.get(var1));
			this.events.remove(var1);
			((MAtmosEvent)this.events.get(var2)).nickname = var2;
			Iterator var3 = this.machines.values().iterator();

			while(var3.hasNext()) {
				Iterator var4 = ((MAtmosMachine)var3.next()).etimes.iterator();

				while(var4.hasNext()) {
					MAtmosEventTimed var5 = (MAtmosEventTimed)var4.next();
					if(var5.event.equals(var1)) {
						var5.event = var2;
					}
				}
			}

			return true;
		}
	}

	void applyDynamicFlagNeedsTesting() {
		Iterator var1 = this.conditions.values().iterator();

		while(var1.hasNext()) {
			((MAtmosCondition)var1.next()).flagNeedsTesting();
		}

	}

	public MAtmosDynamic getDynamic(String var1) {
		return (MAtmosDynamic)this.dynamics.get(var1);
	}

	public boolean addDynamic(String var1) {
		if(this.dynamics.containsKey(var1)) {
			return false;
		} else {
			this.dynamics.put(var1, new MAtmosDynamic(this));
			((MAtmosDynamic)this.dynamics.get(var1)).nickname = var1;
			this.applyDynamicFlagNeedsTesting();
			return true;
		}
	}

	public boolean removeDynamic(String var1) {
		if(!this.dynamics.containsKey(var1)) {
			return false;
		} else {
			this.dynamics.remove(var1);
			this.applyDynamicFlagNeedsTesting();
			return true;
		}
	}

	public boolean renameDynamic(String var1, String var2) {
		if(!this.dynamics.containsKey(var1)) {
			return false;
		} else if(this.dynamics.containsKey(var2)) {
			return false;
		} else {
			this.dynamics.put(var2, this.dynamics.get(var1));
			this.dynamics.remove(var1);
			((MAtmosDynamic)this.dynamics.get(var2)).nickname = var2;
			Iterator var3 = this.conditions.values().iterator();

			while(var3.hasNext()) {
				((MAtmosCondition)var3.next()).replaceDynamicName(var1, var2);
			}

			return true;
		}
	}

	void applyListFlagNeedsTesting() {
		Iterator var1 = this.conditions.values().iterator();

		while(var1.hasNext()) {
			((MAtmosCondition)var1.next()).flagNeedsTesting();
		}

	}

	public MAtmosList getList(String var1) {
		return (MAtmosList)this.lists.get(var1);
	}

	public boolean addList(String var1) {
		if(this.lists.containsKey(var1)) {
			return false;
		} else {
			this.lists.put(var1, new MAtmosList());
			((MAtmosList)this.lists.get(var1)).nickname = var1;
			this.applyDynamicFlagNeedsTesting();
			return true;
		}
	}

	public boolean removeList(String var1) {
		if(!this.lists.containsKey(var1)) {
			return false;
		} else {
			this.lists.remove(var1);
			this.applyDynamicFlagNeedsTesting();
			return true;
		}
	}

	public boolean renameList(String var1, String var2) {
		if(!this.lists.containsKey(var1)) {
			return false;
		} else if(this.lists.containsKey(var2)) {
			return false;
		} else {
			this.lists.put(var2, this.lists.get(var1));
			this.lists.remove(var1);
			((MAtmosList)this.lists.get(var2)).nickname = var2;
			Iterator var3 = this.conditions.values().iterator();

			while(var3.hasNext()) {
				((MAtmosCondition)var3.next()).replaceListName(var1, var2);
			}

			return true;
		}
	}

	void applyDataConditionNeedsTesting() {
		Iterator var1 = this.csets.values().iterator();

		while(var1.hasNext()) {
			((MAtmosConditionSet)var1.next()).flagNeedsTesting();
		}

	}

	public MAtmosCondition getDataCondition(String var1) {
		return (MAtmosCondition)this.conditions.get(var1);
	}

	public boolean addDataCondition(String var1) {
		if(this.conditions.containsKey(var1)) {
			return false;
		} else {
			this.conditions.put(var1, new MAtmosCondition(this));
			((MAtmosCondition)this.conditions.get(var1)).nickname = var1;
			this.applyDataConditionNeedsTesting();
			return true;
		}
	}

	public boolean renameDataCondition(String var1, String var2) {
		if(!this.conditions.containsKey(var1)) {
			return false;
		} else if(this.conditions.containsKey(var2)) {
			return false;
		} else {
			this.conditions.put(var2, this.conditions.get(var1));
			this.conditions.remove(var1);
			((MAtmosCondition)this.conditions.get(var2)).nickname = var2;
			Iterator var3 = this.csets.values().iterator();

			while(var3.hasNext()) {
				((MAtmosConditionSet)var3.next()).replaceConditionName(var1, var2);
			}

			this.applyDataConditionNeedsTesting();
			return true;
		}
	}

	public boolean removeDataCondition(String var1) {
		if(!this.conditions.containsKey(var1)) {
			return false;
		} else {
			this.conditions.remove(var1);
			this.applyDataConditionNeedsTesting();
			return true;
		}
	}

	void applyConditionSetNeedsTesting() {
		Iterator var1 = this.machines.values().iterator();

		while(var1.hasNext()) {
			((MAtmosMachine)var1.next()).flagNeedsTesting();
		}

	}

	public MAtmosConditionSet getConditionSet(String var1) {
		return (MAtmosConditionSet)this.csets.get(var1);
	}

	public boolean addConditionSet(String var1) {
		if(this.csets.containsKey(var1)) {
			return false;
		} else {
			this.csets.put(var1, new MAtmosConditionSet(this));
			((MAtmosConditionSet)this.csets.get(var1)).nickname = var1;
			this.applyConditionSetNeedsTesting();
			return true;
		}
	}

	public boolean renameConditionSet(String var1, String var2) {
		if(!this.csets.containsKey(var1)) {
			return false;
		} else if(this.csets.containsKey(var2)) {
			return false;
		} else {
			this.csets.put(var2, this.csets.get(var1));
			this.csets.remove(var1);
			((MAtmosConditionSet)this.csets.get(var2)).nickname = var2;
			Iterator var3 = this.machines.values().iterator();

			while(var3.hasNext()) {
				((MAtmosMachine)var3.next()).replaceSetName(var1, var2);
			}

			this.applyConditionSetNeedsTesting();
			return true;
		}
	}

	public boolean removeConditionSet(String var1) {
		if(!this.csets.containsKey(var1)) {
			return false;
		} else {
			this.csets.remove(var1);
			this.applyConditionSetNeedsTesting();
			return true;
		}
	}

	void applyMachineNeedsTesting() {
	}

	public MAtmosMachine getMachine(String var1) {
		return (MAtmosMachine)this.machines.get(var1);
	}

	public boolean addMachine(String var1) {
		if(this.machines.containsKey(var1)) {
			return false;
		} else {
			this.machines.put(var1, new MAtmosMachine(this));
			((MAtmosMachine)this.machines.get(var1)).nickname = var1;
			this.applyMachineNeedsTesting();
			return true;
		}
	}

	public boolean removeMachine(String var1) {
		if(!this.machines.containsKey(var1)) {
			return false;
		} else {
			this.machines.remove(var1);
			this.applyMachineNeedsTesting();
			return true;
		}
	}

	public boolean renameMachine(String var1, String var2) {
		if(!this.machines.containsKey(var1)) {
			return false;
		} else if(this.machines.containsKey(var2)) {
			return false;
		} else {
			this.machines.put(var2, this.machines.get(var1));
			this.machines.remove(var1);
			((MAtmosMachine)this.machines.get(var2)).nickname = var2;
			this.applyConditionSetNeedsTesting();
			return true;
		}
	}

	public void routine() {
		if(this.isRunning) {
			if(this.dataLastVersion != this.data.updateVersion) {
				this.evaluate();
			}

			this.dataLastVersion = this.data.updateVersion;
			this.soundManager.routine();
			Iterator var1 = this.machines.values().iterator();

			while(var1.hasNext()) {
				((MAtmosMachine)var1.next()).routine();
			}
		}

	}

	void evaluate() {
		if(this.isRunning) {
			Iterator var1 = this.dynamics.values().iterator();

			while(var1.hasNext()) {
				((MAtmosDynamic)var1.next()).evaluate();
			}

			var1 = this.conditions.values().iterator();

			while(var1.hasNext()) {
				((MAtmosCondition)var1.next()).evaluate();
			}

			var1 = this.csets.values().iterator();

			while(var1.hasNext()) {
				((MAtmosConditionSet)var1.next()).evaluate();
			}

			var1 = this.machines.values().iterator();

			while(var1.hasNext()) {
				((MAtmosMachine)var1.next()).evaluate();
			}
		}

	}

	public String createXML() throws XMLStreamException {
		StreamResult var1 = new StreamResult(new StringWriter());
		XMLOutputFactory var2 = XMLOutputFactory.newInstance();
		XMLEventFactory var3 = XMLEventFactory.newInstance();
		XMLEventWriter var4 = var2.createXMLEventWriter(var1);
		DTD var5 = var3.createDTD("\n");
		DTD var6 = var3.createDTD("\n");
		var4.add(var3.createStartDocument());
		var4.add(var5);
		var4.add(var3.createStartElement("", "", "contents"));
		Object[] var7 = this.dynamics.keySet().toArray();
		Arrays.sort(var7);

		int var8;
		String var9;
		for(var8 = 0; var8 < var7.length; ++var8) {
			var9 = var7[var8].toString();
			var4.add(var5);
			var4.add(var3.createStartElement("", "", "dynamic"));
			var4.add(var3.createAttribute("name", var9));
			var4.add(var5);
			((MAtmosDynamic)this.dynamics.get(var9)).serialize(var4);
			var4.add(var3.createEndElement("", "", "dynamic"));
		}

		var7 = this.lists.keySet().toArray();
		Arrays.sort(var7);

		for(var8 = 0; var8 < var7.length; ++var8) {
			var9 = var7[var8].toString();
			var4.add(var5);
			var4.add(var3.createStartElement("", "", "list"));
			var4.add(var3.createAttribute("name", var9));
			var4.add(var5);
			((MAtmosList)this.lists.get(var9)).serialize(var4);
			var4.add(var3.createEndElement("", "", "list"));
		}

		var7 = this.conditions.keySet().toArray();
		Arrays.sort(var7);

		for(var8 = 0; var8 < var7.length; ++var8) {
			var9 = var7[var8].toString();
			var4.add(var5);
			var4.add(var3.createStartElement("", "", "condition"));
			var4.add(var3.createAttribute("name", var9));
			var4.add(var5);
			((MAtmosCondition)this.conditions.get(var9)).serialize(var4);
			var4.add(var3.createEndElement("", "", "condition"));
		}

		var7 = this.csets.keySet().toArray();
		Arrays.sort(var7);

		for(var8 = 0; var8 < var7.length; ++var8) {
			var9 = var7[var8].toString();
			var4.add(var5);
			var4.add(var3.createStartElement("", "", "set"));
			var4.add(var3.createAttribute("name", var9));
			var4.add(var5);
			((MAtmosConditionSet)this.csets.get(var9)).serialize(var4);
			var4.add(var3.createEndElement("", "", "set"));
		}

		var7 = this.events.keySet().toArray();
		Arrays.sort(var7);

		for(var8 = 0; var8 < var7.length; ++var8) {
			var9 = var7[var8].toString();
			var4.add(var5);
			var4.add(var3.createStartElement("", "", "event"));
			var4.add(var3.createAttribute("name", var9));
			var4.add(var5);
			((MAtmosEvent)this.events.get(var9)).serialize(var4);
			var4.add(var3.createEndElement("", "", "event"));
		}

		var7 = this.machines.keySet().toArray();
		Arrays.sort(var7);

		for(var8 = 0; var8 < var7.length; ++var8) {
			var9 = var7[var8].toString();
			var4.add(var5);
			var4.add(var3.createStartElement("", "", "machine"));
			var4.add(var3.createAttribute("name", var9));
			var4.add(var5);
			((MAtmosMachine)this.machines.get(var9)).serialize(var4);
			var4.add(var3.createEndElement("", "", "machine"));
		}

		var4.add(var5);
		var4.add(var3.createEndElement("", "", "contents"));
		var4.add(var6);
		var4.add(var3.createEndDocument());
		var4.close();
		return var1.getWriter().toString();
	}
}
