package catcher;

import beans.CallResult;

public class StartHere {
	public static void main(String[] args) {
		VirtualLogin login = VirtualLogin.getLogin();
		CallResult result = login.login();
		String uinitialid = result.getUniqueid();
		System.out.println(uinitialid);
		}
}
