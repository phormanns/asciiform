package de.jalin.formular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Field implements Widget {

	private final int id;
	private final int length;
	private final List<String> selectValues;

	private int xc;
	private int yc;
	private String name;
	private FieldType type;
	private String value;
	private Validator validator;
	
	public Field(final int fieldId, final int length) {
		this.id = fieldId;
		this.length = length;
		this.selectValues = new ArrayList<String>();
		this.value = "";
	}

	@Override
	public void setXC(final int xCharacter) {
		xc = xCharacter;
	}

	@Override
	public void setYC(int yCharacter) {
		yc = yCharacter;
	}

	@Override
	public int length() {
		return length;
	}

	public int id() {
		return id;
	}

	public void setName(final String fieldName) {
		name = fieldName;
	}

	public void setType(final FieldType fieldType) {
		this.type = fieldType;
	}

	public void setValidator(final Validator validator) {
		this.validator = validator;
	}

	public String getName() {
		return name;
	}

	public FieldType getType() {
		return type;
	}

	@Override
	public int getXC() {
		return xc;
	}

	@Override
	public int getYC() {
		return yc;
	}

	public void addSelectValues(final String... selectableValues) {
		selectValues.addAll(Arrays.asList(selectableValues));
	}

	public String[] getSelectValues() {
		return selectValues.toArray(new String[0]);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isValid() {
		return validator == null || validator.isValid(value);
	}

}
