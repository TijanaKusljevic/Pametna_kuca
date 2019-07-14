/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korisnickiuredjaj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

/**
 *
 * @author tijana
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    static ConnectionFactory connectionFactory;

    @Resource(lookup = "reprodukuj")
    static Queue reprodukuj;

    @Resource(lookup = "istorija1")
    static Queue istorija;

    @Resource(lookup = "navijanje3")
    static Queue navijanje;

    @Resource(lookup = "ponudjeno")
    private static Queue ponudjeno;

    @Resource(lookup = "planiranje4")
    private static Queue planiranje;
    
    @Resource(lookup="spisak3")
    private static Queue spisak;

    public static void main(String[] args) {

        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(istorija);
        JMSProducer producer1 = context.createProducer(); //za navijanje
        Scanner sc = new Scanner(System.in);
        JMSConsumer consumer11 = context.createConsumer(spisak);
        Socket soc;
        ObjectOutputStream izlazni=null;
        ObjectInputStream ulazni=null;
        
        try {
            soc=new Socket("localhost",50003);
            izlazni=new ObjectOutputStream(soc.getOutputStream());
            ulazni=new ObjectInputStream(soc.getInputStream());
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        Socket soc2;
        ObjectOutputStream izlazni2=null;
        ObjectInputStream ulazni2=null;
        
        try {
            soc2=new Socket("localhost",50004);
            izlazni2=new ObjectOutputStream(soc2.getOutputStream());
            ulazni2=new ObjectInputStream(soc2.getInputStream());
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            try {
                System.out.println("Izaberite opciju(uneti redni broj): ");
                System.out.println("1. Reprodukcija zvuka");
                System.out.println("2. Navijanje alarma");
                System.out.println("3. Planer dogadjaja");

                // int izbor= sc.nextInt();
                // sc.nextLine();
                int izbor = Integer.parseInt(sc.nextLine());
                boolean nazad = false;

                while (izbor < 1 || izbor > 3) {
                    System.out.println("Nepostojeci izbor \n\n");
                    System.out.println("Izaberite opciju(uneti redni broj): ");
                    System.out.println("1. Reprodukcija zvuka");
                    System.out.println("2. Navijanje alarma");
                    System.out.println("3. Planer dogadjaja");
                    //izbor = sc.nextInt();
                    //sc.nextLine();
                    // izbor= Integer.parseInt(sc.nextLine());
                    izbor = Integer.parseInt(sc.nextLine());
                }

                switch (izbor) {
                    case 1:
                        System.out.println("Izaberite opciju(uneti redni broj): ");
                        System.out.println("1. Pusti pesmu");
                        System.out.println("2. Prikazi istoriju");
                        System.out.println("3. Povratak na prethodni meni");
                        //Scanner sc1 = new Scanner(System.in);
                        //int izbor1 = sc.nextInt();
                        //sc.nextLine();
                        int izbor1 = Integer.parseInt(sc.nextLine());
                        switch (izbor1) {
                            case 1:
                                System.out.println("Unesite naziv pesme");
                                String naziv = sc.nextLine();
                                TextMessage textMessage = context.createTextMessage(naziv);
                                producer.send(reprodukuj, textMessage);

                                // sc.close();
                                //sc.
                                break;
                            case 2:
                                TextMessage textMessage1 = context.createTextMessage("istorija");
                                textMessage1.setStringProperty("vrsta", "istorija");
                                producer.send(reprodukuj, textMessage1);
                                int brojac = 0;
                                while (true) {
                                    brojac++;
                                    Message m = consumer.receive();
                                    TextMessage tm = (TextMessage) m;
                                    if (tm.getStringProperty("vrednost") != null) {
                                        System.out.println(tm.getText());
                                        
                                        break;
                                    }
                                    System.out.println(tm.getText());
                                }
                                break;
                            case 3:
                                nazad = true;
                                break;

                        }
                        break;
                    case 2:
                        System.out.println("Izaberite opciju(uneti redni broj): ");
                        System.out.println("1. Navij alarm");
                        System.out.println("2. Prikazi istoriju");
                        System.out.println("3. Promeni melodiju");
                        System.out.println("4. Povratak na prethodni meni");
                        int izbor2 = Integer.parseInt(sc.nextLine());
                        switch (izbor2) {
                            case 1:
                                System.out.println("Unesite vreme  u formatu hh:mm");
                                String vreme = sc.nextLine();
                                TextMessage textMessage1 = context.createTextMessage(vreme);
                                producer.send(navijanje, textMessage1);
                                System.out.println("Da li zelite da se alarm ponavlja? ");
                                System.out.println("1. Da");
                                System.out.println("2. Ne");
                                int ponavljanje = Integer.parseInt(sc.nextLine());
                                if (ponavljanje == 1) {
                                    TextMessage textMessage2 = context.createTextMessage("ponovi");
                                    textMessage2.setStringProperty("ponavljanje", "1");
                                    producer.send(navijanje, textMessage2);

                                } else {
                                    TextMessage textMessage2 = context.createTextMessage("ne ponovi");
                                    producer.send(navijanje, textMessage2);

                                }
                                // SimpleDateFormat sdf=new SimpleDateFormat(vreme);
                                // sdf.parse(sdf);

                                break;
                            case 3:
                                System.out.println("Unesite naziv pesme");
                                String naziv = sc.nextLine();
                                TextMessage textMessage = context.createTextMessage(naziv);
                                textMessage.setStringProperty("melodija", "jeste");
                                producer.send(reprodukuj, textMessage);
                                break;
                            case 2:
                                TextMessage textMessage2 = context.createTextMessage("lista");
                                textMessage2.setStringProperty("lista", "jeste");
                                producer.send(navijanje, textMessage2);
                                JMSConsumer consumer1 = context.createConsumer(ponudjeno);
                                ArrayList<String> stringovi = new ArrayList<String>();
                                int i = 0;
                                while (true) {
                                    Message mes = consumer1.receive();
                                    TextMessage tm = (TextMessage) mes;
                                    if (tm.getStringProperty("kraj") != null) {
                                        break;
                                    }
                                    stringovi.add(tm.getText());
                                    System.out.println(i + ". " + tm.getText());
                                    i++;
                                }
                                int navij = Integer.parseInt(sc.nextLine());
                                String str = stringovi.get(navij);
                                TextMessage textMessage3 = context.createTextMessage(str);
                                producer.send(navijanje, textMessage3);
                                System.out.println("Da li zelite ponavljajuci alarm? ");

                                System.out.println("1. Da");
                                System.out.println("2. Ne");
                                int ponavljanje1 = Integer.parseInt(sc.nextLine());
                                if (ponavljanje1 == 1) {
                                    TextMessage textMessage4 = context.createTextMessage("ponovi");
                                    textMessage4.setStringProperty("ponavljanje", "1");
                                    producer.send(navijanje, textMessage4);

                                } else {
                                    TextMessage textMessage4 = context.createTextMessage("ne ponovi");
                                    producer.send(navijanje, textMessage4);

                                }

                                break;
                        }
                        break;
                    case 3:

                        System.out.println("Izaberite opciju(uneti redni broj): ");
                        System.out.println("1. Kreiraj dogadjaj");
                        System.out.println("2. Izlistaj dogadjaje");
                        System.out.println("3. Povratak na prethodni meni");
                        System.out.println("4. Racunanje vremena putovanja izmedju dva grada");
                        System.out.println("5. Racunanje vremena putovanja do odredjenog grada od trenutne lokacije");
                        int izb = Integer.parseInt(sc.nextLine());
                        TextMessage tmm=context.createTextMessage(izb+"");
                        tmm.setIntProperty("izbor", izb);
                        producer.send(planiranje, tmm);
                        switch (izb) {
                            case 1:
                                System.out.println("Uneti opis dogadjaja:");
                                String str = sc.nextLine();
                                TextMessage tm1=context.createTextMessage(str);
                                producer.send(planiranje, tm1);
                                System.out.println("Uneti vreme dogadjaja u formatu ss:mm :");
                                String vreme = sc.nextLine();
                                TextMessage tm2=context.createTextMessage(vreme);
                                producer.send(planiranje, tm2);
                                
                                System.out.println("Uneti datum dogadjaja u formatu gggg-mm-dd:");
                                String vreme2 = sc.nextLine();
                                TextMessage tm22=context.createTextMessage(vreme2);
                                producer.send(planiranje, tm22);
                                
                                System.out.println("Uneti destinaciju (0 ako ne zelite da unseste):");
                                String destinaciju = sc.nextLine();
                                TextMessage tm3=context.createTextMessage(destinaciju);
                                if(destinaciju.equals("0")){
                                    System.out.println("nula");
                                    tm3.setStringProperty("nula", "0");
                                }
                                //TextMessage tm3=context.createTextMessage(destinaciju);
                                producer.send(planiranje, tm3);
                                
                                System.out.println("Da li zelite podsetnik?");
                                System.out.println("1. Da");
                                System.out.println("2. Ne");
                                int zeli=Integer.parseInt(sc.nextLine());
                                TextMessage tmz=context.createTextMessage("zeli li");
                                tmz.setIntProperty("zeli", zeli);
                                producer.send(planiranje, tmz);
                                
                                
                                break;
                            case 2:
                                int i=0;
                                ArrayList<Long> lista=new ArrayList<>();
                                while(true){
                                    Message receive = consumer11.receive();
                                    TextMessage t=(TextMessage) receive;
                                    if(t.getStringProperty("kraj")!=null){
                                        break;
                                    }
                                    lista.add(t.getLongProperty("ID"));
                                    System.out.println(i+". "+t.getText());
                                    i++;
                                }
                                
                                
                                System.out.println("Izaberite opciju(uneti redni broj): ");
                                System.out.println("1. Obrisi dogadjaj");
                                System.out.println("2. Izmeni dogadjaje");
                                System.out.println("3. Povratak na prethodni meni");
                                int odaberi=Integer.parseInt(sc.nextLine());
                               
                                
                                TextMessage tmess=context.createTextMessage(odaberi+"");
                                tmess.setIntProperty("izbor", odaberi);
                                producer.send(planiranje, tmess);
                                switch(odaberi){
                                    case 1:
                                        System.out.println("Izaberite dogadjaj(uneti redni broj): ");
                                        int koji=Integer.parseInt(sc.nextLine());
                                        long l=lista.get(koji);
                                        System.out.println("long "+l);
                                        TextMessage nova=context.createTextMessage("nebitno");
                                        nova.setLongProperty("poruka", l);
                                        producer.send(planiranje, nova);
                                        break;
                                    case 2:
                                        System.out.println("Izaberite dogadjaj(uneti redni broj): ");
                                        int koji1=Integer.parseInt(sc.nextLine());
                                        
                                        long l1=lista.get(koji1);
                                        TextMessage nova2=context.createTextMessage("nebitno");
                                        nova2.setLongProperty("poruka", l1);
                                        producer.send(planiranje, nova2);
                                        
                                        
                                        System.out.println("Sta zelite da izmenite?");
                                        System.out.println("1. Opis");
                                        System.out.println("2. Vreme");
                                        System.out.println("3. Datum");
                                        System.out.println("4. Destinaciju");
                                        System.out.println("5. Navijanje");
                                        int sta=Integer.parseInt(sc.nextLine());
                                        TextMessage nova1=context.createTextMessage("nebitno");
                                        nova1.setIntProperty("izmena", sta);
                                       // producer.send(planiranje, nova1);
                                        switch(sta){
                                            case 1: 
                                                System.out.println("Unesite novi opis");
                                                String opis_prepravka=sc.nextLine();
                                               // TextMessage opis=context.createTextMessage(opis_prepravka);
                                                nova1.setText(opis_prepravka);
                                                producer.send(planiranje, nova1);
                                                break;
                                            case 2:
                                                System.out.println("Unesite novo vreme u formatu ss:mm");
                                                String vreme_prepravka=sc.nextLine();
                                                nova1.setText(vreme_prepravka);
                                                producer.send(planiranje, nova1);
                                                break;
                                            case 3:
                                                System.out.println("Unesite novi datum u formatu gggg-mm-dd");
                                                String datum_prepravka=sc.nextLine();
                                                nova1.setText(datum_prepravka);
                                                producer.send(planiranje, nova1);
                                                break;
                                            case 4:
                                                System.out.println("Unesite novu destinaciju");
                                                String destinacija_prepravka=sc.nextLine();
                                                nova1.setText(destinacija_prepravka);
                                                producer.send(planiranje, nova1);
                                                break;
                                            case 5:
                                                producer.send(planiranje, nova1);
                                                break;
                                        }
                                        break;
                                    case 3:
                                        break;
                                        
                                }
                                
                                TextMessage tm=context.createTextMessage();
                                break;
                                
                            case 4:
                                System.out.println("Unesite prvi grad");
                                String prvi=sc.nextLine();
                                System.out.println("Unesite drugi grad");
                                String drugi=sc.nextLine();
                                izlazni.writeObject(prvi);
                                izlazni.writeObject(drugi);
                                izlazni.flush();
                                String iii=(String) ulazni.readObject();
                                System.out.println(iii);
                                break;
                                
                            case 5:
                                System.out.println("Unesite odrediste");
                                String odrediste=sc.nextLine();
                                izlazni2.writeObject("nebitno");
                                izlazni2.flush();
                                String gdesam=(String) ulazni2.readObject();
                                
                                izlazni.writeObject(gdesam);
                                izlazni.writeObject(odrediste);
                                izlazni.flush();
                                String ii=(String) ulazni.readObject();
                                System.out.println(ii);
                                break;
                                
                        }
                        

                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
