package outmail.gui;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import outmail.controller.API;
import outmail.model.Config;
import outmail.model.Mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/18 20:29
 */

/**
 * @author Coke.
 * @version 2.0
 * @date 2021/12/19 10:25
 */

public class HomePane {
    static Config config;

    public static void start() {
        Stage stage = new Stage();
        Mail tmp = new Mail();
        BorderPane pane = new BorderPane(); // 主面板

        //下拉列表
        ComboBox<Config> cbo = new ComboBox<>();
        cbo.getItems().addAll(API.CONFIGS);
        cbo.setValue(API.CONFIGS.get(0));
        config = cbo.getValue();
        cbo.setPrefSize(80, 35);

        // 列表
        ListView<Mail> listView = mailListView(config);


        ImageView imageViewGetMail = null;
        ImageView imageViewSendMail = null;
        ImageView imageViewReplyMail = null;
        ImageView imageViewDelMail = null;
        ImageView imageViewAddNewConfig = null;
        ImageView imageViewDelConfig = null;
        ImageView imageViewInbox = null;
        ImageView imageViewDraftBox = null;
        ImageView imageViewSendBox = null;
        ImageView imageViewContact = null;

        try {
            imageViewGetMail = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/getMail.png").getFile())));
            imageViewSendMail = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/sendMail.png").getFile())));
            imageViewReplyMail = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/replyMail.png").getFile())));
            imageViewDelMail = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/delMail.png").getFile())));
            imageViewAddNewConfig = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/addNewConfig.png").getFile())));
            imageViewDelConfig = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/delConfig.png").getFile())));
            imageViewInbox = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/inbox.png").getFile())));
            imageViewDraftBox = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/draftBox.png").getFile())));
            imageViewSendBox = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/sendBox.png").getFile())));
            imageViewContact = new ImageView(new Image(new FileInputStream(HomePane.class.getClassLoader().getResource("home/contact.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 顶部按钮
//        ImageView imageViewGetMail = new ImageView("file:D:\\Java高编\\FoxMail\\img\\getMail.png");
        imageViewGetMail.setFitHeight(25);
        imageViewGetMail.setFitWidth(25);
//        ImageView imageViewSendMail = new ImageView("file:D:\\Java高编\\FoxMail\\img\\sendMail.png");
        imageViewSendMail.setFitHeight(25);
        imageViewSendMail.setFitWidth(25);
//        ImageView imageViewReplyMail = new ImageView("file:D:\\Java高编\\FoxMail\\img\\replyMail.png");
        imageViewReplyMail.setFitHeight(25);
        imageViewReplyMail.setFitWidth(25);
//        ImageView imageViewDelMail = new ImageView("file:D:\\Java高编\\FoxMail\\img\\delMail.png");
        imageViewDelMail.setFitHeight(25);
        imageViewDelMail.setFitWidth(25);
//        ImageView imageViewAddNewConfig = new ImageView("file:D:\\Java高编\\FoxMail\\img\\addNewConfig.png");
        imageViewAddNewConfig.setFitHeight(25);
        imageViewAddNewConfig.setFitWidth(25);
//        ImageView imageViewDelConfig = new ImageView("file:D:\\Java高编\\FoxMail\\img\\delConfig.png");
        imageViewDelConfig.setFitHeight(25);
        imageViewDelConfig.setFitWidth(25);
//        ImageView imageViewInbox = new ImageView("file:D:\\Java高编\\FoxMail\\img\\inbox.png");
        imageViewInbox.setFitHeight(25);
        imageViewInbox.setFitWidth(25);
//        ImageView imageViewDraftBox = new ImageView("file:D:\\Java高编\\FoxMail\\img\\draftBox.png");
        imageViewDraftBox.setFitHeight(25);
        imageViewDraftBox.setFitWidth(25);
//        ImageView imageViewSendBox = new ImageView("file:D:\\Java高编\\FoxMail\\img\\sendBox.png");
        imageViewSendBox.setFitHeight(25);
        imageViewSendBox.setFitWidth(25);
//        ImageView imageViewContact = new ImageView("file:D:\\Java高编\\FoxMail\\img\\contact.png");
        imageViewContact.setFitHeight(25);
        imageViewContact.setFitWidth(25);


        // 顶部按钮
        Button getMail = new Button("收取邮件", imageViewGetMail);
        Button sendMail = new Button("发送邮件", imageViewSendMail);
        Button replyMail = new Button("回复", imageViewReplyMail);
        Button delMail = new Button("删除", imageViewDelMail);
        Button addNewConfig = new Button("添加邮箱", imageViewAddNewConfig);
        Button delConfig = new Button("删除邮箱", imageViewDelConfig);

        // 将按钮装入HBox容器中
        HBox hBox = new HBox();
        hBox.getChildren().addAll(getMail, sendMail, replyMail, delMail, addNewConfig, delConfig, cbo);
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(50);
        hBox.setAlignment(Pos.BOTTOM_LEFT);

        // 左边按钮
        Button inbox = new Button("收件箱", imageViewInbox);
        Button draftBox = new Button("草稿箱", imageViewDraftBox);
        Button sendBox = new Button("已发送", imageViewSendBox);
        Button contact = new Button("联系人", imageViewContact);

        // 将按钮装入HBox容器中
        VBox vBox = new VBox();
        vBox.getChildren().addAll(inbox, draftBox, sendBox, contact);
        vBox.setPadding(new Insets(20, 50, 50, 50));
        vBox.setSpacing(50);

        // 按钮样式
        // 收取邮件按钮
        getMail.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        // 变换鼠标样式
        getMail.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                getMail.setCursor(Cursor.HAND);
            }
        });
        // 设置按钮效果
        getMail.setEffect(new DropShadow());
        // 鼠标移动到按钮时显示提示
        Tooltip tooltipGetMail = new Tooltip("点击获取邮件");
        getMail.setTooltip(tooltipGetMail);

        // 发送邮件样式
        sendMail.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        sendMail.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sendMail.setCursor(Cursor.HAND);
            }
        });
        sendMail.setEffect(new DropShadow());
        Tooltip tooltipSendMail = new Tooltip("点击发送邮件");
        sendMail.setTooltip(tooltipSendMail);

