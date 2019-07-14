/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pomoc2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
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

    public void run() {
        try {
            ObjectOutputStream izlazni = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ulazni = new ObjectInputStream(socket.getInputStream());

            while (true) {
                String grad = (String) ulazni.readObject();
                

               
                String google = "http://mylocation.org/";

                String charset = "UTF-8";
                String userAgent = "Tijana";
                Elements links = Jsoup.connect(google).userAgent(userAgent).get().getAllElements();

                Pattern pat = Pattern.compile("City [a-zA-Z]+");
                Pattern pm = Pattern.compile("\\d+ minutes?");
                
                String resenje="";
                for (Element elem : links) {
                    Matcher matcher = pat.matcher(elem.text());
                    if (matcher.find()) {
                        resenje=matcher.group(0);
                        resenje=resenje.substring(5);
                    }

                }

                izlazni.writeObject(resenje);
                izlazni.flush();

            }
        } catch (IOException ex) {
            Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
