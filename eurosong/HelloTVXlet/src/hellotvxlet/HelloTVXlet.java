package hellotvxlet;

import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.tv.xlet.*;

import org.davic.resources.ResourceClient;
import org.davic.resources.ResourceProxy;
import org.dvb.ui.DVBColor;
import org.havi.ui.*;
import org.havi.ui.event.*;
import org.havi.ui.event.HBackgroundImageEvent;
import org.havi.ui.event.HBackgroundImageListener;

public class HelloTVXlet implements Xlet, HActionListener, ResourceClient, HBackgroundImageListener {

    private XletContext actueleXletContext;
    private HTextButton startShowKnop;
    private HStaticText titelLabel,stemLabel;
    private int aantalLanden = 5;
    private HStaticText[] uurLabels = new HStaticText[aantalLanden];
    private HStaticText[] landenLabels = new HStaticText[aantalLanden];
    private HStaticText[] statusLabels = new HStaticText[aantalLanden];
    private HTextButton[] stemKnoppen = new HTextButton[aantalLanden];
    private int startShowTeller = 0;
    private HScene scene;
    private boolean debug=true;
    
    private HScreen screen;
    private HBackgroundDevice bgDevice;
    private HBackgroundConfigTemplate bgTemplate;
    private HStillImageBackgroundConfiguration bgConfiguration;
    private HBackgroundImage agrondimg = new HBackgroundImage("eurosong.jpg");
    
    public void notifyRelease(ResourceProxy proxy) { }
    public void release(ResourceProxy proxy) { }
    public boolean requestRelease(ResourceProxy proxy, Object requestData){
        return false;
    }
    
    public void imageLoaded(HBackgroundImageEvent e)
    {
        try{
            bgConfiguration.displayImage(agrondimg);
        }
        catch (Exception s){
            System.out.println(s.toString());
        }
    }
    
    public void imageLoadFailed(HBackgroundImageEvent e)
    {
        System.out.println("Image kan niet geladen worden.");
    }
    
    
    public String[] landenNamen = {"Belgie", "Frankrijk", "Nederland", "Duitsland",
                      "Spanje"};
    String beginUur = "20:00";
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    Date d;
    Calendar cal;
    public Land[] deelnemendeLanden = new Land[aantalLanden];
    private String status;
     
    public HelloTVXlet() {
        
    }

