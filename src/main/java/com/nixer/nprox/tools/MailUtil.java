package com.nixer.nprox.tools;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MailUtil {

        // Replace sender@example.com with your "From" address.
        // This address must be verified.
        static final String FROM = "code@honeypool.farm";
        static final String FROMNAME = "HoneyPool";

        // Replace recipient@example.com with a "To" address. If your account
        // is still in the sandbox, this address must be verified.
        static final String TO = "jingbo123654789@163.com";

        // Replace smtp_username with your Amazon SES SMTP user name.
        static final String SMTP_USERNAME = "AKIA6OWEKU5XXJTDKROM";

        // Replace smtp_password with your Amazon SES SMTP password.
        static final String SMTP_PASSWORD = "BI+K2ziuyllN7jEOsuoiOks//3xQUSEh6Svfcz3JzwGr";

        // The name of the Configuration Set to use for this message.
        // If you comment out or remove this variable, you will also need to
        // comment out or remove the header below.
        //static final String CONFIGSET = "ConfigSet";

        // Amazon SES SMTP host name. This example uses the US West (Oregon) region.
        // See https://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html#region-endpoints
        // for more information.
        static final String HOST = "email-smtp.us-west-1.amazonaws.com";

        // The port you will connect to on the Amazon SES SMTP endpoint.
        static final int PORT = 587;

        static final String SUBJECT = "HoenyPool";


        public static void sendEmail(String to,String code) throws Exception {
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(props);

            String BODY = String.join(
                    System.getProperty("line.separator"),
                    "<h1>HoenyPool</h1>",
                    "<p>欢迎您使用HoenyPool ",
                    "您的验证码是:"+code,
                    ""
            );
            // Create a message with the specified information.
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM,FROMNAME));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(SUBJECT);
            msg.setContent(BODY,"text/html;charset=utf-8");
            Transport transport = session.getTransport();
            try
            {
                System.out.println("Sending...");
                // Connect to Amazon SES using the SMTP username and password you specified above.
                transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
                // Send the email.
                transport.sendMessage(msg, msg.getAllRecipients());
                System.out.println("Email sent!");
            }
            catch (Exception ex) {
                System.out.println("The email was not sent.");
                System.out.println("Error message: " + ex.getMessage());
            }
            finally
            {
                transport.close();
            }
        }




        public static void main(String[] args) throws Exception {
            sendEmail("jingbo123654789@163.com","123123");
        }

    }
