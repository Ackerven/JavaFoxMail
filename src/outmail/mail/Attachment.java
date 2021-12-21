package outmail.mail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Attachment {
    public void haveLink(List<DataSource> attachments, MimeMultipart mm) throws MessagingException, UnsupportedEncodingException {
        for (DataSource attach : attachments) {
            //创建附件"节点"
            MimeBodyPart attachment = new MimeBodyPart();
            DataHandler dh = new DataHandler(attach);
            // 将附件数据添加到"节点"
            attachment.setDataHandler(dh);
            // 设置附件的文件名（需要编码）
            attachment.setFileName(MimeUtility.encodeText(dh.getName()));
            // 添加到mm
            mm.addBodyPart(attachment);
        }
    }
}
