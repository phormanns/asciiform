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

public class Html5BootstrapRenderer implements Renderer {

	public static final int DEFAULT_GRID = 12;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	private final int grid;
	
	public Html5BootstrapRenderer() {
		grid = DEFAULT_GRID;
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
				writer.write("<div class=\"container-fluid\">\n<form method=\"post\" class=\"form-horizontal\">\n");
			} else {
				writer.write("<div class=\"container-fluid\">\n<form method=\"post\" class=\"form-horizontal\">\n");
			}
			final List<List<Widget>> rows = form.getRows();
			for (final List<Widget> row : rows) {
				render(form, row, writer, mode);
			}
			if (RenderMode.CONFIRM.equals(mode)) {
				writer.write("<p style=\"font-weight:bold;\">Ihre Daten wurden erfolgreich gesendet.</p>");
			} 
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("<div class=\"form-group\">"
						+ "<input type=\"submit\" value=\"Absenden\" class=\"btn btn-primary\"/>"
						+ "<input type=\"reset\" value=\"Abbrechen\" class=\"btn\"/>"
						+ "</div>\n");
			}
			if (RenderMode.INPUT.equals(mode)) {
				writer.write("</form></div>\n");
			}
		} catch (IOException e) {
			throw new FormError(e);
		}
	}

	private void render(final Form form, final List<Widget> row, final Writer writer, final RenderMode mode) throws IOException {
		writer.write("<div class=\"row form-group has-feedback\">\n");
		int colsCount = 0;
		for (int idx = 1; idx < row.size(); idx++) {
			final Widget widget = row.get(idx-1);
			final Widget next = row.get(idx);
			final int colspan = ((next.getXC() - widget.getXC()) * grid + form.getWidthC() / 2) / form.getWidthC();
			colsCount += colspan;
			render(form, widget, colspan, writer, mode);
		}
		render(form, row.get(row.size()-1), grid - colsCount, writer, mode);
		writer.write("</div>\n");
	}

	private void render(final Form form, final Widget w, final int colspan, final Writer writer, final RenderMode mode) throws IOException {
		if (w instanceof Label) {
			writer.write("<label class=\"control-label col-xs-" + Integer.toString(colspan) + "\">"+ ((Label) w).getLabel() + "</label>");
		}
		if (w instanceof Field) {
			final Field f = (Field) w;
			final FieldType type = f.getType();
			writer.write("<div class=\"col-xs-" + Integer.toString(colspan) + " " + (f.isValid() ? "has-success" : "has-error") + "\">");
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
	}

}
