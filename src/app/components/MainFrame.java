package app.components;


import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import javax.swing.JComboBox;
import java.awt.Button;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MainFrame extends JFrame {

	private static ESPFinalSellerService service;
	private JPanel contentPane;
	private JTextField txtFileName;
	private JFileChooser chooser;
	private List<Type> types;
	private JComboBox comboBox;
	private String filePath;
	private String reply;
	private JTextArea output;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		String baseUrl = "http://localhost:9999/";
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient client = new OkHttpClient.Builder()
				.addInterceptor(interceptor)
				.build();	
		Gson gson = new GsonBuilder().create();
		Retrofit retrofit = new Retrofit.Builder()
				.client(client)
				.baseUrl(baseUrl) 
				.addConverterFactory(GsonConverterFactory.create(gson))
				.build();
		service = retrofit.create(ESPFinalSellerService.class);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 557, 416);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		comboBox = new JComboBox();
		comboBox.setBounds(22, 57, 154, 31);
		comboBox.addItem("Users");
		comboBox.addItem("Types");
		comboBox.addItem("Parts");
		contentPane.add(comboBox);
		
		JButton btnPopulate = new JButton("Populate");
		btnPopulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(filePath != null){
					switch (comboBox.getSelectedItem()+"") {
					case "Users":
						populateUsers();
						break;
					case "Types":
						populateTypes();
						break;
					case "Parts":
						populateParts();
					default:
						break;
					}
				}else{output.setText("File not choosen.");}
			}
		});
		btnPopulate.setBounds(223, 115, 100, 43);
		contentPane.add(btnPopulate);
		
		txtFileName = new JTextField();
		txtFileName.setBounds(202, 57, 193, 31);
		contentPane.add(txtFileName);
		txtFileName.setColumns(10);
		
		JButton btnChoose = new JButton("Browse Files");
		btnChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
				    "CSV files", "csv");
				chooser.setFileFilter(filter);
				File currentDir = new File(System.getProperty("user.dir"));
				chooser.setCurrentDirectory(currentDir);
				try{
					int returnVal = chooser.showOpenDialog(getContentPane());
					filePath = chooser.getSelectedFile().getAbsolutePath();
					if(returnVal == JFileChooser.APPROVE_OPTION) {
					   txtFileName.setText(chooser.getSelectedFile().getName());
					}
				} catch (NullPointerException e){
					e.printStackTrace();
				}
			}
		});
		btnChoose.setBounds(405, 57, 108, 31);
		contentPane.add(btnChoose);
		
		JLabel lblEntity = new JLabel("Entity");
		lblEntity.setBounds(22, 32, 46, 14);
		contentPane.add(lblEntity);
		
		JLabel lblFilePath = new JLabel("CSV File");
		lblFilePath.setBounds(202, 32, 46, 14);
		contentPane.add(lblFilePath);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(21, 183, 492, 169);
		contentPane.add(scrollPane);
		
		output = new JTextArea();
		scrollPane.setViewportView(output);
	}
private interface ESPFinalSellerService {

		@FormUrlEncoded
		@POST("http://localhost:9999/findType")
		Call<Type> findType(@Field("name") String name);

		@FormUrlEncoded
		@POST("http://localhost:9999/addType")
		Call<ResponseBody> addType(@Field("name") String name);
		
		@FormUrlEncoded
		@POST("http://localhost:9999/sellers/{seller}/parts/new")
		Call<ResponseBody> addPart(@Path("seller") String sellerName, 
								@Field("name") String itemName,
								@Field("price") Double itemPrice,
								@Field("description") String itemDesc,
								@Field("img_path") String imgSrc,
								@Field("type_id") Long type);
		@FormUrlEncoded
		@POST("http://localhost:9999/register")
		Call<ResponseBody> register(@Field("username") String username, 
								@Field("email") String email,
								@Field("password") String password,
								@Field("confirm_password") String confPassword,
								@Field("phone") String phone,
								@Field("isSeller") boolean isSeller,
								@Field("is_admin") boolean isAdmin);
	
	}
	



public void populateUsers(){
	output.setText("Populating Users...\n");
    String line = "";
    String cvsSplitBy = ",";
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        while ((line = br.readLine()) != null) {
            String[] user = line.split(cvsSplitBy);
            try{
                Call<ResponseBody> call = service.register(user[0], user[2], user[1], user[1], "", true, user[3].equals("TRUE")? true : false);
				Response<ResponseBody> response;
				try {
					response = call.execute();	
					reply = response.body().string(); //taken from global variable
					output.append("Added user:"+ "\n" +"User="+user[0]+" Password="+user[1]+" Email="+user[2] +" isAdmin=" + user[3] +"\n");	
				} catch (IOException e1) {
					e1.printStackTrace();
				}		
            }
            catch(ArrayIndexOutOfBoundsException exception) {
               break;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ArrayIndexOutOfBoundsException e){
    	System.out.println("Reached the end of the file.");
    }
           
	
}

public void populateTypes(){
	output.setText("Populating Types...\n");
    String line = "";
    String cvsSplitBy = ",";
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        while ((line = br.readLine()) != null) {
            String[] type = line.split(cvsSplitBy);
            try{
                Call<ResponseBody> call = service.addType(type[0]);
				Response<ResponseBody> response;
				try {
					response = call.execute();	
					reply = response.body().string(); //taken from global variable
					output.append("Added type: " + type[0] + "\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}		
            }
            catch(ArrayIndexOutOfBoundsException exception) {
               break;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ArrayIndexOutOfBoundsException e){
    	System.out.println("Reached the end of the file.");
    }
           
	
}


public void populateParts(){
	output.setText("Populating Parts...\n");
    String line = "";
    String cvsSplitBy = ",";
    // Read file
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        while ((line = br.readLine()) != null) { // Read per row
            String[] part = line.split(cvsSplitBy);
            Long typeID;
            // get Type ID
    		Call<Type> callGetTypes = service.findType(part[0]);
    		try { // get response
    			Response<Type> responseGetTypes = callGetTypes.execute();
    			Type replyType = responseGetTypes.body();
    			typeID = replyType.getId();
    			
    			Call<ResponseBody> call = service.addPart("bpalmer1", part[1], Double.parseDouble(part[2]),
                		part[3], "", typeID);
    			Response<ResponseBody> response;
    			output.append("Added part:\n" + "Type="+part[0]+" Name="+part[1]+" Price="+part[2]+" Description="+part[3] + "\n");
    			try { // get response
    				response = call.execute();	
    				reply = response.body().string(); //taken from global variable
    			} catch (IOException e1) { e1.printStackTrace(); }	
    			catch (NullPointerException e){
    				System.out.println("Part not added.");
    			}//end of get response
            }
            catch(ArrayIndexOutOfBoundsException exception) {
               break;
            } catch (NullPointerException e){
            	System.out.println("Type not found.");// end get response
            } //end of get Type ID
        } // end read row
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ArrayIndexOutOfBoundsException e){
    	System.out.println("Reached the end of the file.");
    }
           
	
}

public class Type{
	private Long id;
	private String name;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

}

	
