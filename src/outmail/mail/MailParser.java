package outmail.mail;

import org.apache.commons.mail.util.MimeMessageParser;
import outmail.model.Mail;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 2:13
 */
public class MailParser {

    private Mail parse(Message message) {
        Mail mail = new Mail();
        try {
            MimeMessageParser parser = new MimeMessageParser((MimeMessage) message).parse();
            mail.setSender(parser.getFrom());
            mail.setTo(parser.getTo());
            mail.setCc(parser.getCc());
            mail.setBcc(parser.getBcc());
            mail.setReplyTo(parser.getReplyTo());
            mail.setSubject(parser.getSubject());
            mail.setHtmlContent(parser.getHtmlContent());
            mail.setPlainContent(parser.getPlainContent());
            mail.setSendData(parser.getMimeMessage().getSentDate());
            mail.setAttachments(parser.getAttachmentList());
            mail.setStatus(Mail.RECENT);
            mail.setHasHtmlContent(parser.hasHtmlContent());
            mail.setHasPlainContent(parser.hasPlainContent());
            mail.setHasAttachments(parser.hasAttachments());

            Enumeration enumMail = parser.getMimeMessage().getAllHeaders();
            while (enumMail.hasMoreElements()) {
                Header h = (Header) enumMail.nextElement();
                boolean messageId = (h.getName().equals("Message-ID"))
                        || (h.getName().equals("Message-Id"));
                if (messageId) {
                    mail.setMsgId(h.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mail;
    }

    public synchronized ArrayList<Mail> parse(Message... messages) {
        ArrayList<Mail> list = new ArrayList<>();
        if (messages == null || messages.length == 0) {
            System.out.println("没有任何邮件");
        } else {
            for (Message m : messages) {
                list.add(parse(m));
            }
            if (messages[0] != null) {
                Folder folder = messages[0].getFolder();
                if (folder != null) {
                    try {
                        Store store = folder.getStore();
                        folder.close(false);
                        if (store != null) {
                            store.close();
                        }
                    } catch (MessagingException e) {
                        // ignore
                    }
                }
            }
        }
        return list;
    }
}
