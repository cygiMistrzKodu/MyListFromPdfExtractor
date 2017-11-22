package com.pdfread.app;

import static org.junit.Assert.*;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.junit.Ignore;
import org.junit.Test;

public class ReadPdfTest {


	@Test @Ignore
	public void readPdf() {
		
		String pdfFile = "D:\\CV zyciorys\\InterviewQuestions\\50 Servlet Interview Questions and Answers\\50 Servlet Interview Questions and Answers.pdf";
		
		
		PDDocument pdDocument = null;
		
		try {
			pdDocument = PDDocument.load(new File(pdfFile));
			pdDocument.getClass();
			
			if(!pdDocument.isEncrypted()) {
				
				PDFTextStripperByArea stripperByArea = new PDFTextStripperByArea();
				stripperByArea.setSortByPosition(true);
				
				PDFTextStripper pdfTextStripper = new PDFTextStripper();
				String pdfContent = pdfTextStripper.getText(pdDocument);
				
				String onlyQuestionsRegexPattern = "\\d.*\\?";
				
				Pattern pattern = Pattern.compile(onlyQuestionsRegexPattern);
				Matcher matcher = pattern.matcher(pdfContent);
				
				String copyToClipboardTest = "";
				
				 while (matcher.find()) {
			           
					    copyToClipboardTest += matcher.group() + "\n";
//			            System.out.println(copyToClipboardTest);
			        }
				 
				 
				 StringSelection selection = new StringSelection(copyToClipboardTest);
				 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				 clipboard.setContents(selection, selection);
				 System.out.println(copyToClipboardTest);
				 
				
				
//				System.out.println("PDF FIle: " + pdfContent);
				
			}
			
			
		} catch (InvalidPasswordException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		
	}
	
	
	

}
