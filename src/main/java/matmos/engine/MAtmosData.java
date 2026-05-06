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

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MAtmosData {
	public HashMap<String, ArrayList<Integer>> sheets = new HashMap<>();
	public int updateVersion = 0;

	public void flagUpdate() {
		++this.updateVersion;
	}

	public String createXML() throws XMLStreamException {
		StreamResult result = new StreamResult(new StringWriter());
		XMLOutputFactory output = XMLOutputFactory.newInstance();
		XMLEventFactory event = XMLEventFactory.newInstance();
		XMLEventWriter writer = output.createXMLEventWriter(result);
		DTD space1 = event.createDTD("\n");
		DTD seq = event.createDTD("\t");
		DTD space2 = event.createDTD("\n");
		writer.add(event.createStartDocument());
		writer.add(space1);
		writer.add(event.createStartElement("", "", "contents"));

        for (Entry<String, ArrayList<Integer>> entry : this.sheets.entrySet()) {
            writer.add(space1);
            writer.add(event.createStartElement("", "", "sheet"));
            writer.add(event.createAttribute("name", entry.getKey()));
            writer.add(event.createAttribute("size", entry.getValue().size() + ""));
            writer.add(space1);
            int index = 0;

            for (Iterator<Integer> var11 = entry.getValue().iterator(); var11.hasNext(); ++index) {
                writer.add(seq);
                writer.add(event.createStartElement("", "", "key"));
                writer.add(event.createAttribute("id", index + ""));
                writer.add(event.createCharacters(var11.next().toString()));
                writer.add(event.createEndElement("", "", "key"));
                writer.add(space1);
            }

            writer.add(event.createEndElement("", "", "sheet"));
        }

		writer.add(space1);
		writer.add(event.createEndElement("", "", "contents"));
		writer.add(space2);
		writer.add(event.createEndDocument());
		writer.close();
		return result.getWriter().toString();
	}
}
