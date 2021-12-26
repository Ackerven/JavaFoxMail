package outmail.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import outmail.controller.API;
import outmail.model.Config;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/18 20:29
 */
public class InitPane {
    public static void start(boolean newConfig, ComboBox<Config> cbo) {
        final String[] IP = new String[]{"POP 服务器：", "IMAP服务器："};
        Stage stage = new Stage();
        stage.setTitle("新建账号");
        Pane pane = new Pane();
        Hyperlink help = new Hyperlink("帮助");
        help.setBorder(null);
        help.setUnderline(false);
        help.setLayoutX(515);
        help.setLayoutY(7);
        help.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.baidu.com"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        javafx.scene.control.Label type = new javafx.scene.control.Label("接收服务器类型：");
        type.setLayoutX(56);
        type.setLayoutY(66);
        type.setPrefHeight(28);
        type.setPrefWidth(120);
        ChoiceBox<String> kind = new ChoiceBox<>();
        kind.getItems().addAll("POP", "IMAP");
        kind.setLayoutX(179);
        kind.setLayoutY(65);
        kind.setPrefHeight(30);
        kind.setPrefWidth(359);
        kind.setValue("POP");
        kind.setTooltip(new Tooltip("Select the receiving server type"));
        kind.setOnAction((event) -> {
            int selectedIndex = kind.getSelectionModel().getSelectedIndex();
            Object selectedItem = kind.getSelectionModel().getSelectedItem();

            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
            System.out.println(" ChoiceBox.getValue(): " + kind.getValue());
        });
        javafx.scene.control.Label server = new javafx.scene.control.Label("POP 服务器：");
        server.setLayoutX(79);
        server.setLayoutY(186);
        server.setPrefHeight(20);
        server.setPrefWidth(100);
        kind.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            server.setText(IP[newValue.intValue()]);
        });
        javafx.scene.control.Label account = new javafx.scene.control.Label("邮件帐号：");
        account.setLayoutX(101);
        account.setLayoutY(106);
        javafx.scene.control.TextField accounts = new javafx.scene.control.TextField();
        accounts.setLayoutX(179);
        accounts.setLayoutY(101);
        accounts.setPrefHeight(30);
        accounts.setPrefWidth(359);
        javafx.scene.control.Label password = new javafx.scene.control.Label("密码：");
        password.setLayoutX(131);
        password.setLayoutY(145);
        PasswordField passwordField = new PasswordField();
        passwordField.setLayoutX(179);
        passwordField.setLayoutY(140);
        passwordField.setPrefHeight(30);
        passwordField.setPrefWidth(359);
        javafx.scene.control.TextField server1 = new javafx.scene.control.TextField();
        server1.setLayoutX(179);
        server1.setLayoutY(181);
        server1.setPrefHeight(30);
        server1.setPrefWidth(162);
        javafx.scene.control.Label Server = new javafx.scene.control.Label("SMTP服务器：");
        Server.setLayoutX(76);
        Server.setLayoutY(221);
        Server.setPrefHeight(20);
        Server.setPrefWidth(103);
        javafx.scene.control.TextField server2 = new javafx.scene.control.TextField();
        server2.setLayoutX(179);
        server2.setLayoutY(216);
        server2.setPrefHeight(30);
        server2.setPrefWidth(162);
        CheckBox SSL1 = new CheckBox("SSL");
        SSL1.setLayoutX(359);
        SSL1.setLayoutY(186);
        SSL1.getTypeSelector();
        SSL1.setOnAction(event -> {
            if (SSL1.isSelected()){
                System.out.println("收件服务已开启SSL！");
            }
        });
        javafx.scene.control.Label serverPort = new javafx.scene.control.Label("端口：");
        serverPort.setLayoutX(423);
        serverPort.setLayoutY(186);
        javafx.scene.control.TextField port1 = new javafx.scene.control.TextField();
        port1.setLayoutX(471);
        port1.setLayoutY(181);
        port1.setPrefHeight(30);
        port1.setPrefWidth(66);
        CheckBox SSL2 = new CheckBox("SSL");
        SSL2.setLayoutX(359);
        SSL2.setLayoutY(221);
        SSL2.setOnAction(event -> {
            if (SSL2.isSelected()){
                System.out.println("SMTP已开启SSL！");
            }
        });
        javafx.scene.control.Label SMTPPort = new Label("端口：");
        SMTPPort.setLayoutX(423);
        SMTPPort.setLayoutY(221);
        javafx.scene.control.TextField port2 = new TextField();
        port2.setLayoutX(471);
        port2.setLayoutY(216);
        port2.setPrefHeight(30);
        port2.setPrefWidth(66);
        Button create = new Button("创建");
        create.setLayoutX(379);
        create.setLayoutY(497);
        create.setPrefHeight(30);
        create.setPrefWidth(88);
        create.setOnAction(event -> {
            if (Objects.equals(accounts.getText(), "")){
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("警告");
                warning.setHeaderText("您必须输入邮箱地址！");
                warning.setContentText("You must enter email address!");
                warning.showAndWait();
            }
            else {
                System.out.println(accounts.getText()+ " "+ passwordField.getText()+" "+ server2.getText()+" "+
                        port2.getText()+" "+ kind.getValue()+" "+ server1.getText()+" "+ port1.getText()+" "+ SSL2.isSelected()+" "+ SSL1.isSelected());
                if (API.addNewConfig(accounts.getText(),passwordField.getText(),server2.getText(),
                        port2.getText(),kind.getValue(),server1.getText(),port1.getText(),SSL2.isSelected(),SSL1.isSelected())) {
                    stage.close();
                    if(!newConfig) {
                        API.checkNewMail();
                        HomePane.start();
                    } else {
                        API.checkNewMail();
                        cbo.getItems().add(API.CONFIGS.get(API.CONFIGS.size() - 1));
                    }

                } else {
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("警告");
                    warning.setHeaderText("添加失败！");
                    warning.setContentText("Add failed！");
                    warning.showAndWait();
                }
            }
        });
        Button cancel = new Button("取消");
        cancel.setLayoutX(475);
        cancel.setLayoutY(497);
        cancel.setPrefHeight(30);
        cancel.setPrefWidth(88);
        cancel.setOnAction(event -> {
            stage.close();
        });
        pane.getChildren().addAll(help,type,kind,account,accounts,
                password,passwordField,server,server1,Server,
                server2,SSL1,serverPort,SSL2,SMTPPort,port1,port2,create,cancel);

        Scene scene = new Scene(pane,583,541);
        stage.setScene(scene);
        stage.show();
    }


}
