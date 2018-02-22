import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static javafx.scene.paint.Color.RED;

public class BoardStart {

    private Scene boardScene;
    private TableView<String> history;
    private TableView<String> link;
    private TextField text;
    private Text sizeImages;
    private History baseHistory;


    public BoardStart(){
        baseHistory = new History();
    }

    public Scene boardInitialization() {
        boardScene = new Scene(createContent());
        history.setItems(baseHistory.getLinks());
        return boardScene;
    }

    private StackPane createContent() {
        StackPane root  = new StackPane();
        root.setPrefSize(1000,600);
        root.getChildren().addAll(getFieldText());
        root.getChildren().addAll(addButon());
        root.getChildren().addAll(addButonBack());
        history = links("History",history, 255);
        root.getChildren().addAll(history);
        link = links("Link", link, -245);
        root.getChildren().addAll(link);
        root.getChildren().addAll(sizeImage());
        System.out.println(link.selectionModelProperty().toString());
        return root;
    }

    private Node sizeImage() {
        sizeImages = new Text("Size Images: ");
        sizeImages.setTranslateY(-200);
        sizeImages.setTranslateX(250);
        return sizeImages;
    }

    private TableView<String> links(String name, TableView<String> table, int rotate) {
        table = new  TableView<>();

        TableColumn<String, String> linkColumn = new TableColumn<>(name);
        linkColumn.setMinWidth(100);
        linkColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        table.getColumns().addAll(linkColumn);
        table.setTranslateY(80);
        table.setTranslateX(rotate);
        table.setMaxSize(500,350);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tv -> {
            TableRow<String> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty() && event.getButton()== MouseButton.PRIMARY
                        && event.getClickCount() == 2) {

                    String clickedRow = row.getItem();
                    System.out.println(clickedRow);
                    refresh(clickedRow, true);
                }
            });
            return row ;
        });


        return table;
    }

    private Node addButon() {

        Button button = new Button();
        button.setText("OK");
        button.setOnAction(event -> {
            refresh(text.getText(),true);
        });
        button.setTranslateY(-150);
        button.setTranslateX(-100);
        return button;
    }

    private Node addButonBack() {

        Button button = new Button();
        button.setText("Back");
        button.setOnAction(event -> {
            refresh(baseHistory.getLastUlr(),false);
        });
        button.setTranslateY(-200);
        button.setTranslateX(-350);
        return button;
    }

    private List<Node> getFieldText() {
        text = new TextField("");
        text.setTranslateY(-200);
        text.setTranslateX(-100);
        text.setMaxSize(400,40);
        Text textShow = new Text("Write adress");
        textShow.setTranslateY(-230);
        textShow.setTranslateX(-100);
        return Arrays.asList(text,textShow);
    }



    public ObservableList<String> getLink(String url) throws IOException {
        ObservableList<String> link = FXCollections.observableArrayList();
        link.addAll(new FindLink().getListLink(url));
        return link;
    }

    private void refresh(String url, boolean checkIfInsert )  {
        text.setText(url);
        try {
            FindImage findImage = new FindImage(url);
            if (checkIfInsert) baseHistory.insertLinks(url);
            sizeImages.setText(String.valueOf(findImage.getSumSize()) + "Mb, Amount " + findImage.getAmount());
            link.setItems(getLink(url));
            history.setItems(baseHistory.getLinks());
        }
        catch ( Exception e) {
            text.setText("Wrong adress!!!");
        }
    }

}
