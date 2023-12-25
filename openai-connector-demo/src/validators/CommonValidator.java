package validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

@FacesValidator(value = "commonValidator")
public class CommonValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (isRequiredInputValidator(component, value)) {
			addGrowlErrorMessage();
		}

	}

	private boolean isRequiredInputValidator(UIComponent component, Object value) {
		return value == null || StringUtils.isBlank(value.toString());
	}

	private void addGrowlErrorMessage() {
		String message = "This field is required";
		throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}
}
