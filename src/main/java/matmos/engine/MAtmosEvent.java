package matmos.engine;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

public class MAtmosEvent extends MAtmosDescriptible {
	MAtmosKnowledge knowledge;
	public ArrayList paths = new ArrayList();
	public float volMin;
	public float volMax;
	public float pitchMin;
	public float pitchMax;
	public int metaSound;

	MAtmosEvent(MAtmosKnowledge var1) {
		this.knowledge = var1;
		this.volMin = 1.0F;
		this.volMax = 1.0F;
		this.pitchMin = 1.0F;
		this.pitchMax = 1.0F;
		this.metaSound = 0;
	}

	void setKnowledge(MAtmosKnowledge var1) {
		this.knowledge = var1;
	}

	public void cacheSounds() {
		Iterator var1 = this.paths.iterator();

		while(var1.hasNext()) {
			this.knowledge.soundManager.cacheSound((String)var1.next());
		}

	}

	public void playSound(float var1, float var2) {
		if(!this.paths.isEmpty()) {
			float var3 = this.volMax - this.volMin;
			float var4 = this.pitchMax - this.pitchMin;
			var3 = this.volMin + (var3 > 0.0F ? this.knowledge.random.nextFloat() * var3 : 0.0F);
			var4 = this.pitchMin + (var4 > 0.0F ? this.knowledge.random.nextFloat() * var4 : 0.0F);
			String var5 = (String)this.paths.get(this.knowledge.random.nextInt(this.paths.size()));
			var3 *= var1;
			var4 *= var2;
			this.knowledge.soundManager.playSound(var5, var3, var4, this.metaSound);
		}

	}

	public String serialize(XMLEventWriter var1) throws XMLStreamException {
		this.buildDescriptibleSerialized(var1);
		Iterator var2 = this.paths.iterator();

		while(var2.hasNext()) {
			this.createNode(var1, "path", (String)var2.next());
		}

		this.createNode(var1, "volmin", this.volMin + "");
		this.createNode(var1, "volmax", this.volMax + "");
		this.createNode(var1, "pitchmin", this.pitchMin + "");
		this.createNode(var1, "pitchmax", this.pitchMax + "");
		this.createNode(var1, "metasound", this.metaSound + "");
		return "";
	}
}
