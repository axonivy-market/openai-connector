package com.openai.connector.demo.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

@FacesValidator(value = "commonValidator")
public class CommonValidator implements Validator {
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		
		if (isRequiredInputValidator(component, value)) {
			addGrowlErrorMessage("This field is required");
		}

		Object inputType = (Object) component.getAttributes().get("type");
		if (inputType != null && inputType.equals("email") && isValidEmailValidator(component, value)) {
			addGrowlErrorMessage("Invalid email format");
		}
	}

	private boolean isRequiredInputValidator(UIComponent component, Object value) {
		return value == null || StringUtils.isBlank(value.toString());
	}

	public static boolean isValidEmailValidator(UIComponent component, Object value) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher((String) value);
		return !matcher.find();
	}

	private void addGrowlErrorMessage(String message) {
		throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}
}
