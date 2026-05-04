package matmos.engine;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.transform.stream.StreamResult;

public class MAtmosData {
	public HashMap<String, ArrayList<Integer>> sheets = new HashMap();
	public int updateVersion = 0;

	public void flagUpdate() {
		++this.updateVersion;
	}

	public String createXML() throws XMLStreamException {
		StreamResult var1 = new StreamResult(new StringWriter());
		XMLOutputFactory var2 = XMLOutputFactory.newInstance();
		XMLEventFactory var3 = XMLEventFactory.newInstance();
		XMLEventWriter var4 = var2.createXMLEventWriter(var1);
		DTD var5 = var3.createDTD("\n");
		DTD var6 = var3.createDTD("\t");
		DTD var7 = var3.createDTD("\n");
		var4.add(var3.createStartDocument());
		var4.add(var5);
		var4.add(var3.createStartElement("", "", "contents"));
		Iterator var8 = this.sheets.entrySet().iterator();

		while(var8.hasNext()) {
			Entry var9 = (Entry)var8.next();
			var4.add(var5);
			var4.add(var3.createStartElement("", "", "sheet"));
			var4.add(var3.createAttribute("name", (String)var9.getKey()));
			var4.add(var3.createAttribute("size", ((ArrayList)var9.getValue()).size() + ""));
			var4.add(var5);
			int var10 = 0;

			for(Iterator var11 = ((ArrayList)var9.getValue()).iterator(); var11.hasNext(); ++var10) {
				var4.add(var6);
				var4.add(var3.createStartElement("", "", "key"));
				var4.add(var3.createAttribute("id", var10 + ""));
				var4.add(var3.createCharacters(((Integer)var11.next()).toString()));
				var4.add(var3.createEndElement("", "", "key"));
				var4.add(var5);
			}

			var4.add(var3.createEndElement("", "", "sheet"));
		}

		var4.add(var5);
		var4.add(var3.createEndElement("", "", "contents"));
		var4.add(var7);
		var4.add(var3.createEndDocument());
		var4.close();
		return var1.getWriter().toString();
	}
}
