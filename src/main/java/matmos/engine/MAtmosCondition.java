package matmos.engine;

import java.util.ArrayList;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

public class MAtmosCondition extends MAtmosSwitchable {
	String sheet = "";
	int key = 0;
	String dynamicKey = "";
	int conditionType = 0;
	int constant;
	String list = "";
	boolean isTrueEvaluated;

	MAtmosCondition(MAtmosKnowledge var1) {
		super(var1);
	}

	public void setSheet(String var1) {
		this.sheet = var1;
		this.flagNeedsTesting();
	}

	public void setKey(int var1) {
		this.key = var1;
		this.flagNeedsTesting();
	}

	public void setDynamic(String var1) {
		this.key = -1;
		this.dynamicKey = var1;
		this.sheet = "";
		this.flagNeedsTesting();
	}

	public void setSymbol(String var1) {
		this.conditionType = -1;
		if(var1.equals("!=")) {
			this.conditionType = 0;
		} else if(var1.equals("==")) {
			this.conditionType = 1;
		} else if(var1.equals(">")) {
			this.conditionType = 2;
		} else if(var1.equals(">=")) {
			this.conditionType = 3;
		} else if(var1.equals("<")) {
			this.conditionType = 4;
		} else if(var1.equals("<=")) {
			this.conditionType = 5;
		} else if(var1.equals("in")) {
			this.conditionType = 6;
		} else if(var1.equals("!in")) {
			this.conditionType = 7;
		}

		this.flagNeedsTesting();
	}

	public void setConstant(int var1) {
		this.constant = var1;
		this.flagNeedsTesting();
	}

	public void setList(String var1) {
		this.list = var1;
		this.flagNeedsTesting();
	}

	public boolean isDynamic() {
		return this.key == -1;
	}

	public String getSheet() {
		return this.sheet;
	}

	public int getKey() {
		return this.key;
	}

	public String getDynamic() {
		return this.dynamicKey;
	}

	public String getList() {
		return this.list;
	}

	public int getConditionType() {
		return this.conditionType;
	}

	public int getConstant() {
		return this.constant;
	}

	protected boolean testIfValid() {
		if(this.conditionType == -1) {
			return false;
		} else {
			boolean var1 = false;
			if(!this.isDynamic()) {
				if(this.knowledge.data.sheets.containsKey(this.sheet) && this.key >= 0 && this.key < ((ArrayList)this.knowledge.data.sheets.get(this.sheet)).size()) {
					var1 = true;
				}
			} else if(this.knowledge.dynamics.containsKey(this.dynamicKey)) {
				var1 = true;
			}

			if(var1 && (this.conditionType == 6 || this.conditionType == 7)) {
				var1 = this.knowledge.lists.containsKey(this.list);
			}

			return var1;
		}
	}

	public boolean evaluate() {
		if(!this.isValid()) {
			return false;
		} else {
			boolean var1 = this.isTrueEvaluated;
			this.isTrueEvaluated = this.testIfTrue();
			if(var1 != this.isTrueEvaluated) {
				MAtmosLogger.notice("(MAtmos) C:" + this.nickname + (this.isTrueEvaluated ? " now On." : " now Off."));
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
			int var1;
			if(!this.isDynamic()) {
				var1 = ((Integer)((ArrayList)this.knowledge.data.sheets.get(this.sheet)).get(this.key)).intValue();
			} else {
				var1 = ((MAtmosDynamic)this.knowledge.dynamics.get(this.dynamicKey)).value;
			}

			return this.conditionType == 0 ? var1 != this.constant : (this.conditionType == 1 ? var1 == this.constant : (this.conditionType == 2 ? var1 > this.constant : (this.conditionType == 3 ? var1 >= this.constant : (this.conditionType == 4 ? var1 < this.constant : (this.conditionType == 5 ? var1 <= this.constant : (this.conditionType == 6 ? ((MAtmosList)this.knowledge.lists.get(this.list)).contains(var1) : (this.conditionType == 7 ? !((MAtmosList)this.knowledge.lists.get(this.list)).contains(var1) : false)))))));
		}
	}

	public String serialize(XMLEventWriter var1) throws XMLStreamException {
		this.buildDescriptibleSerialized(var1);
		if(!this.isDynamic()) {
			this.createNode(var1, "sheet", this.sheet);
			this.createNode(var1, "key", "" + this.key);
		} else {
			this.createNode(var1, "key", "" + this.key);
			this.createNode(var1, "dynamickey", this.dynamicKey);
		}

		if(this.conditionType == 0) {
			this.createNode(var1, "symbol", "!=");
		} else if(this.conditionType == 1) {
			this.createNode(var1, "symbol", "==");
		} else if(this.conditionType == 2) {
			this.createNode(var1, "symbol", ">");
		} else if(this.conditionType == 3) {
			this.createNode(var1, "symbol", ">=");
		} else if(this.conditionType == 4) {
			this.createNode(var1, "symbol", "<");
		} else if(this.conditionType == 5) {
			this.createNode(var1, "symbol", "<=");
		} else if(this.conditionType == 6) {
			this.createNode(var1, "symbol", "in");
		} else if(this.conditionType == 7) {
			this.createNode(var1, "symbol", "!in");
		} else {
			this.createNode(var1, "symbol", "><");
		}

		this.createNode(var1, "constant", "" + this.constant);
		this.createNode(var1, "list", "" + this.list);
		return "";
	}

	public void replaceDynamicName(String var1, String var2) {
		if(this.isDynamic()) {
			if(this.dynamicKey.equals(var1)) {
				this.dynamicKey = var2;
			}

			this.flagNeedsTesting();
		}

	}

	public void replaceListName(String var1, String var2) {
		if(this.list.equals(var1)) {
			this.list = var2;
		}

		this.flagNeedsTesting();
	}
}
