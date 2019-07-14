/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pomoc3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/**
 *
 * @author tijana
 */
public class Nit extends Thread {

    private Socket socket;

    Nit(Socket c) {
        socket = c;

// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static String pusti(String ime, boolean b) {
        try {
            String google = "http://google.com/search?q=";
            String search = "youtube " + ime;
            String charset = "UTF-8";
            Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)).get().getElementsByClass("r H1u2de");
            if (links.isEmpty()) {
                links = Jsoup.connect(google + URLEncoder.encode(search, charset)).get().getElementsByTag("cite");
                if (links.isEmpty()) {

                    return null;
                } else {
                    if(b){
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(links.get(0).text()));
                    }
                    return links.get(0).text();

                }
            }
            Pattern pa = Pattern.compile("www\\.youtube\\.com/watch([^\"]+)");
            Matcher ma = pa.matcher(links.toString());
            if (ma.find()) {
                if(b){
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(ma.group(0)));
                }
                return ma.group(0);
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void run() {
        try {
            ObjectOutputStream izlazni = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ulazni = new ObjectInputStream(socket.getInputStream());

            while (true) {
                String g = (String) ulazni.readObject();
                boolean b=(boolean) ulazni.readObject();
                System.out.println("primio " + g);
                String vraceno = pusti(g, b);
                System.out.println("vratio " + vraceno);
                izlazni.writeObject(vraceno);
                izlazni.flush();
            }

        } catch (IOException ex) {
            Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
