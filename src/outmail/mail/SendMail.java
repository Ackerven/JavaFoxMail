package outmail.mail;

/**
 * @Author:Unité
 * @Date 2021/12/15 18:10
 * 邮件的发送
 */


import outmail.model.Config;
import outmail.model.Mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;

public class SendMail {

    private final Mail mail;
    private final Config config;

    public SendMail(Config config, Mail mail) {
        this.config = config;
        this.mail = mail;
    }

    public void send() throws Exception {
        //1、连接邮件服务器的参数配置
        Properties props = config.getProperties();
        //2、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(props);
        //设置调试信息在控制台打印出来
        session.setDebug(true);
        //3、创建邮件的实例对象
        Message msg = getMimeMessage(session);
        //4、根据session对象获取邮件传输对象Transport
        Transport transport = session.getTransport();

        transport.connect(config.getUserName(), config.getPassWord());
        //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(msg, msg.getAllRecipients());
        //5、关闭邮件连接
        transport.close();
    }

    /**
     * 获得创建一封邮件的实例对象
     *
     * @param session
     * @return  返回MimeMessage对象
     * @throws MessagingException
     * @throws AddressException
     */
    public MimeMessage getMimeMessage(Session session) throws Exception {

        //1.创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //2.设置发件人地址
        msg.setFrom(new InternetAddress(mail.getSender()));


        /**
         * 3.设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */

        /**
         * MimeMessage.RecipientType.TO:发送
         */

        msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(mail.getTo()));

        /**
         * MimeMessage.RecipientType.CC:抄送
         */
        if (!(mail.getCc() == null || mail.getCc().equals(""))) {
            msg.setRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(mail.getCc()));
        }


        /**
         * MimeMessage.RecipientType.BCC:密送
         */
        if (!(mail.getBcc() == null || mail.getBcc().equals(""))) {
            msg.setRecipients(MimeMessage.RecipientType.BCC, InternetAddress.parse(mail.getBcc()));
        }


        // 设置邮件主题
        msg.setSubject(mail.getSubject(), "UTF-8");

        /**
         * 下面是设置邮件正文
         * 设置(文本+图片)和附件的关系(合成一个大的混合"节点" / Multipart)
         */
        MimeMultipart mm = new MimeMultipart();

        /**
         * 附件处理
         */
        if (mail.hasAttachments()) {
            Attachment attachment = new Attachment();
            attachment.haveLink(mail.getAttachments(), mm);
        }

        /**
         * HTML处理
         */
        if (mail.hasHtmlContent()) {
            //创建文本"节点"
            MimeBodyPart HTML = new MimeBodyPart();
            HTML.setContent(mail.getHtmlContent(), "text/html;charset=UTF-8");
            mm.addBodyPart(HTML);
        }

        /**
         * main body text处理
         */
        if (mail.hasPlainContent()) {
            //创建文本"节点"
            MimeBodyPart TEXT = new MimeBodyPart();
            TEXT.setContent(mail.getPlainContent(), "text/html;charset=UTF-8");
            mm.addBodyPart(TEXT);
        }

        // 设置(文本+图片)和 附件 的关系(合成一个大的混合"节点" / Multipart)
        mm.setSubType("mixed");         // 混合关系

        // 设置整个邮件的关系（将最终的混合"节点"作为邮件的内容添加到邮件对象）
        msg.setContent(mm);

        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());
        msg.saveChanges();

        return msg;
    }
}

