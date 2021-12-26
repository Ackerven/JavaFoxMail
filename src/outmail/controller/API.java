package outmail.controller;

import outmail.mail.CheckNewMail;
import outmail.mail.SendMail;
import outmail.model.Config;
import outmail.model.Contact;
import outmail.model.Mail;
import outmail.mysql.MySQL;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * * 前后端接口
 *
 * @author Ackerven
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 3:39
 */
public class API {
    public static final ArrayList<Config> CONFIGS = new ArrayList<>();
    private static final String URL = "jdbc:mysql://110.42.156.179:3306/foxmail?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "foxmail";
    private static final String PASSWORD = "foxmail";
    private static final MySQL MYSQL = new MySQL(URL, USERNAME, PASSWORD);
    private static CheckNewMail cnm = new CheckNewMail(20000L);

    /**
     * 程序运行时先执行初始化（T）
     *
     * @return 如果存在一个或多个配置文件，则返回true，否则返回false
     */
    public static boolean init() {
        System.out.println("[INFO] Calling API::init()...");
        ArrayList<Config> list = MYSQL.queryAllConfig();
        if (list.size() == 0 || list == null) {
            return false;
        }
        CONFIGS.addAll(list);
        cnm.start();
//        checkNewMail();
        return true;
    }

    /**
     * 启动接收新邮件的线程
     */
    public static void checkNewMail() {
        if(!cnm.isAlive()) {
            cnm.start();
        }
    }

