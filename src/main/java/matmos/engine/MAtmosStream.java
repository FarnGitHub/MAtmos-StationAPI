package matmos.engine;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;

public class MAtmosStream extends MAtmosDescriptible {
	MAtmosMachine machine;
	int token;
	public String path = "";
	public float volume;
	public float pitch;
	public float fadeInTime;
	public float fadeOutTime;
	public float delayBeforeFadeIn;
	public float delayBeforeFadeOut;
	public boolean isLooping;
	public boolean isUsingPause;
	boolean isTurnedOn;
	boolean isPlaying;
	long startTime;
	long stopTime;
	boolean firstCall;

	MAtmosStream(MAtmosMachine var1) {
		this.machine = var1;
		this.volume = 1.0F;
		this.pitch = 1.0F;
		this.fadeInTime = 0.0F;
		this.fadeOutTime = 0.0F;
		this.delayBeforeFadeIn = 0.0F;
		this.delayBeforeFadeOut = 0.0F;
		this.isLooping = true;
		this.isUsingPause = false;
		this.isTurnedOn = false;
		this.isPlaying = false;
		this.firstCall = true;
		this.token = -1;
		this.startTime = 0L;
		this.stopTime = 0L;
	}

	void setMachine(MAtmosMachine var1) {
		this.machine = var1;
	}

	public void signalPlayable() {
		if(!this.isTurnedOn) {
			this.startTime = this.machine.knowledge.getTimeMillis() + (long)(this.delayBeforeFadeIn * 1000.0F);
			this.isTurnedOn = true;
		}

	}

	public void signalStoppable() {
		if(this.isTurnedOn) {
			this.stopTime = this.machine.knowledge.getTimeMillis() + (long)(this.delayBeforeFadeOut * 1000.0F);
			this.isTurnedOn = false;
		}

	}

	public void clearToken() {
		if(!this.firstCall) {
			this.machine.knowledge.soundManager.eraseStreamingToken(this.token);
			this.isPlaying = false;
			this.token = -1;
			this.firstCall = true;
		}

	}

	public void routine() {
		if(this.isLooping || !this.isUsingPause) {
			if(this.isTurnedOn && !this.isPlaying) {
				if(this.machine.knowledge.getTimeMillis() > this.startTime) {
					this.isPlaying = true;
					if(this.firstCall) {
						this.token = this.machine.knowledge.soundManager.getNewStreamingToken();
						this.machine.knowledge.soundManager.setupStreamingToken(this.token, this.path, this.volume, this.pitch);
						this.firstCall = false;
					}

					this.machine.knowledge.soundManager.startStreaming(this.token, this.fadeInTime, this.isLooping ? 0 : 1);
				}
			} else if(!this.isTurnedOn && this.isPlaying && this.machine.knowledge.getTimeMillis() > this.stopTime) {
				this.isPlaying = false;
				if(!this.isUsingPause) {
					this.machine.knowledge.soundManager.stopStreaming(this.token, this.fadeOutTime);
				} else {
					this.machine.knowledge.soundManager.pauseStreaming(this.token, this.fadeOutTime);
				}
			}
		}

	}

	public String serialize(XMLEventWriter var1) throws XMLStreamException {
		XMLEventFactory var2 = XMLEventFactory.newInstance();
		DTD var3 = var2.createDTD("\n");
		DTD var4 = var2.createDTD("\t");
		var1.add(var4);
		var1.add(var2.createStartElement("", "", "stream"));
		var1.add(var3);
		this.createNode(var1, "path", this.path, 2);
		this.createNode(var1, "volume", "" + this.volume, 2);
		this.createNode(var1, "pitch", "" + this.pitch, 2);
		this.createNode(var1, "fadeintime", "" + this.fadeInTime, 2);
		this.createNode(var1, "fadeouttime", "" + this.fadeOutTime, 2);
		this.createNode(var1, "delaybeforefadein", "" + this.delayBeforeFadeIn, 2);
		this.createNode(var1, "delaybeforefadeout", "" + this.delayBeforeFadeOut, 2);
		this.createNode(var1, "islooping", this.isLooping ? "1" : "0", 2);
		this.createNode(var1, "isusingpause", this.isUsingPause ? "1" : "0", 2);
		var1.add(var4);
		var1.add(var2.createEndElement("", "", "stream"));
		var1.add(var3);
		return "";
	}
}
