package com.pdfread.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentNarrower implements Callable<List<String>> {

	// private String searchPattern = "\\d.*\\?";
	// private String searchPattern = "\\d.*(?<!\\?)";
	// private String searchPattern = "\\d+\\..*(?<!\\?)";
	// private String searchPattern = "\\d.*(?=\\?)";
	// private String searchPattern = "\\d+\\.[\\s\\S]*?(?=\\?)";
	// private String searchPattern = "\\d+?\\.\\s+?[\\s\\S]*?(?=\\?)";
	// private String searchPattern = "\\d.*[\\S\\s]*?(?=\\?)";
	private String searchPattern = "(?<=\\?)[\\S\\s]*?\\.\\d+";
	private String content = "";

	public ContentNarrower(String content) {
		this.content = content;
	}

	public ContentNarrower(String searchPattern, String content) {
		this.searchPattern = searchPattern;
	}

	private List<String> findPatternMatch() {

		Pattern pattern = Pattern.compile(searchPattern);
		Matcher matcher = pattern.matcher(content);
		List<String> foundedPatterns = new ArrayList<>(60);

		StringBuilder stringBuilder = new StringBuilder();

		while (matcher.find()) {

			String matchReversed = stringBuilder.append(matcher.group()).reverse().toString();
			
			matchReversed = matchReversed.replace("\r\n", " ");
			matchReversed = matchReversed.replace("?", "");

			foundedPatterns.add(matchReversed);

			stringBuilder.setLength(0);

		}

		Collections.reverse(foundedPatterns);

		return foundedPatterns;
	}

	@Override
	public List<String> call() throws Exception {

		return findPatternMatch();
	}

}
