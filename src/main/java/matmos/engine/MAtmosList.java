package matmos.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

public class MAtmosList extends MAtmosDescriptible {
	ArrayList<Integer> list = new ArrayList<>();

	public ArrayList<Integer> getList() {
		return this.list;
	}

	public boolean contains(int var1) {
		return this.list.contains(var1);
	}

	public void add(int var1) {
		if(!this.list.contains(var1)) {
			this.list.add(var1);
			Collections.sort(this.list);
		}

	}

	public void remove(int var1) {
		this.list.remove(var1);
	}

	public void clear() {
		this.list.clear();
	}

	public String serialize(XMLEventWriter var1) throws XMLStreamException {
		this.buildDescriptibleSerialized(var1);

        for (Integer integer : this.list) {
            this.createNode(var1, "constant", integer.toString());
        }

		return null;
	}
}
