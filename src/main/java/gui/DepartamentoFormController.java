package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import dao.DepartamentoDao;
import entities.Departamento;
import exceptions.ValidarDados;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import util.Constraint;
import util.ObserverChangerListener;
import util.Util;

public class DepartamentoFormController implements Initializable {

	private DepartamentoDao departamentoDao;
	private Departamento departamento;

	private List<ObserverChangerListener> observers = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private Label labelErroNome;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	public void setDepartamentoDao(DepartamentoDao departamentoDao) {
		this.departamentoDao = departamentoDao;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public void subscreverObserverChangerListener(ObserverChangerListener listener) {
		observers.add(listener);
	}

	public void notificarObserverChangerListener() {
		for (ObserverChangerListener listener : observers) {
			listener.updateDate();
		}
	}

	@FXML
	public void btSalvarAction(ActionEvent event) {

		if (departamento == null) {
			throw new IllegalStateException("Entity Departamento was null");
		}
		if (departamentoDao == null) {
			throw new IllegalStateException("DepartamentoDao was null");
		}

		try {
			if (departamento.getId() == null) {
				departamento = getFormData();
				departamentoDao.insert(departamento);

				notificarObserverChangerListener();

				Util.currentsStage(event).close();
				
			} else {
				// EDITANDO DEPARTAMENTO
				departamento = getFormData();
				departamentoDao.update(departamento);

				notificarObserverChangerListener();

				Util.currentsStage(event).close();
			}
		} catch (ValidarDados e) {
			setErroMensagens(e.getError());
		}

	}

	// METODO RESPOSAVÉL POR ADIONAR O OBJETO DEPARTAMENTO NO TEXTfIELD
	public void updateFormData() {
		if (departamento == null) {
			throw new IllegalStateException("Departamento was null");
		}
		txtId.setText(String.valueOf(departamento.getId()));
		txtNome.setText(departamento.getNome());
	}

	// RESPONSAVÉL POR ADIONAR TEXTfIELD DA VIEW NO OBJETO DEPARTAMENTO
	private Departamento getFormData() {

		Departamento obj = new Departamento();

		ValidarDados validar = new ValidarDados("Erro Validação dos dados");

		obj.setId(Util.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			validar.addErros("nome", "Nome não pode ser vazio");
		}
		obj.setNome(txtNome.getText());

		if (validar.getError().size() > 0) {
			throw validar;
		}

		return obj;
	}

	@FXML
	public void btCancelarAction(ActionEvent event) {
		Util.currentsStage(event).close();

	}

	public void setErroMensagens(Map<String, String> erro) {
		Set<String> fields = erro.keySet();

		if (fields.contains("nome")) {
			labelErroNome.setText(erro.get("nome"));
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNode();

	}

	private void initializeNode() {
		Constraint.settextFieldInteger(txtId);
		Constraint.setTextFieldMaxLength(txtNome, 10);

	}

}
