package vista;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;

import controlador.MainAgendaApp;
import controlador.UtilesData;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelo.Persona;

public class ControladorPersona {
    @FXML
    private TableView<Persona> personTable;
    @FXML
    private TableColumn<Persona, String> firstNameColumn;
    @FXML
    private TableColumn<Persona, String> lastNameColumn;
    
    @FXML
    private Label idLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label streetLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label birthdayLabel;
    
    // Reference to the main application.
    private MainAgendaApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public ControladorPersona() {
    }
    /**
     * Fills all text fields to show details about the person.
     * If the specified person is null, all text fields are cleared.
     * 
     * @param person the person or null
     */
    
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        firstNameColumn.setCellValueFactory(
                cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(
                cellData -> cellData.getValue().lastNameProperty());
        
        
        // Clear person details.
        showPersonDetails(null);

        // Listen for selection changes and show the person details when changed.
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));
    }
    
    /**
     * Is called by the main application to give a reference back to itself.
     * @param mainApp
     */
    public void setMainApp(MainAgendaApp mainApp) {
        this.mainApp = mainApp;
        
        // Add observable list data to the table
        personTable.setItems(mainApp.getPersonData());
    }

    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    public void borrarPersonaBaseData() {
        int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex >= 0) {
        	Persona ptemp = personTable.getSelectionModel().getSelectedItem();
        	
        	ODB odb = ODBFactory.open ("BaseDatosNeoDatis.odb");
        	
        	try {
    			Objects <Persona> personas = odb.getObjects(Persona.class);
    			while (personas.hasNext()) {
    				Persona p = personas.next();
    				if (p.getIdPersona() == ptemp.getIdPersona()) {
    					odb.delete(p);
    					personTable.getItems().remove(selectedIndex);

    				}
    				
    			}
           	}catch (Exception e) { // catches ANY exception
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se puede cargar los datos");

                alert.showAndWait();
           	}finally {
           		if (odb != null) {
            // Close the database
               	odb.close();
           		}
           	}
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No seleccinado");
            alert.setHeaderText("Ninguna persona seleccionada");
            alert.setContentText("Por favor, seleccione a alguna persona.");

            alert.showAndWait();
        }
    }
    
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new person. 
     */
    @FXML
    private void handleNewPerson() {
        Persona tempPerson = new Persona();
        boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
        if (okClicked) {
        	int id = -1;
            mainApp.getPersonData().add(tempPerson);
			ODB odb = ODBFactory.open ("BaseDatosNeoDatis.odb");
				try {
					Objects <Persona> lista = odb.getObjects(Persona.class);
					if (lista.isEmpty()) {
						id = 1;
					}else {
						id = obtenerId(lista);
						id++;
					}
					
					tempPerson.setIdPersona(id);
					odb.store(tempPerson);
					
				}catch (Exception e ) {
					e.printStackTrace();
				}finally {
		             // Close the database
					odb.close();
			}
        }
    }
    
    public int obtenerId (Objects<Persona> lista) {
    	int id = 0;
    	while (lista.hasNext()) {
    		Persona persona = (Persona) lista.next();
    		if (persona.getIdPersona() > id) {
    			id = persona.getIdPersona();
    		}
    	}
    	return id;
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected person.
     */
    @FXML
    private void handleEditPerson() {
        Persona selectedPerson = personTable.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            boolean okClicked = mainApp.showPersonEditDialog(selectedPerson);
            
            if (okClicked) {
                showPersonDetails(selectedPerson);
                editarPersonaBaseData (selectedPerson);
            }
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No seleccionado");
            alert.setHeaderText("No se ha seleccionado ninguna persona");
            alert.setContentText("Por favor, selecciona a una persona.");

            alert.showAndWait();
        }
    } 
    
    public void editarPersonaBaseData(Persona persona) {
        try {
        	ODB odb = ODBFactory.open ("BaseDatosNeoDatis.odb");
        	try {
    			Objects <Persona> personas = odb.getObjects(Persona.class);
    			
    			while (personas.hasNext()) {
    				Persona p = personas.next();
    				
    				if (p.getIdPersona() == persona.getIdPersona()) {
    					p.setFirstName(persona.getFirstName());
    	    			p.setLastName(persona.getLastName());
    	    			p.setStreet(persona.getStreet());
    	    			p.setPostalCode(persona.getPostalCode());
    	    			p.setCity(persona.getCity());
    	    			p.setBirthday(persona.getNacimiento());
    	    			
    	    			odb.store(p);
    				}
    			}
    			
    			
    			
           	}catch (Exception e) { // catches ANY exception
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se puede cargar los datos");

                alert.showAndWait();
           	}finally {
           		if (odb != null) {
            // Close the database
               	odb.close();
           		}
           	}
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se puede cargar los datos");

            alert.showAndWait();
        }
    }

    
    
    
    private void showPersonDetails(Persona person) {
        if (person != null) {
            // Fill the labels with info from the person object.
        	idLabel.setText(Integer.toString(person.getIdPersona()));
        	firstNameLabel.setText(person.getFirstName());
            lastNameLabel.setText(person.getLastName());
            streetLabel.setText(person.getStreet());
            postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
            cityLabel.setText(person.getCity());
            birthdayLabel.setText(UtilesData.format(person.getNacimiento()));
           // cargarPersonas (person);
            
        } else {
            // Person is null, remove all the text.
        	idLabel.setText("");
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            streetLabel.setText("");
            postalCodeLabel.setText("");
            cityLabel.setText("");
            birthdayLabel.setText("");
        }
    }
}

