package de.jalin.formular;

public enum FieldType {

	TEXT("char"), REGEXP("regexp"), SELECT("select"), DATE("date"), CHECK("check");
	
	private final String type;

	private FieldType(final String name) {
		this.type = name;
	}
	
	public String type() {
		return type;
	}
	
}
