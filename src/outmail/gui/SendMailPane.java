package outmail.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import outmail.controller.API;
import outmail.model.Config;
import outmail.model.Mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/18 20:29
 */
public class SendMailPane {
    public static void start(Config config, Mail draft) {
        //TODO 写各种按钮，输入框等

        Stage stage = new Stage();
        stage.setTitle("写邮件");
        stage.setWidth(750);
        stage.setHeight(650);
        Scene scene = new Scene(new Group());

        //使用VBOX
        final VBox root = new VBox();
        root.setPadding(new Insets(8, 8, 8, 8));
        root.setSpacing(5);
        root.setAlignment(Pos.BOTTOM_LEFT);

        //添加图片
//        ImageView image = new ImageView("file:/Users/mac/IdeaProjects/大作业/src/outmail/gui/1.png");
//        System.out.println(SendMailPane.class.getClassLoader().getResource("send/1.png").getPath());
        ImageView image = null;
        try {
            image = new ImageView(new Image(new FileInputStream(SendMailPane.class.getClassLoader().getResource("send/1.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image.setFitHeight(20);
        image.setFitWidth(20);

//        ImageView image1 = new ImageView("file:/Users/mac/IdeaProjects/大作业/src/outmail/gui/2.png");
        ImageView image1 = null;
        try {
            image1 = new ImageView(new Image(new FileInputStream(SendMailPane.class.getClassLoader().getResource("send/2.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        image1.setFitHeight(20);
        image1.setFitWidth(20);

//        ImageView image2 = new ImageView("file:/Users/mac/IdeaProjects/大作业/src/outmail/gui/3.png");
        ImageView image2 = null;
        try {
            image2 = new ImageView(new Image(new FileInputStream(SendMailPane.class.getClassLoader().getResource("send/3.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        image2.setFitHeight(20);
        image2.setFitWidth(20);

//        ImageView image3 = new ImageView("file:/Users/mac/IdeaProjects/大作业/src/outmail/gui/4.png");
        ImageView image3 = null;
        try {
            image3 = new ImageView(new Image(new FileInputStream(SendMailPane.class.getClassLoader().getResource("send/4.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        image3.setFitHeight(20);
        image3.setFitWidth(20);

//        ImageView image4 = new ImageView("file:/Users/mac/IdeaProjects/大作业/src/outmail/gui/5.png");
        ImageView image4 = null;
        try {
            image4 = new ImageView(new Image(new FileInputStream(SendMailPane.class.getClassLoader().getResource("send/5.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        image4.setFitHeight(20);
        image4.setFitWidth(20);

//        ImageView image5 = new ImageView("file:/Users/mac/IdeaProjects/大作业/src/outmail/gui/6.png");
        ImageView image5 = null;
        try {
            image5 = new ImageView(new Image(new FileInputStream(SendMailPane.class.getClassLoader().getResource("send/6.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image5.setFitHeight(20);
        image5.setFitWidth(20);

        //发送按钮
        Button send = new Button("发送", image);
        //设置Butron远角
        send.setStyle(
                "-fx-background-radius:20;" +
                        "-fx-border-radius:20;" +
                        "-fx-border-color:#B0C4DE;");

        //保存按钮
        Button save = new Button("保存", image1);
        save.setStyle(
                "-fx-background-radius:20;" +
                        "-fx-border-radius:20;" +
                        "-fx-border-color:#B0C4DE;");

        //附件按钮
        Button appendix = new Button("附件", image2);
        appendix.setStyle(
                "-fx-background-radius:20;" +
                        "-fx-border-radius:20;" +
                        "-fx-border-color:#B0C4DE;");

        //超大附件
        Button bigAppendix = new Button("超大附件", image3);
        bigAppendix.setStyle(
                "-fx-background-radius:20;" +
                        "-fx-border-radius:20;" +
                        "-fx-border-color:#B0C4DE;");

        //截屏按钮
        Button screenShot = new Button("截屏", image4);
        screenShot.setStyle(
                "-fx-background-radius:20;" +
                        "-fx-border-radius:20;" +
                        "-fx-border-color:#B0C4DE;");

        //图片按钮
        Button picture = new Button("图片", image5);
        picture.setStyle(
                "-fx-background-radius:20;" +
                        "-fx-border-radius:20;" +
                        "-fx-border-color:#B0C4DE;");

        AnchorPane anchorPane = new AnchorPane();
        HBox box = new HBox();
        box.setStyle("-fx-background-color: #B0C4DE");
        box.getChildren().addAll(send, save, appendix, bigAppendix, screenShot, picture);
        box.setPrefHeight(5);
        box.setPrefWidth(100);
        box.setPadding(new Insets(10));
        box.setSpacing(10);///设置子组件的相互之间距离
        box.setAlignment(Pos.BOTTOM_LEFT); //左对齐
        root.getChildren().add(box);

        final GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        //收件人输入框
        final Label sendTo = new Label("收件人:");
        sendTo.setPrefWidth(100);
        GridPane.setConstraints(sendTo, 0, 0);
        grid.getChildren().add(sendTo);

        final TextField tbTo = new TextField();
        tbTo.setPrefWidth(400);
        GridPane.setConstraints(tbTo, 1, 0);
        grid.getChildren().add(tbTo);

        //抄送输入框
        final Label cc = new Label("抄送:");
        cc.setPrefWidth(100);
        GridPane.setConstraints(cc, 0, 1);
        grid.getChildren().add(cc);

        final TextField tbCc = new TextField();
        tbCc.setPrefWidth(400);
        GridPane.setConstraints(tbCc, 1, 1);
        grid.getChildren().add(tbCc);

        //密送输入框
        final Label bcc = new Label("密送:");
        bcc.setPrefWidth(100);
        GridPane.setConstraints(bcc, 0, 2);
        grid.getChildren().add(bcc);

        final TextField tbBcc = new TextField();
        tbBcc.setPrefWidth(400);
        GridPane.setConstraints(tbBcc, 1, 2);
        grid.getChildren().add(tbBcc);

        //主题输入框
        final Label subjectLabel = new Label("主题:");
        GridPane.setConstraints(subjectLabel, 0, 3);
        grid.getChildren().add(subjectLabel);

        final TextField tbSubject = new TextField();
        tbSubject.setPrefWidth(400);
        GridPane.setConstraints(tbSubject, 1, 3);
        grid.getChildren().add(tbSubject);

        root.getChildren().add(grid);

        //富文本编辑器
        final HTMLEditor htmlEditor = new HTMLEditor();
        htmlEditor.setPrefHeight(370);
        htmlEditor.setHtmlText("欢迎使用outmail");

        //附件显示面板
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("noborder-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(180);

        root.getChildren().addAll(htmlEditor, scrollPane);

        if (draft != null) {
            //如果draft不为null，即为编辑草稿邮箱的邮件
            //需要调用各种输入框的set方法把draft的值写进去
            tbTo.setText(draft.getTo());
            tbCc.setText(draft.getCc());
            tbBcc.setText(draft.getBcc());
            tbSubject.setText(draft.getSubject());
            htmlEditor.setHtmlText(draft.getHtmlContent());
        }

        //发送按钮
        Mail mail = new Mail();
        send.setOnAction(e -> {
            //TODO 获取各种框的值。
            //调用mail.setXXX方法构建Mail对象
            mail.setTo(tbTo.getText());
            mail.setCc(tbCc.getText());
            mail.setBcc(tbBcc.getText());
            mail.setSubject(tbSubject.getText());
            mail.setHtmlContent(htmlEditor.getHtmlText());
            mail.setSender(config.getConfigName());
            mail.setSendData(new Date());

//            System.out.println(mail);
            //调用API发送邮件
            API.sendMail(config, mail);
            if(draft != null) {
                draft.setStatus(Mail.DELETE);
                API.updateMailStatus(config, draft);
            }
        });


        //保存为草稿按钮
        save.setOnAction(e -> {
            //TODO 获取各种框的值。
            //调用mail.setXXX方法构建Mail对象
            mail.setTo(tbTo.getText());
            mail.setCc(tbCc.getText());
            mail.setBcc(tbBcc.getText());
            mail.setSubject(tbSubject.getText());
            mail.setHtmlContent(htmlEditor.getHtmlText());
            mail.setSender(config.getConfigName());
            mail.setSendData(new Date());
            //调用API保存邮件为草稿
            API.saveMail(config, mail);
        });

        //附件按钮
        appendix.setOnAction(new EventHandler<ActionEvent>() {
            String s = "";

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                //设置标题
                fileChooser.setTitle("选择文件");
                List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
                for (File i : list) {
                    s += i.getName() + '\n';
                }
                //显示附件文件名
                Label label = new Label(s);
                scrollPane.setContent(label);
                mail.setAttachment(list);
            }
        });

        //超大附件按钮
        bigAppendix.setOnAction(new EventHandler<ActionEvent>() {
            String s = "";

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                //设置标题
                fileChooser.setTitle("选择文件");
                List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
                for (File i : list) {
                    s += i.getName() + '\n';
                }
                //显示附件文件名
                Label label = new Label(s);
                scrollPane.setContent(label);
            }
        });

        //图片按钮
        picture.setOnAction(new EventHandler<ActionEvent>() {
            String s = "";

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                //设置标题
                fileChooser.setTitle("选择文件");
                List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
                for (File i : list) {
                    s += i.getName() + '\n';
                }
                //显示图片文件名
                Label label = new Label(s);
                scrollPane.setContent(label);
            }
        });

        scene.setRoot(root);
        stage.setScene(scene);
        stage.show();
    }
}
