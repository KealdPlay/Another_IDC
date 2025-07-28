
package controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
public class ConfirmacionVEController implements Initializable {
    
    @FXML private VBox mainContainer;
    @FXML private Label messageLabel;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    
    public enum DialogResult {
        CONFIRM, CANCEL
    }
    
    private DialogResult result = DialogResult.CANCEL;
    private Stage dialogStage;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupHoverEffects();
    }
    
    private void setupHoverEffects() {
        cancelButton.setOnMouseEntered(e -> 
            cancelButton.setStyle(cancelButton.getStyle() + " -fx-background-color: rgba(255,255,255,0.1);")
        );
        cancelButton.setOnMouseExited(e -> 
            cancelButton.setStyle(cancelButton.getStyle().replace(" -fx-background-color: rgba(255,255,255,0.1);", ""))
        );
        
        confirmButton.setOnMouseEntered(e -> 
            confirmButton.setStyle(confirmButton.getStyle().replace("#ff4444", "#ff6666"))
        );
        confirmButton.setOnMouseExited(e -> 
            confirmButton.setStyle(confirmButton.getStyle().replace("#ff6666", "#ff4444"))
        );
    }
    
    @FXML
    private void handleCancel() {
        result = DialogResult.CANCEL;
        closeDialog();
    }
    
    @FXML
    private void handleConfirm() {
        result = DialogResult.CONFIRM;
        closeDialog();
    }
    
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    public void setButtonTexts(String cancelText, String confirmText) {
        cancelButton.setText(cancelText);
        confirmButton.setText(confirmText);
    }
    
    public void setConfirmButtonStyle(String style) {
        confirmButton.setStyle(style);
    }
    
    public DialogResult getResult() {
        return result;
    }
    
}