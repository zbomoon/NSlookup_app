package com.nslookup;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user, password, subject, body, sender, recipients;
    private Session session;
    private File attachment;
    private Context ct;
    private MimeMessage message;

    static {
        Security.addProvider(new com.nslookup.JSSEProvider());
    }

    public GMailSender(String user, String password, Context ct) {
        this.user = user;
        this.password = password;
        this.ct = ct;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients, File attachment) {
        this.subject = subject;
        this.body = body;
        this.sender = sender;
        this.recipients = recipients;
        this.attachment = attachment;
        try {
            (new Send_thread()).execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    class Send_thread extends AsyncTask<String, Void, String> {
        boolean success = true;

        @Override
        protected String doInBackground(String... params) {
            try {
                message = new MimeMessage(session);
                message.setSender(new InternetAddress(sender));
                message.setSubject(subject);

                FileDataSource fds = new FileDataSource(attachment);

                MimeBodyPart mbp1 = new MimeBodyPart();
                mbp1.setText(body);
                MimeBodyPart mbp2 = new MimeBodyPart();
                mbp2.setDataHandler(new DataHandler(fds));
                mbp2.setFileName("ISP_gps.jpg");

                Multipart mp = new MimeMultipart();
                mp.addBodyPart(mbp1);
                mp.addBodyPart(mbp2);

                message.setContent(mp);
                if (recipients.indexOf(',') > 0)
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
                else message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

                Transport tp = session.getTransport("smtp");
                tp.connect(mailhost, user, password);
                tp.sendMessage(message, message.getAllRecipients());
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            MainActivity.MailingEnded(ct, success);
        }
    }
}  