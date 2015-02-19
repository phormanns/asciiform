package de.jalin.formular.render;

import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import de.jalin.formular.Form;
import de.jalin.formular.FormError;
import de.jalin.formular.Widget;

public interface Renderer {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	public abstract void render(Form form, Writer writer, RenderMode mode,
			String title, String formName, String logoURL) throws FormError;

	public abstract void printHeader(Writer writer, RenderMode mode,
			String title, String formName, String logoURL) throws FormError;
	
	public abstract void printFooter(Writer writer, RenderMode mode,
			String title, String formName, String logoURL) throws FormError;
	
	public abstract void printSubmittedMessage(Writer writer, RenderMode mode) throws FormError;
	
	public abstract void printSubmitButtons(Writer writer, RenderMode mode) throws FormError;

	public abstract void printFormBegin(Writer writer, RenderMode mode) throws FormError;
		
	public abstract void printFormEnd(Writer writer, RenderMode mode) throws FormError;
	
	public abstract void printRowBegin(Writer writer, RenderMode mode) throws FormError;
	
	public abstract void printRowEnd(Writer writer, RenderMode mode) throws FormError;
	
	public abstract void printWidgetBegin(Writer writer, Widget widget, RenderMode mode, int colspan) throws FormError;
	
	public abstract void printWidgetEnd(Writer writer, RenderMode mode) throws FormError;

}