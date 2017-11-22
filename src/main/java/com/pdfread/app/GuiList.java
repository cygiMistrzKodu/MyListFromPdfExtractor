package com.pdfread.app;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GuiList extends Application {

	private List<String> narrowContent = new ArrayList<>();
	private ListView<String> list = new ListView<String>();

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		StackPane root = new StackPane();

		list.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {

				StringSelection selection = new StringSelection(list.getSelectionModel().getSelectedItem());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);

				System.out.println(list.getSelectionModel().getSelectedItem());

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

		root.getChildren().add(list);

		Scene scene = new Scene(root, 600, 900);

		primaryStage.setTitle("Drag PDF file");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {

		launch(args);
	}

}
