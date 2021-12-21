package test;

import org.apache.commons.mail.util.MimeMessageParser;
import outmail.model.Config;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 14:17
 */
public class TestConfig {

    public static void main(String[] args) {
        String smtpServer = "smtp.qq.com";
        String smtpPort = "587";
        String protocol = "POP";
        String popServer = "pop.qq.com";
        String popPort = "995";
        Config config = new Config("Ackerven@qq.com");
        config.setAuthenticator("Ackerven@qq.com", "cgibleejkzvdbihg");
//        config.load();
        config.setProperties(smtpServer, smtpPort,false,protocol, popServer, popPort, true);
//        parse(fetchInbox(config.getProperties(), config.getAuthenticator()));
        config.save();
    }

    private static String classPath = new File("").getPath() + "LearmJavaMail/source/mailbox/";
    private static final String folder = classPath;

    private static void parse(Message message) {
        try {
            MimeMessageParser parser = new MimeMessageParser((MimeMessage) message).parse();
            String from = parser.getFrom();
            List<Address> cc = parser.getCc();
            List<Address> to = parser.getTo();
            List<Address> bcc = parser.getBcc();
            String replyTo = parser.getReplyTo();
            String subject = parser.getSubject();
            String htmlContent = parser.getHtmlContent();
            String plainContent = parser.getPlainContent();
            Date sendData = parser.getMimeMessage().getSentDate();

            System.out.println(subject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parse(Message... messages) {
        if (messages == null || messages.length == 0) {
            System.out.println("没有任何邮件");
        } else {
            for(Message m: messages) {
                parse(m);
            }
            if(messages[0] != null) {
                Folder folder = messages[0].getFolder();
                if(folder != null) {
                    try {
                        Store store = folder.getStore();
                        folder.close(false);
                        if(store != null) {
                            store.close();
                        }
                    } catch (MessagingException e) {
                        // ignore
                    }
                }
            }
        }
    }

    public static Message[] fetchInbox(Properties props, Authenticator authenticator) {
        return fetchInbox(props, authenticator, null);
    }

    public static Message[] fetchInbox(Properties props, Authenticator authenticator, String protocol) {
        Message[] messages = null;
        Session session = Session.getDefaultInstance(props, authenticator);
        Store store = null;
        Folder folder = null;
        try {
            store = protocol == null || protocol.trim().length() == 0 ?  session.getStore() : session.getStore(protocol);
            store.connect();
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            messages = folder.getMessages();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
