package cygnus.flicker;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;


public class GUImanager extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final String title = "Flicker Encrypter";
	private final int winHeight = 350;
	private final int winWidth = 550;
	
	private static final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private File file; 
	private String[] bitString = { "128 Bit", "192 Bit", "256 Bit"}; 
	private String fileDisplay = ""; 
	private String encryptString = "Encrypt";
	private String decryptString = "Decrypt";
	private boolean phase = true;
	private int bitPref = 128;

	public GUImanager (){
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 350);
		setSize(winWidth, winHeight);
		setTitle(title);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		//Encrypt / Decrypt Button. 
		final JButton btnEncrypt = new JButton("Encrypt");
		btnEncrypt.setEnabled(false);
		btnEncrypt.setBounds(196, 249, 97, 25);
		contentPanel.add(btnEncrypt);
		// Sub Method, call encrypt / decrypt file 
		btnEncrypt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String password = textField.getText();
				if (phase){
					Encryption.encryptFile(fileDisplay, password, bitPref);
				}
				if (!phase){
					Encryption.decryptFile(fileDisplay, password, bitPref);	
	
				}
			}
		});// End Encryption Button
		
		//Encryption Button Listener, sets the phase of the encryption button
	    ActionListener radioListener = new ActionListener(){
	    	// Sub Method for radio button
	    	public void actionPerformed(ActionEvent e){
	    		String command = e.getActionCommand();
	    		btnEncrypt.setText(command); // Set button to radio states
	    		if (command.equals("Encrypt")){
	    			phase = true;
	    		}
	    		else
	    		{
	    			phase = false;
	    		}
	    	}
	    }; // End action listener
		  
		// Encrypt Radio Button
		JRadioButton encryptRadio = new JRadioButton("Encrypt");
		encryptRadio.setBounds(79, 42, 78, 38);
		contentPanel.add(encryptRadio);
		encryptRadio.setActionCommand(encryptString);
		encryptRadio.addActionListener(radioListener);
		
		// Decrypt Radio Button
		JRadioButton decryptRadio = new JRadioButton("Decrypt");
		decryptRadio.setBounds(331, 42, 78, 25);
		contentPanel.add(decryptRadio);
		decryptRadio.setActionCommand(decryptString);
		decryptRadio.addActionListener(radioListener);
		
	    //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(encryptRadio);
	    group.add(decryptRadio);

	 
	    //Password Text Field
	    textField = new JTextField();
		textField.setBounds(42, 99, 400, 22);
		contentPanel.add(textField);
		textField.setColumns(10);
	
		//Password Text Field Label
		JLabel lblEnterPassword = new JLabel("Enter password:");
		lblEnterPassword.setBounds(205, 70, 132, 16);
		contentPanel.add(lblEnterPassword);
		
		//Combo Box Bits select
		JComboBox<String> bitList = new JComboBox<>(bitString);
		bitList.setBounds(42, 130, 70, 24);
		bitList.setSelectedIndex(0);
		contentPanel.add(bitList);
		bitList.addActionListener(new ActionListener(){
			//SUB METHOD: Get the encryption bit preference when changed
			public void actionPerformed(ActionEvent e){
				String bitPass = bitList.getSelectedItem().toString();
				bitPass = bitPass.substring(0, bitPass.length()-4);
				bitPref = Integer.parseInt(bitPass);
			}
		}); // Close action listener
		
		//File Path display
		final JLabel lblFile = new JLabel(": " + fileDisplay);
		lblFile.setBounds(160, 185, 300, 16);
		contentPanel.add(lblFile);
		
		//ChooseFile Button
		JButton btnChooseAFile = new JButton("Choose a file");
		btnChooseAFile.setBounds(42, 180, 116, 25);
		contentPanel.add(btnChooseAFile);
		btnChooseAFile.addActionListener(new ActionListener(){  
			//SUB METHOD: Load file, Set file display label, enable encrypt button
			public void actionPerformed(ActionEvent e){  
			            file = chooseFile();  
			            if (!(file==null)){
				            fileDisplay = file.toString();
				            lblFile.setText(": "+fileDisplay);
				            btnEncrypt.setEnabled(true);
			            }
			}  
		});  // Close action listener
		
		
		// Get rid of the Java logo, sorry Oracle :(
		Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
		setIconImage(icon);
		// Make Jframe visible
		setVisible(true);
	} // END CONSTRUCTOR
	
	
	// Opens a file chooser GUI, returns selected file, returns null if canceled.
	private File chooseFile(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(contentPanel);
			if (result == JFileChooser.APPROVE_OPTION) {
				return fileChooser.getSelectedFile();
			}
			else
			{
				return null;
			}
	}
	
	// Shows information message 
    public static void infoBox(String infoMessage, String titleBar)
	    {
	        JOptionPane.showMessageDialog(contentPanel, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
	    }
}
