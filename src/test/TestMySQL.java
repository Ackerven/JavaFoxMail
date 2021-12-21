package test;

import outmail.controller.API;
import outmail.model.Config;
import outmail.model.Mail;
import outmail.mysql.MySQL;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 19:46
 */
public class TestMySQL {
    private static final String URL = "jdbc:mysql://110.42.156.179:3306/foxmail?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "foxmail";
    private static final String PASSWORD = "foxmail";
    private static final MySQL mySQL = new MySQL(URL, USERNAME, PASSWORD);

    public static void main(String[] args) {
        API.init();
        testQueryAllMail(API.CONFIGS.get(0));
//        testAddMail(API.CONFIGS.get(0));
//        ArrayList<Mail> mail = new ArrayList<>();
//        mail = testQueryAllMail(API.CONFIGS.get(0));
        //testDownLoadAttachment(mail.get(0), "text.txt", new File("down.txt"));
//        testDelMail();
//        testDelAllMail(API.CONFIGS.get(0));
    }

    public static void testConfig() {
        // Test addConfig
        if(mySQL.addConfig(new Config("123456@qq.com"), new File(""))) {
            System.out.println("addConfig Success!");
        } else {
            System.out.println("addConfig Failed!");
        }

//        Config config = mySQL.queryOneConfig(1);
//        System.out.println(config);
        ArrayList<Config> list = mySQL.queryAllConfig();
        for(Config c: list) {
            System.out.println(c);
        }

        if(mySQL.delConfig(3)) {
            System.out.println("delConfig Success");
        } else {
            System.out.println("delConfig Failed");
        }

        list = mySQL.queryAllConfig();
        for(Config c: list) {
            System.out.println(c);
        }

    }

    public static void testQueryAllMail(Config config) {
        long start = System.currentTimeMillis();
        ArrayList<Mail> mail = mySQL.queryAllMail(config);
        for(Mail m: mail) {
            System.out.println(m);
        }
        long end = System.currentTimeMillis();
        System.out.println(((end - start) / 1000) + "s");
    }

    public static void testAddMail(Config config) {
        Mail mail = new Mail();
        mail.setSender("Ackerven@qq.com");
        mail.setReplyTo("61426092@qq.com");
        mail.setTo("ackerven@qq.com,61426092@qq.com");
//        mail.setCc("admin@qq.com,admin@wechat.com");
//        mail.setBcc("admin@bnuz.edu.cn,2@bnuz.edu.cn");
        mail.setSubject("New Mail");
        mail.setHtmlContent("<h1>");
        mail.setPlainContent("ASCII");
        mail.setStatus(Mail.DELETE);
        mail.setSendData(new Date());
        List<DataSource> list = new ArrayList<>();
        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\text.txt")));
        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\test.txt")));
        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\a.txt")));
        mail.setAttachments(list);
        mySQL.addMessage(config, mail);
    }

    public static void testUpdateMailStatus() {
        Mail mail = new Mail();
        mail.setId(23);
        mail.setStatus(Mail.SEEN);
        mySQL.updateStatus(mail);
    }

    public static void testUpdateAttachment() {
        Mail mail = new Mail();
        mail.setId(23);
        List<DataSource> list = new ArrayList<>();
        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\a.txt")));
        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\text.txt")));
        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\test.txt")));
        mail.setAttachments(list);
        if(mySQL.updateAttachment(mail)) {
            System.out.println("Success update attachment");
        } else {
            System.out.println("Failed update attachment");
        }
    }

    public static void testUpdateMail() {
        Mail mail = new Mail();
        mail.setId(23);
        mail.setSender("61426092@qq.com");
        mail.setReplyTo("");
        mail.setTo("Ackerven@qq.com");
//        mail.setCc("61426092@qq.com");
//        mail.setBcc("admin@qq.com");
        mail.setSubject("Update Mail again");
        mail.setHtmlContent("<h2></h2>");
        mail.setPlainContent("");
        mail.setStatus(Mail.SEEN);
        List<DataSource> list = new ArrayList<>();
        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\a.txt")));
        //list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\text.txt")));
        //list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\test.txt")));
        mail.setAttachments(list);
        mail.setSendData(new Date());
        if(mySQL.updateMail(mail)) {
            System.out.println("Success");
        } else {
            System.out.println("Failed");
        }
    }

    public static void testDelMail() {
        if(mySQL.delMail(22)) {
            System.out.println("Success");
        } else {
            System.out.println("Failed");
        }
    }

    public static void testDelAllMail(Config config) {
        if(mySQL.delAllMail(config)) {
            System.out.println("Success");
        } else {
            System.out.println("Failed");
        }
    }

    public static void testDownLoadAttachment(Mail mail, String fileName, File file) {
        try {
            API.downLoadAttachment(mail, fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
