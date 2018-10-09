package core;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
/**
 * Session Bean implementation class Servercore
 */
@Singleton
public class Servercore implements ServercoreRemote, ServercoreLocal {
private ABACPolicyAuthDemo accessPolicy;
    	
    	/**
    	 * fake server session states
    	 */
    	private List<Session> sessions;
    	
    	/**
    	 * users authenticator knows about
    	 */
    	private List<User> credentials;
    /**
     * Default constructor. 
     */
    public Servercore() {

    	
    		//init the session list and credentials list
    		sessions = new ArrayList<Session>();
    		credentials = new ArrayList<User>();
    		
    		//create a default ABAC policy and add some permissions for user bob
    		accessPolicy = new ABACPolicyAuthDemo();

    		//create bob as valid user
    		//NOTE: we don't store the user's password in our credential store
    		//User hashes the password in its constructor
    		User u = new User("wilma", "arugula", "wilma the User", "Administrator");
    		credentials.add(u);
    		//bob can access everything
    		//accessPolicy.createSimpleUserACLEntry(u.getLogin(), true, true, true);

    		//create sue as valid user
    		u = new User("sasquatch", "jerky", "Sue the User","intern");
    		credentials.add(u);
    		//sue can access choices 1 and 2
    		//accessPolicy.createSimpleUserACLEntry(u.getLogin(), true, true, false);

    		//create sue as valid user
    		u = new User("leroy", "wipeout", "Leroy Jenkins the User","Data Entry");
    		credentials.add(u);
    		//ragnar can only access choice 3
    		//accessPolicy.createSimpleUserACLEntry(u.getLogin(), false, false, true);
    		
    		u = new User("abc", "efg", "New User", "intern");
    		credentials.add(u);
    		//ragnar can only access choice 3
    		///accessPolicy.createSimpleUserACLEntry(u.getLogin(), false, true, false);
    	}
    		
    	/**
    	 * determine if session user has access to function f 
    	 * @param sessionId id of session to lookup in user reference identifier in policy
    	 * @param f function for which permission is being asked
    	 * @return
    	 * @throws LoginException
    	 */@Override
    	public String hasAccess(int sessionId) {
    		String role = "";
    		for(Session s : sessions) {
    			if(s.getSessionId() == sessionId) {
    				//session id matches, use serverSession for user object for access control
    				role= s.getSessionUser().getRole();
    			}else {
    				role = "NoRole";
    			}
    		}
    			
    	 return role;
    	}
    		
    	/**
    	 * login and create a new session if credentials match using Sha-256 hash of user's password
    	 * @param l
    	 * @param pwHash Sha256 hash of password
    	 * @return id of newly created session
    	 * @throws LoginException
    	 */
    	public int loginSha256(String l, String pwHash){
    		//iterate through user credentials and see if l and pwHash match. if so, make a session and returns its id
    		Session s = null;
    		for(User u : credentials) {
    			if(u.getLogin().equals(l) && u.getPasswordHash().equals(pwHash)) {
    				//create a fake server-side session from the user object given to us
    				//in reality this makes no sense. authentication happens on the server side
    				s = new Session(u);
    				sessions.add(s);
    				return s.getSessionId();
    			}
    		}
    		return s.getSessionId();
    	}
    	
    	/**
    	 * remove the session object with id of sessionId
    	 * @param sessionId
    	 */
    	public void logout(int sessionId) {
    		for(int i = sessions.size() - 1; i >= 0; i--) {
    			Session s = sessions.get(i);
    			if(s.getSessionId() == sessionId) {
    				sessions.remove(i);
    			}
    		}
    	}
    	
    	/**
    	 * client does not store any session or user info other than sessionId
    	 * so must ask authenticator for user info
    	 * 
    	 * @param sessionId
    	 * @return
    	 */
    	public String getUserNameFromSessionId(int sessionId){
    		if(sessionId == ServercoreRemote.INVALID_SESSION) 
    			return "Not logged in"; 
    		for(Session s : sessions) {
    			if(s.getSessionId() == sessionId)
    				return s.getSessionUser().getUserName();
    		}
    		return "";
    	}

		
    }

