package matmos.engine;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;

public abstract class MAtmosDescriptible {
	public String nickname = "";
	public String description = "";
	public String icon = "";
	public String meta = "";

	public abstract String serialize(XMLEventWriter var1) throws XMLStreamException;

	protected void buildDescriptibleSerialized(XMLEventWriter var1) throws XMLStreamException {
		XMLEventFactory factory = XMLEventFactory.newInstance();
		DTD space = factory.createDTD("\n");
		DTD seq = factory.createDTD("\t");
		var1.add(seq);
		var1.add(factory.createStartElement("", "", "descriptible"));
		var1.add(space);
		this.createNode(var1, "nickname", this.nickname, 2);
		this.createNode(var1, "description", this.description, 2);
		this.createNode(var1, "icon", this.icon, 2);
		this.createNode(var1, "meta", this.meta, 2);
		var1.add(seq);
		var1.add(factory.createEndElement("", "", "descriptible"));
		var1.add(space);
	}

	protected void createNode(XMLEventWriter var1, String var2, String var3, int var4) throws XMLStreamException {
		XMLEventFactory factory = XMLEventFactory.newInstance();
		DTD var6 = factory.createDTD("\t");
		DTD var7 = factory.createDTD("\n");

		for(int var8 = 0; var8 < var4; ++var8) {
			var1.add(var6);
		}

		var1.add(factory.createStartElement("", "", var2));
		var1.add(factory.createCharacters(var3));
		var1.add(factory.createEndElement("", "", var2));
		var1.add(var7);
	}

	protected void createNode(XMLEventWriter var1, String var2, String var3) throws XMLStreamException {
		this.createNode(var1, var2, var3, 1);
	}
}
