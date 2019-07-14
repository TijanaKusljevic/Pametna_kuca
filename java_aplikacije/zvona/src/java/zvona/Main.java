/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zvona;

import entiteti.Dogadjaj;
import entiteti.Zvono;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author tijana
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    static ConnectionFactory connectionFactory;

    @Resource(lookup = "navijanje3")
    static Queue navijanje;

    @Resource(lookup = "reprodukuj")
    private static Queue reprodukuj;
    
    @Resource(lookup="ponudjeno")
    private static Queue ponudjeno;

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("zvonaPU");
        EntityManager em = emf.createEntityManager();
        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(navijanje);
        JMSProducer producer = context.createProducer();
        Nit n = new Nit();
        n.setEm(em);
        n.setConnectionFactory(connectionFactory);
        n.setReprodukuj(reprodukuj);
        // n.setReprodukuj(navijanje);
        n.start();

        while (true) {
            try {
                Message message = consumer.receive();
                
                TextMessage textMessage = (TextMessage) message;
                
                if(textMessage.getStringProperty("del")!=null){
                     synchronized (n) {
                        n.notifyAll();
                    }
                    continue;
                }
                
                if(textMessage.getStringProperty("lista")!=null){
                   
                    Query query = em.createQuery("SELECT p FROM Zvono as p where p.datum is null ");
                    List<Zvono> zvona = query.getResultList();
                    for(Zvono z: zvona){
                        TextMessage tm=context.createTextMessage(z.getVreme().toString());
                        producer.send(ponudjeno, tm);
                    }
                    TextMessage tm=context.createTextMessage("kraj");
                    tm.setStringProperty("kraj", "kraj");
                    producer.send(ponudjeno, tm);
                    continue;
                } 
                
                if(textMessage.getStringProperty("apdejt")!=null){
                    String datst=textMessage.getStringProperty("datum");
                    String vremest=textMessage.getStringProperty("vreme");
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date date = new Date();
                    date = sdf.parse(vremest);
                
                    java.sql.Time sqlTime = new java.sql.Time(date.getTime());
                    //System.out.println(sqlTime);
                    //Calendar cal = Calendar.getInstance();
                    //cal.setTime(date);
                    
                    Zvono z = new Zvono();
                    
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date datee = format.parse(datst);
                    
                    java.sql.Date sqlDate = new java.sql.Date(datee.getTime()); //datum za stavljanje
                    
                    
                    Message msg=consumer.receive();
                    ObjectMessage objm= (ObjectMessage) msg;
                    Object o=objm.getObject();
                    Dogadjaj d=null;
                    if(o instanceof Dogadjaj){
                        d=(Dogadjaj) o;
                    }
                    
                    Query query = em.createQuery("SELECT p FROM Dogadjaj as p WHERE p.id=:nesto");
                    query.setParameter("nesto", d.getId());
                    List<Dogadjaj> doge = query.getResultList();
                    
                    Dogadjaj d1=doge.get(0);
                    
                    Query queryz = em.createQuery("SELECT p FROM Zvono as p WHERE p.dogadjaj.id=:nesto");
                    queryz.setParameter("nesto", d1.getId());
                    
                    
                    List<Zvono> zvonce = queryz.getResultList();
                    
                    z=zvonce.get(0);
                    
                    
                    em.getTransaction().begin();
                    z.setAktivan(1);
                    z.setDatum(sqlDate);
                    z.setVreme(sqlTime);
                    z.setPonavlja_se(0);
                   // z.setDogadjaj(d1);
                    em.persist(z);
                    em.getTransaction().commit();
                    synchronized (n) {
                        n.notifyAll();
                    }
                    
                       
                    continue;
                
                }
                
               
                
                
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                date = sdf.parse(textMessage.getText());
                
                java.sql.Time sqlTime = new java.sql.Time(date.getTime());
                 System.out.println(sqlTime);
                 
                 
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Message message1 = consumer.receive();
                
                TextMessage textMessage1 = (TextMessage) message1;
                
                
                Zvono z = new Zvono();
                
                
                if(textMessage1.getStringProperty("dogadjaj")!=null){
                    String datum=textMessage1.getText();
                    Message msg=consumer.receive();
                    ObjectMessage objm= (ObjectMessage) msg;
                    Object o=objm.getObject();
                    Dogadjaj d=null;
                    if(o instanceof Dogadjaj){
                        d=(Dogadjaj) o;
                    }
                    
                    
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date datee = format.parse(datum);
                    System.out.println(datee);
                    java.sql.Date sqlDate = new java.sql.Date(datee.getTime());
                    System.out.println(sqlDate);
                    
                    
                    Query query = em.createQuery("SELECT p FROM Dogadjaj as p WHERE p.id=:nesto");
                    query.setParameter("nesto", d.getId());
                    List<Dogadjaj> doge = query.getResultList();
                    
                    
                    Dogadjaj d1=doge.get(0);
                    
                    
                    em.getTransaction().begin();
                    z.setAktivan(1);
                    z.setDatum(sqlDate);
                    z.setVreme(sqlTime);
                    z.setPonavlja_se(0);
                    z.setDogadjaj(d1);
                    em.persist(z);
                    em.getTransaction().commit();
                    synchronized (n) {
                        n.notifyAll();
                    }
                    continue;
                }
                
                
                Query query = em.createQuery("SELECT p FROM Zvono as p WHERE p.vreme=:novo and p.aktivan=1");
                query.setParameter("novo", sqlTime);
                List<Zvono> zvona = query.getResultList();
                if (!zvona.isEmpty()) {
                    
                    em.getTransaction().begin();
                    z=zvona.get(0);
                    z.setAktivan(1);
                    if(textMessage1.getStringProperty("ponavljanje") != null){
                        z.setPonavlja_se(1);
                    } else {
                        z.setPonavlja_se(0);
                    }
                    em.persist(z);
                    em.getTransaction().commit();
                    synchronized (n) {
                        n.notifyAll();
                    }
                    continue;
                }
                Query query1 = em.createQuery("SELECT p FROM Zvono as p WHERE p.vreme=:novo and p.aktivan=0");
                query1.setParameter("novo", sqlTime);
                List<Zvono> zvona1 = query1.getResultList();
                if(!zvona1.isEmpty()){
                    
                    em.getTransaction().begin();
                    z=zvona1.get(0);
                    z.setAktivan(1);
                    if(textMessage1.getStringProperty("ponavljanje") != null){
                        z.setPonavlja_se(1);
                    } else {
                        z.setPonavlja_se(0);
                    }
                    em.persist(z);
                    em.getTransaction().commit();
                    synchronized (n) {
                        n.notifyAll();
                    }
                    continue;
                }
                
                
                
                if (textMessage1.getStringProperty("ponavljanje") != null) {

                    em.getTransaction().begin();
                    z.setVreme(sqlTime);
                    z.setPonavlja_se(1);
                    z.setAktivan(1);
                    z.setDatum(null);
                    em.persist(z);
                    em.getTransaction().commit();
                } else {
                    em.getTransaction().begin();
                    z.setVreme(sqlTime);
                    z.setPonavlja_se(0);
                    z.setAktivan(1);
                    z.setDatum(null);
                    em.persist(z);
                    em.getTransaction().commit();
                }
                synchronized (n) {
                    n.notifyAll();
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                System.out.println("nije dobar format");
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
