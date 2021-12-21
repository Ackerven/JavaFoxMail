package outmail.model;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 4:33
 * 邮件的封装
 */
public class Mail {
    //四种状态
    /**
     * 邮件在草稿箱标记
     */
    public static final String DRAFT = "DRAFT";
    /**
     * 邮件已删除标记
     */
    public static final String DELETE = "DELETE";
    /**
     * 邮件已读标记
     */
    public static final String SEEN = "SEEN";
    /**
     * 邮件未读标记
     */
    public static final String RECENT = "RECENT";
    /**
     * 邮件发送标记
     */
    public static final String SEND = "SEND";
    /**
     * id
     * 对应数据库表mail的id字段
     */
    private int id;
    /**
     * 邮件的Message-ID
     * 对应数据库表mail的msgid字段
     */
    private String msgId;
    /**
     * 发件人
     * 对应数据库表mail的sender字段
     */
    private String sender;
    /**
     * 回复邮件的收件人
     * 对应数据库表mail的reply_to字段
     */
    private String replyTo;
    /**
     * 邮件的主题
     * 对应数据库表mail的title字段
     */
    private String subject;
    /**
     * 邮件中的html内容
     * 对应数据库表mail的htmlText字段
     */
    private String htmlContent;
    /**
     * 邮件的纯文本内容
     * 对应数据库表mail的plainText字段
     */
    private String plainContent;
    /**
     * 抄送人列表
     * 对应数据库表mail_cc的address字段
     */
    private String cc;
    /**
     * 收件人列表
     * 对应数据库表mail_to的address字段
     */
    private String to;
    /**
     * 密送人列表
     * 对应数据库表mail_bcc的address字段
     */
    private String bcc;
    /**
     * 附件列表
     * 对应数据库表mail_attachment的address字段
     */
    private List<DataSource> attachments;
    /**
     * 发送时间
     * 对应数据库表mail的date字段
     */
    private Date sendData;
    /**
     * 邮件的状态
     * 对应数据库表mail的status字段
     * 状态有四种,分别是DRAFT, DELETE, SEEN, RECENT
     */
    private String status;
    /**
     * 邮件是否存在html内容
     */
    private boolean hasHtmlContent;
    /**
     * 邮件是否存在纯文本内容
     */
    private boolean hasPlainContent;
    /**
     * 邮件是否有附件
     */
    private boolean hasAttachments;
    /**
     * 附件列表名
     */
    private ArrayList<String> attachmentName;

    public Mail() {
        this.attachmentName = new ArrayList<>();
    }

    public ArrayList<String> getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(ArrayList<String> attachmentName) {
        this.attachmentName = attachmentName;
        this.setHasAttachments(true);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
        this.setHasHtmlContent(true);
    }

    public String getPlainContent() {
        return plainContent;
    }

