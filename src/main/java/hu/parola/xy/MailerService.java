// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class MailerService {
    public static final Logger LOGGER = LoggerFactory.getLogger(MailerService.class);

    @Resource(mappedName = "java:jboss/mail/xyMail")
    private Session mySession;

    public void send1(String to, String from, String subject, String body) throws Exception {
        Message message = new MimeMessage(mySession);
        message.setFrom(new InternetAddress(from));
        Address toAddress = new InternetAddress(to);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        message.setSubject(subject);
        message.setText("Hello");
        message.setContent(body, "text/plain");
        Transport.send(message);
    }

    public void send(final String to, final String cc_to, final String bcc_to, final String subj, final String body, final String... fileNames) {
        LOGGER.info(Arrays.toString(fileNames));
        // config();
        try {
            final Message message = new MimeMessage(mySession);

            final List<InternetAddress> addressTo = new ArrayList<>();
            // TNRUKAYCM-7: Progrmozó e-mailcím
            addAddressesToList(addressTo, to);
            if (addressTo.size() > 0) {
                message.setRecipients(Message.RecipientType.TO, addressTo.toArray(new InternetAddress[0]));
            } else {
                //throw new MailerException("No Addressees specified!");
                LOGGER.error("No Addressees specified!");
            }

            final List<InternetAddress> addressCc = new ArrayList<>();
            addAddressesToList(addressCc, cc_to);
            if (addressCc.size() > 0) {
                message.setRecipients(Message.RecipientType.CC, addressCc.toArray(new InternetAddress[0]));
            }

            final List<InternetAddress> addressBcc = new ArrayList<>();
            addAddressesToList(addressBcc, bcc_to);

            if (addressBcc.size() > 0) {
                message.setRecipients(Message.RecipientType.BCC, addressBcc.toArray(new InternetAddress[0]));
            }

            // String from = "VideoPortal admin <pm@parola.hu>"; //System.getProperty("video.email");
            String from = System.getProperty("xy.emailFrom", "XY Egyedi rendszer <xydev@parola.hu>");
            final InternetAddress fromIA = new InternetAddress(from);

            // From/Reply To kódolása
            fromIA.setPersonal(from.replaceFirst(" <.+>$", ""), "utf-8");
            message.setFrom(fromIA);

            // TODO Reply To kódolása, akárcsak From esetén...
            // message.setReplyTo(new Address[] { new InternetAddress(reply) });
            message.setSentDate(new Date());
            // EPRGAYCMRF-421 Levél subject nyelvi fájlból
            message.setSubject(MimeUtility.encodeText(subj, "UTF-8", "B"));

            final MimeMultipart mul = new MimeMultipart("related");
            final MimeBodyPart[] parts = createAttachmentParts(fileNames);

            if (!Tools.isEmptyOrNull(body)) {
                String b = body;

                // Fájlnév helyett CID
                for (final MimeBodyPart p : parts) {
                    // TNRUKAYCM-567 Összevetve más forrásból származó levéllel, úgy néz ki itt nem kell kacsacsőr köré...
                    b = b.replaceAll("cid:" + Pattern.quote(p.getFileName()), "cid:" + p.getContentID().replaceAll("[<>]+", ""));
                }

                final MimeBodyPart part = new MimeBodyPart();
                part.setText(b, "UTF-8");
                part.setHeader("Content-Type", "text/html; charset=UTF-8");
                mul.addBodyPart(part);
            }

            for (final MimeBodyPart element : parts) {
                LOGGER.info("part: {} = {}", element.getFileName(), element.getSize());
                mul.addBodyPart(element);
            }

            message.setContent(mul);
            message.saveChanges();

            LOGGER.debug("MESSAGE: {}", message);

            Transport.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.error("sendMail", e);
        }
    }

    // Tools

    private void addAddressesToList(final List<InternetAddress> list, final String addresses) throws AddressException {

        if (!Tools.isEmptyOrNull(addresses)) {
            for (final String mail : addresses.split("\\s*;\\s*")) {
                list.add(new InternetAddress(mail));
            }
        }
    }

    // FIXED Ezt meg kell oldani másként, hogy ne URL menjen. Vagy külső könyvtár, vagy külső URL:
    // Szöveges resource path, majd a hívott fél alakítja URL-lé...
    private MimeBodyPart[] createAttachmentParts(final String[] mailFileNames) throws MessagingException {

        if (mailFileNames != null) {
            final MimeBodyPart[] part = new MimeBodyPart[mailFileNames.length];
            for (int i = 0; i < mailFileNames.length; i++) {
                part[i] = new MimeBodyPart();
                part[i].setDisposition(Part.INLINE);

                final URL fileNameURL = getClass().getResource(mailFileNames[i]);
                LOGGER.info("{} = {}, {}", i, mailFileNames[i], fileNameURL);
                final DataSource fd = new URLDataSource(fileNameURL);
                final DataHandler mailFile = new DataHandler(fd);

                part[i].setDataHandler(mailFile);
                part[i].setFileName(fileNameURL.getFile().replaceFirst(".*/", ""));
                // TNRUKAYCM-567 Összevetve más forrásból származó levelekkel, úgy néz ki itt kacsacsőr kell köré...
                part[i].setContentID(String.format("<%s_%x>", part[i].getFileName(), System.currentTimeMillis()));
            }
            return part;
        } else
            return new MimeBodyPart[0];
    }
}
