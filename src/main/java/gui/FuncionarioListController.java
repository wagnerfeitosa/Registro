package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import app.MainFX;
import dao.DepartamentoDao;
import dao.FuncionarioDao;
import entities.Funcionario;
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

public class FuncionarioListController implements Initializable,ObserverChangerListener{
	
	private FuncionarioDao funcionarioDao;
	private ObservableList<Funcionario> obsList;
	
	@FXML
	private TableView<Funcionario> tableViewFuncionario;
	
	@FXML
	private TableColumn<Funcionario, Integer> tbId;
	
	@FXML
	private TableColumn<Funcionario, String> tbNome;
	
	@FXML
	private TableColumn<Funcionario, String> tbEmail;
	
	@FXML
	private TableColumn<Funcionario, Date> tbNascimento;
	
	@FXML
	private TableColumn<Funcionario, Double> tbSalario;
	
	@FXML
	private TableColumn<Funcionario, Funcionario> tbEditar;
	
	@FXML
	private TableColumn<Funcionario, Funcionario> tbRemover;
	
	@FXML
	private Button btNovo;
	
	public void setFuncionarioDao(FuncionarioDao funcionarioDao) {
		this.funcionarioDao = funcionarioDao;
	}
	
	@FXML
	public void btNovoAction(ActionEvent event) {
		Stage parent = Util.currentsStage(event);
		Funcionario obj = new Funcionario();
		createFormData(obj,"/gui/FuncionarioFormView.fxml", parent);
		
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		inicializarNode();
		
	}
	private void inicializarNode() {
		tbId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tbNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
		tbEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
		
		tbNascimento.setCellValueFactory(new PropertyValueFactory<>("Nascimento"));
		Util.formatTableColumnDate(tbNascimento, "dd/MM/yyyy");
		
		tbSalario.setCellValueFactory(new PropertyValueFactory<>("SalarioBase"));
		Util.formatTableColumnDouble(tbSalario, 2);
		
		Stage stage = (Stage) MainFX.getMainScene().getWindow();
		tableViewFuncionario.prefHeightProperty().bind(stage.heightProperty());
		
		
	}
	
	public void updateTableView() {
		if(funcionarioDao == null) {
			throw new IllegalStateException("funcionarioDao é vazio");
		}
		List<Funcionario> list = funcionarioDao.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewFuncionario.setItems(obsList);
		
		initEditButton();
		initRemoveButton();
	}
	
	private void createFormData(Funcionario obj,String fxml,Stage parent) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
			Pane pane = loader.load();
			
			FuncionarioFormController controller = loader.getController();
			controller.setDepartamentoDao(DepartamentoDao.getInstace(),FuncionarioDao.getInstance());
			controller.setFuncionario(obj);
			controller.loadCombobox();
			controller.escreverObserverChangerListener(this);
			controller.updateData();
			
			Stage stage = new Stage();
			stage.setScene(new Scene(pane));
			stage.setTitle("FORMULARIO FUNCIONARIO");
			stage.setResizable(false);
			stage.initOwner(parent);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.showAndWait();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateDate() {
		updateTableView();
		
	}
	
	/*Responsavel em criar um botao Edit em cada linha da tabela
	 * Para atualizar os dados */
	public void initEditButton() {
		tbEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbEditar.setCellFactory(param -> new TableCell<Funcionario,Funcionario>(){
			private final Button button = new Button("Editar");
			
			@Override
			protected void updateItem(Funcionario obj,boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return ;
				}
				setGraphic(button);
				button.setOnAction(event -> createFormData(obj,"/gui/FuncionarioFormView.fxml", Util.currentsStage(event)));
			}
			
		});
	}
	/*responsavel por criar um botao para remover itens*/
	public void initRemoveButton() {
		tbRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tbRemover.setCellFactory(param -> new TableCell<Funcionario,Funcionario>(){
			private final Button button = new Button("Remover");
			
			@Override
			protected void updateItem(Funcionario obj,boolean empty) {
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
	private void  removeEntity(Funcionario obj) {
		Optional<ButtonType> result = util.Alerts.showConfirmation("Confirmation", "Tem certeza que deseja deletar");
		
		if(result.get() == ButtonType.OK) {
			if(funcionarioDao == null) {
				throw new IllegalStateException("service was null");
			}
			try {
				funcionarioDao.delete(obj);
				updateTableView();
				
			}catch(Exception e) {
				util.Alerts.showAlerts("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
						
		}
	}


}
