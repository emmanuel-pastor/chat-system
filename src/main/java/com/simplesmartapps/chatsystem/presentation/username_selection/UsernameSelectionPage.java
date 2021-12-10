package com.simplesmartapps.chatsystem.presentation.username_selection;


import com.simplesmartapps.chatsystem.ChatSystemApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class UsernameSelectionPage implements Initializable {
    private final UsernameSelectionViewModel mViewModel;

    public UsernameSelectionPage() {
        mViewModel = ChatSystemApplication.injector.getInstance(UsernameSelectionViewModel.class);
    }

    @FXML
    private TextField usernameTextField;

    @FXML
    private FontIcon validationIcon;

    @FXML
    private Text errorTextView;

    @FXML
    private Button submitButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mViewModel.mState.observe(this, newState -> {
            switch (newState) {
                case LOADING -> {
                    submitButton.setVisible(false);
                    loadingIndicator.setVisible(true);
                    usernameTextField.setDisable(true);
                    validationIcon.setVisible(false);
                    errorTextView.setVisible(false);
                }
                case READY -> {
                    loadingIndicator.setVisible(false);
                    submitButton.setVisible(true);
                    usernameTextField.setDisable(false);
                    validationIcon.setVisible(true);
                    errorTextView.setVisible(false);
                }
                case ERROR -> {
                    loadingIndicator.setVisible(false);
                    submitButton.setVisible(true);
                    usernameTextField.setDisable(false);
                    validationIcon.setVisible(false);
                    errorTextView.setVisible(true);
                }
            }
        });
        mViewModel.mErrorText.observe(this, newText -> errorTextView.setText(newText));

        mViewModel.mIsValid.observe(this, newState -> {
            switch (newState) {
                case VALID -> {
                    validationIcon.setIconLiteral("fa-check-circle");
                    validationIcon.setIconColor(Color.web("#1f9a4e"));
                }
                case INVALID -> {
                    validationIcon.setIconLiteral("fa-times-circle");
                    validationIcon.setIconColor(Color.web("#d93939"));
                }
            }
        });

        submitButton.setOnMouseClicked(event -> mViewModel.onSubmitButtonClicked(usernameTextField.getText()));

        usernameTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                mViewModel.onEnterKeyPressed(usernameTextField.getText());
            }
        });
    }
}
