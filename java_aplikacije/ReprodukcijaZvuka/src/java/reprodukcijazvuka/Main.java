/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reprodukcijazvuka;

//import java.net.URLEncoder;
import entiteti.Pesma;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jms.JMSProducer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/**
 *
 * @author tijana
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    static ConnectionFactory connectionFactory;

    @Resource(lookup = "reprodukuj")
    static Queue reprodukuj;

    static int id = 0;
    static String idString = "Id" + id;

    @Resource(lookup = "istorija1")
    static Queue istorija;

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ReprodukcijaZvukaPU");
        EntityManager em = emf.createEntityManager();
        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(reprodukuj);
        JMSProducer producer = context.createProducer();

        Socket soc;
        ObjectOutputStream izlazni = null;
        ObjectInputStream ulazni = null;

        try {
            soc = new Socket("localhost", 50000);
            izlazni = new ObjectOutputStream(soc.getOutputStream());
            ulazni = new ObjectInputStream(soc.getInputStream());

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            try {
                Message message = consumer.receive();
                TextMessage textMessage = (TextMessage) message;
                if (textMessage.getStringProperty("alarm") != null) {
                    
                    Query query = em.createQuery("SELECT p FROM Pesma as p WHERE p.melodija=1");
                    List<Pesma> pesme = query.getResultList();
                    Pesma p = pesme.get(0);
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(p.getLink()));
                    continue;
                }
                if (textMessage.getStringProperty("melodija") != null) {
                    Query query = em.createQuery("SELECT p FROM Pesma as p WHERE p.melodija=1");
                    List<Pesma> pesme = query.getResultList();
                    for (Pesma pesma : pesme) {
                        em.getTransaction().begin();
                        pesma.setMelodija(0);
                        em.persist(pesma);
                        em.getTransaction().commit();
                    }
                    String text = textMessage.getText();

                    
                    izlazni.writeObject(text);
                    izlazni.writeObject(false);
                    izlazni.flush();

                    String vraceno = (String) ulazni.readObject();

                    if (vraceno == null) {
                        continue;
                    }

                    Pesma p = new Pesma();
                    em.getTransaction().begin();
                    p.setPretraga(text);

                    p.setLink(vraceno);
                    p.setMelodija(1);
                    em.persist(p);
                    em.getTransaction().commit();
                    continue;
                }
                if (textMessage.getStringProperty("vrsta") != null) {
                    
                    Query query = em.createQuery("SELECT p FROM Pesma as p");
                    List<Pesma> pesme = query.getResultList();
                   
                    int brojac = 0;
                    for (Pesma pes : pesme) {
                        brojac++;
                        TextMessage textMes = context.createTextMessage(pes.getPretraga());
                        producer.send(istorija, textMes);
                    }
                    
                    TextMessage textMes1 = context.createTextMessage("gotovo");
                    textMes1.setStringProperty("vrednost", "kraj");
                    producer.send(istorija, textMes1);
                    continue;
                }
                String text = textMessage.getText();
               
                izlazni.writeObject(text);
                 izlazni.writeObject(true);
                izlazni.flush();

                String vraceno = (String) ulazni.readObject();

                if (vraceno == null) {
                    continue;
                }

                Pesma p = new Pesma();
                em.getTransaction().begin();
                p.setPretraga(text);
                p.setLink(vraceno);
                em.persist(p);
                em.getTransaction().commit();

            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
