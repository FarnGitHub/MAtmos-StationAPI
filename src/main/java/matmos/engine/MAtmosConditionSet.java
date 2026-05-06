package matmos.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MAtmosConditionSet extends MAtmosSwitchable {
	HashMap<String, Boolean> conditions = new HashMap<>();
	private boolean isTrueEvaluated = false;

	MAtmosConditionSet(MAtmosKnowledge var1) {
		super(var1);
	}

	protected boolean testIfValid() {
		if(this.conditions.isEmpty()) {
			return false;
		} else {

            for (String var2 : this.conditions.keySet()) {
                if (!this.knowledge.conditions.containsKey(var2)) {
                    return false;
                }
            }

			return true;
		}
	}

	public void replaceConditionName(String var1, String var2) {
		this.flagNeedsTesting();
		if(this.conditions.containsKey(var1)) {
			this.conditions.put(var2, this.conditions.get(var1));
			this.conditions.remove(var1);
		}

	}

	public void setSet(Object... var1) throws IllegalArgumentException {
		this.flagNeedsTesting();
		if(var1.length % 2 != 0) {
			this.conditions.clear();
			throw new IllegalArgumentException();
		} else {
			this.conditions.clear();

			for(int var2 = 0; var2 < var1.length / 2; ++var2) {
				this.conditions.put((String)var1[var2], (Boolean)var1[var2 + 1]);
			}

		}
	}

	public void addCondition(String var1, boolean var2) throws IllegalArgumentException {
		this.flagNeedsTesting();
		this.conditions.put(var1, var2);
	}

	public void removeCondition(String var1) {
		this.flagNeedsTesting();
		this.conditions.remove(var1);
	}

	public HashMap<String, Boolean> getSet() {
		return this.conditions;
	}

	public boolean evaluate() {
		if(!this.isValid()) {
			return false;
		} else {
			boolean var1 = this.isTrueEvaluated;
			this.isTrueEvaluated = this.testIfTrue();
			if(var1 != this.isTrueEvaluated) {
				MAtmosLogger.notice("(MAtmos) S:" + this.nickname + (this.isTrueEvaluated ? " now On." : " now Off."));
			}

			return this.isTrueEvaluated;
		}
	}

	public boolean isActive() {
		return this.isTrue();
	}

	public boolean isTrue() {
		return this.isTrueEvaluated;
	}

	public boolean testIfTrue() {
		if(!this.isValid()) {
			return false;
		} else {
			boolean var1 = true;
			Iterator<Entry<String, Boolean>> var2 = this.conditions.entrySet().iterator();

			while(var1 && var2.hasNext()) {
				Entry<String, Boolean> var3 = var2.next();
				if(var3.getValue() != ((MAtmosCondition)this.knowledge.conditions.get(var3.getKey())).isTrue()) {
					var1 = false;
				}
			}

			return var1;
		}
	}

	public String serialize(XMLEventWriter var1) throws XMLStreamException {
		this.buildDescriptibleSerialized(var1);

        for (Entry<String, Boolean> entry : this.conditions.entrySet()) {
            if (entry.getValue()) {
                this.createNode(var1, "truepart", entry.getKey());
            } else {
                this.createNode(var1, "falsepart", entry.getKey());
            }
        }

		return "";
	}
}