    public void initXlet(XletContext context) throws XletStateChangeException
    {
        
        screen = HScreen.getDefaultHScreen();

        bgDevice = screen.getDefaultHBackgroundDevice(); 

        if (bgDevice.reserveDevice(this))
        {
            System.out.println("Backgroundimage device has been reserved");
        }
        else
        {
            System.out.println("Backgroundimage device cannot be reserved");
        }

        bgTemplate = new HBackgroundConfigTemplate();

        bgTemplate.setPreference(HBackgroundConfigTemplate.STILL_IMAGE, HBackgroundConfigTemplate.REQUIRED);

        bgConfiguration = (HStillImageBackgroundConfiguration) bgDevice.getBestConfiguration(bgTemplate);

        try {
            bgDevice.setBackgroundConfiguration(bgConfiguration);
        } 
        catch (java.lang.Exception e) {
            System.out.println(e.toString());
        }
        
        if(debug)System.out.println("Xlet Initialiseren");
       // Het template maken
       HSceneTemplate sceneTemplate = new HSceneTemplate ( ) ;
       // Grootte en positie ingeven
       sceneTemplate . setPreference (HSceneTemplate.SCENE_SCREEN_DIMENSION,new HScreenDimension(1.0f,1.0f) ,HSceneTemplate.
REQUIRED);
        sceneTemplate . setPreference (HSceneTemplate.SCENE_SCREEN_LOCATION,
        new HScreenPoint(0.0f,0.0f),HSceneTemplate.REQUIRED);
        
        // Een instantie van een Scene vragen aan de factory
        scene = HSceneFactory . getInstance ( ).getBestScene ( sceneTemplate ) ;
        
        titelLabel = new HStaticText("Programma Eurovision Songfestival",150,50,400,50);
        scene.add(titelLabel);
        try {

            d = df.parse(beginUur);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        cal = Calendar.getInstance();
        cal.setTime(d);
        
        aantalLanden = landenNamen.length;
        for(int i=0;i<aantalLanden;i++){
            
            String landNaam = landenNamen[i];
            System.out.println("aantalLanden "+aantalLanden+" land "+landNaam+" beginuur "+beginUur);
            deelnemendeLanden[i] = new Land(landNaam,beginUur);
            cal.add(Calendar.MINUTE, 10);
            beginUur = df.format(cal.getTime());        
            System.out.println("Land"+deelnemendeLanden[i].naam);
            
            uurLabels[i] = new HStaticText(deelnemendeLanden[i].beginUur,200,(150+50*i),150,50);            
            uurLabels[i].setBackground (new DVBColor(255,255,255,179) ) ;  
            uurLabels[i].setBackgroundMode ( HVisible .BACKGROUND_FILL) ;
            landenLabels[i] = new HStaticText(deelnemendeLanden[i].naam,350,(150+50*i),150,50);
            landenLabels[i].setBackground (new DVBColor(255,255,255,179) ) ;  
            landenLabels[i].setBackgroundMode ( HVisible .BACKGROUND_FILL) ;
            scene.add(uurLabels[i]);
            scene.add(landenLabels[i]);
            
        }
        
        startShowKnop = new HTextButton("Start Show",80,476,150,50);
        startShowKnop.setActionCommand("startShow");
        startShowKnop.addHActionListener(this);

        scene.add(startShowKnop);
        startShowKnop.requestFocus();
        
        
        
    
    }

    public void startXlet() {
        if (debug) System.out.println("Xlet Starten");
        agrondimg.load(this);
        // Scene zichtbaar maken
        scene . validate ( ) ; 
        scene . setVisible ( true );
    }

    public void pauseXlet() {
     
    }

    public void destroyXlet(boolean unconditional) throws XletStateChangeException{
     agrondimg.flush();
    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        
        if(action == "startShow"){
            startShow();
        }
        if(action == "stem0"){
            System.out.println("Gestemd");
            stem(0);
        }
        
    }
    
    public void startShow(){
        for(int i=0;i<aantalLanden;i++){
            status = deelnemendeLanden[startShowTeller].laatLandZingen();
            System.out.println(aantalLanden+status);
            statusLabels[i] = new HStaticText(status,100,(150+50*i),100,50);
            scene.add(statusLabels[i]);
            
            stemKnoppen[i] = new HTextButton("Stem",550,(150+50*i),100,50);
            if(i==0){
                stemKnoppen[i].setFocusTraversal(null,stemKnoppen[i+1], null, null);
                scene.add(stemKnoppen[i]);
                stemKnoppen[i].requestFocus();
            }else if(i==aantalLanden-1){
            stemKnoppen[i].setFocusTraversal(stemKnoppen[i-1],null, null, null);
            scene.add(stemKnoppen[i]);
            }else{
                stemKnoppen[i].setFocusTraversal(stemKnoppen[i-1],stemKnoppen[i+1], null, null);
                scene.add(stemKnoppen[i]);
            }
            stemKnoppen[i].setActionCommand("stem0");
            stemKnoppen[i].addHActionListener(this);
        }
            scene.remove(startShowKnop);
            scene.setVisible(false);
            scene.setVisible(true);
            startShowTeller++;
            
            
    }
    
    public void stem(int key){
        for(int i=0;i<aantalLanden;i++){
            
            if(i == key){
               String stemText = deelnemendeLanden[i].gebruikerStemtOpLand();
               stemLabel = new HStaticText("Gestemd",550,(150+50*i),100,50);
               scene.add(stemLabel); 
            }
            scene.remove(stemKnoppen[i]);
            scene.setVisible(false);
            scene.setVisible(true);
        }
        
    }
    
   
    
 
}