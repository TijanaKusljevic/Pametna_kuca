/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pomoc2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author tijana
 */
public class Pomoc2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         try {
            ServerSocket soc=new ServerSocket(50004);
            while(true){
                Socket c=soc.accept();
                new Nit(c).start();
                
            }
        } catch (IOException ex) {
            //Logger.getLogger(Pomoc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
