package de.jalin.formular.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jalin.formular.Field;
import de.jalin.formular.FieldType;
import de.jalin.formular.Form;
import de.jalin.formular.FormError;
import de.jalin.formular.Label;

public class Parser {

	public Form parse(final Reader reader, final String applicationName, final String formName) throws FormError {
		final BufferedReader buffer = new BufferedReader(reader);
		final Form form = new Form(applicationName, formName);
		parseLayout(buffer, form);
		parseDefinitions(buffer, form);
		close(buffer);
		return form;
	}
	
	private void parseLayout(final BufferedReader buffer, final Form form) throws FormError {
		try {
			String line = buffer.readLine();
			while (line != null && line.length() == 0) {
				line = buffer.readLine();
			}
			int lineCount = 0;
			while (line != null && line.length() > 0) {
				form.addRow();
				final int len = line.length();
				int pos = 0;
				while (pos < len) {
					final char charAt = line.charAt(pos);
					switch (charAt) {
					case '[':
						final int endIdx = line.indexOf(']', pos) + 1;
						final Field fld = parseField(line.substring(pos, endIdx));
						fld.setXC(pos);
						fld.setYC(lineCount);
						form.addField(lineCount, fld);
						pos = endIdx;
						break;
					case ' ':
					case '\t':
						pos++;
						break;
					default:
						final Label lbl = parseLabel(line.substring(pos));
						lbl.setXC(pos);
						lbl.setYC(lineCount);
						form.addLabel(lineCount, lbl);
						pos += lbl.length();
						break;
					}
				}
				line = buffer.readLine();
				lineCount++;
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
		
	}

	private Label parseLabel(final String label) {
		final Pattern pattern = Pattern.compile("[^\\s\\[]*(\\s[^\\s\\[]+)*");
		final Matcher matcher = pattern.matcher(label);
		if (matcher.lookingAt()) {
			final String group = matcher.group();
			return new Label(group);
		}
		return new Label("");
	}

	private Field parseField(final String field) throws FormError {
		final Pattern pattern = Pattern.compile("[0-9]+");
		final Matcher matcher = pattern.matcher(field);
		if (matcher.find()) {
			final String group = matcher.group();
			final int fieldId = Integer.parseInt(group);
			return new Field(fieldId, field.length());
		}
		throw new FormError(FormError.FIELD_ID_MISSING);
	}
	
	private void parseDefinitions(final BufferedReader buffer, final Form form) throws FormError {
		try {
			String line = buffer.readLine();
			while (line != null && line.length() > 0) {
				line = line.trim();
				final int id = parseFieldId(line);
				final Field field = form.getField(id);
				field.setName(parseFieldName(line));
				final FieldType fieldType = parseFieldType(line);
				if (FieldType.SELECT.equals(fieldType)) {
					field.addSelectValues(parseSelectValues(line));
				}
				field.setType(fieldType);
				line = buffer.readLine();
			}
			form.checkForErrors();
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	private List<String> parseSelectValues(final String line) {
		final String typeString = line.substring(line.indexOf(':') + 1).trim();
		if (typeString.startsWith(FieldType.SELECT.type())) {
			final int begin = typeString.indexOf('('); 
			final int end = typeString.lastIndexOf(')');
			final String params = typeString.substring(begin+1, end);
			final String[] splitedValues = params.split(",");
			final List<String> parsedValues = new ArrayList<String>();
			for (final String val : splitedValues) {
				final String trimmed = val.trim();
				if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
					parsedValues.add(trimmed.substring(1, trimmed.length()-1));
				} else {
					parsedValues.add(trimmed);
				}
			}
			return parsedValues;
			
		}
		return Arrays.asList(new String[] { "none" }) ;
	}

	private FieldType parseFieldType(final String line) {
		final int colonPos = line.indexOf(':');
		if (colonPos > 0) {
			final String typeString = line.substring(colonPos + 1).trim();
			for (final FieldType ft : FieldType.values()) {
				if (typeString.startsWith(ft.type())) {
					return ft;
				}
			}
		}
		return FieldType.TEXT;
	}

	private String parseFieldName(final String line) throws FormError {
		Matcher matcher = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*").matcher(line);
		if (matcher.find()) {
			return matcher.group();
		}
		throw new FormError(FormError.FIELD_NAME_MISSING);
	}

	private int parseFieldId(final String line) throws FormError {
		Matcher matcher = Pattern.compile("[0-9]+").matcher(line);
		if (matcher.lookingAt()) {
			return Integer.parseInt(matcher.group());
		}
		throw new FormError(FormError.FIELD_ID_MISSING);
	}

	private void close(final BufferedReader buffer) throws FormError {
		try {
			buffer.close();
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

}
