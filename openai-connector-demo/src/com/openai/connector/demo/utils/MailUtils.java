package com.openai.connector.demo.utils;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.connector.demo.AIGeneratedMail;
import com.openai.connector.demo.CandidateInformation;

public class MailUtils {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static AIGeneratedMail convert(String generatedEmail) {
		try {
			return mapper.readValue(generatedEmail, new TypeReference<AIGeneratedMail>() {
			});
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static boolean isCanSendMail(AIGeneratedMail mail, CandidateInformation candidateInformation) {
		return StringUtils.isNotEmpty(candidateInformation.getEmail()) && StringUtils.isNotEmpty(mail.getSubject())
				&& StringUtils.isNotEmpty(mail.getContent());
	}
}
