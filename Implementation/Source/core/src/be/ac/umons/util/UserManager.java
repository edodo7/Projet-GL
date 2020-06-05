package be.ac.umons.util;


import be.ac.umons.game.GameLevel;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class contains all registered users and allow to do operations on theses users.
 * Only one instance of UserManager exist while the program is running.
 */
public class UserManager implements Serializable{

    private static UserManager instance;

    private ArrayList<User> users;

    private User currentUser;

    private UserManager(){ }

    /**
     * @return One unique instance of UserManager
     */
    public static UserManager getUserManager(){
        if(instance == null)
            instance = new UserManager();
        return instance;
    }

    /**
     * Allow to register an user if his username is not already registered
     * @param name The name of the user to add
     * @param password tThe password of the user to add
     * @return true if this username doesn't exist already, false otherwise
     */
    public boolean addUser(String name, String password){
        if (name.equals("Anonymous")){
            currentUser = new User();
            return true;
        }
        loadUsers();
        for (User user : users){
            if(user.getName().equals(name))
                return  false;
        }
        User userToAdd = new User(name,password);
        users.add(userToAdd);
        currentUser = userToAdd;
        saveUsers();
        return true;
    }


    /**
     * This method allow to login if the username and password are correctly written
     * @param name The name of the user to add
     * @param password tThe password of the user to add
     * @return true if the login succeeded, false if the username or the password are wrong.
     */
    public boolean login(String name, String password) {
        loadUsers();
        for (User user : users){
            if (user.getName().equals(name)){
                if (user.getPassword().equals(password)) {
                    currentUser = user;
                    return true;
                }
                else
                    break;
            }
        }
        return false;
    }

    /**
     * @param level The level from which we want the best users
     * @return An ArrayList that contains the 10 best users
     */
    public ArrayList<User> getBestUsers(GameLevel level) {
        loadUsers();
        for (User user : users) {
            user.setScore(level.loadUserScore(user));
        }
        Comparator<User> comparator = Comparator.comparing(User::getScore).reversed();
        users.sort(comparator);
        if (users.size() <= 10)
            return new ArrayList<>(users);
        else
            return (ArrayList<User>) users.subList(0,10);
    }


    /**
     * Save all users in a file
     */
    public void saveUsers(){
        try {
            File file = new File("users.save");
            if(!file.exists()) {
                file.createNewFile();
            }
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(users);
            out.flush();
            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Load all users fom a file
     */
    private void loadUsers(){
        if (users != null)
            return;
        try {
            File file = new File("users.save");
            if(!file.exists()) {
                users = new ArrayList<>();
            }
            else {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                users = (ArrayList<User>) in.readObject();
                in.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public User getCurrentUser(){
        return currentUser;
    }
}
