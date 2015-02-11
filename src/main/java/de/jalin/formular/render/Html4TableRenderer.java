package de.jalin.formular.render;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.jalin.formular.Field;
import de.jalin.formular.FieldType;
import de.jalin.formular.Form;
import de.jalin.formular.FormError;
import de.jalin.formular.Label;
import de.jalin.formular.Widget;

public class Html4TableRenderer implements Renderer {

	public static final int DEFAULT_GRID = 12;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	private final int grid;
	
	public Html4TableRenderer() {
		grid = DEFAULT_GRID;
	}
	
	public Html4TableRenderer(final int gridWidth) {
		grid = gridWidth;
	}
	
	@Override
	public void render(final Form form, final Writer writer, final RenderMode mode, final String title, final String formName, final String logoURL) throws FormError {
		try {
			if (RenderMode.PRINT.equals(mode)) {
				writer.write("<table border=\"1\" cellspacing=\"1\" cellpadding=\"4\" style=\"width:100%;\">"
						+ "<tr>"
						+ ""
						+ "<td align=\"center\" colspan=\"1\"><img src=\"" + logoURL + "\" width=\"33%\" /></td>"
						+ "<td align=\"center\" colspan=\"2\">" + title + "</td>"
						+ "<td colspan=\"1\">Datum: " + DATE_FORMAT.format(new Date()) + "<br/>"
								+ "Formular: <br /><b>" + formName + "</b></td>"
						+ "</tr>"
						+ "</table>"
						+ "&nbsp;<br/>"
						+ "&nbsp;<br/>");
			}
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("<form method=\"post\" class=\"form-horizontal\">\n"
						+ "<table border=\"0\" style=\"width:100%;table-layout: fixed;\">\n");
			} else {
				writer.write("<table border=\"1\" cellspacing=\"0\" cellpadding=\"4\" style=\"width:100%;table-layout: fixed;\">\n");
			}
			final List<List<Widget>> rows = form.getRows();
			for (final List<Widget> row : rows) {
				render(form, row, writer, mode);
			}
			writer.write("</table>\n");
			if (RenderMode.CONFIRM.equals(mode)) {
				writer.write("<p style=\"font-weight:bold;\">Ihre Daten wurden erfolgreich gesendet.</p>");
			} 
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("<p>"
						+ "<input type=\"submit\" value=\"Absenden\" />"
						+ "<input type=\"reset\" value=\"Abbrechen\" />"
						+ "</p>\n");
			}
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("</form>\n");
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	private void render(final Form form, final List<Widget> row, final Writer writer, final RenderMode mode) throws IOException {
		writer.write("<tr>\n");
		int colsCount = 0;
		for (int idx = 1; idx < row.size(); idx++) {
			final Widget widget = row.get(idx-1);
			final Widget next = row.get(idx);
			final int colspan = ((next.getXC() - widget.getXC()) * grid + form.getWidthC() / 2) / form.getWidthC();
			colsCount += colspan;
			render(form, widget, colspan, writer, mode);
		}
		render(form, row.get(row.size()-1), grid - colsCount, writer, mode);
		writer.write("</tr>\n");
	}

	private void render(final Form form, final Widget w, final int colspan, final Writer writer, final RenderMode mode) throws IOException {
		writer.write("  <td nowrap=\"nowrap\" colspan=\"");
		writer.write(Integer.toString(colspan));
		writer.write("\">");
		if (w instanceof Label) {
			writer.write("<div style=\"width:100%;text-align:right;\">"+ ((Label) w).getLabel() + "</div>");
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
					if (f.isValid()) {
						writer.write("<input style=\"width:100%;\" type=\"text\" name=\"");
					} else {
						writer.write("<input style=\"width:98%;\" type=\"text\" name=\"");
					}
					writer.write(f.getName());
					writer.write("\" value=\"");
					writer.write(f.getValue());
					writer.write("\"");
					if (RenderMode.CONFIRM.equals(mode)) {
						writer.write(" disabled=\"disabled\"");
					}
					writer.write(" />");
					if (!f.isValid()) {
						writer.write("<span class=\"sup\" style=\"color:red;\">*</span>");
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
		}
		writer.write("</td>\n");
	}

}
