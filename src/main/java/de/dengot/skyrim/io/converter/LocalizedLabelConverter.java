package de.dengot.skyrim.io.converter;

import java.util.SortedSet;
import java.util.TreeSet;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.dengot.skyrim.model.LocalizedLabel;

public class LocalizedLabelConverter implements Converter {

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return LocalizedLabel.class.isAssignableFrom(type);
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		LocalizedLabel locLabel = (LocalizedLabel) source;

		writer.addAttribute("Key", locLabel.getKey());
		
		SortedSet<String> sortedLangs = new TreeSet<String>(locLabel.getLanguages());
		for (String lang : sortedLangs) {
			writer.startNode("Localization");
			writer.addAttribute("Lang", lang);
			writer.addAttribute("Text", locLabel.getText(lang));
			writer.endNode();
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String key = reader.getAttribute("Key");
		LocalizedLabel locLabel = new LocalizedLabel(key);
		
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String lang = reader.getAttribute("Lang");
			String translation = reader.getAttribute("Text");
			locLabel.setText(lang, translation);
			reader.moveUp();
		}
		return locLabel;
	}

}
