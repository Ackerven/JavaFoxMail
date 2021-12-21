package test;

import outmail.mail.SendMail;
import outmail.model.Config;
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
 * @date 2021/12/15 19:24
 */
public class TestSend {
    public static void main(String[] args) {
        testSend();
    }

    public static void testSend() {
        String smtpServer = "smtp.qq.com";
        String smtpPort = "587";
        String protocol = "POP";
        String popServer = "pop.qq.com";
        String popPort = "995";
        Config config = new Config("Ackerven@qq.com");
        config.setAuthenticator("Ackerven@qq.com", "cgibleejkzvdbihg");
        config.setProperties(smtpServer, smtpPort,false,protocol, popServer, popPort, true);
        Mail mail = new Mail();
        mail.setSender("Ackerven@qq.com");
        mail.setTo("ackerven@qq.com");
        mail.setSubject("OutMail");
        mail.setHtmlContent("<h1>ABC</h1>");
        mail.setPlainContent("ASCII");
//        mail.setBcc("61426092@qq.com,78617289@qq.com");
//        mail.setBcc("3139432325@qq.com,3226169743@qq.com");
        mail.setStatus(Mail.SEEN);
        mail.setSendData(new Date());
        List<DataSource> list = new ArrayList<>();
//        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\text.txt")));
//        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\test.txt")));
        list.add(new FileDataSource(new File("E:\\macBackup\\work\\Java\\idea\\FoxMail\\src\\outmail\\Main.java")));
        mail.setAttachments(list);

        SendMail sendMail = new SendMail(config, mail);
        try {
            sendMail.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
