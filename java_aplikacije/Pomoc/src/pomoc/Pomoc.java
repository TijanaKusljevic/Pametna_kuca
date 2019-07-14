/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pomoc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tijana
 */
public class Pomoc {

    
    public static void main(String[] args) {
        try {
            ServerSocket soc=new ServerSocket(50003);
            while(true){
                Socket c=soc.accept();
                new Nit(c).start();
                
            }
        } catch (IOException ex) {
            //Logger.getLogger(Pomoc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
