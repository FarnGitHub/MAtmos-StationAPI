package ha3;

import farn.matmos.MatmosMod;
import net.minecraft.client.option.KeyBinding;

public abstract class Ha3KeyBinding {
	private int time = 0;
	private int diffKey = 0;
	private boolean pending = false;
	public KeyBinding key;
	public MatmosMod mod;

	public Ha3KeyBinding(KeyBinding var1) {
		this.key = var1;
	}

	void setupMod(MatmosMod var1) {
		this.mod = var1;
	}

	void handleBefore() {
		if(this.time == 0) {
			this.doBefore();
		}

		this.pending = true;
		this.diffKey = 0;
		++this.time;
	}

	void handle() {
		if(this.pending) {
			++this.diffKey;
			if(this.diffKey > 1) {
				this.doAfter(this.time);
				this.pending = false;
				this.time = 0;
			} else {
				this.doDuring(this.time);
			}
		}

	}

	protected abstract void doBefore();

	protected abstract void doDuring(int var1);

	protected abstract void doAfter(int var1);
}
