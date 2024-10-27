package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import dao.DepartamentoDao;
import dao.FuncionarioDao;
import entities.Departamento;
import entities.Funcionario;
import exceptions.ValidarDados;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import util.Constraint;
import util.ObserverChangerListener;
import util.Util;

public class FuncionarioFormController implements Initializable {
	
	private DepartamentoDao departamentoDao;
	private FuncionarioDao funcionarioDao;
	private Funcionario entity;
	private ObservableList<Departamento> obsList;
	private List<ObserverChangerListener> observers = new ArrayList<>();
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker datePickerNascimento;
	
	@FXML
	private TextField txtSalario;
	
	@FXML
	private ComboBox<Departamento> comboboxDepartamento;
	
	
	@FXML
	private Label labelErroNome;
	
	@FXML
	private Label labelErroEmail;
	
	@FXML
	private Label labelErroNascimento;
	
	@FXML
	private  Label labelErroSalario;
	
	@FXML
	private Label labelErroDepartamento;
	
	@FXML 
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	public void setDepartamentoDao(DepartamentoDao departamentoDao,FuncionarioDao funcionarioDao) {
		this.departamentoDao = departamentoDao;
		this.funcionarioDao = funcionarioDao;
	
	}
	public void setFuncionario(Funcionario entity) {
		this.entity = entity;
	}
	
	public void escreverObserverChangerListener(ObserverChangerListener observer) {
		observers.add(observer);
	}
	
	public void notificarObserverChangerListener() {
		for(ObserverChangerListener observer : observers){
			observer.updateDate();
		}
	}
	
	@FXML
	public void btSalvarAction(ActionEvent event) {
		if(funcionarioDao == null) {
			throw new IllegalStateException("funcionarioDao é vazio");
		}
		if(entity == null) {
			throw new IllegalStateException("entity é vazio");
		}
		try {
			if(entity.getId() == null) {
				entity = getEntity();
				funcionarioDao.insert(entity);
				
				Util.currentsStage(event).close();
				notificarObserverChangerListener();
			}
			else {
				entity = getEntity();
				funcionarioDao.update(entity);
				
				Util.currentsStage(event).close();
				notificarObserverChangerListener();
			}
			
			
		}catch(ValidarDados e) {
			setErroLabel(e.getError());
			e.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
	
		}
	}
	
	private Funcionario getEntity() {
		Funcionario obj = new Funcionario();
		ValidarDados validar = new ValidarDados("Erro ao validar entrada");
		
		obj.setId(Util.tryParseToInt(txtId.getText()));
		
		if(txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			validar.addErros("nome", "Nome não pode ser vazio");
		}
		obj.setNome(txtNome.getText());
		
		if(txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			validar.addErros("email", "Email não pode ser vazio");
		}
		obj.setEmail(txtEmail.getText());
		
		if(datePickerNascimento.getValue() == null) {
			validar.addErros("data", "Data nascimento não pode ser vazio");
		}
		else {
			Instant instant = Instant.from(datePickerNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setNascimento(Date.from(instant));
		}
		
		if(txtSalario.getText() == null || txtSalario.getText().trim().equals("")) {
			validar.addErros("salario", "Salario não pode vazio");
		}
		
		obj.setSalarioBase(Util.tryParseToDouble(txtSalario.getText()));
		
		obj.setDepartamento(comboboxDepartamento.getValue());
		
		if(validar.getError().size() > 0) {
			throw validar;
		}
		return obj;
	}
	
	public void updateData() {
		if(entity ==  null) {
			throw new IllegalStateException("Entidade é nulo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtEmail.setText(entity.getEmail());
		
		//Locale.setDefault(Locale.US);
		txtSalario.setText(String.format("%.2f", entity.getSalarioBase()));
		
		//PEgando horario da maquina local do usuario
		if(entity.getNascimento() != null) {
			datePickerNascimento.setValue(LocalDate.ofInstant(entity.getNascimento().toInstant(), ZoneId.systemDefault()));
		}
		
		//seleciona o primeiro elemento
		if(entity.getDepartamento() == null) {
			comboboxDepartamento.getSelectionModel().selectFirst();
		}else {
			comboboxDepartamento.setValue(entity.getDepartamento());
		}
	}
	@FXML
	public void btCancelarAction(ActionEvent event) {
		Util.currentsStage(event).close();
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		inicializaNode();
		
	}

	private void inicializaNode() {
		Constraint.settextFieldInteger(txtId);
		Constraint.setTextFieldMaxLength(txtNome, 10);
		Constraint.setTextFieldMaxLength(txtEmail, 20);
		Constraint.setTextFieldDouble(txtSalario);
		Constraint.setTextFieldMaxLength(txtSalario, 9);
		
		Util.formatDatePicker(datePickerNascimento, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
		
		
	}
	public void loadCombobox() {
		
		List<Departamento> list = departamentoDao.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboboxDepartamento.setItems(obsList);
	}
	
	//RESPONSAVEL POR APRESENTAÇÃO DOS DEPARTAMENTO NA COMBOBOX FORMATAÇÃO 
		private void initializeComboBoxDepartment() {
			Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
				@Override
				protected void updateItem(Departamento item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? "" : item.getNome());
				}
			};

			comboboxDepartamento.setCellFactory(factory);
			comboboxDepartamento.setButtonCell(factory.call(null));
		}
		
		public void setErroLabel(Map<String,String> erro) {
			Set<String> campo = erro.keySet();
			
			labelErroNome.setText(campo.contains("nome") ? erro.get("nome") : "");
			labelErroEmail.setText(campo.contains("email") ? erro.get("email") : "");
			labelErroNascimento.setText(campo.contains("data") ? erro.get("data") : "");
			labelErroSalario.setText(campo.contains("salario") ? erro.get("salario") :"");
		}
		
		
}
