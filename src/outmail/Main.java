package outmail;

import javafx.application.Application;
import javafx.stage.Stage;
import outmail.gui.HomePane;


/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 OutMail All rights reserved.
 * @date 2021/12/12 8:35
 * 程序启动的入口
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
//        if(API.init()) {
//            SendMailPane.start(API.CONFIGS.get(0), null);
//
//        } else {
//            InitPane.start(false);
//
//        }
//        ContactPane.Start(new Config());
        HomePane.start();
    }

}

