/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pomoc;

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
                String od = (String) ulazni.readObject();
                String doo = (String) ulazni.readObject();

                String bg = "00:00";
                String google = "http://www.travelmath.com/driving-time/from/";

                String charset = "UTF-8";
                String userAgent = "Tijana";
                Elements links = Jsoup.connect(google + od + "/to/" + doo).userAgent(userAgent).get().getAllElements();

                Pattern pat = Pattern.compile("\\d+ hours?, \\d+ minutes?");
                Pattern pm = Pattern.compile("\\d+ minutes?");

                for (Element elem : links) {
                    Matcher matcher = pat.matcher(elem.text());
                    if (matcher.find()) {
                        String s = matcher.group(0);
                        System.out.println(s);
                        bg = "00:00";
                        Pattern p = Pattern.compile("\\d+");
                        Matcher m = p.matcher(s);
                        ArrayList<String> lista = new ArrayList<>();
                        int i = 0;
                        while (m.find()) {
                            lista.add(m.group());
                            i++;
                        }
                        if (lista.isEmpty()) {
                            bg = "00:00";
                        } else {
                            if (lista.size() == 1) {
                                if(lista.get(0).equals("0")){
                                    bg="00:00";
                                } else {
                                    bg = "00:" + lista.get(0);
                                }
                            } else {
                                bg = lista.get(0) + ":" + lista.get(1);
                            }
                        }
                    }

                }

                izlazni.writeObject(bg);
                izlazni.flush();

            }
        } catch (IOException ex) {
            Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
