package examples;

import java.util.HashMap;
import java.util.Map;

public class UserManagerImpl {
	private Map<String,Pair> users =
		new HashMap<String,Pair>();
	public UserManagerImpl() {
		users.put(
				"administrator",
				new Pair(new User("administrator","admin"),"adadadmin"));
		users.put(
				"demo",
				new Pair(new User("demo","user"),"demodemo"));
	}
	public User authenticate(String name,String password){
		Pair pair = users.get(name);
		if(pair==null){
			return null;
		}
		if(pair.user.getRole().equals(password)){
			return pair.user;
		}
		return null;
	}
}
class Pair{
	public User user;
	public String password;
	public Pair(User user, String password) {
		this.user = user;
		this.password = password;
	}
	
}