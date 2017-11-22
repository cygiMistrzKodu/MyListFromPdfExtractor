package com.pdfread.app;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class DataReader implements Callable<String> {

	private String path;

	private PDDocument pdFDocument = null;

	public DataReader(String path) {
		this.path = path;
	}

	private String read() {

		String pdfContent = "";

		try {
			pdFDocument = PDDocument.load(new File(path));
			pdFDocument.getClass();

			if (!pdFDocument.isEncrypted()) {

				PDFTextStripperByArea stripperByArea = new PDFTextStripperByArea();
				stripperByArea.setSortByPosition(true);

				PDFTextStripper pdfTextStripper = new PDFTextStripper();
				pdfContent = pdfTextStripper.getText(pdFDocument);
				
				String reversPdfContent = new StringBuilder(pdfContent).reverse().toString();
				
				pdfContent = reversPdfContent;
				
			}

		} catch (InvalidPasswordException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		} finally {

			try {
				pdFDocument.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		return pdfContent;
	}

	@Override
	public String call() throws Exception {

		return read();
	}

}
