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

    private Socket socketToServer;
    private SocketUtil IO;
    private JFrame frame;
    private String CURRENT_USER="";
    private JTextField password, firstname, lastname, DOB, homephone, cellphone, email;
    private JLabel userTest;
    private JComboBox<String> username, admin;
    private boolean admincheck=false;

    public static void main(String[] arg) {
        new Client();
    }

    private Client() {
        buildGUI();//builds the frame

        makeConnection();//starts the connection
    }

    private void loadGUI_Password() {
        if (frame==null)//checks to make sure the frame is there
            return;

        //making and loading in the login panel
        JPanel LoginPanel = new JPanel();
        frame.setContentPane(LoginPanel);
        BorderLayout borderLayout = new BorderLayout();
        LoginPanel.setLayout(borderLayout);

        //making input panel
        JPanel InputPanel = new JPanel();

        InputPanel.add(new JLabel("Username:"));
        JTextField username = new JTextField(20);
        username.requestFocusInWindow();
        InputPanel.add(username);

        InputPanel.add(new JLabel("Password:"));
        JTextField password = new JTextField(20);
        InputPanel.add(password);

        LoginPanel.add(InputPanel, BorderLayout.NORTH);


        //making submit button
        JButton submit = new JButton("Login");
        submit.addActionListener(e -> {
            if (!username.getText().isEmpty() && !password.getText().isEmpty()) 
            {
               System.out.println(username.getText() + "," + password.getText());
               IO.write("LOGIN`"+username.getText() + "," + password.getText());
               
               		switch (IO.read()){
                        case "-1":
                            CURRENT_USER = "";
                            JOptionPane.showMessageDialog(null,"Couldnt Log In. PLease Try again!", "Unable to login", JOptionPane.WARNING_MESSAGE);
                            loadGUI_Password();
                            break;
                            
                        case "1":
                            CURRENT_USER = username.getText();
                            JOptionPane.showMessageDialog(null,"Welcome to the Membership Portal", "Signed In", JOptionPane.INFORMATION_MESSAGE);
                            loadGUI_DashBoard(false);
                            admincheck=false;
                            break;
                            
                        case "2":
                            CURRENT_USER = username.getText();
                            JOptionPane.showMessageDialog(null,"Welcome Admin", "Logged In", JOptionPane.INFORMATION_MESSAGE);
                            admincheck=true;
                            loadGUI_AdminDashboard();
                            break;
                        default:
                            JOptionPane.showMessageDialog(null,
                                    "Unknown reply from server", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
            }else {
                JOptionPane.showMessageDialog(null,
                        "username or password is empty, try again", "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        });
        LoginPanel.add(submit, BorderLayout.SOUTH);

        frame.setContentPane(LoginPanel);
        frame.pack();
    }

    private void loadGUI_DashBoard(boolean EDIT) {
        if (frame==null)//checks to make sure the frame is there
            return;

        //making and loading in the login panel
        JPanel Panel = new JPanel();
        frame.setContentPane(Panel);
        BorderLayout borderLayout = new BorderLayout();
        Panel.setLayout(borderLayout);

        if (admincheck) {
            //top search field
            JPanel InputPanel = new JPanel();
            GridLayout gridLayout = new GridLayout(1,3);
            InputPanel.setLayout(gridLayout);
            InputPanel.setBorder(BorderFactory.createTitledBorder("Search for users to view"));

            InputPanel.add(new JLabel("Enter Username:"));
            username = getUsers();
            //username.setSelectedItem(String.valueOf(CURRENT_USER));
            username.addActionListener(e -> setUser(String.valueOf(username.getSelectedItem()), EDIT));
            InputPanel.add(username);

            Panel.add(InputPanel, BorderLayout.NORTH);
        }


        //center account info
        JPanel info = new JPanel();
        GridLayout gridLayout1 = new GridLayout(7,1);
        info.setLayout(gridLayout1);
        info.setBorder(BorderFactory.createTitledBorder("Selected user info"));

        JPanel userFor = new JPanel();
        userTest = new JLabel("Showing info for \""+(username!= null && !String.valueOf(username.getSelectedItem()).equals(CURRENT_USER)? String.valueOf(username.getSelectedItem()): CURRENT_USER)+"\"");
        userFor.add(userTest);
        userFor.setBorder(BorderFactory.createBevelBorder(4));
        info.add(userFor);

        JPanel infoin = new JPanel();
        if (admincheck) {
            infoin.add(new JLabel("Is Admin:"));
            admin = new JComboBox<>();
            admin.addItem("yes");
            admin.addItem("no");
            infoin.add(admin);
            info.add(infoin);
        }

        infoin = new JPanel();
        infoin.add(new JLabel("Password:"));
        password = new JTextField(30);
        infoin.add(password);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("First Name:"));
        firstname = new JTextField(30);
        infoin.add(firstname);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Last Name:"));
        lastname = new JTextField(30);
        infoin.add(lastname);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("DOB:"));
        DOB = new JTextField(10);//TODO make a j date picker
        DOB.setToolTipText("yyyy-mm-dd");
        infoin.add(DOB);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Home Phone:"));
        homephone = new JTextField(12);
        //noinspection SpellCheckingInspection
        homephone.setToolTipText("123-456-7890");
        infoin.add(homephone);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Cell Phone:"));
        cellphone = new JTextField(12);
        //noinspection SpellCheckingInspection
        cellphone.setToolTipText("123-456-7890");
        infoin.add(cellphone);
        info.add(infoin);

        infoin = new JPanel();
        infoin.add(new JLabel("Email:"));
        email = new JTextField(30);
        infoin.add(email);
        info.add(infoin);

        if (!EDIT) {
            password.setEnabled(false);
            firstname.setEditable(false);
            lastname.setEditable(false);
            DOB.setEditable(false);
            homephone.setEditable(false);
            cellphone.setEditable(false);
            email.setEditable(false);
        }

        Panel.add(info);


        //Submit, Log out, and back
        JPanel logoutP = new JPanel();
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

        if (EDIT){//back for admin
            JButton back = new JButton("Back");
            back.addActionListener(e -> loadGUI_AdminDashboard());
            logoutP.add(back);
        }

        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> loadGUI_Password());
        logoutP.add(logout);

        Panel.add(logoutP, BorderLayout.SOUTH);

        frame.setContentPane(Panel);
        frame.pack();

        if (!EDIT)//sets the current user info for normal users
            setUser(CURRENT_USER, EDIT);
    }

    private void setUser(String user, boolean EDIT) {
        if (user.equals("")){
            JOptionPane.showMessageDialog(null,"The username has been has not been entered. Please try again", "Edit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        IO.write("GET_USER`"+user);

        //tests to see if the user was deleted
        String answer = IO.read();
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
                            (!firstNameText.equals(" ")? ("firstName='"+firstNameText+"', "): "") +
                            (!lastNameText.equals(" ")? ("lastName='"+lastNameText+"', "): "") +
                            (!dobText.equals(" ")? ("DOB='"+dobText+"', "): "") +
                            (!home_phoneText.equals(" ")? ("home_phone='"+home_phoneText+"', "): "") +
                            (!cell_phoneText.equals(" ")? ("cell_phone='"+cell_phoneText+"', "): "") +
                            (!emailText.equals(" ")? ("email='"+emailText+"', "): "") +
                            (!adminText.equals(" ")? ("isAdmin='"+adminText+"', "): "");
            IO.write( a.substring(0,a.length()-2)+" WHERE userName='"+user+"';");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"The user ("+user+") has NOT been edited. There was an error because of some of the input. Please try again", "Edited", JOptionPane.ERROR_MESSAGE);
        }

        //tests to see if the user was deleted
        if (IO.read().equals("1"))
            JOptionPane.showMessageDialog(null,"The user ("+user+") has been edited.", "Edited", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(null,"The user ("+user+") has NOT been edited.", "Edited", JOptionPane.ERROR_MESSAGE);

        //updating GUI
        loadGUI_DashBoard(EDIT);
    }

    @SuppressWarnings("EmptyMethod")
    private JComboBox<String> getUsers() {
        JComboBox<String> box = new JComboBox<>();

        //getting users
        IO.write("GET_USERS`0");

        String[] users = IO.read().split(",");

        for (String user:users){
            box.addItem(user);
        }

        return box;
    }

    private void loadGUI_AdminDashboard() {
        if (frame==null)//checks to make sure the frame is there
            return;

        //making and loading in the login panel
        JPanel Panel = new JPanel();
        frame.setContentPane(Panel);
        BorderLayout borderLayout = new BorderLayout();
        Panel.setLayout(borderLayout);

        JPanel contents = new JPanel();
        BoxLayout boxLayout = new BoxLayout(contents, BoxLayout.Y_AXIS);
        contents.setLayout(boxLayout);

        //editing info for the select user button
        JButton edit = new JButton("Edit Users");
        edit.addActionListener(e -> loadGUI_DashBoard(true));
        contents.add(edit);


        //add user button
        JPanel inner = new JPanel();
        inner.setBorder(BorderFactory.createTitledBorder("Add new user"));
        inner.add(new JLabel("Enter new user's username:"));
        JTextField newUser = new JTextField(30);
        inner.add(newUser);
        JButton add = new JButton("Add User");
        add.addActionListener(e -> addUser(newUser.getText()));
        inner.add(add);
        contents.add(inner);


        //delete user combo box
        inner = new JPanel();
        inner.setBorder(BorderFactory.createTitledBorder("Select user to delete"));
        inner.add(new JLabel("Select Username:"));
        JComboBox<String> username = getUsers();
        username.addActionListener(e -> deleteUser(String.valueOf(username.getSelectedItem())));
        inner.add(username);
        contents.add(inner);

        Panel.add(contents, BorderLayout.CENTER);

        //Log out
        JPanel logoutP = new JPanel();
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> loadGUI_Password());
        logoutP.add(logout);

        Panel.add(logoutP, BorderLayout.SOUTH);

        frame.setContentPane(Panel);
        frame.pack();
    }

    private void addUser(String user) {
        if (user.equals("")){
            JOptionPane.showMessageDialog(null,"The username has been has not been entered. Please try again", "Added", JOptionPane.WARNING_MESSAGE);
            return;
        }

        IO.write("ADD_USERS`"+user);

        //tests to see if the user was deleted
        if (IO.read().equals("1"))
            JOptionPane.showMessageDialog(null,"The user ("+user+") has been added. default password is 'password123'.", "Added", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(null,"The user ("+user+") has NOT been added.", "Added", JOptionPane.ERROR_MESSAGE);

        //updating GUI
        loadGUI_AdminDashboard();
    }

    private void deleteUser(String user) {
        if (user.equals(CURRENT_USER)){
            JOptionPane.showMessageDialog(null,"You can't delete yourself", "Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }

        IO.write("REMOVE_USERS`"+user);

        //tests to see if the user was deleted
        if (IO.read().equals("1"))
            JOptionPane.showMessageDialog(null,"The user ("+user+") has been deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(null,"The user ("+user+") has NOT been deleted.", "Deleted", JOptionPane.ERROR_MESSAGE);

        //updating GUI
        loadGUI_AdminDashboard();
    }

    private void buildGUI() {
        //making the frame
        frame = new JFrame("Accounts");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000, 400));
        frame.setResizable(false);

        //building and showing the frame
        frame.pack();
        frame.setVisible(true);
    }

    private void makeConnection(){
        //noinspection SpellCheckingInspection
        try{
            @SuppressWarnings("unused") DataObject myObject = new DataObject();//new sending object
            socketToServer = new Socket("afsaccess1.njit.edu", 4009);//making the connection
            //afsaccess2.njit.edu

            IO = new SocketUtil(socketToServer);//making the input and output

            //sending the connection header to the server
            String host="UNKNOWN";
            try {
                host =InetAddress.getByName("www.example.com").getHostAddress();
            } catch (UnknownHostException ignored) {}
            IO.write("Connecting to Server from: "+host);

            //reading the password request
            String INPUT=IO.read();
                System.out.println(INPUT);
                if (INPUT.equals("USERNAME AND PASSWORD?"))
                    loadGUI_Password();//loads the password content
                else{
                    JOptionPane.showMessageDialog(null,
                            "Logging request expected but not received","ERROR",JOptionPane.ERROR_MESSAGE);
                }
            } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (ConnectException e){
            JOptionPane.showMessageDialog(null,
                    "Can't connect to the Server right now so im closing","ERROR",JOptionPane.ERROR_MESSAGE);
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
                socketToServer.close();
                IO.close();
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
