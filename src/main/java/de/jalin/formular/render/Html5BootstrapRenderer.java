package de.jalin.formular.render;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import de.jalin.formular.Field;
import de.jalin.formular.FieldType;
import de.jalin.formular.Form;
import de.jalin.formular.FormError;
import de.jalin.formular.Label;
import de.jalin.formular.Widget;

public class Html5BootstrapRenderer implements Renderer {

	public static final int DEFAULT_GRID = 12;
	
	private final int grid;
	
	public Html5BootstrapRenderer() {
		grid = DEFAULT_GRID;
	}
	
	@Override
	public void render(final Form form, final Writer writer, final RenderMode mode, final String title, final String formName, final String logoURL) throws FormError {
		printHeader(writer, mode, title, formName, logoURL);
		printFormBegin(writer, mode);
		final List<List<Widget>> rows = form.getRows();
		for (final List<Widget> row : rows) {
			render(form, row, writer, mode);
		}
		printFormEnd(writer, mode);
	}

	private void render(final Form form, final List<Widget> row, final Writer writer, final RenderMode mode) throws FormError {
		printRowBegin(writer, mode);
		int colsCount = 0;
		for (int idx = 1; idx < row.size(); idx++) {
			final Widget widget = row.get(idx-1);
			final Widget next = row.get(idx);
			final int colspan = ((next.getXC() - widget.getXC()) * grid + form.getWidthC() / 2) / form.getWidthC();
			colsCount += colspan;
			render(form, widget, colspan, writer, mode);
		}
		render(form, row.get(row.size()-1), grid - colsCount, writer, mode);
		printRowEnd(writer, mode);
	}

	private void render(final Form form, final Widget w, final int colspan, final Writer writer, final RenderMode mode) throws FormError {
		printWidgetBegin(writer, mode, colspan);
		try {
			if (w instanceof Label) {
				writer.write("<div class=\"col-xs-" + Integer.toString(colspan) + "\"><p class=\"form-control-static\">"+ ((Label) w).getLabel() + "</p></div>");
			}
			if (w instanceof Field) {
				final Field f = (Field) w;
				final FieldType type = f.getType();
				writer.write("<div class=\"col-xs-" + Integer.toString(colspan) 
						+ " " + (f.isValid() ? "has-success" : "has-error") + "\">");
				if (RenderMode.INPUT.equals(mode)) {
					if (FieldType.SELECT.equals(type)) {
						writer.write("<select class=\"form-control\" name=\"");
						writer.write(f.getName());
						writer.write("\"");
						if (RenderMode.CONFIRM.equals(mode)) {
							writer.write(" disabled=\"disabled\"");
						}
						writer.write(">");
						for (final String opt : f.getSelectValues()) {
							writer.write("<option value=\"");
							writer.write(opt);
							writer.write("\"");
							if (f.getValue().equals(opt)) {
								writer.write(" selected=\"selected\"");
							}
							writer.write(">");
							writer.write(opt);
							writer.write("</option>");
						}
						writer.write("</select>");
					} else if (FieldType.CHECK.equals(type)) {
						writer.write("<input class=\"form-control\" type=\"checkbox\" name=\"");
						writer.write(f.getName());
						writer.write("\" value=\"");
						writer.write(f.getName());
						writer.write("\"");
						writer.write(" />");
					} else {
						writer.write("<input class=\"form-control\" type=\"text\" name=\"");
						writer.write(f.getName());
						writer.write("\" value=\"");
						writer.write(f.getValue());
						writer.write("\"");
						if (RenderMode.CONFIRM.equals(mode)) {
							writer.write(" disabled=\"disabled\"");
						}
						writer.write(" />");
						if (f.isValid()) {
							writer.write("<span class=\"glyphicon glyphicon-ok form-control-feedback\"></span>");
						} else {
							writer.write("<span class=\"glyphicon glyphicon-remove form-control-feedback\"></span>");
						}
					}
				} else {
					if (FieldType.CHECK.equals(type)) {
						if (f.getValue().isEmpty()) {
							writer.write("[&emsp;]");
						} else {
							writer.write("[X]");
						}
					} else {
						writer.write("<b>" + f.getValue() + "</b>");
					}
				}
				writer.write("</div>");
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
		printWidgetEnd(writer, mode);
	}

	@Override
	public void printHeader(final Writer writer, final RenderMode mode, final String title,
			final String formName, final String logoURL) throws FormError {
		// no header
	}

	@Override
	public void printFooter(final Writer writer, final RenderMode mode, final String title,
			final String formName, final String logoURL) throws FormError {
		// no footer
	}

	@Override
	public void printSubmittedMessage(final Writer writer, final RenderMode mode)
			throws FormError {
		try {
			if (RenderMode.CONFIRM.equals(mode)) {
				writer.write("<p style=\"font-weight:bold;\">Ihre Daten wurden erfolgreich gesendet.</p>");
			}
		} catch (IOException e) {
			throw new FormError(e);
		} 
	}

	@Override
	public void printSubmitButtons(final Writer writer, final RenderMode mode)
			throws FormError {
		try {
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("<div class=\"row form-group\">"
						+ "<div class=\"col-xs-3\"><input type=\"submit\" value=\"Absenden\" class=\"btn btn-primary\"/></div>"
						+ "<div class=\"col-xs-3\"><input type=\"reset\" value=\"Abbrechen\" class=\"btn\"/></div>"
						+ "</div>\n");
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printFormBegin(Writer writer, RenderMode mode) throws FormError {
		try {
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("<div class=\"container-fluid\">\n<form method=\"post\" class=\"form-horizontal\">\n");
			} else {
				writer.write("<div class=\"container-fluid\">\n");
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printFormEnd(Writer writer, RenderMode mode) throws FormError {
		printSubmittedMessage(writer, mode);
		printSubmitButtons(writer, mode);
		try {
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("</form></div>\n");
			} else {
				writer.write("</div>\n");
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printRowBegin(Writer writer, RenderMode mode) throws FormError {
		try {
			writer.write("<div class=\"row has-feedback\">\n");
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printRowEnd(Writer writer, RenderMode mode) throws FormError {
		try {
			writer.write("</div>\n");
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printWidgetBegin(Writer writer, RenderMode mode, int colspan)
			throws FormError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printWidgetEnd(Writer writer, RenderMode mode) throws FormError {
		// TODO Auto-generated method stub
		
	}

}