    public void setPlainContent(String plainContent) {
        this.plainContent = plainContent;
        this.setHasPlainContent(true);
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * Address列表转String
     * @param list  address对象列表
     */
    public void setCc(List<Address> list) {
        String s = "";
        for(int i = 0; i < list.size(); i++) {
            String m = getMailFromString(list.get(i).toString());
            if(i != list.size() - 1) {
                s += m + ',';
            } else {
                s += m;
            }
        }
        this.cc = s;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = getMailFromString(to);
    }

    private String getMailFromString(String string) {
        String s = "";
        /** Regex for single EmailValidator */
        String SINGLE_EMAIL_REGEX = "(?:(?:[A-Za-z0-9\\-_@!#$%&'*+/=?^`{|}~]|(?:\\\\[\\x00-\\xFF]?)|(?:\"[\\x00-\\xFF]*\"))+(?:\\.(?:(?:[A-Za-z0-9\\-_@!#$%&'*+/=?^`{|}~])|(?:\\\\[\\x00-\\xFF]?)|(?:\"[\\x00-\\xFF]*\"))+)*)@(?:(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+(?:(?:[A-Za-z0-9]*[A-Za-z][A-Za-z0-9]*)(?:[A-Za-z0-9-]*[A-Za-z0-9])?))";
        Pattern SINGLE_EMAIL_REGEX_PATTERN = Pattern.compile(SINGLE_EMAIL_REGEX);
        Matcher m = SINGLE_EMAIL_REGEX_PATTERN.matcher(string);
        while (m.find()) {
            s += string.substring(m.start(),m.end());
        }
        return s;
    }

    /**
     * Address列表转String
     * @param list  address对象列表
     */
    public void setTo(List<Address> list) {
        String s = "";
        for(int i = 0; i < list.size(); i++) {
            String m = getMailFromString(list.get(i).toString());
            if(i != list.size() - 1) {
                s += m + ',';
            } else {
                s += m;
            }
        }
        this.to = s;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    /**
     * Address列表转String
     * @param list  address对象列表
     */
    public void setBcc(List<Address> list) {
        String s = "";
        for(int i = 0; i < list.size(); i++) {
            String m = getMailFromString(list.get(i).toString());
            if(i != list.size() - 1) {
                s += m + ',';
            } else {
                s += m;
            }
        }
        this.bcc = s;
    }

    public List<DataSource> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<DataSource> attachments) {
        this.attachments = attachments;
        this.setHasAttachments(true);
        for(DataSource ds: attachments) {
            this.attachmentName.add(ds.getName());
        }
    }

    public Date getSendData() {
        return sendData;
    }

    public void setSendData(Date sendData) {
        this.sendData = sendData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasHtmlContent() {
        return hasHtmlContent;
    }

    public void setHasHtmlContent(boolean hasHtmlContent) {
        this.hasHtmlContent = hasHtmlContent;
    }

    public boolean hasPlainContent() {
        return hasPlainContent;
    }

    public void setHasPlainContent(boolean hasPlainContent) {
        this.hasPlainContent = hasPlainContent;
    }

    public boolean hasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    /**
     * 把htmlContent转为输入流
     * @return InputStream对象
     */
    public InputStream getHtmlContentBinaryStream() {
        if(!this.hasHtmlContent) {
            return null;
        }
        if(this.htmlContent == null || this.htmlContent.equals("")) {
            return null;
        }
        return new ByteArrayInputStream(this.htmlContent.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 把plainContent转为输入流
     * @return InputStream对象
     */
    public InputStream getPlainContentBinaryStream() {
        if(!this.hasPlainContent) {
            return null;
        }
        if(this.plainContent == null || this.plainContent.equals("")) {
            return null;
        }
        return new ByteArrayInputStream(this.plainContent.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 把输入流转为htmlContent
     * @param inputStream   输入流对象
     */
    public void setHtmlContent(InputStream inputStream) {
        if(inputStream == null) {
            return;
        }
        StringBuilder buffer = new StringBuilder();
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.htmlContent = buffer.toString();
        this.setHasHtmlContent(true);
    }

    /**
     * 把输入流转为plainContent
     * @param inputStream   输入流对象
     */
    public void setPlainContent(InputStream inputStream) {
        if(inputStream == null) {
            this.setPlainContent("");
            return;
        }
        StringBuilder buffer = new StringBuilder();
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.plainContent = buffer.toString();
        this.setHasPlainContent(true);
    }

    /**
     * 把文件转为DataSource对象
     * @param attachments  File对象列表
     */
    public void setAttachment(List<File> attachments) {
        List<DataSource> list = new ArrayList<>();
        for(File file: attachments) {
            list.add(new FileDataSource(file));
        }
        this.setAttachments(list);
    }

    @Override
    public String toString() {
//        return "Mail{" +
//                "id=" + id +
//                ", msgid='" + msgId + '\'' +
//                ", sender='" + sender + '\'' +
//                ", replyTo='" + replyTo + '\'' +
//                ", subject='" + subject + '\'' +
//                ", htmlContent='" + htmlContent + '\'' +
//                ", plainContent='" + plainContent + '\'' +
//                ", cc='" + cc + '\'' +
//                ", to='" + to + '\'' +
//                ", bcc='" + bcc + '\'' +
//                ", attachments=" + attachments +
//                ", sendData=" + sendData +
//                ", status='" + status + '\'' +
//                ", hasHtmlContent=" + hasHtmlContent +
//                ", hasPlainContent=" + hasPlainContent +
//                ", hasAttachments=" + hasAttachments +
//                ", attachmentName=" + attachmentName +
//                '}';
        return this.subject;
    }
}
