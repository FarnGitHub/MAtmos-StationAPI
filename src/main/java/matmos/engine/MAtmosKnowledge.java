package matmos.engine;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.transform.stream.StreamResult;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MAtmosKnowledge {
	HashMap<String, MAtmosDynamic> dynamics;
	HashMap<String, MAtmosList> lists;
	HashMap<String, MAtmosCondition> conditions;
	HashMap<String, MAtmosConditionSet> csets;
	HashMap<String, MAtmosMachine> machines;
	HashMap<String, MAtmosEvent> events;
	MAtmosData data = new MAtmosData();
	MAtmosSoundManager soundManager = null;
	public boolean isRunning = false;
	int dataLastVersion = 0;
	Random random = new Random(System.currentTimeMillis());

	public MAtmosKnowledge() {
		this.patchKnowledge();
	}

	public void patchKnowledge() {
		this.turnOff();
		this.dynamics = new HashMap<>();
		this.lists = new HashMap<>();
		this.conditions = new HashMap<>();
		this.csets = new HashMap<>();
		this.machines = new HashMap<>();
		this.events = new HashMap<>();
	}

	public void turnOn() {
		if(this.soundManager != null && !this.isRunning) {
			this.reclaimKeyring();
			this.isRunning = true;

            for (MAtmosMachine o : this.machines.values()) {
                 o.powerOn();
            }
		}

	}

	public void turnOff() {
		if(this.isRunning) {
			this.isRunning = false;

            for (MAtmosMachine machine : this.machines.values()) {
                machine.powerOff();
            }
		}

	}

	public boolean isTurnedOn() {
		return this.isRunning;
	}

	public Set<String> getDynamicsKeySet() {
		return this.dynamics.keySet();
	}

	public Set<String> getListsKeySet() {
		return this.lists.keySet();
	}

	public Set<String> getConditionsKeySet() {
		return this.conditions.keySet();
	}

	public Set<String> getConditionSetsKeySet() {
		return this.csets.keySet();
	}

	public Set<String> getMachinesKeySet() {
		return this.machines.keySet();
	}

	public Set<String> getEventsKeySet() {
		return this.events.keySet();
	}

	public void reclaimKeyring() {
		this.turnOff();

        for (MAtmosDynamic mAtmosDynamic : this.dynamics.values()) {
            mAtmosDynamic.setKnowledge(this);
        }

        for (MAtmosCondition mAtmosCondition : this.conditions.values()) {
            mAtmosCondition.setKnowledge(this);
        }

        for (MAtmosConditionSet mAtmosConditionSet : this.csets.values()) {
            mAtmosConditionSet.setKnowledge(this);
        }

        for (MAtmosMachine mAtmosMachine : this.machines.values()) {
            mAtmosMachine.setKnowledge(this);
        }

        for (MAtmosEvent mAtmosEvent : this.events.values()) {
            mAtmosEvent.setKnowledge(this);
        }

	}

	@SuppressWarnings({"unchecked", "rawtypes"})
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

        for (MAtmosEvent mAtmosEvent : this.events.values()) {
            mAtmosEvent.cacheSounds();
        }

	}

	public void setData(MAtmosData var1) {
		this.data = var1;
		this.applySheetFlagNeedsTesting();
	}

	public long getTimeMillis() {
		return System.currentTimeMillis();
	}

	void applySheetFlagNeedsTesting() {

        for (MAtmosCondition mAtmosCondition : this.conditions.values()) {
            mAtmosCondition.flagNeedsTesting();
        }

        for (MAtmosDynamic mAtmosDynamic : this.dynamics.values()) {
            mAtmosDynamic.flagNeedsTesting();
        }

	}

	public MAtmosEvent getEvent(String var1) {
		return this.events.get(var1);
	}

	public boolean addEvent(String var1) {
		if(this.events.containsKey(var1)) {
			return false;
		} else {
			this.events.put(var1, new MAtmosEvent(this));
			this.events.get(var1).nickname = var1;
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
			this.events.get(var2).nickname = var2;

            for (MAtmosMachine mAtmosMachine : this.machines.values()) {
                for (MAtmosEventTimed timed : mAtmosMachine.etimes) {
                    if (timed.event.equals(var1)) {
						timed.event = var2;
                    }
                }
            }

			return true;
		}
	}

	void applyDynamicFlagNeedsTesting() {

        for (MAtmosCondition mAtmosCondition : this.conditions.values()) {
            mAtmosCondition.flagNeedsTesting();
        }

	}

	public MAtmosDynamic getDynamic(String var1) {
		return this.dynamics.get(var1);
	}

	public boolean addDynamic(String var1) {
		if(this.dynamics.containsKey(var1)) {
			return false;
		} else {
			this.dynamics.put(var1, new MAtmosDynamic(this));
			this.dynamics.get(var1).nickname = var1;
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
			this.dynamics.get(var2).nickname = var2;

            for (MAtmosCondition mAtmosCondition : this.conditions.values()) {
                mAtmosCondition.replaceDynamicName(var1, var2);
            }

			return true;
		}
	}

	void applyListFlagNeedsTesting() {

        for (MAtmosCondition mAtmosCondition : this.conditions.values()) {
            mAtmosCondition.flagNeedsTesting();
        }

	}

	public MAtmosList getList(String var1) {
		return this.lists.get(var1);
	}

	public boolean addList(String var1) {
		if(this.lists.containsKey(var1)) {
			return false;
		} else {
			this.lists.put(var1, new MAtmosList());
			this.lists.get(var1).nickname = var1;
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
			this.lists.get(var2).nickname = var2;

            for (MAtmosCondition mAtmosCondition : this.conditions.values()) {
                mAtmosCondition.replaceListName(var1, var2);
            }

			return true;
		}
	}

	void applyDataConditionNeedsTesting() {

        for (MAtmosConditionSet mAtmosConditionSet : this.csets.values()) {
            mAtmosConditionSet.flagNeedsTesting();
        }

	}

	public MAtmosCondition getDataCondition(String var1) {
		return this.conditions.get(var1);
	}

	public boolean addDataCondition(String var1) {
		if(this.conditions.containsKey(var1)) {
			return false;
		} else {
			this.conditions.put(var1, new MAtmosCondition(this));
			this.conditions.get(var1).nickname = var1;
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
			this.conditions.get(var2).nickname = var2;

            for (MAtmosConditionSet mAtmosConditionSet : this.csets.values()) {
                mAtmosConditionSet.replaceConditionName(var1, var2);
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

        for (MAtmosMachine mAtmosMachine : this.machines.values()) {
            mAtmosMachine.flagNeedsTesting();
        }

	}

	public MAtmosConditionSet getConditionSet(String var1) {
		return this.csets.get(var1);
	}

	public boolean addConditionSet(String var1) {
		if(this.csets.containsKey(var1)) {
			return false;
		} else {
			this.csets.put(var1, new MAtmosConditionSet(this));
			this.csets.get(var1).nickname = var1;
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
			this.csets.get(var2).nickname = var2;

            for (MAtmosMachine mAtmosMachine : this.machines.values()) {
                mAtmosMachine.replaceSetName(var1, var2);
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
		return this.machines.get(var1);
	}

	public boolean addMachine(String var1) {
		if(this.machines.containsKey(var1)) {
			return false;
		} else {
			this.machines.put(var1, new MAtmosMachine(this));
			this.machines.get(var1).nickname = var1;
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
			this.machines.get(var2).nickname = var2;
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

            for (MAtmosMachine mAtmosMachine : this.machines.values()) {
                mAtmosMachine.routine();
            }
		}

	}

	void evaluate() {
		if(this.isRunning) {

            for (MAtmosDynamic mAtmosDynamic : this.dynamics.values()) {
                mAtmosDynamic.evaluate();
            }

            for (MAtmosCondition mAtmosCondition : this.conditions.values()) {
                mAtmosCondition.evaluate();
            }

            for (MAtmosConditionSet mAtmosConditionSet : this.csets.values()) {
                mAtmosConditionSet.evaluate();
            }

            for (MAtmosMachine mAtmosMachine : this.machines.values()) {
                mAtmosMachine.evaluate();
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
			this.dynamics.get(var9).serialize(var4);
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
			this.lists.get(var9).serialize(var4);
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
			this.conditions.get(var9).serialize(var4);
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
			this.csets.get(var9).serialize(var4);
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
			this.events.get(var9).serialize(var4);
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
			this.machines.get(var9).serialize(var4);
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
