package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import app.MainFX;
import dao.DepartamentoDao;
import entities.Departamento;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.ObserverChangerListener;
import util.Util;

public class DepartamentoListController implements Initializable,ObserverChangerListener {
	
	private DepartamentoDao departamentoDao;
	private ObservableList<Departamento> obsList;
	
	@FXML
	private TableView<Departamento> tableViewDepartamento;
	
	@FXML
	private TableColumn<Departamento, Integer> tbId;
	
	@FXML
	private TableColumn<Departamento, String> tbNome;
	
	@FXML
	private TableColumn<Departamento, Departamento> tbEditar;
	
	@FXML
	private TableColumn<Departamento, Departamento> tbRemover;
	
	@FXML
	private Button btNew;
	
	public void setDepartamentoDao(DepartamentoDao departamentoDao) {
		this.departamentoDao = departamentoDao;
	}
	
	@FXML
	public void btNewAction(ActionEvent event) {
		Stage parent = Util.currentsStage(event);
		Departamento obj = new Departamento();
		createFormData(obj, "/gui/DepartamentoFormView.fxml", parent);
	  
	}
    
	//METODO DE EXECUÇÃO 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNode();

		
	}
    
	//ASSOCIANDO A ENTIDADE DEPARTAMENTO COM TABLECOLUMN VIEW
	private void initializeNode() {
		tbId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tbNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
		
		Stage stage = (Stage) MainFX.getMainScene().getWindow();
		tableViewDepartamento.prefHeightProperty().bind(stage.heightProperty());
		
	}
	//CAPTURANDO OS DADOS DEPARTAMENTO E CARREGANDO A LISTA tableViewdepartamento
	public void updateTableView() {
		if(departamentoDao == null) {
			throw new IllegalStateException("depDao is null");
			
		}
		List<Departamento> list = departamentoDao.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartamento.setItems(obsList);
		
		initEditButton();
		initRemoveButton();
	}
	
	//METODO PARA SALVAR NOVO DEPARTAMENTO
	private void createFormData(Departamento obj,String view,Stage parent) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
			Pane pane = loader.load();
			
			DepartamentoFormController controller = loader.getController();
			controller.setDepartamento(obj);
			controller.setDepartamentoDao(departamentoDao.getInstace());
			controller.subscreverObserverChangerListener(this);
			controller.updateFormData();
			
			
			Stage stage = new Stage();
			stage.setTitle("NOVO DEPARTAMENTO");
			stage.setScene(new Scene(pane));
			stage.setResizable(false);
			stage.initOwner(parent);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.showAndWait();
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateDate() {
		updateTableView();
		
	}
	
	public void initEditButton() {
		tbEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbEditar.setCellFactory(param -> new TableCell<Departamento,Departamento>(){
			private final Button button = new Button("Editar");
			
			@Override
			protected void updateItem(Departamento obj,boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return ;
				}
				setGraphic(button);
				button.setOnAction(event -> createFormData(obj,"/gui/DepartamentoFormView.fxml", Util.currentsStage(event)));
			}
			
		});
	}
	/*responsavel por criar um botao para remover itens*/
	public void initRemoveButton() {
		tbRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbRemover.setCellFactory(param -> new TableCell<Departamento,Departamento>(){
			private final Button button = new Button("Remover");
			
			@Override
			protected void updateItem(Departamento obj,boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	/*responsavel por remover dados*/
	private void  removeEntity(Departamento obj) {
		Optional<ButtonType> result = util.Alerts.showConfirmation("Confirmation", "Tem certeza que deseja deletar");
		
		if(result.get() == ButtonType.OK) {
			if(departamentoDao == null) {
				throw new IllegalStateException("service was null");
			}
			try {
				departamentoDao.delete(obj);
				updateTableView();
				
			}catch(Exception e) {
				util.Alerts.showAlerts("Erro", "ao remover entity", null, AlertType.ERROR);
			}
						
		}
	}


}
