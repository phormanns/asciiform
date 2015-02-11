package de.jalin.formular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Form {

	private final List<Widget> widgets;
	private final Map<Integer, Field> fieldsById;
	private final List<List<Widget>> gridRows;
	private final String name;
	private final String application;
	
	public Form(final String application, final String name) throws FormError {
		this.application = application;
		this.name = name;
		widgets = new ArrayList<Widget>();
		fieldsById = new HashMap<Integer, Field>();
		gridRows = new ArrayList<List<Widget>>();
	}

	public List<List<Widget>> getRows() {
		return gridRows;
	}


	public void addRow() {
		gridRows.add(new ArrayList<Widget>());
	}

	public void addField(final int lineCount, final Field fld) {
		widgets.add(fld);
		gridRows.get(lineCount).add(fld);
		fieldsById.put(fld.id(), fld);
	}

	public void addLabel(final int lineCount, final Label lbl) {
		widgets.add(lbl);
		gridRows.get(lineCount).add(lbl);
	}

	public Field getField(final int id) {
		return fieldsById.get(id);
	}

	public int getWidthC() {
		int widthC = 0;
		for (final Widget w : widgets) {
			final int xmax = w.getXC() + w.length();
			if (xmax > widthC) {
				widthC = xmax;
			}
		}
		return widthC;
	}

	public void extractValues(final Map<?, ?> requestParams) {
		for (final Field fld : fieldsById.values()) {
			final Object object = requestParams.get(fld.getName());
			if (object != null && object instanceof Object[]) {
				fld.setValue((String) ((Object[])object)[0]);
			}
		}
	}

	public boolean validate() {
		boolean isValid = true;
		for (final Field f : fieldsById.values()) {
			isValid = isValid && f.isValid();
		}
		return isValid;
	}

	public String getName() {
		return name;
	}

	public String getApplicationName() {
		return application;
	}

	public void checkForErrors() throws FormError {
		final List<String> fieldNames = new ArrayList<>();
		for (final Field f : fieldsById.values()) {
			final String n = f.getName();
			if (n == null || n.isEmpty()) {
				throw new FormError(FormError.FIELD_NAME_MISSING);
			}
			if (fieldNames.contains(n)) {
				throw new FormError(FormError.FIELD_NAME_NOT_UNIQUE);
			}
			fieldNames.add(n);
		}
	}

}