    /**
     * 添加一个新的配置（T）
     *
     * @param username      用户名
     * @param password      密码
     * @param smtpServer    SMTP服务器
     * @param smtpPort      SMTP端口
     * @param protocol      收件服务器的协议
     * @param receiveServer 收件服务器
     * @param receivePort   收件服务器端口
     * @param smtpSSL       SMTP是否开启SSL
     * @param receiveSSL    收件服务是否开启SSL
     * @return 添加成功返回True，失败返回False
     */
    public static boolean addNewConfig(String username, String password, String smtpServer, String smtpPort,
                                       String protocol, String receiveServer, String receivePort,
                                       Boolean smtpSSL, Boolean receiveSSL) {
        System.out.println("[INFO] Calling API::addNewConfig()...");
        Config config = new Config(MYSQL.getId("config"), username);
        config.setAuthenticator(username, password);
        config.setProperties(smtpServer, smtpPort, smtpSSL, protocol, receiveServer, receivePort, receiveSSL);
        if (!isConnect(config)) {
            System.out.println("[ERROR] API::addNewConfig() execution failed! (Can't connect)");
            return false;
        }
        if (!config.save()) {
            System.out.println("[ERROR] API::addNewConfig() execution failed");
            return false;
        }
        CONFIGS.add(config);
        File file = null;
        try {
            file = new File(new File("").getCanonicalPath() + config.getConfigName());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (addConfig(config, file)) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 测试是否配置信息是否能连接服务器（T）
     *
     * @param config Config对象
     * @return 如果配置正确，能连接服务器，返回true，否则返回false
     */
    private static boolean isConnect(Config config) {
        System.out.println("[INFO] Calling API::isConnect()...");
        Session session = Session.getDefaultInstance(config.getProperties(), config.getAuthenticator());
        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            System.out.println("[ERROR] API::isConnect() throws Exception! (create transport)");
            e.printStackTrace();
            return false;
        }
        try {
            transport.connect(config.getUserName(), config.getPassWord());
        } catch (MessagingException e) {
            System.out.println("[ERROR] API::isConnect() throws Exception! (connect)");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 清理数据库无效（T）<br>
     * 只有数据库有记录，但本地配置文件以及不存在的，称为无效配置
     *
     * @param list 根据这个列表清理
     */
    public static void clearCache(ArrayList<Config> list) {
        System.out.println("[INFO] Calling API::clearCache()...");
        Iterator<Config> iterator = list.iterator();
        while (iterator.hasNext()) {
            Config c = iterator.next();
            try {
                File file = new File(new File("").getCanonicalPath() + "\\config\\" + c.getConfigName());
                if (!file.exists()) {
                    iterator.remove();
                    delConfig(c.getId());
                }
            } catch (Exception e) {
                System.out.println("[ERROR] API::clearCache() throws Exception!");
                e.printStackTrace();
            }
        }
    }

    /**
     * 往数据库添加配置记录（T）
     * @param config Config对象
     * @param file File 对象
     * @return 成功返回true，失败返回false
     */
    public static boolean addConfig(Config config, File file) {
        System.out.println("[INFO] Calling API::addConfig()...");
        return MYSQL.addConfig(config, file);
    }

    /**
     * 从数据库删除配置记录（T）
     *
     * @param id 配置文件id
     * @return 成功返回true，失败返回false
     */
    public static boolean delConfig(int id) {
        System.out.println("[INFO] Calling API::delConfig()...");
        return MYSQL.delConfig(id);
    }

    /**
     * 查询一条配置记录（T）
     *
     * @param id 配置文件的ID
     * @return 返回一个Config对象
     */
    public static Config queryOneConfig(int id) {
        System.out.println("[INFO] Calling API::queryOneConfig()...");
        return MYSQL.queryOneConfig(id);
    }

    /**
     * 查询所有配置记录（T）
     *
     * @return 返回一个Config对象列表
     */
    public static ArrayList<Config> queryAllConfig() {
        System.out.println("[INFO] Calling API::queryAllConfig()...");
        return MYSQL.queryAllConfig();
    }

    /**
     * 添加联系人（T）
     *
     * @param config  代表哪一个邮箱
     * @param contact 联系人对象
     * @return 如果添加成功，返回true，否则返回false
     */
    public static boolean addContact(Config config, Contact contact) {
        System.out.println("[INFO] Calling API::addContact()...");
        return MYSQL.addContact(config, contact);
    }

    /**
     * 删除联系人（T）
     * 需要传入联系人的ID，通过 getID()获取
     *
     * @param contact Contact对象
     * @return 如果删除成功，返回true，否则返回false
     */
    public static boolean delContact(Contact contact) {
        int id = contact.getId();
        System.out.println("[INFO] Calling API::delContact()...");
        return MYSQL.delContact(id);
    }

    /**
     * 查询联系人（T）<br>
     * 需要传入关键字key，需要查询的字段field，以及是否模糊匹配
     *
     * @param config Config对象
     * @param key    搜索关键字 Key
     * @param field  查询的字段，如果需要查询全部字段，传 null 给field
     * @param fuzzy  是否开启模糊匹配，true为开启，false为关闭
     * @return 返回一个联系人对象，如果没有找到，返回的对象为null
     */
    public static ArrayList<Contact> queryContact(Config config, String key, String field, boolean fuzzy) {
        System.out.println("[INFO] Calling API::queryOneContact()...");
        return MYSQL.queryContact(config, key, field, fuzzy);
    }

    /**
     * 获取全部联系人（T）
     *
     * @param config 代表哪个邮箱
     * @return 返回一个Contact对象列表
     */
    public static ArrayList<Contact> queryAllContact(Config config) {
        System.out.println("[INFO] Calling API::queryAllContact()...");
        return MYSQL.queryAllContact(config);
    }

    /**
     * 更新联系人（T）
     * 注意，在前端通过调用联系人的 Getter和 Setter 修改这个对象的 name，email， description信息
     *
     * @param contact 传入一个联系人对象
     * @return 如果修改成功，返回true，否则返回false
     */
    public static boolean updateContact(Contact contact) {
        System.out.println("[INFO] Calling API::updateContact()...");
        return MYSQL.updateContact(contact);
    }

    /**
     * 发送邮件（T）
     *
     * @param config Config对象
     * @param mail   邮件对象
     * @return 成功返回true，失败返回false
     */
    public static boolean sendMail(Config config, Mail mail) {
        SendMail sendMail = new SendMail(config, mail);
        try {
            sendMail.send();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        mail.setStatus(Mail.SEND);
        MYSQL.addMessage(config, mail);
        return true;
    }

    /**
     * 保存邮件为草稿（T）
     *
     * @param config config对象
     * @param mail   邮件对象
     * @return 成功返回true，失败返回false
     */
    public static boolean saveMail(Config config, Mail mail) {
        mail.setStatus(Mail.DRAFT);
        MYSQL.addMessage(config, mail);
        return true;
    }

    /**
     * 获取收件箱所有邮件（T）
     *
     * @param config Config对象
     * @return 成功返回true，失败返回false
     */
    public static ArrayList<Mail> queryInbox(Config config) {
        ArrayList<Mail> list = MYSQL.queryAllMail(config);
        ArrayList<Mail> inbox = new ArrayList<>();
        if (list == null) {
            return null;
        }
        for (Mail m : list) {
            if (m.getStatus().equals(Mail.SEEN) || m.getStatus().equals(Mail.RECENT)) {
                inbox.add(m);
            }
        }
        return inbox;
    }

    /**
     * 获取已发送的所有邮件（T）
     *
     * @param config Config对象
     * @return 成功返回true，失败返回false
     */
    public static ArrayList<Mail> querySendBox(Config config) {
        return filterMail(config, Mail.SEND);
    }

    /**
     * 获取草稿箱的所有邮件（T）
     *
     * @param config Config对象
     * @return 成功返回true，失败返回false
     */
    public static ArrayList<Mail> queryDraftBox(Config config) {
        return filterMail(config, Mail.DRAFT);
    }

    /**
     * 获取未读邮件（T）
     * @param config Config对象
     * @return 返回邮件列表
     */
    public static ArrayList<Mail> queryRecentBox(Config config) {
        return filterMail(config, Mail.RECENT);
    }

    /**
     * 查询所有邮件
     * @param config    Config对象
     * @return          返回Mail对象列表
     */
    public static ArrayList<Mail> queryAllMail(Config config) {
        return MYSQL.queryAllMail(config);
    }

    /**
     * 邮件过滤器（T）
     *
     * @param config Config
     * @param status 过滤的状态
     * @return 邮件列表
     */
    private static ArrayList<Mail> filterMail(Config config, String status) {
        ArrayList<Mail> list = MYSQL.queryAllMail(config);
        ArrayList<Mail> result = new ArrayList<>();
        if (list == null) {
            return null;
        }
        for (Mail m : list) {
            if (m.getStatus().equals(status)) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * 删除一封邮件（T）
     *
     * @param mail Mail对象
     * @return 成功返回true，失败返回false
     */
    public static boolean delMail(Mail mail) {
//        return MYSQL.delMail(mail.getId());
        mail.setStatus(Mail.DELETE);
        return MYSQL.updateStatus(mail);
    }

    /**
     * 按条件查询邮件
     *
     * @param config Config对象
     * @return 返回Mail的对象列表
     */
    public static ArrayList<Mail> queryMail(Config config, String key) {
        ArrayList<Mail> allMail = MYSQL.queryAllMail(config);
        ArrayList<Mail> result = new ArrayList<>();
        if (allMail != null) {
            for (Mail m : allMail) {
                if (m.getSender().equals(key)) {
                    result.add(m);
                } else if (isExist(key, m.getTo())) {
                    result.add(m);
                } else if (isExist(key, m.getCc())) {
                    result.add(m);
                } else if (isExist(key, m.getBcc())) {
                    result.add(m);
                }
            }
        }
        return result;
    }

    /**
     * 是否有匹配的邮箱
     *
     * @param key    key
     * @param string 原数据
     * @return 如果存在返回true，失败返回false
     */
    private static boolean isExist(String key, String string) {
        String[] strings = string.split(",");
        for (String s : strings) {
            if (s.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 下载邮件附件（T）
     * @param mail     邮件对象
     * @param fileName 附件名
     * @param file     文件对象
     * @return          成功返回true，否则返回false
     */
    public static boolean downLoadAttachment(Mail mail, String fileName, File file) throws IOException {
        System.out.println("[INFO] Calling API::downLoadAttachment()...");
        InputStream inputStream = MYSQL.queryFile(mail, fileName);
        if (inputStream == null) {
            return false;
        }
        int index;
        byte[] bytes = new byte[1024];
        try {
            FileOutputStream downloadFile = new FileOutputStream(file);
            while ((index = inputStream.read(bytes)) != -1) {
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
            inputStream.close();
            downloadFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("[INFO] API::downLoadAttachment() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 已读邮件（T）
     *
     * @param mail Mail对象
     */
    public static void seenMail(Mail mail) {
        mail.setStatus(Mail.SEEN);
        MYSQL.updateStatus(mail);
    }

    /**
     * 添加邮件到数据库（T）
     *
     * @param config Config对象
     * @return 成功返回true，失败返回false
     */
    public static boolean addMailToMySQL(Config config) {
        ArrayList<Mail> list = config.getNewMail();
        for(Mail m: list) {
            MYSQL.addMessage(config, m);
        }
        config.getNewMail().clear();
        return true;
    }

    /**
     * 是否有新邮件
     *
     * @param config Config对象
     * @return 有新邮件返回true，没有返回false
     */
    public static boolean hasNewMain(Config config) {
        return config.hasNewMail();
    }

    /**
     * 获取新邮件列表
     *
     * @param config Config对象
     * @return 返回邮件列表
     */
    public static ArrayList<Mail> getNewMail(Config config) {
        return config.getNewMail();
    }

}
