package outmail.model;

import outmail.utils.AuthenticatorGenerator;

import javax.mail.Authenticator;
import javax.mail.Message;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 OutMail All rights reserved.
 * @date 2021/12/12 10:02
 */
public class Config {
    /**
     * POP协议
     */
    public static final String POP = "POP";
    /**
     * IMAP协议
     */
    public static final String IMAP = "IMAP";
    /**
     * 配置信息
     */
    private final Properties properties = new Properties();
    /**
     * 新邮件
     */
    private final ArrayList<Mail> newMail = new ArrayList<>();
    /**
     * id
     */
    private int id;
    /**
     * 配置名称
     */
    private String configName;
    /**
     * 认证信息
     */
    private Authenticator authenticator;
    /**
     * 是否有新邮件
     */
    private boolean hasNewMail = false;

    public Config() {
    }

    /**
     * 通过配置名称构造Config
     *
     * @param configName 配置名称
     */
    public Config(String configName) {
        this.configName = configName;
    }

    /**
     * 通过id和config名称构建Config
     *
     * @param id         id
     * @param configName 配置名称
     */
    public Config(int id, String configName) {
        this.id = id;
        this.configName = configName;
    }

    /**
     * 获取配置id（T）
     *
     * @return 返回配置信息的id
     */
    public int getId() {
        return id;
    }

    /**
     * 设置 Poroperties（T）
     *
     * @param smtpServer    SMTP服务器
     * @param smtpPort      SMTP服务器端口
     * @param smtpSSL       SMTP是否启用SSL
     * @param protocol      收件服务器采用的协议 POP 和 IMAP
     * @param receiveServer 收件服务器
     * @param receivePort   收件服务器的端口
     * @param receiveSSL    收件服务器是否开启SSL
     */
    public void setProperties(String smtpServer, String smtpPort, boolean smtpSSL, String protocol,
                              String receiveServer, String receivePort, boolean receiveSSL) {
//        this.properties.setProperty("mail.debug", "true");
        this.properties.setProperty("mail.transport.protocol", "smtp");
        this.properties.setProperty("mail.smtp.host", smtpServer);
        this.properties.setProperty("mail.smtp.port", smtpPort);
        this.properties.setProperty("mail.smtp.auth", "true");
        if (smtpSSL) {
            this.properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            this.properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        }
        if (protocol.equals(Config.POP)) {
            this.properties.setProperty("mail.pop3.host", receiveServer);
            if (receiveSSL) {
                this.properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                this.properties.setProperty("mail.pop3.socketFactory.fallback", "false");
            }
            this.properties.setProperty("mail.pop3.port", receivePort);
            this.properties.setProperty("mail.store.protocol", "pop3");
        }
        if (protocol.equals(Config.IMAP)) {
            this.properties.setProperty("mail.imap.host", receiveServer);
            if (receiveSSL) {
                this.properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                this.properties.setProperty("mail.imap.socketFactory.fallback", "false");
            }
            this.properties.setProperty("mail.imap.port", receivePort);
            this.properties.setProperty("mail.store.protocol", "imap");
        }
    }

    /**
     * 设置鉴权类（T）
     *
     * @param username 用户名
     * @param password 密码
     */
    public void setAuthenticator(String username, String password) {
        this.properties.setProperty("username", username);
        this.properties.setProperty("password", password);
        this.authenticator = AuthenticatorGenerator.getAuthenticator(username, password);
    }

    /**
     * 获取配置类（T）
     *
     * @return 返回properties对象
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * 获取鉴权类（T）
     *
     * @return 返回Authenticator对象
     */
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    /**
     * 获取配置名（T）
     *
     * @return 返回配置名称
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * 获取用户名（T）
     *
     * @return 返回用户的用户名
     */
    public String getUserName() {
        return this.properties.getProperty("username");
    }

    /**
     * 获取用户密码（T）
     *
     * @return 返回用户的密码
     */
    public String getPassWord() {
        return this.properties.getProperty("password");
    }

    /**
     * 保存配置到磁盘（T）
     * @return 成功返回true，否则返回false
     */
    public boolean save() {
        System.out.println("[INFO] Calling Config::save()...");
        try (
                FileOutputStream output = new FileOutputStream(new File("").getCanonicalPath() + this.configName)
        ) {
            this.properties.store(output, "");
        } catch (Exception e) {
            System.out.println("[ERROR] Config::save() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param inputStream 配置文件的IO流
     * 从磁盘加载配置（T）
     */
    public void load(InputStream inputStream) {
        System.out.println("[INFO] Calling Config::load()...");
        //(FileInputStream input = new FileInputStream(new File("").getCanonicalPath() + this.configName);)
        try {
            this.properties.load(inputStream);
            this.setAuthenticator((String) this.properties.get("username"), (String) this.properties.get("password"));
        } catch (Exception e) {
            System.out.println("[ERROR] Config::load() throws Exception!");
            e.printStackTrace();
        }
    }

    /**
     * 获取是否有新邮件（T）
     * @return  如果有新邮件返回true，否则返回false
     */
    public boolean hasNewMail() {
        return this.hasNewMail;
    }

    /**
     * 获取新邮件（T）
     * @return 返回一个邮件列表
     */
    public ArrayList<Mail> getNewMail() {
        System.out.println("[INFO] Calling Config::getNewMail...");
        this.hasNewMail = false;
        return newMail;
    }

    /**
     * 添加一份邮件到列表（T）
     * @param mail Mail对象
     */
    public void addMailToNewMail(Mail mail) {
        this.hasNewMail = true;
        newMail.add(mail);
    }

    @Override
    public String toString() {
//        return "Config{" +
//                "properties=" + properties +
//                ", newMail=" + newMail +
//                ", id=" + id +
//                ", configName='" + configName + '\'' +
//                ", authenticator=" + authenticator +
//                ", hasNewMail=" + hasNewMail +
//                '}';
        return this.configName;
    }

    private ArrayList<Message> serverMailList = new ArrayList<>();

    public ArrayList<Message> getServerMail() {
        return serverMailList;
    }

    public void setServerMailList(ArrayList<Message> list) {
        this.serverMailList = list;
    }
}
