/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dogadjaji;

import entiteti.Dogadjaj;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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

    @Resource(lookup = "planiranje4")
    private static Queue planiranje;

    @Resource(lookup = "spisak3")
    private static Queue spisak;

    @Resource(lookup = "navijanje3")
    static Queue navijanje;

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dogadjajiPU");
        EntityManager em = emf.createEntityManager();
        JMSContext context = connectionFactory.createContext();
        JMSConsumer consumer = context.createConsumer(planiranje);
        JMSProducer producer = context.createProducer();

        Socket soc;
        ObjectOutputStream izlazni = null;
        ObjectInputStream ulazni = null;

        try {
            soc = new Socket("localhost", 50003);
            izlazni = new ObjectOutputStream(soc.getOutputStream());
            ulazni = new ObjectInputStream(soc.getInputStream());

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        Socket soc2;
        ObjectOutputStream izlazni2 = null;
        ObjectInputStream ulazni2 = null;

        try {
            soc2 = new Socket("localhost", 50004);
            izlazni2 = new ObjectOutputStream(soc2.getOutputStream());
            ulazni2 = new ObjectInputStream(soc2.getInputStream());

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            try {
                Message receive = consumer.receive();
                if (receive.getIntProperty("izbor") == 1) {

                    Message receive1 = consumer.receive();
                    TextMessage tm = (TextMessage) receive1;

                    String opis = tm.getText();

                    Message receive2 = consumer.receive();
                    TextMessage tm1 = (TextMessage) receive2;
                    String vreme = tm1.getText();

                    Message receive22 = consumer.receive();
                    TextMessage tm11 = (TextMessage) receive22;
                    String datum = tm11.getText();

                    Message receive3 = consumer.receive();
                    TextMessage tm2 = (TextMessage) receive3;
                    String destinacija = tm2.getText();
                    if (tm2.getStringProperty("nula") != null) {
                        destinacija = null;
                        //System.out.println("null dest");
                    }

                    Message receive4 = consumer.receive();
                    TextMessage tm3 = (TextMessage) receive4;

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date date = new Date();
                    date = sdf.parse(vreme);

                    java.sql.Time sqlTime = new java.sql.Time(date.getTime()); //stvarno vreme

                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date2 = new Date();
                    date2 = sdf2.parse(datum);

                    java.sql.Time sqlTime2 = sqlTime;

                    java.sql.Date datumsql = new java.sql.Date(date2.getTime()); //stvarni datum
                    java.sql.Date poslednji = datumsql;

                    if (destinacija != null) {
                        izlazni2.writeObject("bzvz");
                        izlazni2.flush();
                        String gdesam = (String) ulazni2.readObject();

                        izlazni.writeObject(gdesam);
                        izlazni.writeObject(destinacija);
                        izlazni.flush();
                        String s = (String) ulazni.readObject();

                        Date date1 = new Date();
                        date1 = sdf.parse(s);
                        java.sql.Time sqlTime1 = new java.sql.Time(date1.getTime()); //kolko treba vremena

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(sqlTime);
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(sqlTime1);
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(date2);

                        cal.set(Calendar.DAY_OF_MONTH, cal2.get(Calendar.DAY_OF_MONTH));
                        cal.set(Calendar.MONTH, cal2.get(Calendar.MONTH));
                        cal.set(Calendar.YEAR, cal2.get(Calendar.YEAR));

                        cal.add(Calendar.HOUR, (-1) * cal1.get(Calendar.HOUR));
                        cal.add(Calendar.MINUTE, (-1) * cal1.get(Calendar.MINUTE));

                        java.util.Date tjava = cal.getTime(); //stvarni java datum kad treba krenuti
                        System.out.println("tjava "+tjava);
                        String datestavi = new SimpleDateFormat("yyyy-MM-dd").format(tjava);
                        System.out.println("datestavi "+datestavi);
                        String timestavi = new SimpleDateFormat("HH:mm").format(tjava);
                        System.out.println("timestavi "+timestavi);
                        
                        
                        
                        

                        sqlTime = new java.sql.Time(tjava.getTime()); //kad treba da se krene
                        //java.sql.Date poslednji = new java.sql.Date(tjava.getTime());
                        poslednji = new java.sql.Date(tjava.getTime());

                        Dogadjaj dog = new Dogadjaj();
                        boolean treba = false;
                       // System.out.println("transakcija");
                        em.getTransaction().begin();
                        dog.setOpis(opis);
                        dog.setVreme(sqlTime);
                        dog.setDestinacija(destinacija);
                        dog.setDatum(poslednji);
                        dog.setSvreme(sqlTime2);
                        dog.setSdatum(datumsql);

                        if (tm3.getIntProperty("zeli") == 1) {
                            dog.setPodseti(1);
                            treba = true;

                        } else {
                            dog.setPodseti(0);
                        }
                        em.persist(dog);
                        em.getTransaction().commit();

                        if (treba) {
                            TextMessage tma = context.createTextMessage(timestavi);
                            producer.send(navijanje, tma);
                            TextMessage tmb = context.createTextMessage(datestavi);
                            tmb.setStringProperty("dogadjaj", "nis");
                            tmb.setLongProperty("strani", dog.getId());
                            producer.send(navijanje, tmb);
                            ObjectMessage objmess = context.createObjectMessage(dog);
                            producer.send(navijanje, objmess);
                        }
                    } else {

                        sqlTime = sqlTime2;
                        // java.sql.Date poslednji=datumsql;

                       // System.out.println(sqlTime + " " + poslednji);

                        Dogadjaj dog = new Dogadjaj();
                        boolean treba = false;
                       // System.out.println("transakcija else");
                        em.getTransaction().begin();
                        dog.setOpis(opis);
                        dog.setVreme(sqlTime);
                        dog.setDestinacija(destinacija);
                        dog.setDatum(poslednji);
                        dog.setSvreme(sqlTime2);
                        dog.setSdatum(datumsql);

                        if (tm3.getIntProperty("zeli") == 1) {
                            dog.setPodseti(1);
                            treba = true;

                        } else {
                            dog.setPodseti(0);
                        }
                        em.persist(dog);
                        em.getTransaction().commit();

                        if (treba) {
                           // System.out.println("navij alarm");

                            String datestavi = new SimpleDateFormat("yyyy-MM-dd").format(poslednji);
                            //String timestavi = new SimpleDateFormat("HH:mm").format(sqlTime2);

                            SimpleDateFormat sdfp = new SimpleDateFormat("HH:mm");
                            Date dt = new Date(sqlTime2.getTime());
                            String timestavi = sdf.format(dt);

                            //System.out.println(datestavi + " " + timestavi);
                            TextMessage tma = context.createTextMessage(timestavi);
                            producer.send(navijanje, tma);
                            TextMessage tmb = context.createTextMessage(datestavi);
                            tmb.setStringProperty("dogadjaj", "nis");
                            tmb.setLongProperty("strani", dog.getId());
                            producer.send(navijanje, tmb);
                            ObjectMessage objmess = context.createObjectMessage(dog);
                            producer.send(navijanje, objmess);
                        }

                    }
                } else {

                    Query query = em.createQuery("SELECT p FROM Dogadjaj as p ");
                    List<Dogadjaj> doge = query.getResultList();
                    for (Dogadjaj dog : doge) {
                        TextMessage textMessage = null;
                        if (dog.getDestinacija() != null) {
                            textMessage = context.createTextMessage(dog.getOpis() + " " + dog.getSvreme() + " "
                               + dog.getSdatum() + " " + dog.getDestinacija()+ " vreme polaska "+dog.getDatum()+ "  "+ dog.getVreme() + " "
                                       + " podsetnik (0-ne; 1-da) "+dog.getPodseti());
                        } else {
                            textMessage = context.createTextMessage(dog.getOpis() + " " + dog.getSvreme() + " "
                                    + dog.getSdatum() + " podsetnik (0-ne; 1-da) "+dog.getPodseti());
                        }
                        textMessage.setLongProperty("ID", dog.getId());
                        producer.send(spisak, textMessage);
                    }
                    TextMessage jos = context.createTextMessage();
                    jos.setStringProperty("kraj", "kraj");
                    producer.send(spisak, jos);
                    Message receive1 = consumer.receive();
                    System.out.println("izbor + " + receive1.getIntProperty("izbor"));
                    if (receive1.getIntProperty("izbor") == 1) {
                        Message receive2 = consumer.receive();
                        Long l = receive2.getLongProperty("poruka");
                        Query query1 = em.createQuery("DELETE FROM Zvono as p WHERE p.dogadjaj.id=:l");
                        query1.setParameter("l", l);

                        em.getTransaction().begin();
                        query1.executeUpdate();
                        em.getTransaction().commit();

                        em.getTransaction().begin();
                        Dogadjaj d = em.find(Dogadjaj.class, l);
                        em.remove(d);
                        //query1.executeUpdate();
                        em.getTransaction().commit();
                        
                        TextMessage del=context.createTextMessage("notify");
                        del.setStringProperty("del", "del");
                        producer.send(navijanje,del);

                    } else {
                        if (receive1.getIntProperty("izbor") == 3) {
                            continue;
                        }
                        Message receive2 = consumer.receive();
                        Long l1 = receive2.getLongProperty("poruka");
                        System.out.println(l1);
                        Query query2 = em.createQuery("SELECT p FROM Dogadjaj as p WHERE p.id=:l");
                        query2.setParameter("l", l1);
                        List<Dogadjaj> promeni = query2.getResultList();
                        Dogadjaj d = promeni.get(0);

                        Message receive3 = consumer.receive();
                        TextMessage tm3 = (TextMessage) receive3;
                        int izmena = receive3.getIntProperty("izmena");

                        String destinacija = null;
                        java.sql.Time sqlTime = null;
                        java.sql.Date datumsql = null;
                        Date date2 = new Date(); //java date
                        //int podsetnik=d.getPodseti();
                        
                        boolean opi = false;
                        switch (izmena) {
                            case 1:
                                em.getTransaction().begin();
                                d.setOpis(tm3.getText());
                                em.persist(d);
                                em.getTransaction().commit();
                                opi = true;
                                break;
                            case 2:
                                destinacija = d.getDestinacija();
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                Date date = new Date();
                                date = sdf.parse(tm3.getText());
                                sqlTime = new java.sql.Time(date.getTime());
                                datumsql = d.getSdatum();
                                break;
                            case 3:
                                destinacija = d.getDestinacija();
                                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = new Date();
                                date1 = sdf1.parse(tm3.getText());
                                datumsql = new java.sql.Date(date1.getTime());
                                sqlTime = d.getSvreme();
                                break;
                            case 4:
                                destinacija = tm3.getText();
                                datumsql = d.getSdatum();
                                sqlTime = d.getSvreme();
                                break;
                            case 5:
                                int ind=d.getPodseti();
                                
                                if(ind==1){ // obrisi alarm
                                    System.out.println("brisem alarm");
                                    Query query1 = em.createQuery("DELETE FROM Zvono as p WHERE p.dogadjaj.id=:l");
                                    query1.setParameter("l", l1);

                                    em.getTransaction().begin();
                                    query1.executeUpdate();
                                    em.getTransaction().commit();
                                    
                                    em.getTransaction().begin();
                                    d.setPodseti(0);
                                    em.persist(d);
                                    em.getTransaction().commit();
                                    
                                    TextMessage del=context.createTextMessage("notify");
                                    del.setStringProperty("del", "del");
                                    producer.send(navijanje,del);
                                    
                                    opi=true;
                                } else { //upisi alarm
                                    System.out.println("dodajem alarm");
                                    em.getTransaction().begin();
                                    d.setPodseti(1);
                                    em.persist(d);
                                    em.getTransaction().commit();
                                    
                                    
                                    sqlTime = d.getVreme();
                                    datumsql = d.getDatum();
                                    
                                    SimpleDateFormat sdfp = new SimpleDateFormat("HH:mm");
                                    java.util.Date tjava=new java.util.Date(sqlTime.getTime());
                                    String svreme = sdfp.format(tjava);
                                    
                                    System.out.println(tjava);
                            
                                    SimpleDateFormat sdfp1 = new SimpleDateFormat("yyyy-MM-dd");
                                    java.util.Date tjava1=new java.util.Date(datumsql.getTime());
                                    String sdatum=sdfp1.format(tjava1);
                                    
                                    System.out.println(tjava1);
                                    
                                    TextMessage tma = context.createTextMessage(svreme);
                                    producer.send(navijanje, tma);
                                    TextMessage tmb = context.createTextMessage(sdatum);
                                    tmb.setStringProperty("dogadjaj", "nis");
                                    tmb.setLongProperty("strani", d.getId());
                                    producer.send(navijanje, tmb);
                                    ObjectMessage objmess = context.createObjectMessage(d);
                                    producer.send(navijanje, objmess);
                                    
                                    opi=true;
                                    
                                }
                               
                               
                                break;
                        }

                        if (opi) {
                            continue;
                        }

                        date2 = new Date(datumsql.getTime());
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                        java.sql.Time sqlTime2 = sqlTime;

                        izlazni2.writeObject("bzvz");
                        izlazni2.flush();
                        String gdesam = (String) ulazni2.readObject();
                       // System.out.println(gdesam);

                        izlazni.writeObject(gdesam);
                        izlazni.writeObject(destinacija);
                        izlazni.flush();
                        String s = (String) ulazni.readObject();
                       // System.out.println(s);
                        Date date1 = new Date();
                        date1 = sdf.parse(s);
                        java.sql.Time sqlTime1 = new java.sql.Time(date1.getTime()); //kolko treba vremena

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(sqlTime);
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(sqlTime1);
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(date2);

                        cal.set(Calendar.DAY_OF_MONTH, cal2.get(Calendar.DAY_OF_MONTH));
                        cal.set(Calendar.MONTH, cal2.get(Calendar.MONTH));
                        cal.set(Calendar.YEAR, cal2.get(Calendar.YEAR));

                        cal.add(Calendar.HOUR, (-1) * cal1.get(Calendar.HOUR));
                        cal.add(Calendar.MINUTE, (-1) * cal1.get(Calendar.MINUTE));

                        java.util.Date tjava = cal.getTime();

                        sqlTime = new java.sql.Time(tjava.getTime()); //kad treba da se krene
                        java.sql.Date poslednji = new java.sql.Date(tjava.getTime());
                        
                       

                        em.getTransaction().begin();

                        d.setVreme(sqlTime);
                        d.setDestinacija(destinacija);
                        d.setDatum(poslednji);
                        d.setSvreme(sqlTime2);
                        d.setSdatum(datumsql);
                        //d.setPodseti(podsetnik);
                        em.persist(d);
                        em.getTransaction().commit();
                        
                        if(d.getPodseti()==1){
                            TextMessage msgt=context.createTextMessage();
                            msgt.setStringProperty("apdejt", "treba da se menja");
                            //DateFormat df = new SimpleDateFormat("hh:mm");
                            //String svreme=df.format(tjava);
                            
                            SimpleDateFormat sdfp = new SimpleDateFormat("HH:mm");
                            String svreme = sdfp.format(tjava);
                            
                            SimpleDateFormat sdfp1 = new SimpleDateFormat("yyyy-MM-dd");
                            String sdatum=sdfp1.format(tjava);
                            
                            //System.out.println(sdatum+ " "+svreme);
                            
                            msgt.setStringProperty("datum", sdatum);
                            msgt.setStringProperty("vreme", svreme);
                            
                            producer.send(navijanje, msgt);
                            
                            ObjectMessage objmess = context.createObjectMessage(d);
                            producer.send(navijanje, objmess);
                            
                            //Query query1 = em.createQuery("UPDATE  Zvono as p SET p.datum =:sda, p.vreme =:svr WHERE p.dogadjaj.id=:l");
                           // query1.setParameter("l", l1);
                           // query1.setParameter("sda", poslednji);
                            //query1.setParameter("svr", sqlTime);
                            
                           // Query query1=em.createQuery("SELECT z FROM Zvono as z WHERE z.dogadjaj.id=:l");
                          //  query1.setParameter("l", l1);
                          //  List<Zvono> zv = query1.getResultList();
                           // Zvono zz = zv.get(0);
                            

                            //em.getTransaction().begin();
                            //query1.executeUpdate();
                            //em.getTransaction().commit();
                        }

                    }
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                System.out.println("los format");
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
