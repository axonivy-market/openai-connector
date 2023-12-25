package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.connector.demo.AIGeneratedMail;

public class MailUtils {
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static AIGeneratedMail convert(String generatedEmail) {
		try {
			return mapper.readValue(generatedEmail, new TypeReference<AIGeneratedMail>() {});
		} catch (JsonProcessingException e) {
			return null;
		}
	}
}
