package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import app.MainFX;
import dao.DepartamentoDao;
import dao.FuncionarioDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import util.Alerts;

public class MainController implements Initializable{
	
	@FXML
	private MenuItem menuItemDepartamento;
	
	@FXML
	private MenuItem menuItemFuncionario;
	
	@FXML
	private MenuItem menuItemSobre;
	
	@FXML
	public void menuItemDepartamentoAction() {
		loadView("/gui/DepartamentoListView.fxml", (DepartamentoListController controller) ->{
			controller.setDepartamentoDao(DepartamentoDao.getInstace());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void menuItemFuncionarioAction() {
		loadView("/gui/FuncionarioListView.fxml", (FuncionarioListController controller)->{
			controller.setFuncionarioDao(FuncionarioDao.getInstance());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void menuItemSobreAction() {
		loadView("/gui/SobreView.fxml",x->{});
	}
	
	private synchronized <T> void loadView(String view, Consumer<T> initializeController) {
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
			VBox newVbox = loader.load();
			Scene mainScene = MainFX.getMainScene();
			VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVbox.getChildren().get(0);
			mainVbox.getChildren().clear();
			mainVbox.getChildren().add(mainMenu);
			mainVbox.getChildren().addAll(newVbox.getChildren());
			
			T controller = loader.getController();
			initializeController.accept(controller);
			
			
			
			
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlerts("IOException", "Erro MainController.loadView", null, AlertType.ERROR);
		}
	}
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
	

}
