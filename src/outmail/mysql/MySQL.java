package outmail.mysql;

import outmail.model.Config;
import outmail.model.Contact;
import outmail.model.Mail;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 封装与数据库有关的操作
 *
 * @author vanessa
 * @version 1.0
 * Copyright (c) 2021 Ackerven All rights reserved.
 * @date 2021/12/12 3:53
 */
public final class MySQL {
    /**
     * JDBC链接
     */
    private final String url;
    /**
     * 数据库用户名
     */
    private final String username;
    /**
     * 数据库密码
     */
    private final String password;

    /**
     * 构造MySQL对象
     *
     * @param url      JDBC 链接
     * @param username 数据库用户名
     * @param password 数据库密码
     */
    public MySQL(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * 获取一个数据库连接（T）
     * 注意：获取和close是要同对出现
     *
     * @return 放回一个 Connection 对象
     */
    private Connection connectMySQL() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("[ERROR] Class.forName() 出错！");
            System.out.println("[ERROR]: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println("connectMySQL() 出错！");
            System.out.println("[ERROR]: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 获取表中自增字段的值（T）
     *
     * @param table 表名
     * @return 自增字段的值
     */
    public int getId(String table) {
        System.out.println("[INFO] Calling MySQL::getId()...");
        int id = -1;
        String sql = "select AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'foxmail' AND TABLE_NAME = ? limit 1";
        try (Connection connection = connectMySQL();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, table);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::getId() throws Exception");
            e.printStackTrace();
            return -1;
        }
        return id;
    }

    /* BEGIN Config */

    /**
     * 增加配置（T）<br>
     * 增加配置记录到数据库
     *
     * @param config Config对象
     * @param file File对象
     * @return 添加成功返回true，失败返回false
     */
    public boolean addConfig(Config config, File file) {
        System.out.println("[INFO] Calling MySQL::addConfig()...");
        String sql = "INSERT INTO config(config_name, properties) VALUES(?, ?)";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, config.getConfigName());
            preparedStatement.setBlob(2, new FileInputStream(file));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::addConfig() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除配置（T）
     *
     * @param id 需要删除配置的id
     * @return 删除成功返回true，失败返回false
     */
    public boolean delConfig(int id) {
        System.out.println("[INFO] Calling MySQL::delConfig()...");
        String sql = "DELETE FROM config WHERE id = ?";
        String sqls = "DELETE FROM contact WHERE cid = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                PreparedStatement preparedStatements = connection.prepareStatement(sqls)
        ) {
            preparedStatements.setInt(1, id);
            preparedStatement.setInt(1, id);
            preparedStatements.executeUpdate();
            if (!delAllMail(new Config(id, ""))) {
                throw new Exception("delAllMail Failed");
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::addConfig() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查询一个配置文件信息（T）
     *
     * @param id 配置记录ID
     * @return 找到返回Config对象，失败返回null
     */
    public Config queryOneConfig(int id) {
        System.out.println("[INFO] Calling MySQL::queryOneConfig()...");
        Config config = null;
        String sql = "SELECT * FROM config WHERE id = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                config = new Config(resultSet.getInt("id"), resultSet.getString("config_name"));
                config.load(resultSet.getBlob("properties").getBinaryStream());
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::queryOneConfig() throws Exception!");
            e.printStackTrace();
            return null;
        }
        return config;
    }

    /**
     * 查询全部配置信息（T）
     *
     * @return 找到返回Config对象列表，失败返回null
     */
    public ArrayList<Config> queryAllConfig() {
        System.out.println("[INFO] Calling MySQL::queryAllConfig()...");
        ArrayList<Config> list = new ArrayList<>();
        String sql = "SELECT * FROM config ORDER BY id";
        try (
                Connection connection = connectMySQL();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                Config config = new Config(resultSet.getInt("id"), resultSet.getString("config_name"));
                config.load(resultSet.getBlob("properties").getBinaryStream());
                list.add(config);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::queryAllConfig() throws Exception!");
            e.printStackTrace();
            return null;
        }
        return list;
    }

    /* END Config */

    /* BEGIN ContactPane */

    /**
     * 添加联系人到数据库（T）<br>
     * 联系人表的 id 字段是自增的，所以写sql语句可以不用填id字段<br>
     * 联系人的表中cid字段通过外键关联config表中的id，所以config的id就是contact的cid<br>
     *
     * @param config  Config对象
     * @param contact Contact对象
     * @return 如果不抛出异常，返回true，否则返回false
     */
    public boolean addContact(Config config, Contact contact) {
        System.out.println("[INFO] Calling MySQL::addContact()...");
        String sql = "INSERT INTO contact(cid, name, email, description) VALUES (?, ?, ?, ?)";
        contact.setId(getId("contact"));
        try (Connection connection = connectMySQL();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, config.getId());
            preparedStatement.setString(2, contact.getName());
            preparedStatement.setString(3, contact.getEmail());
            preparedStatement.setString(4, contact.getDescription());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::addContact() throws Exception! ");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从数据库中删除联系人（T）
     *
     * @param id 联系人的id
     * @return 如果不抛出异常，证明数据库修改是没有问题的，返回true，否则返回false
     */
    public boolean delContact(int id) {
        System.out.println("[INFO] Calling MySQL::delContact()...");
        String sql = "DELETE FROM contact WHERE id = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::delContact() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据关键字key在字段field中查找联系人（T）<br>
     * 联系人的表中cid字段通过外键关联config表中的id，所以config的id就是contact的cid，查找联系人时加上筛选cid为config的id的人
     *
     * @param config Config对象
     * @param key    查找的关键词
     * @param field  查找到字段，如果为null，则不区分字段，即搜索全部字段
     * @param fuzzy  值为true时是开启模糊匹配，为false为精准匹配
     * @return 返回找到的联系人，如果出错或找不到，返回null
     */
    public ArrayList<Contact> queryContact(Config config, String key, String field, boolean fuzzy) {
        System.out.println("[INFO] Calling MySQL::queryAllContact()...");
        ArrayList<Contact> list = new ArrayList<>();
        String sqlName = "SELECT * FROM contact WHERE name like ?";
        String sqlEmail = "SELECT * FROM contact WHERE email like ?";
        String sqlDescription = "SELECT * FROM contact WHERE description like ?";
        if (fuzzy) {
            key = "%" + key + "%";
        }
        try (
                Connection connection = connectMySQL();
                PreparedStatement name = connection.prepareStatement(sqlName);
                PreparedStatement email = connection.prepareStatement(sqlEmail);
                PreparedStatement des = connection.prepareStatement(sqlDescription)
        ) {
            if (field == null) {

            } else {

            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::queryContact() throws Exception");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private void handler(ResultSet resultSet, ArrayList<Contact> list) throws Exception {
        while (resultSet.next()) {
            list.add(new Contact(resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("description")));
        }
    }

    /**
     * 查询所有的联系人（T）<br>
     * 联系人的表中cid字段通过外键关联config表中的id，所以config的id就是contact的cid，查找联系人时加上筛选cid为config的id的人
     *
     * @param config Config对象
     * @return 返回一个Contact的列表，如果出错，返回null
     */
    public ArrayList<Contact> queryAllContact(Config config) {
        System.out.println("[INFO] Calling MySQL::queryAllContact()...");
        ArrayList<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM contact WHERE cid = ? ORDER BY id";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, config.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(new Contact(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("description")));
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::queryAllContact() throws Exception!");
            e.printStackTrace();
            return null;
        }
        return list;
    }

    /**
     * 更新联系人（T）<br>
     * 新的联系人，更新数据库的时候，查询条件为 where id = contact.getId()
     *
     * @param contact Contact对象
     * @return 如果不抛出异常，证明数据库修改是没有问题的，返回true，否则返回false
     */
    public boolean updateContact(Contact contact) {
        System.out.println("[INFO] Calling MySQL::updateContact()...");
        String sql = "UPDATE contact SET name = ?, email = ?, description = ? WHERE id = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, contact.getName());
            preparedStatement.setString(2, contact.getEmail());
            preparedStatement.setString(3, contact.getDescription());
            preparedStatement.setInt(4, contact.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::queryAllContact() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* END BEGIN */

    /* BEGIN Mail */

    /**
     * 添加一封邮件到（T）
     * 事务提交
     *
     * @param config Config对象
     * @param mail   添加的邮件对象
     * @return 如果添加成功，返回true，否则返回false
     */
    public boolean addMessage(Config config, Mail mail) {
        System.out.println("[INFO] Calling MySQL::addMessage...");
//        System.out.println(mail);
        mail.setId(this.getId("mail"));
        String sql = "INSERT INTO mail(cid, msgid, sender, reply_to, receiver, cc, bcc, title, htmlText, plainText, Status, date) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = connectMySQL();
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[ERROR] MySQL::addMessage() throws Exception! (setAutoCommit)");
            return false;
        }
        try (
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, config.getId());
            ps.setString(2, mail.getMsgId());
            ps.setString(3, mail.getSender());
            ps.setString(4, mail.getReplyTo());
            ps.setString(5, mail.getTo());
            ps.setString(6, mail.getCc());
            ps.setString(7, mail.getBcc());
            ps.setString(8, mail.getSubject());
            ps.setBlob(9, mail.getHtmlContentBinaryStream());
            ps.setBlob(10, mail.getPlainContentBinaryStream());
            ps.setString(11, mail.getStatus());
            Timestamp time = null;
            if(mail.getSendData() != null) {
                time = new Timestamp(mail.getSendData().getTime());
            }
            ps.setTimestamp(12, time);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::addMessage throws Exception! (mail table)");
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException ex) {
                e.printStackTrace();
                System.out.println("[ERROR] MySQL::addMessage() throws Exception! (rollback)");
                return false;
            }
            return false;
        }

        if (mail.hasAttachments()) {
            sql = "INSERT INTO mail_attachment(cid, filename, file) VALUES(?, ?, ?)";
            for (DataSource ds : mail.getAttachments()) {
                if (ds.getName() != null) {
                    try (
                            PreparedStatement ps = connection.prepareStatement(sql)
                    ) {
                        ps.setInt(1, mail.getId());
                        ps.setString(2, ds.getName());
                        ps.setBlob(3, ds.getInputStream());
                        ps.executeUpdate();
                    } catch (Exception e) {
                        System.out.println("[ERROR] MySQL::addMessage() throws Exception! (mail_attachment table)");
//                    System.out.println(mail.getSubject());
                        e.printStackTrace();
                        try {
                            connection.rollback();
                            connection.close();
                        } catch (SQLException ex) {
                            e.printStackTrace();
                            System.out.println("[ERROR] MySQL::addMessage() throws Exception! (rollback)");
                        }
                        return false;
                    }
                }
            }
        }

        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            System.out.println("[ERROR] MySQL::addMessage() throws Exception! (commit)");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新邮件的状态（T）
     *
     * @param mail 哪一封邮件
     * @return 如果没有异常抛出，返回true，否则返回false
     */
    public boolean updateStatus(Mail mail) {
        System.out.println("[INFO] Calling MySQL::updateStatus()...");
        String status = mail.getStatus();   // 获取状态
        int id = mail.getId();  // 获取邮件id

        String sql = "UPDATE mail SET Status='" + status + "' WHERE id=" + id + ";";
        try (
                Connection connection = connectMySQL();
                Statement stmt = connection.createStatement()
        ) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::updateStatus() throws Exception!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新邮件的收件人，抄送人，密送人列表（None）
     *
     * @param mail  属于哪一封邮件
     * @param table 修改哪个表   收件人(mail_receiver) 抄送(mail_cc) 密送(mail_bcc)
     * @return 如果没有异常抛出，返回true，否则返回false
     */
    public boolean updateTable(Mail mail, String table) {
        System.out.println("[INFO] 调用updateTable()...");
        //更新邮件的接收者列表
        //这个更新是指先把原来这个邮件id关联的的记录（即cid = mail.getId())删掉，再把新的写进去

        // 获取邮件id 存入cid
        int cid = mail.getId();
        // 删除所有table中 cid = cid 的数据
        String sql_delete = "DELETE FROM " + table + " WHERE cid=" + cid + ";";
        try (
                Connection connection = connectMySQL();
                Statement stmt = connection.createStatement()
        ) {
            stmt.execute(sql_delete);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 新的list 用于存更新后mail 中对应的list
        List<Address> tableList = new ArrayList<>();

        // 找找是哪个 Bcc/Cc/To 并存入tableList
//        if(table == "mail_bcc"){
//            tableList = mail.getBcc();
//        }else if(table == "mail_cc"){
//            tableList = mail.getCc();
//        }else if(table == "mail_receiver"){
//            tableList = mail.getTo();
//        }

        // 找到table里最大的id
        int id = 0;
        String sql1 = "select max(id) from " + table + ";";
        try (
                Connection connection = connectMySQL();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql1)
        ) {
            while (rs.next()) {
                id = rs.getInt(1);
                id += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        // 遍历并且调用数据库存入
        for (int i = 0; i < tableList.size(); i++) {
            String address = "" + tableList.get(i);
            String sql = "INSERT INTO " + table + " VALUES(" + id + "," + cid + " '" + address + "');";
            try (
                    Connection connection = connectMySQL();
                    Statement stmt = connection.createStatement()
            ) {
                stmt.execute(sql);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            // id增加1
            id++;
        }
        return true;
    }

    /**
     * 根据附件id删除附件（None）
     *
     * @param idList id列表
     * @return 成功返回true，失败返回false
     */
    public boolean delAttachment(ArrayList<Integer> idList) {
        System.out.println("[INFO] Calling MySQL::delAttachment...");
        String sql = "DELETE FROM mail_attachment WHERE id = ?";
        Connection connection = connectMySQL();
        //开启事务
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[ERROR] MySQL::delAttachment() throws Exception! (setAutoCommit)");
            return false;
        }
        //操作
        try (
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            for (int id : idList) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::delAttachment throws Exception!");
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException ex) {
                e.printStackTrace();
                System.out.println("[ERROR] MySQL::delAttachment() throws Exception! (rollback)");
                return false;
            }
            return false;
        }
        //提交事务
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            System.out.println("[ERROR] MySQL::delAttachment() throws Exception! (commit)");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新邮件附件列表（None）
     *
     * @param mail 邮件对象
     * @return 成功返回true，失败返回false
     */
    public boolean updateAttachment(Mail mail) {
        System.out.println("[INFO] Calling MySQL::updateAttachment()...");
        String sql = "DELETE FROM mail_attachment WHERE cid = ?";
        Connection connection = connectMySQL();
        //开启事务
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[ERROR] MySQL::updateAttachment() throws Exception! (setAutoCommit)");
            return false;
        }
        //操作——删除文件
        try (
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, mail.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::updateAttachment throws Exception!");
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException ex) {
                e.printStackTrace();
                System.out.println("[ERROR] MySQL::updateAttachment() throws Exception! (rollback)");
                return false;
            }
            return false;
        }
        //操作——添加文件
        sql = "INSERT INTO mail_attachment(cid, filename, file) VALUES(?, ?, ?)";
        for (DataSource ds : mail.getAttachments()) {
            try (
                    PreparedStatement ps = connection.prepareStatement(sql)
            ) {
                ps.setInt(1, mail.getId());
                ps.setString(2, ds.getName());
                ps.setBlob(3, ds.getInputStream());
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("[ERROR] MySQL::updateAttachment() throws Exception! (mail_attachment table)");
                e.printStackTrace();
                try {
                    connection.rollback();
                    connection.close();
                } catch (SQLException ex) {
                    e.printStackTrace();
                    System.out.println("[ERROR] MySQL::updateAttachment() throws Exception! (rollback)");
                }
                return false;
            }
        }
        //提交事务
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            System.out.println("[ERROR] MySQL::updateAttachment() throws Exception! (commit)");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新草稿箱的邮件（None）
     *
     * @param mail 属于哪一封邮件
     * @return 如果没有异常抛出，返回true，否则返回false
     */
    public boolean updateMails(Mail mail) {
        System.out.println("[INFO] 调用updateMail()...");
        // 1:更新mail（除cid
        try {
            Blob html = new SerialBlob(mail.getHtmlContent().getBytes(StandardCharsets.UTF_8));
            Blob text = new SerialBlob(mail.getPlainContent().getBytes(StandardCharsets.UTF_8));
            Timestamp time = new Timestamp(mail.getSendData().getTime());
            String sql = "UPDATE mail SET msgid ='" + mail.getMsgId() +
                    "' AND sender='" + mail.getSender() +
                    "' AND reply_to='" + mail.getReplyTo() +
                    "' AND title='" + mail.getSubject() +
                    "' AND htmlText=" + html +
                    " AND plainText=" + text +
                    " AND Status='" + mail.getStatus() +
                    "' AND date='" + time + "'" +
                    " WHERE" +
                    "id=" + mail.getId() + ";";
            try (
                    Connection connection = connectMySQL();
                    Statement stmt = connection.createStatement()
            ) {
                stmt.execute(sql);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            // 处理发送的表收件人(mail_receiver) 抄送(mail_cc) 密送(mail_bcc)
            updateTable(mail, "mail_receiver");
            updateTable(mail, "mail_cc");
            updateTable(mail, "mail_bcc");

            // **********************处理附件**********************

            // **********************处理完毕**********************
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //写更新主表的逻辑，附表调用上面的函数updateTable
        return false;
    }

    /**
     * 更新Mail（T）
     *
     * @param mail Mail对象
     * @return 成功返回true，失败返回false
     */
    public boolean updateMail(Mail mail) {
        System.out.println("[INFO] Calling MySQL::updateMail()...");
        String sql = "UPDATE mail SET sender = ?, reply_to = ?, receiver = ?," +
                "cc = ?, bcc = ?, title = ?, htmlText = ?, plainText = ?," +
                "Status = ?, date = ? WHERE id = ?";
        Connection connection = connectMySQL();
        //开启事务
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[ERROR] MySQL::updateMail() throws Exception! (setAutoCommit)");
            return false;
        }
        //操作——更新Mail
        try (
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, mail.getSender());
            ps.setString(2, mail.getReplyTo());
            ps.setString(3, mail.getTo());
            ps.setString(4, mail.getCc());
            ps.setString(5, mail.getBcc());
            ps.setString(6, mail.getSubject());
            ps.setBlob(7, mail.getHtmlContentBinaryStream());
            ps.setBlob(8, mail.getPlainContentBinaryStream());
            ps.setString(9, mail.getStatus());
            Timestamp time = new Timestamp(mail.getSendData().getTime());
            ps.setTimestamp(10, time);
            ps.setInt(11, mail.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::updateMail throws Exception!");
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException ex) {
                e.printStackTrace();
                System.out.println("[ERROR] MySQL::updateMail() throws Exception! (rollback)");
                return false;
            }
            return false;
        }

        sql = "DELETE FROM mail_attachment WHERE cid = ?";
        //操作——删除文件
        try (
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, mail.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::updateAttachment throws Exception!");
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException ex) {
                e.printStackTrace();
                System.out.println("[ERROR] MySQL::updateAttachment() throws Exception! (rollback)");
                return false;
            }
            return false;
        }
        //操作——添加文件
        sql = "INSERT INTO mail_attachment(cid, filename, file) VALUES(?, ?, ?)";
        for (DataSource ds : mail.getAttachments()) {
            try (
                    PreparedStatement ps = connection.prepareStatement(sql)
            ) {
                ps.setInt(1, mail.getId());
                ps.setString(2, ds.getName());
                ps.setBlob(3, ds.getInputStream());
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("[ERROR] MySQL::updateAttachment() throws Exception! (mail_attachment table)");
                e.printStackTrace();
                try {
                    connection.rollback();
                    connection.close();
                } catch (SQLException ex) {
                    e.printStackTrace();
                    System.out.println("[ERROR] MySQL::updateAttachment() throws Exception! (rollback)");
                }
                return false;
            }
        }
        //提交事务
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            System.out.println("[ERROR] MySQL::updateMail() throws Exception! (commit)");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除一封邮件（None）
     *
     * @param id 邮件的id
     * @return 如果没有异常抛出，返回true，否则返回false
     */
    public boolean delMails(int id) {
        System.out.println("[INFO] 调用delMail()...");

        //有外键关联,所以先删除子表(CC,BCC,receiver,attachment)的数据,再删主表的数据
        // 1:删cc表
        String sql = "DETELE FROM mail_cc WHERE cid=" + id + ";";
        try (
                Connection connection = connectMySQL();
                Statement stmt = connection.createStatement()
        ) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 2: 删bcc
        sql = "DETELE FROM mail_bcc WHERE cid=" + id + ";";
        try (
                Connection connection = connectMySQL();
                Statement stmt = connection.createStatement()
        ) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 3:删mail_receiver
        sql = "DETELE FROM mail_receiver WHERE cid=" + id + ";";
        try (
                Connection connection = connectMySQL();
                Statement stmt = connection.createStatement()
        ) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }

        // 删除邮件
        sql = "DETELE FROM mail WHERE id=" + id + ";";
        try (
                Connection connection = connectMySQL();
                Statement stmt = connection.createStatement()
        ) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 删除一封邮件（T）
     *
     * @param id 邮件的id
     * @return 如果没有异常抛出，返回true，否则返回false
     */
    public boolean delMail(int id) {
        System.out.println("[INFO] Calling MySQL::delMail()...");
        String sql = "DELETE FROM mail_attachment WHERE cid = ?";
        Connection connection = connectMySQL();
        //开启事务
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[ERROR] MySQL::delMail() throws Exception! (setAutoCommit)");
            return false;
        }
        //操作——删除附件
        try (
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::delMail() throws Exception! (delete file)");
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException ex) {
                e.printStackTrace();
                System.out.println("[ERROR] MySQL::delMail() throws Exception! (rollback)");
                return false;
            }
            return false;
        }

        sql = "DELETE FROM mail WHERE id = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::delMail() throws Exception! (delete mail)");
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException ex) {
                e.printStackTrace();
                System.out.println("[ERROR] MySQL::delMail() throws Exception! (rollback)");
                return false;
            }
            return false;
        }

        //提交事务
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            System.out.println("[ERROR] MySQL::delMail() throws Exception! (commit)");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除某个配置关联的所有邮件（T）
     *
     * @param config Config对象
     * @return 成功返回
     */
    public boolean delAllMail(Config config) {
        System.out.println("[INFO] Calling MySQL::delMail()...");
        //先查出哪些邮件ID是与config.getId()关联的，（即cid = config.getId())
        String sql = "SELECT id FROM mail WHERE cid =" + config.getId() + ";";
        try (
                Connection connection = connectMySQL();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                //然后再调用上面的delMail，传这些id进去
                delMail(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查询某个配置关联的所有邮件 （T）
     *
     * @param config Config 对象
     * @return Mail对象列表
     */
    public ArrayList<Mail> queryAllMail(Config config) {
        System.out.println("[INFO] Calling MySQL::queryAllMail()...");

        ArrayList<Mail> mailList = new ArrayList<>();
        //查询主表,子表,构建Mail对象列表
        String sql = "SELECT * FROM mail WHERE cid=" + config.getId() + ";";
        try (
                Connection connection = connectMySQL();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                Mail mail = new Mail();
                mail.setId(rs.getInt("id"));
                mail.setMsgId(rs.getString("msgid"));
                mail.setSender(rs.getString("sender"));
                mail.setReplyTo(rs.getString("reply_to"));
                mail.setTo(rs.getString("receiver"));
                mail.setCc(rs.getString("cc"));
                mail.setBcc(rs.getString("bcc"));
                mail.setSubject(rs.getString("title"));
                Blob blob = rs.getBlob("htmlText");
                if (blob != null) {
                    mail.setHtmlContent(blob.getBinaryStream());
                }
                blob = rs.getBlob("plainText");
                if (blob != null) {
                    mail.setPlainContent(blob.getBinaryStream());
                }
                mail.setStatus(rs.getString("Status"));
                mail.setSendData(new Date(rs.getTimestamp("date").getTime()));
                mailList.add(mail);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] MySQL::queryAllMail() throws Exception! (mail)");
            e.printStackTrace();
            return null;
        }

        sql = "SELECT filename FROM mail_attachment WHERE cid = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            for (Mail mail : mailList) {
                ps.setInt(1, mail.getId());
                ResultSet rs = ps.executeQuery();
                ArrayList<String> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(rs.getString("filename"));
                }
                mail.setAttachmentName(list);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::queryAllMail() throws Exception! (mail_attachment)");
            e.printStackTrace();
            return null;
        }
        return mailList;
    }

    /**
     * 获取邮件附件（T）
     *
     * @param mail     Mail对象
     * @param fileName 附加名
     * @return 返回一个InputStream流
     */
    public InputStream queryFile(Mail mail, String fileName) {
        System.out.println("[INFO] Calling MySQL::queryFile()...");
        InputStream inputStream = null;
        String sql = "SELECT * FROM mail_attachment WHERE filename = ? AND cid = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, fileName);
            ps.setInt(2, mail.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Blob blob = rs.getBlob("file");
                inputStream = blob.getBinaryStream();
            }
        } catch (Exception e) {
            System.out.println("[INFO] Calling MySQL::queryFile()...");
            e.printStackTrace();
            return null;
        }
        return inputStream;
    }
    /* END Mail */

    /**
     * 获取未读邮件（None）
     *
     * @param config Config 对象
     * @return 返回邮件列表
     */
    public ArrayList<Mail> queryRecentMail(Config config) {
        System.out.println("[INFO] Calling MySQL::queryRecentMail()...");

        ArrayList<Mail> mailList = new ArrayList<>();
        //查询主表,子表,构建Mail对象列表
        String sql = "SELECT * FROM mail WHERE cid = ? AND Status = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, config.getId());
            ps.setString(2, Mail.RECENT);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Mail mail = new Mail();
                mail.setId(rs.getInt("id"));
                mail.setMsgId(rs.getString("msgid"));
                mail.setSender(rs.getString("sender"));
                mail.setReplyTo(rs.getString("reply_to"));
                mail.setTo(rs.getString("receiver"));
                mail.setCc(rs.getString("cc"));
                mail.setBcc(rs.getString("bcc"));
                mail.setSubject(rs.getString("title"));
                Blob blob = rs.getBlob("htmlText");
                if (blob != null) {
                    mail.setHtmlContent(blob.getBinaryStream());
                }
                blob = rs.getBlob("plainText");
                if (blob != null) {
                    mail.setPlainContent(blob.getBinaryStream());
                }
                mail.setStatus(rs.getString("Status"));
                mail.setSendData(new Date(rs.getTimestamp("date").getTime()));
                mailList.add(mail);
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] MySQL::queryRecentMail() throws Exception! (mail)");
            e.printStackTrace();
            return null;
        }

        sql = "SELECT filename FROM mail_attachment WHERE cid = ?";
        try (
                Connection connection = connectMySQL();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            for (Mail mail : mailList) {
                ps.setInt(1, mail.getId());
                ResultSet rs = ps.executeQuery();
                ArrayList<String> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(rs.getString("filename"));
                }
                mail.setAttachmentName(list);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] MySQL::queryRecentMail() throws Exception! (mail_attachment)");
            e.printStackTrace();
            return null;
        }
        return mailList;
    }

}
