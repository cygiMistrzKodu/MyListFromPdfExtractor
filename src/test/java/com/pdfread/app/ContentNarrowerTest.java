package com.pdfread.app;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Ignore;
import org.junit.Test;

public class ContentNarrowerTest {

	@Test @Ignore
	public void checkIfReturnRightText() {
		
		String pdfFilePath = "D:\\CV zyciorys\\InterviewQuestions\\50 Servlet Interview Questions and Answers\\50 Servlet Interview Questions and Answers.pdf";
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
				
		Future<String> future = executor.submit(new DataReader(pdfFilePath));
		
		String contentToParse = "";
		
		try {
			contentToParse = future.get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		
						
		Future<List<String>> futureContentNarrower = executor.submit(new ContentNarrower(contentToParse));
		
		
		try {
			System.out.println(futureContentNarrower.get());
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		
		executor.shutdown();

		
	}

}
