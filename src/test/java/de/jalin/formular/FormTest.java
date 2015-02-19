package de.jalin.formular;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.jalin.formular.parse.Parser;
import de.jalin.formular.render.Html4TableRenderer;
import de.jalin.formular.render.RenderMode;
import de.jalin.formular.render.Renderer;

public class FormTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValidForm() {
		try {
			final Parser parser = new Parser();
			final Form form = parser.parse(new StringReader(createValidForm()), "Application", "Formular"); 
			final List<List<Widget>> rows = form.getRows();
			assertEquals(7L, rows.size());
			final List<Widget> rowAnrede = rows.get(0);
			assertEquals(2L, rowAnrede.size());
			assertTrue(rowAnrede.get(0) instanceof Label);
			assertEquals("Anrede", ((Label)rowAnrede.get(0)).getLabel());
			final Widget anredeWidget = rowAnrede.get(1);
			assertTrue(anredeWidget instanceof Field);
			assertEquals("anrede", ((Field) anredeWidget).getName());
			String[] selectValues = ((Field) anredeWidget).getSelectValues();
			assertTrue(selectValues != null && selectValues.length == 4);
			final List<Widget> rowGeburtstag = rows.get(5);
			assertTrue(rowGeburtstag.get(1) instanceof Field);
			assertEquals("geburtstag", ((Field)rowGeburtstag.get(1)).getName());
			assertEquals(FieldType.DATE, ((Field)rowGeburtstag.get(1)).getType());
		} catch (FormError e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testRenderForm() {
		try {
			final Parser parser = new Parser();
			final Form form = parser.parse(new StringReader(createValidForm()), "Application", "Formular");
			final Renderer renderer = new Html4TableRenderer();
			renderer.render(form, new OutputStreamWriter(System.out), RenderMode.INPUT, "application title", "form name", "");
		} catch (FormError e) {
			fail(e.getLocalizedMessage());
		} 
	}

	@Test
	public void testSelectOptions() {
		try {
			final Parser parser = new Parser();
			final Form form = parser.parse(new StringReader(createValidForm()), "Application", "Formular");
			Field select = form.getField(7);
			final String[] values = select.getSelectValues();
			assertEquals(4, values.length);
			assertEquals("", values[0]);
			assertEquals("Herr", values[1]);
			assertEquals("Frau", values[2]);
			assertEquals("Firma", values[3]);
		} catch (FormError e) {
			fail(e.getLocalizedMessage());
		} 
	}

	private String createValidForm() {
		final StringBuffer form = new StringBuffer();
		form.append("Anrede       [07_______________________]");
		form.append('\n');
		form.append("Vorname      [01_______________________]");
		form.append('\n'); 
		form.append("Nachname     [02_______________________]");
		form.append('\n');
		form.append("Straße / Nr. [03___________________][04]");
		form.append('\n');
		form.append("PLZ / Ort    [05__][06_________________]");
		form.append('\n');
		form.append("Geburtstag   [08_______________________]");
		form.append('\n');
		form.append("Eingabe Ok   [09]                       ");
		form.append('\n');
		form.append("");
		form.append('\n');
		form.append("01 vorname: char(80)");
		form.append('\n');
		form.append("02 nachname: char(80)");
		form.append('\n');
		form.append("03 strasse: char(80)");
		form.append('\n');
		form.append("04 hausnummer: char(20)");
		form.append('\n');
		form.append("05 postleitzahl: regexp([0-9]{5})");
		form.append('\n');
		form.append("06 ort: char(80)");
		form.append('\n');
		form.append("07 anrede: select(\"\",Herr,\"Frau\",\"Firma\")");
		form.append('\n');
		form.append("08 geburtstag: date(dd.MM.yyyy)");
		form.append('\n');
		form.append("09 geprueft: check");
		form.append('\n');
		return form.toString();
	}
	
}
