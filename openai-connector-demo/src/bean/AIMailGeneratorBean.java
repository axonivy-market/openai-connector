package bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.openai.connector.demo.enums.JobPosition;

@ManagedBean
@ViewScoped
public class AIMailGeneratorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public JobPosition[] getJobPositions() {
		return JobPosition.values();
	}

}
