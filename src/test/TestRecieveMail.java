package test;

import outmail.controller.API;
import outmail.mail.MailParser;
import outmail.mail.ReceiveMail;
import outmail.model.Mail;
import outmail.mysql.MySQL;

import java.util.ArrayList;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/15 22:45
 */
public class TestRecieveMail {
    private static final String URL = "jdbc:mysql://110.42.156.179:3306/foxmail?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "foxmail";
    private static final String PASSWORD = "foxmail";
    private static final MySQL mySQL = new MySQL(URL, USERNAME, PASSWORD);
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        API.init();
        ReceiveMail receiveMail = new ReceiveMail(API.CONFIGS.get(0));
        ArrayList<Mail> list = MailParser.parse(receiveMail.fetchInbox());
        for(Mail m: list) {
//            System.out.println(m);
            mySQL.addMessage(API.CONFIGS.get(0), m);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(((endTime - startTime) / 1000.0) + "s");

    }
}
