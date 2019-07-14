/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zvona;

import entiteti.Zvono;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
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
public class Nit extends Thread {

    // @Resource(lookup = "jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    private Queue reprodukuj;

    EntityManager em;
    Zvono z;

    public void setReprodukuj(Queue reprodukuj) {
        this.reprodukuj = reprodukuj;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void run() {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        synchronized (this) {
            try {
                while (true) {
                    
                    Date sad = Calendar.getInstance().getTime();
                    java.sql.Time sqlTime = new java.sql.Time(sad.getTime());
                    java.util.Date utilDate = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    Query query = em.createQuery("SELECT p FROM Zvono as p WHERE (p.vreme>:sad and p.aktivan=1 and p.datum is null) "
                            + " or (p.datum=:dns and p.vreme>:sad) ORDER BY p.vreme");
                    query.setParameter("sad", sqlTime);
                    query.setParameter("dns", sqlDate);
                    List<Zvono> zvona = query.getResultList();
                    if (zvona.size() != 0) {
                        System.out.println(zvona.get(0).getVreme());
                        z = zvona.get(0);
                        Date danas=new Date();
                       
                        
                        
                        
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(zvona.get(0).getVreme());
                        Calendar cal1 = Calendar.getInstance();

                        cal.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
                        cal.set(Calendar.MONTH, cal1.get(Calendar.MONTH));
                        cal.set(Calendar.DATE, cal1.get(Calendar.DATE));

                        wait(cal.getTimeInMillis() - System.currentTimeMillis());
                        TextMessage textMessage = context.createTextMessage("zvoni");
                        textMessage.setStringProperty("alarm", "jeste");
                        if (cal.getTimeInMillis() < System.currentTimeMillis()+5000) {
                            producer.send(reprodukuj, textMessage);
                            if (z.getPonavlja_se() == 0) {
                                em.getTransaction().begin();
                                z.setAktivan(0);
                                em.persist(z);
                                em.getTransaction().commit();
                            }
                        }
                        System.out.println("budan");
                    } else {
                        Query query1 = em.createQuery("SELECT p FROM Zvono as p WHERE (p.vreme<:sad and p.aktivan=1 and p.datum is null) "
                                + " or(p.vreme<:sad and p.datum=:str) ORDER BY p.vreme");
                        query1.setParameter("sad", sqlTime);
                        
                        Date dt = new Date();
                        Calendar c = Calendar.getInstance(); 
                        c.setTime(dt); 
                        c.add(Calendar.DATE, 1);
                        dt = c.getTime();
                        java.sql.Date sqlDate1 = new java.sql.Date(utilDate.getTime());
                        query1.setParameter("str", sqlDate1);
                        List<Zvono> zvona1 = query1.getResultList();
                        if (!zvona1.isEmpty()) {
                            System.out.println(zvona1.get(0).getVreme());
                            z = zvona1.get(0);
                           
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(zvona1.get(0).getVreme());
                            Calendar cal1 = Calendar.getInstance();

                            cal.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
                            cal.set(Calendar.MONTH, cal1.get(Calendar.MONTH));
                            cal.set(Calendar.DATE, cal1.get(Calendar.DATE));
                            cal.add(Calendar.DATE, 1);

                            wait(cal.getTimeInMillis() - System.currentTimeMillis());
                            TextMessage textMessage = context.createTextMessage("zvoni");
                            textMessage.setStringProperty("alarm", "jeste");
                            if (cal.getTimeInMillis() < System.currentTimeMillis()+5000) {
                                producer.send(reprodukuj, textMessage);
                                if (z.getPonavlja_se() == 0) {
                                    em.getTransaction().begin();
                                    z.setAktivan(0);
                                    em.persist(z);
                                    em.getTransaction().commit();
                                }
                            }
                            System.out.println("budan");
                        } else {
                            wait();
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMSException ex) {
                Logger.getLogger(Nit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
