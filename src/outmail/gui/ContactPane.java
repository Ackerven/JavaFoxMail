package outmail.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import outmail.controller.API;
import outmail.model.Config;
import outmail.model.Contact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/18 20:29
 */
public class ContactPane {
    public static void Start(Config config) {
        Stage stage = new Stage();
        ArrayList<Contact> result = API.queryAllContact(config);
//        ImageView imageViewAdd = new ImageView("file:D:\\Java高编\\FoxMail\\img\\添加联系人.png");
        ImageView imageViewAdd = null;
        try {
            imageViewAdd = new ImageView(new Image(new FileInputStream(ContactPane.class.getClassLoader().getResource("home/add.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageViewAdd.setFitHeight(50);
        imageViewAdd.setFitWidth(50);
//        ImageView imageViewDel = new ImageView("file:D:\\Java高编\\FoxMail\\img\\删除联系人.png");
        ImageView imageViewDel = null;
        try {
            imageViewDel = new ImageView(new Image(new FileInputStream(ContactPane.class.getClassLoader().getResource("home/delete.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageViewDel.setFitHeight(50);
        imageViewDel.setFitWidth(50);
//        ImageView imageViewUpdate = new ImageView("file:D:\\Java高编\\FoxMail\\img\\修改联系人.png");
        ImageView imageViewUpdate = null;
        try {
            imageViewUpdate = new ImageView(new Image(new FileInputStream(ContactPane.class.getClassLoader().getResource("home/modify.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageViewUpdate.setFitHeight(50);
        imageViewUpdate.setFitWidth(50);
//        ImageView imageViewQuery = new ImageView("file:D:\\Java高编\\FoxMail\\img\\查找联系人.png");
        ImageView imageViewQuery = null;
        try {
            imageViewQuery = new ImageView(new Image(new FileInputStream(ContactPane.class.getClassLoader().getResource("home/find.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        imageViewQuery.setFitHeight(50);
        imageViewQuery.setFitWidth(50);
        Button add = new Button("添加联系人",imageViewAdd);
        Button del = new Button("删除联系人",imageViewDel);
        Button update = new Button("更新联系人",imageViewUpdate);
        Button query = new Button("查询联系人",imageViewQuery);

//        add.setStyle();

        add.setOnAction(e->{
            Contact contact = new Contact();
            //TODO
            Stage stageAdd = new Stage();
            BorderPane bp = new BorderPane();
            GridPane gp = new GridPane();
            Button AddEnter = new Button("确定添加");
            Label name = new Label("  姓名：");
            Label email = new Label("  邮箱：");
            Label describe = new Label("  描述：");
            TextField textFieldName = new TextField();
            TextField textFieldEmail = new TextField();
            TextField textFieldDescribe = new TextField();

            HBox hBox1 = new HBox();// 输入框与标签按行排列
            HBox hBox2 = new HBox();
            HBox hBox3 = new HBox();
            hBox1.getChildren().addAll(name,textFieldName);
            hBox2.getChildren().addAll(email,textFieldEmail);
            hBox3.getChildren().addAll(describe,textFieldDescribe);

            Label addTitle = new Label("请输入联系人的信息");
            Font font = Font.font("Times New Romes", FontWeight.BOLD, FontPosture.ITALIC, 30);// 设置字体
            addTitle.setPadding(new Insets(20, 0, 20, 0));
            addTitle.setFont(font);
            addTitle.setAlignment(Pos.CENTER);

            gp.add(hBox1, 3, 1);// c,r
            gp.add(hBox2, 3, 2);
            gp.add(hBox3, 3, 3);
            gp.add(AddEnter, 4, 4);
            gp.setHgap(5);// 左右间隔
            gp.setVgap(20);// 上下间隔

            bp.setTop(addTitle);
            bp.setCenter(gp);

            Scene s = new Scene(bp, 500, 500);
            stageAdd.setScene(s);
            stageAdd.show();

            AddEnter.setOnAction(e1->{
                // 获取输入框中的信息
                String name1 = textFieldName.getText();
                String email1 = textFieldEmail.getText();
                String describe1 = textFieldDescribe.getText();
                Contact o = new Contact(name1, email1, describe1);
                API.addContact(config, o);
                Stage stage1 = new Stage();
                BorderPane bp1 = new BorderPane();
                bp1.setCenter(new Label("添加成功!"));
                Scene ss = new Scene(bp1, 300, 300);
                stage1.setScene(ss);
                stage1.show();
            });
        });

        del.setOnAction(e->{
            //TODO
            //删除你当前显示的联系人
            Button DeleteEnter = new Button("确定删除");
            Label deletelabel = new Label("  姓名：");
            TextField deleteName = new TextField();
            HBox deletebox = new HBox();

            Stage stage1 = new Stage();
            BorderPane bp = new BorderPane();
            GridPane gp = new GridPane();
            Label deletetitle = new Label("请输入要删除的联系人");
            Font font = Font.font("Times New Romes", FontWeight.BOLD, FontPosture.ITALIC, 30);// 设置字体
            deletetitle.setPadding(new Insets(20, 0, 20, 0));
            deletetitle.setFont(font);
            deletetitle.setAlignment(Pos.CENTER);

            deletebox.getChildren().addAll(deletelabel, deleteName);
            gp.add(deletebox, 3, 1);// c,r
            gp.add(DeleteEnter, 4, 4);
            bp.setTop(deletetitle);
            bp.setCenter(gp);

            gp.setHgap(5);// 左右间隔
            gp.setVgap(20);// 上下间隔

            Scene s = new Scene(bp, 500, 500);
            stage1.setScene(s);
            stage1.show();

            DeleteEnter.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String name = deleteName.getText();// 获取输入框中的信息
                    deleteName.setText("");// 清空
                    Contact o = null;
                    for (int i = 0; i < result.size(); i++) {
                        Contact a = result.get(i);
                        if(a.getName().equals(name)){
                            o = a ;
                            result.remove(i);
                            break ;
                        }
                    }
                    if(o != null){
                        //写完自行修改下面一行里面的参数，传你要删除的联系人
                        API.delContact(o);
                        Stage stage = new Stage();
                        BorderPane bp = new BorderPane();
                        bp.setCenter(new Label("删除成功!"));
                        Scene s = new Scene(bp, 200, 200);
                        stage.setScene(s);
                        stage.show();
                    }else {
                        Stage stage = new Stage();
                        BorderPane bp = new BorderPane();
                        bp.setCenter(new Label("找不到此联系人!"));
                        Scene s = new Scene(bp, 200, 200);
                        stage.setScene(s);
                        stage.show();
                    }
                }
            });
        });

        update.setOnAction(e->{
            //TODO
            Button ModifyEnter1 = new Button("确定");
            Label modifylabel = new Label("  姓名：");
            TextField modifyName = new TextField();
            HBox modifybox = new HBox();

            Stage stage1 = new Stage();
            BorderPane bp = new BorderPane();
            GridPane gp = new GridPane();
            Label modifytitle = new Label("请输入要修改的联系人");
            Font font = Font.font("Times New Romes", FontWeight.BOLD, FontPosture.ITALIC, 30);// 设置字体
            modifytitle.setPadding(new Insets(20, 0, 20, 0));
            modifytitle.setFont(font);
            modifytitle.setAlignment(Pos.CENTER);

            modifybox.getChildren().addAll(modifylabel, modifyName);

            gp.add(modifybox, 3, 1);// c,r
            gp.add(ModifyEnter1, 4, 4);

            gp.setHgap(5);// 左右间隔
            gp.setVgap(20);// 上下间隔

            bp.setTop(modifytitle);
            bp.setCenter(gp);

            Scene s = new Scene(bp, 500, 500);
            stage1.setScene(s);
            stage1.show();

            ModifyEnter1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String name = modifyName.getText();
                    Contact p = null ;
                    for (int i = 0; i < result.size(); i++){
                        Contact a = result.get(i);
                        if(a.getName().equals(name)){
                            p = a ;
                            break ;
                        }
                    }
                    if(p != null){
                        Contact b = p ;
                        Button ModifyEnter2 = new Button("确定修改");
                        Label modifylabel1 = new Label("  邮箱：");
                        Label modifylabel2 = new Label("  描述：");
                        TextField modifyPhone = new TextField();
                        TextField modifyEmail = new TextField();
                        modifyName.setText("");
                        Stage stage = new Stage();
                        BorderPane bp = new BorderPane();
                        GridPane gp = new GridPane();
                        Label modifytitle1 = new Label("请输入要修改的邮箱和描述!");
                        Font font = Font.font("Times New Romes", FontWeight.BOLD, FontPosture.ITALIC, 30);// 设置字体
                        modifytitle1.setPadding(new Insets(20, 0, 20, 0));
                        modifytitle1.setFont(font);
                        modifytitle1.setAlignment(Pos.CENTER);

                        HBox hbox6 = new HBox();
                        HBox hbox66 = new HBox();
                        hbox6.getChildren().addAll(modifylabel1, modifyPhone);
                        hbox66.getChildren().addAll(modifylabel2, modifyEmail);
                        gp.add(hbox6, 3, 1);// c,r
                        gp.add(hbox66, 3, 2);
                        gp.add(ModifyEnter2, 4, 4);

                        bp.setTop(modifytitle1);
                        bp.setCenter(gp);
                        Scene s = new Scene(bp, 500, 500);
                        stage.setScene(s);
                        stage.show();

                        ModifyEnter2.setOnAction(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                String description = modifyPhone.getText();
                                String email = modifyEmail.getText();
                                modifyPhone.setText("");
                                modifyEmail.setText("");
                                b.setName(name) ;
                                b.setEmail(email) ;
                                b.setDescription(description) ;
                                //写完自行修改下面一行里面的参数，传你要已经修改的联系人
                                API.updateContact(b);
                                Stage stage = new Stage();
                                BorderPane bp = new BorderPane();
                                bp.setCenter(new Label("修改成功!"));
                                Scene s = new Scene(bp, 200, 200);
                                stage.setScene(s);
                                stage.show();
                            }
                        });
                    }else{
                        Stage stage = new Stage();
                        BorderPane bp = new BorderPane();
                        bp.setCenter(new Label("找不到此联系人!"));
                        Scene s = new Scene(bp, 200, 200);
                        stage.setScene(s);
                        stage.show();
                    }
                }
            });
        });

        query.setOnAction(e-> {
            //TODO
            //获取查询的key
            Button FindEnter = new Button("确定查找");
            Label findlabel = new Label("  姓名：");
            TextField findName = new TextField();
            HBox findbox = new HBox();

            Stage stage3 = new Stage();
            BorderPane bp = new BorderPane();
            GridPane gp = new GridPane();
            Label modifytitle = new Label("请输入要查找的联系人名字");
            Font font1 = Font.font("Times New Romes", FontWeight.BOLD, FontPosture.ITALIC, 30);// 设置字体
            modifytitle.setPadding(new Insets(20, 0, 20, 0));
            modifytitle.setFont(font1);
            modifytitle.setAlignment(Pos.CENTER);

            findbox.getChildren().addAll(findlabel, findName);

            gp.add(findbox, 3, 1);// c,r
            gp.add(FindEnter, 4, 4);

            gp.setHgap(5);// 左右间隔
            gp.setVgap(20);// 上下间隔

            bp.setTop(modifytitle);
            bp.setCenter(gp);

            Scene ss = new Scene(bp, 500, 500);
            stage3.setScene(ss);
            stage3.show();

            FindEnter.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    String name = findName.getText();
                    findName.setText("");// 清空
                    ArrayList<Contact> result = API.queryAllContact(config);
                    Contact o = null;
                    for (int i = 0; i < result.size(); i++) {
                        Contact a = result.get(i);
                        if (a.getName().equals(name)) {
                            o = a;
                            break;
                        }
                    }
                    if (o != null) {
                        Stage stage = new Stage();
                        BorderPane bp = new BorderPane();
                        bp.setCenter(new Label(o.getName() + "的邮箱为 :" + o.getEmail() + "\n" + o.getName() + "的描述为 :" + o.getDescription()));
                        Scene s = new Scene(bp, 400, 400);
                        stage.setScene(s);
                        stage.show();
                    } else {
                        Stage stage = new Stage();
                        BorderPane bp = new BorderPane();
                        bp.setCenter(new Label("找不到此联系人!"));
                        Scene s = new Scene(bp, 200, 200);
                        stage.setScene(s);
                        stage.show();
                    }
                }
            });
        });

        GridPane gridPane = new GridPane();
        gridPane.add(add, 0, 0);
        gridPane.add(del, 1, 0);
        gridPane.add(update, 0, 1);
        gridPane.add(query, 1, 1);
        gridPane.setStyle("-fx-background-color:#FFFFFF;");
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(50);
        Scene sss = new Scene(gridPane, 500, 500);
        stage.setScene(sss);
        stage.setTitle("联系人系统");
        stage.show();
    }
}