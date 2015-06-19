package de.jalin.formular.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import de.jalin.formular.Field;
import de.jalin.formular.FieldType;
import de.jalin.formular.Form;
import de.jalin.formular.FormError;
import de.jalin.formular.Label;
import de.jalin.formular.Widget;

public class Html4TableRenderer implements Renderer {

	public static final int DEFAULT_GRID = 24;
	
	private final int grid;
	private final String labelAlign;
	private final String textStrike;
	
	public Html4TableRenderer() {
		grid = DEFAULT_GRID;
		labelAlign = "left";
		textStrike = "normal"; // "bold";
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
		printFooter(writer, mode, title, formName, logoURL);
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
		printWidgetBegin(writer, w, mode, colspan);
		try {
			if (w instanceof Label) {
				final String labelText = ((Label) w).getLabel();
				if (labelText.startsWith("---")) {
					writer.write("<hr />");
				} else {
					writer.write("<div style=\"width:100%;text-align:" + labelAlign + ";\">"+ labelText + "</div>");
				}
			}
			if (w instanceof Field) {
				final Field f = (Field) w;
				final FieldType type = f.getType();
				if (RenderMode.INPUT.equals(mode)) {
					if (FieldType.SELECT.equals(type)) {
						writer.write("<select name=\"");
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
						writer.write("<input type=\"checkbox\" name=\"");
						writer.write(f.getName());
						writer.write("\" value=\"");
						writer.write(f.getName());
						writer.write("\"");
						writer.write(" />");
					} else {
						if (FieldType.MULTI.equals(type)) {
							writer.write("<textarea cols=\"50\" rows=\"5\" ");
						} else {
							writer.write("<input type=\"text\" ");
						}
						if (f.isValid()) {
							writer.write("style=\"width:100%;\" name=\"");
						} else {
							writer.write("style=\"width:98%;\" name=\"");
						}
						writer.write(f.getName());
						writer.write("\"");
						if (RenderMode.CONFIRM.equals(mode)) {
							writer.write(" disabled=\"disabled\"");
						}
						if (FieldType.MULTI.equals(type)) {
							writer.write(">");
							writer.write(f.getValue());
							writer.write("</textarea>");
						} else {
							writer.write(" value=\"");
							writer.write(f.getValue());
							writer.write("\"");
							writer.write(" />");
						}
						if (!f.isValid()) {
							writer.write("&nbsp;<span class=\"sup\" style=\"color:red;\">*</span>");
						}
					}
				} else {
					if (FieldType.CHECK.equals(type)) {
						if (f.getValue().isEmpty()) {
							writer.write("[&emsp;]");
						} else {
							writer.write("[X]");
						}
					} else if (FieldType.MULTI.equals(type)) {
						if ("bold".equals(textStrike)) {
							writer.write("<b><p>" + f.getValue() + "</p></b>");
						} else {
							writer.write("<p>" + f.getValue() + "</p>");
						}
					} else {
						if ("bold".equals(textStrike)) {
							writer.write("<b>" + f.getValue() + "</b>");
						} else {
							writer.write(f.getValue());
						}
					}
				}
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
		printWidgetEnd(writer, mode);
	}

	@Override
	public void printHeader(final Writer writer, final RenderMode mode, final String title,
			final String formName, final String logoURL) throws FormError {
		if (RenderMode.PRINT.equals(mode)) {
			try {
				writer.write("<table border=\"1\" cellspacing=\"0\" cellpadding=\"4\" style=\"width:100%;\">"
						+ "<tr>"
						+ ""
						+ "<td align=\"center\" colspan=\"1\"><img src=\"" + logoURL + "\" width=\"100%\" /></td>"
						+ "<td align=\"center\" colspan=\"2\">" + title + "</td>"
						+ "<td colspan=\"1\">Datum: " + DATE_FORMAT.format(new Date()) + "<br/>"
								+ "Formular: <br /><b>" + formName + "</b></td>"
						+ "</tr>"
						+ "</table>"
						+ "&nbsp;<br/>"
						+ "&nbsp;<br/>");
			} catch (IOException e) {
				throw new FormError(e);
			}
		}
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
				writer.write("<p>"
						+ "<input type=\"submit\" value=\"Absenden\" />"
						+ "<input type=\"reset\" value=\"Abbrechen\" />"
						+ "</p>\n");
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printFormBegin(final Writer writer, final RenderMode mode) throws FormError {
		try {
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("<form method=\"post\" class=\"form-horizontal\">\n"
						+ "<table border=\"0\" style=\"width:100%;table-layout: fixed;\">\n");
			} else {
				writer.write("<table border=\"0\" cellspacing=\"3\" cellpadding=\"2\" style=\"width:100%;table-layout: fixed;\">\n");
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printFormEnd(final Writer writer, final RenderMode mode) throws FormError {
		try {
			writer.write("</table>\n");
			printSubmittedMessage(writer, mode);
			printSubmitButtons(writer, mode);
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("</form>\n");
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printRowBegin(final Writer writer, final RenderMode mode) throws FormError {
		try {
			writer.write("<tr>\n");
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printRowEnd(final Writer writer, final RenderMode mode) throws FormError {
		try {
			writer.write("</tr>\n");
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printWidgetBegin(final Writer writer, final Widget widget, final RenderMode mode, final int colspan)
			throws FormError {
		try {
			writer.write("  <td ");
			if ( RenderMode.INPUT != mode && RenderMode.PRINT_OVERLAY != mode && widget instanceof Field && ((Field) widget).getType() != FieldType.CHECK) {
				writer.write("style=\"background-color:#e0e0e0;\" ");
			}
			if (widget instanceof Field && !FieldType.MULTI.equals(((Field) widget).getType())) {
				writer.write("nowrap=\"nowrap\" ");
			}
			writer.write("colspan=\"");
			writer.write(Integer.toString(colspan));
			writer.write("\">");
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	@Override
	public void printWidgetEnd(final Writer writer, final RenderMode mode) throws FormError {
		try {
			writer.write("</td>\n");
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

}
