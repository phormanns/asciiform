package de.jalin.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import de.jalin.formular.Form;
import de.jalin.formular.FormError;
import de.jalin.formular.parse.Parser;
import de.jalin.formular.render.Html5BootstrapRenderer;
import de.jalin.formular.render.RenderMode;
import de.jalin.formular.render.Renderer;

public class FormTagHandler extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	private final Parser parser;
	private final Renderer renderer;

	public FormTagHandler() {
		parser = new Parser();
		renderer = new Html5BootstrapRenderer();
	}
	
	@Override
	public int doEndTag() throws JspException {
		final BodyContent content = getBodyContent();
		try {
			final Form form = parser.parse(content.getReader(), "App Name", "Form Name");
			renderer.render(form, pageContext.getOut(), RenderMode.INPUT, "Form Title", "Form Name", null);
		} catch (FormError e) {
			try {
				pageContext.getOut().println(e.getLocalizedMessage());
			} catch (IOException e1) {
				// do not care
			}
		}
		return SKIP_BODY;
	}
	
}
