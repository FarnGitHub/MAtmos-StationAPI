package matmos.engine;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MAtmosMachine extends MAtmosSwitchable {
	ArrayList<String> anyallows = new ArrayList<>();
	ArrayList<String> anyrestricts = new ArrayList<>();
	ArrayList<MAtmosEventTimed> etimes = new ArrayList<>();
	ArrayList<MAtmosStream> streams = new ArrayList<>();
	private boolean powered = false;
	private boolean switchedOn = false;

	MAtmosMachine(MAtmosKnowledge var1) {
		super(var1);
	}

	public void routine() {
		if(this.switchedOn) {

            for (MAtmosEventTimed var2 : this.etimes) {
                var2.routine();
            }
		}

		if(this.powered && !this.streams.isEmpty()) {

            for (MAtmosStream stream : this.streams) {
                stream.routine();
            }
		}

	}

	public void turnOn() {
		if(this.powered && !this.switchedOn) {
			this.switchedOn = true;

            for (MAtmosEventTimed etime : this.etimes) {
                etime.restart();
            }

            for (MAtmosStream stream : this.streams) {
                stream.signalPlayable();
            }
		}

	}

	public void turnOff() {
		if(this.powered && this.switchedOn) {
			this.switchedOn = false;

            for (MAtmosStream stream : this.streams) {
                stream.signalStoppable();
            }
		}

	}

	public void powerOn() {
		this.powered = true;
	}

	public void powerOff() {

        for (MAtmosStream stream : this.streams) {
            stream.clearToken();
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

	public ArrayList<String> getAllows() {
		return this.anyallows;
	}

	public ArrayList<String> getRestricts() {
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

	public ArrayList<MAtmosEventTimed> getEventsTimed() {
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
		return this.etimes.get(var1);
	}

	public ArrayList<MAtmosStream> getStreams() {
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
		return this.streams.get(var1);
	}

	protected boolean testIfValid() {
		if(this.anyallows.isEmpty()) {
			return false;
		} else {

            for (String var2 : this.anyallows) {
                if (!this.knowledge.csets.containsKey(var2)) {
                    return false;
                }
            }

            for (String var4 : this.anyrestricts) {
                if (!this.knowledge.csets.containsKey(var4)) {
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
			Iterator<String> var2 = this.anyallows.iterator();

			while(!var1 && var2.hasNext()) {
				String var3 = var2.next();
				if(this.knowledge.csets.get(var3).isTrue()) {
					var1 = true;
				}
			}

			Iterator<String> var5 = this.anyrestricts.iterator();

			while(var1 && var5.hasNext()) {
				String var4 = var5.next();
				if(this.knowledge.csets.get(var4).isTrue()) {
					var1 = false;
				}
			}

			return var1;
		}
	}

	public String serialize(XMLEventWriter var1) throws XMLStreamException {
		this.buildDescriptibleSerialized(var1);

        for (String anyallow : this.anyallows) {
            this.createNode(var1, "allow", anyallow);
        }

        for (String anyrestrict : this.anyrestricts) {
            this.createNode(var1, "restrict", anyrestrict);
        }

        for (MAtmosEventTimed etime : this.etimes) {
            etime.serialize(var1);
        }

        for (MAtmosStream stream : this.streams) {
            stream.serialize(var1);
        }

		return "";
	}
}
