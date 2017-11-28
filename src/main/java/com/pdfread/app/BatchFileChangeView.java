package com.pdfread.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BatchFileChangeView extends Application {

	private ListView<String> fileListView;

	private List<String> oldFilePatchList;
	private List<String> newFilePatchList;

	@Override
	public void start(Stage primaryStage) throws Exception {

		GridPane fileNameChangeRootPane = new GridPane();

		Button changeNamesButton = new Button("Change names");

		Button applyChangesButton = new Button("Apply Changes");

		ListView<String> fileNamesCandidatesList = new ListView<String>();

		fileNamesCandidatesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		fileNamesCandidatesList.setEditable(true);
		fileNamesCandidatesList.setCellFactory(TextFieldListCell.forListView());

		fileNamesCandidatesList.setOnKeyPressed(keyPresedEvent -> {

			if (keyPresedEvent.isControlDown() && keyPresedEvent.getCode() == KeyCode.V) {

				Clipboard clipboard = Clipboard.getSystemClipboard();

				String allItems = clipboard.getString();

				String[] itemsSplited = allItems.split("\n");

				List<String> itmesTrimed = Stream.of(itemsSplited).map(name -> name.trim())
						.collect(Collectors.toList());

				ObservableList<String> clipboardItems = FXCollections.observableArrayList(itmesTrimed);

				fileNamesCandidatesList.setItems(clipboardItems);

			}

		});

//		ObservableList<String> fileNamesCandidatesListConent = FXCollections
//				.observableArrayList("fileNamesCandidatesList", "jajo", "nunu");
//
//		fileNamesCandidatesList.setItems(fileNamesCandidatesListConent);

		fileListView = new ListView<String>();

		fileListView.setOnDragOver(event -> {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		});

		fileListView.setOnDragDropped(event -> {

			boolean isSuccess = false;

			if (event.getGestureSource() != fileListView && event.getDragboard().hasFiles()) {

				List<File> dropedFiles = event.getDragboard().getFiles();

				List<String> filesPatch = dropedFiles.stream().map(file -> file.getAbsolutePath())
						.collect(Collectors.toList());

				ObservableList<String> itemsRetrive = FXCollections.observableArrayList(filesPatch);
				fileListView.setItems(itemsRetrive);

				isSuccess = true;
			}

			event.setDropCompleted(isSuccess);
			event.consume();
		});

		changeNamesButton.setOnAction(event -> {

			final ObservableList<String> oldFileNames = fileListView.getItems();
			oldFilePatchList = oldFileNames.stream().map(oldPatch -> oldPatch).collect(Collectors.toList());

			final ObservableList<String> newFileShortNames = fileNamesCandidatesList.getItems();
			List<String> shortNewFileNameList = newFileShortNames.stream().map(shortFileName -> shortFileName)
					.collect(Collectors.toList());

			final String regexPattern = "(\\\\.+\\\\)(.+)(\\.\\w{0,8})";

			newFilePatchList = new ArrayList<>(60);
			for (int index = 0; index < Math.min(oldFilePatchList.size(), shortNewFileNameList.size()); index++) {

				String oldPath = oldFilePatchList.get(index);
				String newPath = oldPath.replaceFirst(regexPattern, "$1" + shortNewFileNameList.get(index) + "$3");
				newFilePatchList.add(newPath);
			}

			ObservableList<String> newFileToChange = FXCollections.observableArrayList(newFilePatchList);
			fileListView.setItems(newFileToChange);

			// System.out.println(oldFilePatchList);
			// System.out.println("-------------------------------");
			// System.out.println(newFilePatchList);

		});

		applyChangesButton.setOnAction(event -> {

			if (oldFilePatchList == null || newFilePatchList == null) {
				return;
			}

			if (oldFilePatchList.isEmpty() || newFilePatchList.isEmpty()) {
				return;
			}

			for (int index = 0; index < Math.min(oldFilePatchList.size(), newFilePatchList.size()); index++) {

				Path sourcePath = Paths.get(oldFilePatchList.get(index));
				Path destiantionPath = Paths.get(newFilePatchList.get(index));

				try {
					Files.move(sourcePath, destiantionPath, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {

					e.printStackTrace();
				}

			}

//			System.out.println(oldFilePatchList);
//			System.out.println("-------------------------------");
//			System.out.println(newFilePatchList);
			
			oldFilePatchList = new ArrayList<>();
			
		    for (String path : newFilePatchList) {
		    	oldFilePatchList.add(path);
		    }
			

		});

		VBox vBox = new VBox();
		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().addAll(changeNamesButton, applyChangesButton);

		ColumnConstraints columnConstraints50Percent = new ColumnConstraints();
		columnConstraints50Percent.setPercentWidth(50);

		RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setPrefHeight(100);
		rowConstraints.setFillHeight(false);
		// rowConstraints.setPercentHeight(10);

		RowConstraints rowConstraints2 = new RowConstraints();
		rowConstraints2.setPercentHeight(90);

		fileNameChangeRootPane.getColumnConstraints().add(columnConstraints50Percent);
		fileNameChangeRootPane.getColumnConstraints().add(columnConstraints50Percent);
		fileNameChangeRootPane.getRowConstraints().add(rowConstraints);
		fileNameChangeRootPane.getRowConstraints().add(rowConstraints2);

		fileNameChangeRootPane.add(vBox, 0, 0, 2, 1);

		fileNameChangeRootPane.add(fileNamesCandidatesList, 0, 1);
		fileNameChangeRootPane.add(fileListView, 1, 1);

		Stage filesChangeStage = new Stage();
		filesChangeStage.setTitle("Change files");
		filesChangeStage.setScene(new Scene(fileNameChangeRootPane, 600, 500));

		filesChangeStage.show();

	}

}
