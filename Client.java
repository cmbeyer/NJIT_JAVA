import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

@SuppressWarnings("deprecation")
class Client {
    private SocketUtil SU;
    private JFrame frame;
    private String CURRENT_USER="";
    private JTextField password, firstname, lastname, DOB, homephone, cellphone, email;
    private JLabel userTest;
    private JComboBox<String> username, admin;
    private boolean admincheck=false;
    private Socket ServerSocket;

    public static void main(String[] arg) {
        new Client();
    }

    private Client() {
        makescreen();

        connect();
    }

    private void pass() {
        if (frame==null)
            return;

        JPanel signin = new JPanel();
        frame.setContentPane(signin);
        BorderLayout borderLayout = new BorderLayout();
        signin.setLayout(borderLayout);

        //making input panel
        JPanel InputPanel = new JPanel();

        InputPanel.add(new JLabel("Username:"));
        JTextField username = new JTextField(20);
        username.requestFocusInWindow();
        InputPanel.add(username);

        InputPanel.add(new JLabel("Password:"));
        JPasswordField password = new JPasswordField(20);
        InputPanel.add(password);

        signin.add(InputPanel, BorderLayout.CENTER);


        //making submit button
        JButton submit = new JButton("Login");
        submit.addActionListener(e -> {
            if (!username.getText().isEmpty() && !password.getText().isEmpty()) 
            {
               System.out.println(username.getText() + "," + password.getText());
               SU.write("LOGIN`"+username.getText() + "," + password.getText());
               
               		switch (SU.read()){
                        case "-1":
                            CURRENT_USER = "";
                            JOptionPane.showMessageDialog(null,"Couldnt Log In. PLease Try again!", "Unable to login", JOptionPane.WARNING_MESSAGE);
                            pass();
                            break;
                            
                        case "1":
                            CURRENT_USER = username.getText();
                            JOptionPane.showMessageDialog(null,"Welcome to the Membership Portal!", "Signed In", JOptionPane.INFORMATION_MESSAGE);
                            mainscreen(false);
                            admincheck=false;
                            break;
                            
                        case "2":
                            CURRENT_USER = username.getText();
                            JOptionPane.showMessageDialog(null,"Successful Login to Admin User!", "Logged In", JOptionPane.INFORMATION_MESSAGE);
                            admincheck=true;
                            makeadmin();
                            break;
                        default:
                            JOptionPane.showMessageDialog(null,
                                    "Error From The Server", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
            }else {
                JOptionPane.showMessageDialog(null,
                        "username or password is empty, try again", "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        });
        signin.add(submit, BorderLayout.SOUTH);

        frame.setContentPane(signin);
        frame.pack();
    }

    private void mainscreen(boolean EDIT) {
        if (frame==null)//checks to make sure the frame is there
            return;

        //making and loading in the login panel
        JPanel JP = new JPanel();
        frame.setContentPane(JP);
        BorderLayout borderLayout = new BorderLayout();
        JP.setLayout(borderLayout);

        if (admincheck) {
            //top search field
            JPanel InputPanel = new JPanel();
            GridLayout gridLayout = new GridLayout(1,3);
            InputPanel.setLayout(gridLayout);
            InputPanel.setBorder(BorderFactory.createTitledBorder("Look Up Users"));

            InputPanel.add(new JLabel("Enter Username:"));
            username = getUsers();
            //username.setSelectedItem(String.valueOf(CURRENT_USER));
            username.addActionListener(e -> setUser(String.valueOf(username.getSelectedItem()), EDIT));
            InputPanel.add(username);

            JP.add(InputPanel, BorderLayout.NORTH);
        }


        //center account info
        JPanel memberstuff = new JPanel();
        GridLayout gridLayout1 = new GridLayout(7,1);
        memberstuff.setLayout(gridLayout1);
        memberstuff.setBorder(BorderFactory.createTitledBorder("Selected user info"));

        JPanel userFor = new JPanel();
        userTest = new JLabel("Showing info for \""+(username!= null && !String.valueOf(username.getSelectedItem()).equals(CURRENT_USER)? String.valueOf(username.getSelectedItem()): CURRENT_USER)+"\"");
        userFor.add(userTest);
        userFor.setBorder(BorderFactory.createBevelBorder(4));
        memberstuff.add(userFor);

        JPanel infoin = new JPanel();
        if (admincheck) {
            infoin.add(new JLabel("Is Admin:"));
            admin = new JComboBox<>();
            admin.addItem("Y");
            admin.addItem("N");
            infoin.add(admin);
            memberstuff.add(infoin);
        }

        infoin = new JPanel();
        infoin.add(new JLabel("Password:"));
        password = new JTextField(25);
        infoin.add(password);
        memberstuff.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("First Name:"));
        firstname = new JTextField(25);
        infoin.add(firstname);
        memberstuff.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Last Name:"));
        lastname = new JTextField(25);
        infoin.add(lastname);
        memberstuff.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("DOB:"));
        DOB = new JTextField(10);
        DOB.setToolTipText("yyyy-mm-dd");
        infoin.add(DOB);
        memberstuff.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Home Phone:"));
        homephone = new JTextField(10);
        //noinspection SpellCheckingInspection
        homephone.setToolTipText("123-456-7890");
        infoin.add(homephone);
        memberstuff.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Cell Phone:"));
        cellphone = new JTextField(10);
        cellphone.setToolTipText("123-456-7890");
        infoin.add(cellphone);
        memberstuff.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Email:"));
        email = new JTextField(40);
        infoin.add(email);
        memberstuff.add(infoin);

        if (!EDIT) {
            password.setEnabled(false);
            firstname.setEditable(false);
            lastname.setEditable(false);
            DOB.setEditable(false);
            homephone.setEditable(false);
            cellphone.setEditable(false);
            email.setEditable(false);
        }

        JP.add(memberstuff);
        
        JPanel logoutP = new JPanel();
        

        if (EDIT){
            JButton back = new JButton("Back");
            back.addActionListener(e -> makeadmin());
            logoutP.add(back);
        }

        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> pass());
        logoutP.add(logout);
        
        if (EDIT) {
            JButton submit = new JButton("Submit");
            submit.addActionListener(e -> editUser(
                    String.valueOf(username.getSelectedItem()),
                    password.getText(),
                    String.valueOf(admin.getSelectedItem()),
                    firstname.getText(),
                    lastname.getText(),
                    DOB.getText(),
                    homephone.getText(),
                    cellphone.getText(),
                    email.getText(),
                    EDIT));
            logoutP.add(submit);
        }
        
        JP.add(logoutP, BorderLayout.SOUTH);

        frame.setContentPane(JP);
        frame.pack();

        if (!EDIT)//sets the current user info for normal users
            setUser(CURRENT_USER, EDIT);
    }

    private void setUser(String user, boolean EDIT) {
        if (user.equals("")){
            JOptionPane.showMessageDialog(null,"The username has been has not been entered. Please try again", "Edit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SU.write("GET_USER`"+user);

        //tests to see if the user was deleted
        String answer = SU.read();
        if (answer.equals("-1"))
            JOptionPane.showMessageDialog(null,"The user ("+user+") can not be edited.", "Editing User", JOptionPane.ERROR_MESSAGE);

        System.out.println(answer);
        String[] reply = answer.split(",");
        try {
            password.setText(reply[0]);
            firstname.setText(reply[1]);
            lastname.setText(reply[2]);
            DOB.setText(reply[3]);
            homephone.setText(reply[4]);
            cellphone.setText(reply[5]);
            email.setText(reply[6]);
            admin.setSelectedItem(reply[7]);
        } catch (Exception e) {}

        userTest.setText("Showing info for \""+(username!=null && !String.valueOf(username.getSelectedItem()).equals(CURRENT_USER)? String.valueOf(username.getSelectedItem()): CURRENT_USER)+"\"");
    }

    private void editUser(String user, String password, String adminText, String firstNameText, String lastNameText, String dobText, String home_phoneText, String cell_phoneText, String emailText, boolean EDIT) {
        if (user.equals("")){
            JOptionPane.showMessageDialog(null,"The username has been has not been entered. Please try again", "Edit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String a =
                    "EDIT_USER`UPDATE cmb45.USER SET " +
                            (!password.equals(" ")? ("password='"+password+"', "): "") +
                            (!firstNameText.equals(" ")? ("firstname='"+firstNameText+"', "): "") +
                            (!lastNameText.equals(" ")? ("lastname='"+lastNameText+"', "): "") +
                            (!dobText.equals(" ")? ("DOB='"+dobText+"', "): "") +
                            (!home_phoneText.equals(" ")? ("homephone='"+home_phoneText+"', "): "") +
                            (!cell_phoneText.equals(" ")? ("cellphone='"+cell_phoneText+"', "): "") +
                            (!emailText.equals(" ")? ("email='"+emailText+"', "): "") +
                            (!adminText.equals(" ")? ("admin='"+adminText+"', "): "");
            SU.write( a.substring(0,a.length()-2)+" WHERE username='"+user+"';");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"The user ("+user+") has NOT been edited. There was an error because of some of the input. Please try again", "Edited", JOptionPane.ERROR_MESSAGE);
        }

        //tests to see if the user was deleted
        if (SU.read().equals("1"))
            JOptionPane.showMessageDialog(null,"The user ("+user+") has been edited.", "Edited", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(null,"The user ("+user+") has NOT been edited.", "Edited", JOptionPane.ERROR_MESSAGE);

        //updating GUI
        mainscreen(EDIT);
    }

    private JComboBox<String> getUsers() {
        JComboBox<String> box = new JComboBox<>();

        //getting users
        SU.write("GET_USERS`0");

        String[] users = SU.read().split(",");

        for (String user:users){
            box.addItem(user);
        }

        return box;
    }

    private void makeadmin() {
        if (frame==null)//checks to make sure the frame is there
            return;


        JPanel Panel = new JPanel();
        frame.setContentPane(Panel);
        BorderLayout borderLayout = new BorderLayout();
        Panel.setLayout(borderLayout);

        JPanel contents = new JPanel();
        BoxLayout boxLayout = new BoxLayout(contents, BoxLayout.Y_AXIS);
        contents.setLayout(boxLayout);

        JButton edit = new JButton("Edit Users");
        edit.addActionListener(e -> mainscreen(true));
        contents.add(edit);

        JPanel inner = new JPanel();
        inner.setBorder(BorderFactory.createTitledBorder("Add a member to the site"));
        inner.add(new JLabel("Enter a username for the Member"));
        JTextField newUser = new JTextField(30);
        inner.add(newUser);
        inner.add(new JLabel("Enter new user's password:"));
        JPasswordField newPassword = new JPasswordField(30);
        inner.add(newPassword);
        JButton add = new JButton("Add User");
        add.addActionListener(e -> add(newUser.getText(), newPassword.getText()));
        inner.add(add);
        contents.add(inner);


        inner = new JPanel();
        inner.setBorder(BorderFactory.createTitledBorder("Select user to delete"));
        inner.add(new JLabel("Select Username:"));
        JComboBox<String> username = getUsers();
        username.addActionListener(e -> delete(String.valueOf(username.getSelectedItem())));
        inner.add(username);
        contents.add(inner);

        Panel.add(contents, BorderLayout.CENTER);

        JPanel logoutP = new JPanel();
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> pass());
        logoutP.add(logout);

        Panel.add(logoutP, BorderLayout.SOUTH);

        frame.setContentPane(Panel);
        frame.pack();
    }

    private void add(String user, String password) {
        if (user.equals("")){
            JOptionPane.showMessageDialog(null,"The username has been has not been entered. Please try again", "Added", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SU.write("ADD_USERS`'"+user+"','"+password+"'");

        if (SU.read().equals("1"))
            JOptionPane.showMessageDialog(null,"The user ("+user+") has been added.", "ADD", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(null,"The user ("+user+") has NOT been added.", "ADD", JOptionPane.ERROR_MESSAGE);

        makeadmin();
    }

    private void delete(String member) {
        if (member.equals(CURRENT_USER)){
            JOptionPane.showMessageDialog(null,"You can't delete yourself", "Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SU.write("REMOVE_USERS`"+member);

        //tests to see if the user was deleted
        if (SU.read().equals("1"))
            JOptionPane.showMessageDialog(null,"The user ("+member+") has been deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(null,"The user ("+member+") has NOT been deleted.", "Deleted", JOptionPane.ERROR_MESSAGE);

        //updating GUI
        makeadmin();
    }

    private void makescreen() {
     
        frame = new JFrame("Accounts");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000, 400));
        frame.setResizable(false);

        frame.pack();
        frame.setVisible(true);
    }

    private void connect(){
        
        try{
            @SuppressWarnings("unused") DataObject myObject = new DataObject();
            ServerSocket = new Socket("afsaccess1.njit.edu", 4010);
            
            SU = new SocketUtil(ServerSocket);
            String host="UNKNOWN";
            try {
                host =InetAddress.getByName("www.example.com").getHostAddress();
            } catch (UnknownHostException ignored) {}
            SU.write("Connecting to Server from: "+host);

            String INPUT=SU.read();
                System.out.println(INPUT);
                if (INPUT.equals("USERNAME AND PASSWORD?"))
                    pass();
                else{
                    JOptionPane.showMessageDialog(null,
                            "ERROR WHEN LOGGING IN","ERROR",JOptionPane.ERROR_MESSAGE);
                }
            } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (ConnectException e){
            JOptionPane.showMessageDialog(null,
                    "Error, Server is Not Running on Specified Socket.","ERROR",JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        catch (IOException e1) {
            JOptionPane.showMessageDialog(null,
                    e1.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }
    }

    public void finalize() {
        try {
            try {
                ServerSocket.close();
                SU.close();
                frame.setVisible(false);
                frame.dispose();
            } finally {
                super.finalize();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
