package de.jalin.formular;

public class Label implements Widget {

	private final String text;

	private int xc;
	private int yc;

	public Label(final String label) {
		text = "\\\\".equals(label) ? "&nbsp;" : label; 
	}
	
	@Override
	public void setXC(final int xCharacter) {
		xc = xCharacter;
	}

	@Override
	public void setYC(final int yCharacter) {
		yc = yCharacter;
	}

	@Override
	public int length() {
		return text.length();
	}

	public String getLabel() {
		return text;
	}

	@Override
	public int getXC() {
		return xc;
	}

	@Override
	public int getYC() {
		return yc;
	}

}
