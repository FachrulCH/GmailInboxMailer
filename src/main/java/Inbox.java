import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.*;
import java.io.IOException;
import java.util.Properties;

public class Inbox {

    public static void main(String[] args) throws MessagingException {
//        ambilSemua();
        cari("donk");
    }

    public static void cari(String sesuatu){
        String host = "imap.googlemail.com";
        String user = "email@gmail.com";
        String password = "passwordnya";

        Properties prop = System.getProperties();
        Session session = Session.getDefaultInstance(prop);

        try {
            Store store = session.getStore("imaps");
            store.connect(host, user, password);
            IMAPFolder inbox = (IMAPFolder) store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);

            /*
            // cari yg belum kebaca
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);

            Message messages[] = inbox.search(unseenFlagTerm);
            Message messages[] = cariSesuatu(inbox, "Salam");
            */
            //Message messages[] = untukPenerima(inbox, "email+keegan@gmail.com");

            //Pencarian penerima
            //SearchTerm pencarian = createFieldSearchTerm("to", "email+keegan@gmail.com");
            //Message messages[] = inbox.search(pencarian);

            //pencarian judul
            SearchTerm pencarian = createFieldSearchTerm("subject", "Selamat Datang di fachrul.net");
            Message messages[] = inbox.search(pencarian);

            if (messages.length == 0) System.out.println("Ga ada pesan nih");

            //keluarin isinya
            System.out.println("Ditemukan "+messages.length);
            for (int i = 0; i < messages.length; i++){
                Message msg = messages[i];

                System.out.println("Judul: "+msg.getSubject());
                System.out.println("Dari: "+msg.getFrom()[0]);
                System.out.println("Penerima: "+msg.getAllRecipients()[0]);
                System.out.println("Tanggal: "+msg.getReceivedDate());
                System.out.println(msg.getFlags());
                System.out.println("\n \n");
//                System.out.println(msg.getContent());
                System.out.println(ambilIsi(msg));
                System.out.println(msg.getContentType());
            }

            inbox.close(true);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ambilSemua() throws MessagingException {
        IMAPFolder folder = null;
        Store store = null;
        String subject = null;
        Flags.Flag flag = null;

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        Session session = Session.getDefaultInstance(props, null);

        try {
            store = session.getStore("imaps");
            store.connect("imap.googlemail.com", "email@gmail.com", "passwordnya");
            folder = (IMAPFolder) store.getFolder("inbox");
            if (!folder.isOpen())
                folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();
            System.out.println("Jumlah email "+ folder.getMessageCount());
            System.out.println("Jumlah yg belum kebaca "+folder.getUnreadMessageCount());

            // keluarin semua email
            for (int i=0; i < messages.length; i++){
                System.out.println("===============================");
                System.out.println("Pesan ke "+(i+1)+" : ");

                Message msg = messages[i];

                System.out.println("Judul: "+msg.getSubject());
                System.out.println("Dari: "+msg.getFrom()[0]);
                System.out.println("Penerima: "+msg.getAllRecipients()[0]);
                System.out.println("Tanggal: "+msg.getReceivedDate());
                System.out.println(msg.getFlags());
                System.out.println("\n \n");
                System.out.println(msg.getContent());
                System.out.println(msg.getContentType());

            }

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (folder != null && folder.isOpen()){
                folder.close(true);
            }

            if (store != null){
                store.close();
            }
        }

    }

    private static String ambilIsi(Message msg) throws MessagingException, IOException {
        String contentType = msg.getContentType();
        String content ="";

        if (contentType.contains("multipart")){
            Multipart multipart = (Multipart) msg.getContent();
            int numberOfParts = multipart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++){
                MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(partCount);
                content = part.getContent().toString();
            }
        }else{
            content = msg.getContent().toString();
        }

        return content;
    }

    private static String cariPenerima(Message msg){
        return null;
    }

    private static Message[] cariSesuatu(IMAPFolder inbox, String kata) throws MessagingException {
        SearchTerm searchTerm = new AndTerm(new SubjectTerm(kata), new BodyTerm(kata));
        return inbox.search(searchTerm);
    }

    private static Message[] untukPenerima(IMAPFolder inbox, String kata) throws MessagingException {
        SearchTerm searchTerm = new RecipientStringTerm(Message.RecipientType.TO ,kata);
        return inbox.search(searchTerm);
    }

    private static SearchTerm createFieldSearchTerm(String f, String matchingText) {
        String s = f.toLowerCase();
        if ("from".equals(s)) {
            return new FromStringTerm(matchingText);
        } else if ("cc".equals(s)) {
            return new RecipientStringTerm(Message.RecipientType.CC, matchingText);
        } else if ("to".equals(s)) {
            return new RecipientStringTerm(Message.RecipientType.TO, matchingText);
        } else if ("body".equals(s)) {
            return new BodyTerm(matchingText);
        } else if ("subject".equals(s)) {
            return new SubjectTerm(matchingText);
        } else {
            return null;
        }
    }


}
