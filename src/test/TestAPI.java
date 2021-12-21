package test;

import outmail.controller.API;
import outmail.model.Config;
import outmail.model.Contact;
import outmail.model.Mail;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 16:06
 */
public class TestAPI {
    public static void main(String[] args) {
        testQueryAllConfig();
    }

    public static void testInit() {
        if (!API.init()) {
            System.out.println("第一次运行...");
            String smtpServer = "smtp.qq.com";
            String smtpPort = "587";
            String protocol = "POP";
            String popServer = "pop.qq.com";
            String popPort = "995";
            String username = "Ackerven@qq.com";
            String password = "cgibleejkzvdbihg";
            if (API.addNewConfig(username, password, smtpServer, smtpPort, protocol, popServer, popPort, false, true)) {
                System.out.println("配置添加成功...");
            }
        } else {
            System.out.println("加载...");
        }
        TestConfig.parse(TestConfig.fetchInbox(API.CONFIGS.get(0).getProperties(), API.CONFIGS.get(0).getAuthenticator()));
    }

    public static void testAddNewConfig() {
        String smtpServer = "smtp.qq.com";
        String smtpPort = "587";
        String protocol = "POP";
        String popServer = "pop.qq.com";
        String popPort = "995";
        String username = "ackerven@qq.com";
        String password = "cgibleejkzvdbihg";
        if (API.addNewConfig(username, password, smtpServer, smtpPort, protocol, popServer, popPort, false, true)) {
            System.out.println("配置添加成功");
        }
    }

    public static void testClearCache() {
        ArrayList<Config> list = API.queryAllConfig();
        API.clearCache(list);
    }

    public static void testDelConfig() {
        API.delConfig(1);
    }

    public static void testQueryOneConfig() {
        Config config = API.queryOneConfig(1);
//        config.load();
        System.out.println(config);
    }

    public static void testQueryAllConfig() {
        ArrayList<Config> list = API.queryAllConfig();
        for (Config c : list) {
            System.out.println(c);
        }
    }

    public static void testAddContact() {
        Contact contact = new Contact("CC", "cc@qq.com", "A");
        if (API.addContact(API.CONFIGS.get(2), contact)) {
            System.out.println("AddContact Success");
            System.out.println(contact);
        } else {
            System.out.println("AddContact Failed");
        }
    }

    public static void testDelContact() {
        if (API.delContact(new Contact())) {
            System.out.println("delContact Success");
        } else {
            System.out.println("delContact Failed");
        }
    }

    public static void testQueryAllContact() {
        ArrayList<Contact> list = API.queryAllContact(API.CONFIGS.get(2));
        for (Contact c : list) {
            System.out.println(c);
        }
    }

    public static void testUpdateContact() {
        Contact contact = new Contact(1, "ABC", "Ackerven@qq.com", "Hello Worlds");
        if (API.updateContact(contact)) {
            System.out.println("Success");
        } else {
            System.out.println("Failed");
        }
    }

    public static void testQueryContact() {
        ArrayList<Contact> list = API.queryContact(API.CONFIGS.get(2), "@", null, true);
        if (list.size() == 0) {
            System.out.println("Not found");
        } else {
            for (Contact c : list) {
                System.out.println(c);
            }
        }
    }

    public static void testSendMail(Config config) {
        Mail mail = new Mail();
        mail.setSender("ackerven@qq.com");
        mail.setTo("ackerven@qq.com");
        mail.setSubject("Test API");
        mail.setPlainContent("ASCII");
        mail.setStatus(Mail.SEEN);
        mail.setSendData(new Date());
        List<DataSource> list = new ArrayList<>();
        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\text.txt")));
//        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\test.txt")));
//        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\a.txt")));
        mail.setAttachments(list);
        API.sendMail(config, mail);
    }

    public static void testSaveMail(Config config) {
        Mail mail = new Mail();
        mail.setSender("ackerven@qq.com");
        mail.setTo("ackerven@qq.com");
        mail.setSubject("Test API");
        mail.setPlainContent("Hello World");
        mail.setStatus(Mail.RECENT);
        mail.setSendData(new Date());
        List<DataSource> list = new ArrayList<>();
//        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\text.txt")));
//        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\test.txt")));
//        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\a.txt")));
        mail.setAttachments(list);
        API.saveMail(config, mail);
    }

    public static void testQueryInbox(Config config) {
        ArrayList<Mail> mail = API.queryInbox(config);
        for (Mail m : mail) {
            System.out.println(m);
        }
    }

    public static void testSendBox(Config config) {
        ArrayList<Mail> mail = API.querySendBox(config);
        for (Mail m : mail) {
            System.out.println(m);
        }
    }

    public static void testDraftBox(Config config) {
        ArrayList<Mail> mail = API.queryDraftBox(config);
        for (Mail m : mail) {
            System.out.println(m);
        }
    }

    public static void testDelMail() {
        API.delMail(new Mail());
    }

    public static void testSeenMail() {
        Mail mail = new Mail();
        mail.setId(11);
        API.seenMail(mail);
    }

    public static void testQueryRecentBox(Config config) {
        ArrayList<Mail> mail = API.queryRecentBox(config);
        for (Mail m : mail) {
            System.out.println(m);
        }
    }
}
