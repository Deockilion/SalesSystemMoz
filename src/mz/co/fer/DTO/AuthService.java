package mz.co.fer.DTO;

/**
 *
 * @author Deockilion
 */
public class AuthService {

    private static String currentUser;

    /**
     * @return the currentUser
     */
    public static String getCurrentUser() {
        return currentUser;
    }

    /**
     * @param aCurrentUser the currentUser to set
     */
    public static void setCurrentUser(String aCurrentUser) {
        currentUser = aCurrentUser;
    }

}
