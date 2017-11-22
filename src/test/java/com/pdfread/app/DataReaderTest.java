package com.pdfread.app;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Ignore;
import org.junit.Test;

public class DataReaderTest {

	@Test @Ignore
	public void readAndPrintPdfFileCOntent() {
		
		String pdfFilePath = "D:\\CV zyciorys\\InterviewQuestions\\50 Servlet Interview Questions and Answers\\50 Servlet Interview Questions and Answers.pdf";
	
		ExecutorService executor = Executors.newFixedThreadPool(10);
		
		Callable<String> callable = new DataReader(pdfFilePath);
		
		Future<String> future = executor.submit(callable);
		
		
		try {
			System.out.println(future.get());
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
		}
		
		
	   executor.shutdown();
		
		
	
	}

}
