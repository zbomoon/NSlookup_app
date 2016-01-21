package com.nslookup;

import android.content.Context;
import android.os.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
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
    private String user;
    private String password;
    private Session session;
    MimeMessage message;
    private String subject, body, sender, recipients;
    private File attachment;
    private Context ct;

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
        String res = "0";
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
                else
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));


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
            MainActivity.MailingEnded(success, ct);
        }
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}  