package core;

import javax.ejb.Local;

@Local
public interface ServercoreLocal {

	public static final int INVALID_SESSION = 0;

	/**
	 * returns true if session with given sessionId has permission for function descriptor f
	 * otherwise returns false
	 * @param sessionId
	 * @param f
	 * @return
	 */
	public String hasAccess(int sessionId);

	/**
	 * checks authenticator credential table for parameters
	 * if exist, creates new session in session table and returns new session id
	 * otherwise throws security exception
	 * @param l
	 * @param pwHash
	 * @return new session id
	 * @throws SecurityException
	 */
	public int loginSha256(String l, String pwHash);

	/**
	 * removes session with the given sessionId from the session table 
	 * @param sessionId
	 */
	public void logout(int sessionId);

	//TODO: the below method should be inherited 
	public  String getUserNameFromSessionId(int sessionId);
}
