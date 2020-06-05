package be.ac.umons.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.Serializable;

public class User implements Serializable{
    private String name;
    private String password;
    private Integer score;
    private String pathToAvatarImage = "";
    private transient FileHandle userDirectory;
    private boolean anonymous = false;
    private static final long serialVersionUID = 4313688029221864711L;

    /**
     * The default constructor is used to create an anonymous user
     */
    public User() {
        this.name = "Anonymous";
        anonymous = true;
    }

    /**
     *
     * @param name The name of this user
     * @param password The password of this user
     */
    public User(String name, String password)  {
        this.name = name;
        setPassword(password);
        userDirectory = Gdx.files.local("usersSave" + File.separator + name);
        if (!userDirectory.exists()){
            userDirectory.mkdirs();
        }

    }


    public String getName() {
        return name;
    }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getScore() {
		return score;
	}

    public void setScore(Integer score) {
        this.score = score;
    }
    

    public String getPathToAvatarImage() {
        return pathToAvatarImage;
    }


    public void setPathToAvatarImage(String pathToAvatarImage) {
        this.pathToAvatarImage = pathToAvatarImage;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

}
