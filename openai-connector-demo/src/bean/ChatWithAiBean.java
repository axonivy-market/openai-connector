package bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.openai.api.v1.client.CreateChatCompletionRequest.ModelEnum;

@ManagedBean
@ViewScoped
public class ChatWithAiBean {

	private static List<ModelEnum> models;

	@PostConstruct
	public void init() {
		models = List.of(
		        ModelEnum.GPT_4O,
		        ModelEnum.GPT_4O_2024_11_20,
		        ModelEnum.GPT_4O_2024_08_06,
		        ModelEnum.GPT_4O_2024_05_13,
		        ModelEnum.CHATGPT_4O_LATEST,
		        ModelEnum.GPT_4O_MINI,
		        ModelEnum.GPT_4O_MINI_2024_07_18,
		        ModelEnum.GPT_4_TURBO,
		        ModelEnum.GPT_4_TURBO_2024_04_09,
		        ModelEnum.GPT_4_0125_PREVIEW,
		        ModelEnum.GPT_4_TURBO_PREVIEW,
		        ModelEnum.GPT_4_1106_PREVIEW,
		        ModelEnum.GPT_4_VISION_PREVIEW,
		        ModelEnum.GPT_4,
		        ModelEnum.GPT_4_32K_0314,
		        ModelEnum.GPT_4_32K_0613,
		        ModelEnum.GPT_3_5_TURBO,
		        ModelEnum.GPT_3_5_TURBO_16K,
		        ModelEnum.GPT_3_5_TURBO_1106,
		        ModelEnum.GPT_3_5_TURBO_0125
		    );
	}

	public List<ModelEnum> getModels() {
		return models;
	}

}
