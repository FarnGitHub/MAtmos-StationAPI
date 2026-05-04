package matmos.engine;

public abstract class MAtmosSwitchable extends MAtmosDescriptible {
	MAtmosKnowledge knowledge;
	boolean needsTesting;
	boolean isValid;

	MAtmosSwitchable(MAtmosKnowledge var1) {
		this.knowledge = var1;
		this.isValid = false;
		this.needsTesting = true;
	}

	public abstract boolean isActive();

	public void flagNeedsTesting() {
		this.needsTesting = true;
	}

	public void setKnowledge(MAtmosKnowledge var1) {
		this.knowledge = var1;
		this.flagNeedsTesting();
	}

	public boolean isValid() {
		this.validateUsability();
		return this.isValid;
	}

	private void validateUsability() {
		if(this.needsTesting) {
			this.isValid = this.testIfValid();
			this.needsTesting = false;
		}

	}

	protected abstract boolean testIfValid();
}
