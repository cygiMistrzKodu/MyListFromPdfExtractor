package com.pdfread.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GuiListView extends Application {

	private List<String> narrowContent = new ArrayList<>();
	private ListView<String> list = new ListView<String>();

	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane root = new BorderPane();

		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		MenuItem changeFileNames = new MenuItem("Change file names");

		changeFileNames.setOnAction(event -> {

			try {
				new BatchFileChangeView().start(primaryStage);
			} catch (Exception e) {

				e.printStackTrace();
			}

		});

		menuFile.getItems().addAll(changeFileNames);
		menuBar.getMenus().add(menuFile);

		root.setTop(menuBar);

		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		list.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {

				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent clipboardContent = new ClipboardContent();
				clipboardContent.putString(list.getSelectionModel().getSelectedItem());
				clipboard.setContent(clipboardContent);

			}
		});

		list.setOnKeyPressed(event -> {

			if (event.isControlDown() && event.getCode() == KeyCode.C) {

				ObservableList<String> selectedItems = list.getSelectionModel().getSelectedItems();

				String allItems = selectedItems.stream().map(elment -> elment).collect(Collectors.joining("\n"));

				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent clipboardContent = new ClipboardContent();
				clipboardContent.putString(allItems);
				clipboard.setContent(clipboardContent);

			}

		});

		list.setOnDragOver(event -> {

			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

		});

		list.setOnDragDropped(event -> {

			boolean isSuccess = false;

			if (event.getGestureSource() != list && event.getDragboard().hasFiles()) {

				ExecutorService executor = Executors.newFixedThreadPool(10);

				String path = event.getDragboard().getFiles().get(0).getAbsolutePath();

				primaryStage.setTitle(event.getDragboard().getFiles().get(0).getName());

				Future<String> future = executor.submit(new DataReader(path));

				String contentToParse = "";

				try {
					contentToParse = future.get();
				} catch (InterruptedException | ExecutionException e) {

					e.printStackTrace();
				}

				Future<List<String>> futureContentNarrower = executor.submit(new ContentNarrower(contentToParse));

				try {
					narrowContent = futureContentNarrower.get();
				} catch (InterruptedException | ExecutionException e) {

					e.printStackTrace();
				}

				executor.shutdown();

				ObservableList<String> itemsRetrive = FXCollections.observableArrayList(narrowContent);

				list.setItems(itemsRetrive);

				isSuccess = true;

			}
			event.setDropCompleted(isSuccess);
			event.consume();

		});

		root.setCenter(list);

		Scene scene = new Scene(root, 600, 700);

		primaryStage.setTitle("Drag PDF file");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {

		launch(args);
	}

}