        // 回复样式
        replyMail.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        replyMail.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                replyMail.setCursor(Cursor.HAND);
            }
        });
        replyMail.setEffect(new DropShadow());
        Tooltip tooltipReplyMail = new Tooltip("点击回复邮件");
        replyMail.setTooltip(tooltipReplyMail);

        // 删除样式
        delMail.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        delMail.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                delMail.setCursor(Cursor.HAND);
            }
        });
        delMail.setEffect(new DropShadow());
        Tooltip tooltipDelMail = new Tooltip("点击删除邮件");
        delMail.setTooltip(tooltipDelMail);

        // 添加邮箱样式
        addNewConfig.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        addNewConfig.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addNewConfig.setCursor(Cursor.HAND);
            }
        });
        addNewConfig.setEffect(new DropShadow());
        Tooltip tooltipAddNewConfig = new Tooltip("点击添加邮箱");
        addNewConfig.setTooltip(tooltipAddNewConfig);

        // 删除邮箱样式
        delConfig.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        delConfig.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                delConfig.setCursor(Cursor.HAND);
            }
        });
        delConfig.setEffect(new DropShadow());
        Tooltip tooltipDelConfig = new Tooltip("点击删除邮箱");
        delConfig.setTooltip(tooltipDelConfig);

        // 收件箱样式
        inbox.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        inbox.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                inbox.setCursor(Cursor.HAND);
            }
        });
        inbox.setEffect(new DropShadow());
        Tooltip tooltipInbox = new Tooltip("点击打开收件箱");
        inbox.setTooltip(tooltipInbox);

        // 草稿箱样式
        draftBox.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        draftBox.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                draftBox.setCursor(Cursor.HAND);
            }
        });
        draftBox.setEffect(new DropShadow());
        Tooltip tooltipDraftBox = new Tooltip("点击打开草稿箱");
        draftBox.setTooltip(tooltipDraftBox);

        // 已发送样式
        sendBox.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        sendBox.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sendBox.setCursor(Cursor.HAND);
            }
        });
        sendBox.setEffect(new DropShadow());
        Tooltip tooltipSendBox = new Tooltip("点击打开已发送邮件");
        sendBox.setTooltip(tooltipSendBox);

        // 联系人样式
        contact.setStyle("-fx-background-radius: 20;" + "-fx-border-radius: 18;");
        contact.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contact.setCursor(Cursor.HAND);
            }
        });
        contact.setEffect(new DropShadow());
        Tooltip tooltipContact = new Tooltip("点击打开联系人系统");
        contact.setTooltip(tooltipContact);

        // 标签
        Label senderLabel1 = new Label("发件人：");
        Label timeLabel1 = new Label("发送时间：");
        Label consigneeLabel1 = new Label("收件人：");
        Label ccLabel1 = new Label("抄送：");
        Label bccLabel1 = new Label("密送：");
        Label senderLabel = new Label();
        Label timeLabel = new Label();
        Label consigneeLabel = new Label();
        Label ccLabel = new Label();
        Label bccLabel = new Label();

        Font font = Font.font("Times New Romes", FontWeight.BOLD, FontPosture.ITALIC, 15);// 设置字体
        senderLabel.setFont(font);
        senderLabel1.setFont(font);
        timeLabel.setFont(font);
        timeLabel1.setFont(font);
        consigneeLabel.setFont(font);
        consigneeLabel1.setFont(font);
        ccLabel.setFont(font);
        ccLabel1.setFont(font);
        bccLabel.setFont(font);
        bccLabel1.setFont(font);

        ListView<String> attachmentList = new ListView<>();
        attachmentList.setPrefHeight(100);

        GridPane gp1 = new GridPane();
        GridPane gp2 = new GridPane();
        gp1.add(hBox, 0, 0);
        gp1.setAlignment(Pos.CENTER);
        gp1.setStyle("-fx-background-color:#66B3FF");
        gp1.setHgap(15);
        gp2.add(vBox, 0, 0);
        gp2.setStyle("-fx-background-color:#d0d0d0");
        gp2.setAlignment(Pos.TOP_CENTER);
        gp2.setHgap(15);

        BorderPane bp = new BorderPane();
        // 细节部分
        GridPane gridPane = new GridPane();
        gridPane.add(senderLabel1, 0, 0);
        gridPane.add(senderLabel, 1, 0);
        gridPane.add(timeLabel1, 0, 1);
        gridPane.add(timeLabel, 1, 1);
        gridPane.add(consigneeLabel1, 0, 2);
        gridPane.add(consigneeLabel, 1, 2);
        gridPane.add(ccLabel1, 0, 3);
        gridPane.add(ccLabel, 1, 3);
        gridPane.add(bccLabel1, 0, 4);
        gridPane.add(bccLabel, 1, 4);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // 邮件内容显示部分
        WebView wb = new WebView();
        WebEngine webEngine = wb.getEngine();


        // 按钮事件处理
        inbox.setOnAction(e -> {
            reloadList(listView, "InBox", cbo);
        });

        draftBox.setOnAction(e -> {
            reloadList(listView, "DraftBox", cbo);
        });

        sendBox.setOnAction(e -> {
            reloadList(listView, "SendBox", cbo);
        });

        delMail.setOnAction(e -> {
            Mail mail = listView.getSelectionModel().getSelectedItem();
            API.delMail(mail);
            reloadList(listView, "InBox", cbo);
        });

        getMail.setOnAction(e -> {
            reloadList(listView, "InBox", cbo);
        });

        sendMail.setOnAction(e -> {
            Mail mail = listView.getSelectionModel().getSelectedItem();
            if (mail == null)
                SendMailPane.start(config, null);
            else if (mail.getStatus().equals(Mail.DRAFT)) {
                SendMailPane.start(config, mail);
            } else {
                SendMailPane.start(config, null);
            }
        });

        replyMail.setOnAction(e -> {
            Mail tmps = listView.getSelectionModel().getSelectedItem();
            Mail mail = new Mail();
            mail.setSender(tmps.getTo());
            mail.setTo(tmps.getReplyTo());
            SendMailPane.start(config, mail);
        });

        contact.setOnAction(e -> {
            ContactPane.Start(config);
        });

        addNewConfig.setOnAction(e -> {
            InitPane.start(true);
            cbo.setItems(null);
            cbo.getItems().addAll(API.CONFIGS);
            cbo.setValue(config);
        });

        delConfig.setOnAction(e -> {
            API.delConfig(config.getId());
            cbo.setItems(null);
            cbo.getItems().addAll(API.CONFIGS);
            reloadList(listView, "InBox", cbo);
        });

        //listView 选中事件
        listView.getSelectionModel().selectedItemProperty().addListener(e -> {
            Mail mail = listView.getSelectionModel().getSelectedItem();
            if(mail == null) {
                mail = new Mail();
            } else {
                tmp.setId(mail.getId());
            }
            //调用显示邮件的set，把mail字段的值写进去
            senderLabel.setText(mail.getSender());
            timeLabel.setText(String.valueOf(mail.getSendData()));
            consigneeLabel.setText(mail.getTo());
            ccLabel.setText(mail.getCc());
            bccLabel.setText(mail.getBcc());

            if (mail.getHtmlContent() == null) {
                if (mail.getPlainContent() == null) {
                    webEngine.loadContent("", "text/html");
                } else {
                    webEngine.loadContent(mail.getPlainContent(), "text/plain");
                }
            } else {
                webEngine.loadContent(mail.getHtmlContent(), "text/html");
            }

            ArrayList<String> attachmentName = mail.getAttachmentName();
            attachmentList.setItems(null);
            if (attachmentName.size() > 0)
                attachmentList.setItems(FXCollections.observableList(attachmentName));

        });

        attachmentList.getSelectionModel().selectedItemProperty().addListener(e -> {
            String fileName = attachmentList.getSelectionModel().getSelectedItem();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(fileName);
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All File", "*.*")
            );
            File file = fileChooser.showSaveDialog(new Stage());
            if(file != null) {
                Mail mail = new Mail();
                mail.setId(tmp.getId());
                try {
                    API.downLoadAttachment(mail, fileName, file);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        //ComboBox选中事件
        cbo.setOnAction(e -> {
            reloadList(listView, "InBox", cbo);
        });


        //Layout
        bp.setTop(gridPane);
        bp.setCenter(wb);
        bp.setBottom(attachmentList);
        // 界面显示及位置
        pane.setTop(gp1);
        pane.setLeft(gp2);
        listView.setPrefSize(100, 500);
        pane.setCenter(listView);
        pane.setRight(bp);

        Scene home = new Scene(pane, 1200, 800);
        stage.setScene(home);
        stage.setTitle("OutMail");
        stage.show();

    }

    //构建列表
    public static ListView<Mail> mailListView(Config config) {
        ArrayList<Mail> list = API.queryInbox(config);
        if (list == null) {
            return new ListView<>();
        }
        return new ListView<>(FXCollections.observableArrayList(list));
    }

    //重新加载列表
    public static void reloadList(ListView<Mail> list, String type, ComboBox<Config> cbo) {
        config = cbo.getValue();
        ArrayList<Mail> mailList = null;
        if (type.equals("InBox")) {
            mailList = API.queryInbox(config);
        } else if (type.equals("SendBox")) {
            mailList = API.querySendBox(config);
        } else if (type.equals("DraftBox")) {
            mailList = API.queryDraftBox(config);
        } else if (type.equals("RecentBox")) {
            mailList = API.querySendBox(config);
        }
        list.setItems(null);
        if (mailList != null)
            list.setItems(FXCollections.observableList(mailList));
    }

}
