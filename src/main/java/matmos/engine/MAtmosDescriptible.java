package matmos.engine;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;

public abstract class MAtmosDescriptible {
	public String nickname = new String();
	public String description = new String();
	public String icon = new String();
	public String meta = new String();

	public abstract String serialize(XMLEventWriter var1) throws XMLStreamException;

	protected void buildDescriptibleSerialized(XMLEventWriter var1) throws XMLStreamException {
		XMLEventFactory var2 = XMLEventFactory.newInstance();
		DTD var3 = var2.createDTD("\n");
		DTD var4 = var2.createDTD("\t");
		var1.add(var4);
		var1.add(var2.createStartElement("", "", "descriptible"));
		var1.add(var3);
		this.createNode(var1, "nickname", this.nickname, 2);
		this.createNode(var1, "description", this.description, 2);
		this.createNode(var1, "icon", this.icon, 2);
		this.createNode(var1, "meta", this.meta, 2);
		var1.add(var4);
		var1.add(var2.createEndElement("", "", "descriptible"));
		var1.add(var3);
	}

	protected void createNode(XMLEventWriter var1, String var2, String var3, int var4) throws XMLStreamException {
		XMLEventFactory var5 = XMLEventFactory.newInstance();
		DTD var6 = var5.createDTD("\t");
		DTD var7 = var5.createDTD("\n");

		for(int var8 = 0; var8 < var4; ++var8) {
			var1.add(var6);
		}

		var1.add(var5.createStartElement("", "", var2));
		var1.add(var5.createCharacters(var3));
		var1.add(var5.createEndElement("", "", var2));
		var1.add(var7);
	}

	protected void createNode(XMLEventWriter var1, String var2, String var3) throws XMLStreamException {
		this.createNode(var1, var2, var3, 1);
	}
}
