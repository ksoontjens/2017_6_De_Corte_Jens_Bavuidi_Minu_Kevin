/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hellotvxlet;

/**
 *
 * @author student
 */
public class Land {

   public String naam;
   public String beginUur;
   public String zingStatus;
   public boolean gebruikerHeeftOpHenGestemd = false;
   
    
    
    public Land(String naam, String beginUur){
        this.naam = naam;
        this.beginUur = beginUur;
    }
    
   public String laatLandZingen(){
       return this.zingStatus = "Gezongen";
   }
   
   
   public String gebruikerStemtOpLand(){
       this.gebruikerHeeftOpHenGestemd = true;
       return "Gestemd!";
   }
    
    
    
}
