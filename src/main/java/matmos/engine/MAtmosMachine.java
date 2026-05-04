package matmos.engine;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

public class MAtmosMachine extends MAtmosSwitchable {
	ArrayList anyallows = new ArrayList();
	ArrayList anyrestricts = new ArrayList();
	ArrayList etimes = new ArrayList();
	ArrayList streams = new ArrayList();
	private boolean powered = false;
	private boolean switchedOn = false;

	MAtmosMachine(MAtmosKnowledge var1) {
		super(var1);
	}

	public void routine() {
		Iterator var1;
		if(this.switchedOn) {
			var1 = this.etimes.iterator();

			while(var1.hasNext()) {
				MAtmosEventTimed var2 = (MAtmosEventTimed)var1.next();
				var2.routine();
			}
		}

		if(this.powered && !this.streams.isEmpty()) {
			var1 = this.streams.iterator();

			while(var1.hasNext()) {
				((MAtmosStream)var1.next()).routine();
			}
		}

	}

	public void turnOn() {
		if(this.powered && !this.switchedOn) {
			this.switchedOn = true;
			Iterator var1 = this.etimes.iterator();

			while(var1.hasNext()) {
				((MAtmosEventTimed)var1.next()).restart();
			}

			var1 = this.streams.iterator();

			while(var1.hasNext()) {
				((MAtmosStream)var1.next()).signalPlayable();
			}
		}

	}

	public void turnOff() {
		if(this.powered && this.switchedOn) {
			this.switchedOn = false;
			Iterator var1 = this.streams.iterator();

			while(var1.hasNext()) {
				((MAtmosStream)var1.next()).signalStoppable();
			}
		}

	}

	public void powerOn() {
		this.powered = true;
	}

	public void powerOff() {
		Iterator var1 = this.streams.iterator();

		while(var1.hasNext()) {
			((MAtmosStream)var1.next()).clearToken();
		}

		this.turnOff();
		this.powered = false;
	}

	public boolean isPowered() {
		return this.powered;
	}

	public boolean isOn() {
		return this.switchedOn;
	}

	public ArrayList getAllows() {
		return this.anyallows;
	}

	public ArrayList getRestricts() {
		return this.anyrestricts;
	}

	public void addAllow(String var1) {
		this.anyallows.add(var1);
		this.flagNeedsTesting();
	}

	public void addRestrict(String var1) {
		this.anyrestricts.add(var1);
		this.flagNeedsTesting();
	}

	public void removeSet(String var1) {
		this.anyallows.remove(var1);
		this.anyrestricts.remove(var1);
		this.flagNeedsTesting();
	}

	public void replaceSetName(String var1, String var2) {
		if(this.anyallows.contains(var1)) {
			this.anyallows.add(var2);
			this.anyallows.remove(var1);
		}

		if(this.anyrestricts.contains(var1)) {
			this.anyrestricts.add(var2);
			this.anyrestricts.remove(var1);
		}

		this.flagNeedsTesting();
	}

	public ArrayList getEventsTimed() {
		return this.etimes;
	}

	public int addEventTimed() {
		this.etimes.add(new MAtmosEventTimed(this));
		return this.etimes.size();
	}

	public int removeEventTimed(int var1) {
		this.etimes.remove(var1);
		return this.etimes.size();
	}

	public MAtmosEventTimed getEventTimed(int var1) {
		return (MAtmosEventTimed)this.etimes.get(var1);
	}

	public ArrayList getStreams() {
		return this.streams;
	}

	public int addStream() {
		this.streams.add(new MAtmosStream(this));
		return this.streams.size();
	}

	public int removeStream(int var1) {
		this.streams.remove(var1);
		return this.streams.size();
	}

	public MAtmosStream getStream(int var1) {
		return (MAtmosStream)this.streams.get(var1);
	}

	protected boolean testIfValid() {
		if(this.anyallows.size() == 0) {
			return false;
		} else {
			Iterator var1 = this.anyallows.iterator();

			while(var1.hasNext()) {
				String var2 = (String)var1.next();
				if(!this.knowledge.csets.containsKey(var2)) {
					return false;
				}
			}

			Iterator var3 = this.anyrestricts.iterator();

			while(var3.hasNext()) {
				String var4 = (String)var3.next();
				if(!this.knowledge.csets.containsKey(var4)) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean evaluate() {
		if(!this.isValid()) {
			return false;
		} else if(!this.powered) {
			return false;
		} else {
			boolean var1 = this.switchedOn;
			boolean var2 = this.testIfTrue();
			if(var1 != var2) {
				if(var2) {
					this.turnOn();
				} else {
					this.turnOff();
				}

				MAtmosLogger.notice("(MAtmos) M:" + this.nickname + (this.switchedOn ? " now On." : " now Off."));
			}

			return this.switchedOn;
		}
	}

	public boolean isActive() {
		return this.isTrue();
	}

	public boolean isTrue() {
		return this.switchedOn;
	}

	public boolean testIfTrue() {
		if(!this.isValid()) {
			return false;
		} else {
			boolean var1 = false;
			Iterator var2 = this.anyallows.iterator();

			while(!var1 && var2.hasNext()) {
				String var3 = (String)var2.next();
				if(((MAtmosConditionSet)this.knowledge.csets.get(var3)).isTrue()) {
					var1 = true;
				}
			}

			Iterator var5 = this.anyrestricts.iterator();

			while(var1 && var5.hasNext()) {
				String var4 = (String)var5.next();
				if(((MAtmosConditionSet)this.knowledge.csets.get(var4)).isTrue()) {
					var1 = false;
				}
			}

			return var1;
		}
	}

	public String serialize(XMLEventWriter var1) throws XMLStreamException {
		this.buildDescriptibleSerialized(var1);
		Iterator var2 = this.anyallows.iterator();

		while(var2.hasNext()) {
			this.createNode(var1, "allow", (String)var2.next());
		}

		var2 = this.anyrestricts.iterator();

		while(var2.hasNext()) {
			this.createNode(var1, "restrict", (String)var2.next());
		}

		var2 = this.etimes.iterator();

		while(var2.hasNext()) {
			((MAtmosEventTimed)var2.next()).serialize(var1);
		}

		var2 = this.streams.iterator();

		while(var2.hasNext()) {
			((MAtmosStream)var2.next()).serialize(var1);
		}

		return "";
	}
}
