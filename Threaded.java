import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.io.IOException;
import java.net.ConnectException;

class ThreadedDataObjectServer {
    public static void main(String[] args ) {

        try {
            ServerSocket s = new ServerSocket(4010);//listening

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
        
        private Socket infromcmd;
        private String member="";
        private SocketUtil SU;
        private Connection conn;
       
        private boolean admincheck=false;


        ThreadedDataObjectHandler(Socket i){
            infromcmd = i;
        }

        public void run() {
            SU = new SocketUtil(infromcmd);

            //connecting to the DB
            DBconnect();

            //read in header info form client
            System.out.println("New Connection from: " + SU.read());

            //asking the user to log in
            SU.write("USERNAME AND PASSWORD?");

            while(true) {
            	
                String[] inarray = SU.read().split("`");

                System.out.println("CMD input: "+inarray[0]+" parms: "+inarray[1]==null? "": inarray[1]);
                
                switch (inarray[0]){//CMD switch
                
                    case "LOGIN":
                        System.out.println("CMD: login  "+inarray[0]+" parms: "+inarray[1]);
                        signin(inarray[1]);
                        break;
                        
                    case "EDIT_USER":
                        System.out.println("CMD: edit users");
                        edit(inarray[1]);
                        break;
                    case "GET_USERS":
                        System.out.println("CMD: get users");
                        getlist();
                        break;
                    case "GET_USER":
                        System.out.println("CMD: get user");
                        getmember(inarray[1]);
                        break;
                    case "ADD_USERS":
                        System.out.println("CMD: add users");
                        System.out.println("CMD input: "+inarray[0]+" parms: "+ inarray[1]);
                        add(inarray[1]);
                        break;
                        
                    case "REMOVE_USERS":
                        System.out.println("CMD: delete users");
                        delete(inarray[1]);
                        break;
                    default:
                        SU.write("UNKNOWN CMD");
                }
            }
        }

        private void getmember(String member) {
            String users="";

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt=conn.createStatement();
                rs=stmt.executeQuery("SELECT * FROM cmb45.USER WHERE username='"+member+"';");

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
                SU.write("NO USERS,");
            } finally {
                try {
                    stmt.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            SU.write(users);
        }

        private void getlist() {
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
                SU.write("NO USERS,");
            } finally {
                try {
                    stmt.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            SU.write(users);
        }

        private void delete(String member) {
            Statement stmt = null;
            int result;
            try {
                stmt=conn.createStatement();
                result=stmt.executeUpdate("DELETE from cmb45.USER WHERE username='"+member+"'");
                SU.write(result+"");
            } catch (SQLException e) {
                SU.write("-1");
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void add(String input) {
            Statement stmt = null;
            
            try {
            	System.out.println("INSERT INTO cmb45.USER (username, password, admin) VALUES ("+input+", 'N');");
                stmt=conn.createStatement();
                int result = stmt.executeUpdate("INSERT INTO cmb45.USER (username, password, admin) VALUES ("+input+", 'N');");
                System.out.println("Result:  "+result);
                SU.write(result+"");
            } catch (SQLException e) {
                SU.write("-1");
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void edit(String member) {
            Statement stmt = null;
            int result;
            try {
                stmt=conn.createStatement();
                System.out.println(member);
                result = stmt.executeUpdate(member);
                SU.write(result+"");

            } catch (SQLException e) {
                SU.write("-1");
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private void DBconnect() {
        	String url = "sql2.njit.edu";
    		String ucid = "cmb45";	//your ucid
    		String dbpassword = "XJJiDhyL";	//your MySQL password

    		try {
    			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
    		}
    		catch (Exception e) {
    			System.err.println("NO.");
    			e.printStackTrace();
    		}
    		
    		
    		try {
    			conn = DriverManager.getConnection("jdbc:mysql://"+url+"/"+ucid+"?user="+ucid+"&password="+dbpassword);
    			System.out.println("IN");
    		}
    		catch (SQLException E) {
    			
    		}
    	}

        private void signin(String member) {
            String[] inarray=member.split(",");

            System.out.println("SELECT admin FROM cmb45.USER where username='"+inarray[0]+"' and password='"+inarray[1]+"';");
        
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt=conn.createStatement();
               
                rs=stmt.executeQuery("SELECT admin FROM cmb45.USER where username='"+inarray[0]+"' and password='"+inarray[1]+"';");
                rs.next();
                                
                String admin=rs.getString("admin");
                System.out.println("answer:"+admin);
                if(!admin.equals(null)) {
                	member = inarray[0];
                	if(admin.equals("Y")) 
                    {
                        admincheck=true;
                        SU.write("2");
                    }
                    else 
                    {
                        admincheck=false;
                        SU.write("1");
                    }
                } 
                else
                {
                    SU.write("-1");
                }
            } 
            catch (SQLException e)
            {
                SU.write("-1");
            } 
            finally 
            {
                try 
                {
                    stmt.close();
                    rs.close();
                } 
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }
