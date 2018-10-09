package clientEjb;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import core.ServercoreRemote;

//#1
 public class clientConn {
	// Java program implementing Singleton class
	// with getInstance() method
	
	//class Singleton #1
	     
	    // static variable single_instance of type Singleton
	    private static clientConn single_instance = null;
	 
	    // variable of type String
	    public String s;
	    //declear var // Java program implementing Singleton class
		//NOTE: InitialContext MUST have same scope as the bean variable
		public InitialContext context;
	     public  ServercoreRemote bean ;
	     public Properties props;
	     public int SessionId;
	        // private constructor restricted to this class itself
	    public String name;
	     private clientConn() throws NamingException {
	    	 System.out.println("Client App Started");
	 		Properties props = new Properties();
	 		props.put("java.naming.factory.url.pkgs","org.jboss.ejb.client.naming");
	        context = new InitialContext(props);

	         String appName = "";        	 
	         String moduleName = "MsgFromServerEJB";
	         String distinctName = "";        	 
	         String beanName = ServercoreRemote.class.getSimpleName();        	 
	         String interfaceName = ServercoreRemote.class.getName();
	         String nameas  = "ejb:/MsgFromServerEJB//ServercoreRemote!core.ServercoreRemote";
	         name = "ejb:/serverCode/Servercore!core.ServercoreRemote";
	        		 //"ejb:" + appName + "/" + moduleName + "/" +  distinctName    + "/" + beanName + "!" + interfaceName;
	         bean = (ServercoreRemote)context.lookup(name);
	         System.out.println(name);
	         System.out.println("we can test code");
	    		}
 
	       	        // static method to create instance of clientId class
	     public static clientConn getInstance() throws NamingException {
	      if(single_instance == null)
	    	  single_instance = new clientConn();
	    	  return single_instance;
	      }
	     
	     public  int getLoginsha256(String l, String pwHash) {
	    	 return bean.loginSha256(l, pwHash);
	     }
	     public void getLogout(int sessionId) {
	    	 bean.logout(sessionId);
	     }
	     public  String getHasAccess(int sessionId) {
	    	 return bean.hasAccess(sessionId);
	     }
	  
	     }
	   
	
	

