package de.jalin.formular;


public class FormError extends Exception {

	private static final long serialVersionUID = 1L;
	
	public static final String IO_ERROR = "IO_ERROR";
	public static final String PDF_ERROR = "PDF_ERROR";
	public static final String EMAIL_ERROR = "EMAIL_ERROR";
	public static final String FIELD_ID_MISSING = "FIELD_ID_MISSING";
	public static final String FIELD_NAME_MISSING = "FIELD_NAME_MISSING";
	public static final String FIELD_NAME_NOT_UNIQUE = "FIELD_NAME_NOT_UNIQUE";
	public static final String EMAIL_MISSING_TO_ADDRESS = "EMAIL_MISSING_TO_ADDRESS";


	public FormError(final String errorMessage, final Exception e) {
		super(errorMessage, e);
	}

	public FormError(final Exception e) {
		super(IO_ERROR, e);
	}

	public FormError(final String errorId) {
		super(errorId);
	}

}
