package de.jalin.formular.render;

import java.io.Writer;

import de.jalin.formular.Form;
import de.jalin.formular.FormError;

public interface Renderer {

	public abstract void render(Form form, Writer writer, RenderMode mode,
			String title, String formName, String logoURL) throws FormError;

}