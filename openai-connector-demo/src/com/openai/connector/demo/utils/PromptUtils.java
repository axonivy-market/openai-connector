package com.openai.connector.demo.utils;

import com.openai.connector.demo.CandidateInformation;

public class PromptUtils {
	public static String createPrompt(CandidateInformation data) {
		if (data == null) {
			return null;
		}

		return """
				The candidate Name is '%s',
				Candidate most important skill is '%s',
				Interviewers Name is '%s',
				Job Position is '%s',
				Interview date is '%s',
				company is '%s'.
				Please create a '%s' follow up email, written in the name of the interviewer.
				The position of the Interviewer is '%s'.
				Return in json string with attributes subject, content\"""
				""".formatted(
				data.getName(), data.getMostImportantSkill(), data.getInterviewerName(), data.getJobPosition(),
				data.getInterviewDate(), data.getCompany(), data.getStatus(), data.getInterviewerPosition());
	}
}
