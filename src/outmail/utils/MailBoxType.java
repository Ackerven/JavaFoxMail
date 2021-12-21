package outmail.utils;

import java.util.Properties;

/**
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 3:11
 * 一些常用的邮箱配置
 */
public enum MailBoxType {
    // QQ: QQ邮箱的认证信息
    QQ {
        @Override
        public Properties getProperties() {
            Properties defaults = new Properties();
            defaults.setProperty("mail.smtp.host", "smtp.qq.com");
            defaults.setProperty("mail.smtp.auth", "true");
            defaults.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            defaults.setProperty("mail.smtp.socketFactory.fallback", "false");
            defaults.setProperty("mail.smtp.port", "587");
            defaults.setProperty("mail.transport.protocol", "smtp");
            defaults.setProperty("mail.pop3.host", "pop.qq.com");
            defaults.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            defaults.setProperty("mail.pop3.socketFactory.fallback", "false");
            defaults.setProperty("mail.pop3.port", "995");
            defaults.setProperty("mail.imap.host", "imap.qq.com");
            defaults.setProperty("mail.store.protocol", "pop3");
            defaults.setProperty("mail.debug", "true");
            return defaults;
        }
    };

    public abstract Properties getProperties();
}
