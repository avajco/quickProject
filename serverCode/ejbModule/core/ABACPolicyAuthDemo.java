package core;

import java.util.HashMap;

import javax.security.auth.login.LoginException;
public class ABACPolicyAuthDemo {

	/**
	 * access control list for users and functions (see above)
	 */
	private HashMap<String, String> acl;
	
	public ABACPolicyAuthDemo() {
		//create default fall-through policy where person has no access
		acl = new HashMap<String, String>();
		
		//always good to have a fall through ACL entry configured with default permissions
		createSimpleUserACLEntry("default",null);
	}
	
	/*
	 * convenience function that creates a set of ACL entries specific to this demo app
	 * a general function is below; call it once for each permission 
	 */
	public void createSimpleUserACLEntry(String login, String role) {
		//add user table to acl
		acl.put(login, role);
	}
	
	/**
	 * Change user's permission for function f
	 * @param uName user whose permission will change
	 * @param f the application function
	 * @param val new permission 
	 */
	public void setUserACLEntry(String uName, String role) throws LoginException {
		if(!acl.containsKey(uName))
			throw new LoginException(uName + " does not exist in ACL");
		
		 acl.put(uName, role);
	}
/*	
	public boolean canUserAccessFunction(String userName, String role) {
		//make sure our ACL has a default entry and fail deny if default entry does not exist 
		if(!acl.containsKey("default"))
			return false;
		String userTable = acl.get("default");
		
		//get user's table if it is in acl. otherwise, use default
		if(acl.containsKey(userName))
			userTable = acl.get(userName);
		
		//is permission for function in table? if so return that permission
		if(userTable.containsKey(functionName))
			return userTable.get(functionName);

		//otherwise return false (permission does not exist)
		return false;
	}*/
}
