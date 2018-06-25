import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.io.IOException;
import java.net.ConnectException;

class ThreadedDataObjectServer {
    public static void main(String[] args ) {

        try {
            ServerSocket s = new ServerSocket(4009);//listening

            //noinspection InfiniteLoopStatement
            while (true) {
                Socket incoming = s.accept( );//getting a connection
                new ThreadedDataObjectHandler(incoming).start();//starting a new thread
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

    class ThreadedDataObjectHandler extends Thread {
        @SuppressWarnings("CanBeFinal")
        private Socket incoming;
        @SuppressWarnings("FieldCanBeLocal")
        private SocketUtil IO;
        private Connection conn;
        private String USER="";
        private boolean IS_ADMIN=false;


        ThreadedDataObjectHandler(Socket i){
            incoming = i;
        }

        public void run() {
            IO = new SocketUtil(incoming);

            //connecting to the DB
            connectToDB();

            //read in header info form client
            System.out.println("New Connection from: " + IO.read());

            //asking the user to log in
            IO.write("USERNAME AND PASSWORD?");

            while(true) {
            	
                String[] INPUT = IO.read().split("`");

                System.out.println("CMD input: "+INPUT[0]+" parms: "+INPUT[1]==null? "": INPUT[1]);

                switch (INPUT[0]){//CMD switch
                    case "LOGIN":
                        System.out.println("CMD: login");
                        CMD_login(INPUT[1]);
                        break;
                    case "EDIT_USER":
                        System.out.println("CMD: edit users");
                        CMD_EditUser(INPUT[1]);
                        break;
                    case "GET_USERS":
                        System.out.println("CMD: get users");
                        CMD_GetUsers();
                        break;
                    case "GET_USER":
                        System.out.println("CMD: get user");
                        CMD_GetUser(INPUT[1]);
                        break;
                    case "ADD_USERS":
                        System.out.println("CMD: add users");
                        CMD_AddUser(INPUT[1]);
                        break;
                    case "REMOVE_USERS":
                        System.out.println("CMD: delete users");
                        CMD_DeleteUser(INPUT[1]);
                        break;
                    default:
                        IO.write("UNKNOWN CMD");
                }
            }
        }

        private void CMD_GetUser(String input) {
            String users="";

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt=conn.createStatement();
                rs=stmt.executeQuery("SELECT * FROM cmb45.USER WHERE username='"+input+"';");

                rs.next();

                users+=(rs.getString("password").equals("null")? " ":rs.getString("password"))+",";
                users+=(rs.getString("firstname")==null? " ":rs.getString("firstname"))+",";
                users+=(rs.getString("lastname")==null? " ":rs.getString("lastname"))+",";
                users+=(rs.getString("DOB")==null? " ":rs.getString("DOB"))+",";
                users+=(rs.getString("homephone")==null? " ":rs.getString("homephone"))+",";
                users+=(rs.getString("cellphone")==null? " ":rs.getString("cellphone"))+",";
                users+=(rs.getString("email")==null? " ":rs.getString("email"))+",";
                users+=(rs.getString("admin")==null? " ":rs.getString("admin"))+",";


            } catch (SQLException e) {
                IO.write("NO USERS,");
            } finally {
                try {
                    stmt.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            IO.write(users);
        }

        private void CMD_GetUsers() {
            String users=",";

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt=conn.createStatement();
                rs=stmt.executeQuery("SELECT username FROM cmb45.USER;");

                while(rs.next()){
                    users+=rs.getString("username")+",";
                }
            } catch (SQLException e) {
                IO.write("NO USERS,");
            } finally {
                try {
                    stmt.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            IO.write(users);
        }

        private void CMD_DeleteUser(String input) {
            Statement stmt = null;
            try {
                stmt=conn.createStatement();
                int answer=stmt.executeUpdate("DELETE from cmb45.USER WHERE username='"+input+"'");
                IO.write(answer+"");
            } catch (SQLException e) {
                IO.write("-1");
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void CMD_AddUser(String input) {
            Statement stmt = null;
            try {
                stmt=conn.createStatement();
                int answer=stmt.executeUpdate("INSERT INTO cmb45.USER (username, password, admin) VALUES ('"+input+"', 'password123', 'N');");
                IO.write(answer+"");
            } catch (SQLException e) {
                IO.write("-1");
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void CMD_EditUser(String input) {
            Statement stmt = null;
            try {
                stmt=conn.createStatement();
                System.out.println(input);
                int answer = stmt.executeUpdate(input);
                IO.write(answer+"");

            } catch (SQLException e) {
                IO.write("-1");
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void connectToDB() {
        	String url = "sql2.njit.edu";
    		String ucid = "cmb45";	//your ucid
    		String dbpassword = "XJJiDhyL";	//your MySQL password


    		System.out.println("This example program will create a table in MySQL and "+
    			"populate that table with three rows of sample data. The program " +
    			"will then query the database for the contents of the table and " +
    			"display the result.");

    		System.out.println("Starting test . . .");


    		System.out.println("Loading driver . . .");
    		try {
    			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
    		}
    		catch (Exception e) {
    			System.err.println("Unable to load driver.");
    			e.printStackTrace();
    		}
    		System.out.println("Driver loaded.");
    		System.out.println("Establishing connection . . . ");
    		try {
    			Connection conn;

    			conn = DriverManager.getConnection("jdbc:mysql://"+url+"/"+ucid+"?user="+ucid+"&password="+dbpassword);

    			System.out.println("Connection established.");
    			System.out.println("Creating a Statement object . . . ");
    		}
    		catch (SQLException E) {
    			System.out.println("SQLException: " + E.getMessage());
    			System.out.println("SQLState:     " + E.getSQLState());
    			System.out.println("VendorError:  " + E.getErrorCode());
    		}
    	}

        private void CMD_login(String input) {
            //check the DB to see if the user is allowed in and what there role is: -1: no logged in, 1 user, 2 admin

            String[] INPUT=input.split(",");

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt=conn.createStatement();
                rs=stmt.executeQuery("SELECT admin FROM cmb45.USER where " +
                        "username='"+INPUT[0]+"' and " +
                        "password='"+INPUT[1]+"';");

                rs.next();
                String answer=rs.getString("admin");
                System.out.println("answer:"+answer);
                if(!answer.equals(null)) {//sets the current user
                    USER = INPUT[0];

                    if(answer.equals("Y")) {
                        IS_ADMIN=true;
                        IO.write("2");
                    } else {
                        IS_ADMIN=false;
                        IO.write("1");
                    }
                } else
                    IO.write("-1");
            } catch (SQLException e) {
                IO.write("-1");
            } finally {
                try {
                    stmt.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
