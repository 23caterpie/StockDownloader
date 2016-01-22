/**
 * @author Carter Currin
 * @file theWindow.java
 * @date 4/3/14
 * @version 3.0
 * @description Downloads .csv files from Yahoo Finances as specified by the ada database Dow30,
 * 				and gives a rating (-10 <--> 10) based on the trends of the closing prices and
 * 				volume of the most recent 5 business days. As of v3.0 the program has a GUI. First
 * 				press the "Connect to Database" button. This will load all the stock symbols into
 * 				the GUI's list. The "Download" button can now be pressed which starts the download
 * 				from Yahoo Finance. From here the "Rate" button can now be pressed which will rate
 * 				the downloaded stocks and display the ratings in the console TextArea. From here
 * 				the ratings can be sorted using the "Sort Ratings" button. This will also display
 * 				the ratings in descending order in the console TextArea. At any time after the symbol
 * 				list has been filled, you can click a symbol to display the contents of the .csv file
 * 				associated with it. If the file doesn't exist yet because the download has not happened
 * 				and no previous downloads have happened in a previous session then no information will
 * 				be displayed in the table.
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.concurrent.*;
import au.com.bytecode.opencsv.CSVReader;

public class theWindow {

	private JButton btnConnect;
	private JButton btnDownload;
	private JButton btnRate;
	private JButton btnSortRatings;
	
	private JFrame frmNextBillionareV;
	private JTable table;
	private JTextArea console;
	private JList<String> symbolJList;
	private DefaultListModel<String> symbolListModel;
	private DefaultTableModel stockTableModel;
	private ArrayList<String> symbolList;
	private ArrayList<FiveDayRating> ratingList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				try
				{
					theWindow window = new theWindow();
					window.frmNextBillionareV.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public theWindow()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmNextBillionareV = new JFrame();
		frmNextBillionareV.getContentPane().setBackground(SystemColor.window);
		frmNextBillionareV.setResizable(false);
		frmNextBillionareV.setTitle("Next Billionare v3.0");
		frmNextBillionareV.setSize(750, 647);
		frmNextBillionareV.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmNextBillionareV.getContentPane().setLayout(null);
		
		btnConnect = new JButton("Connect to Database");
		btnConnect.addActionListener(new ActionListener()
		{
			/**
			 * @description Accesses the ada.gonzaga.edu database's table Dow30 and retrieves a list of stock
			 * symbols from it. The symbols are saved for later for use in downloading and they are
			 * displayed onto the symbolJList in the GUI. Once this is complete, it disables itself
			 * and enables "Download" button
			 */
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					MySQLAccess dataBaseAccessor = new MySQLAccess();
					symbolList = dataBaseAccessor.readDataBase();
					for(String symbol:symbolList)
					{
						symbolListModel.addElement(symbol);
					}
					btnDownload.setEnabled(true);
					btnConnect.setEnabled(false);
				}
				catch(Exception except)
				{
					System.out.println(except.getMessage());
				}
			}
		});
		btnConnect.setBounds(10, 11, 173, 23);
		frmNextBillionareV.getContentPane().add(btnConnect);
		
		btnDownload = new JButton("Download");
		btnDownload.addActionListener(new ActionListener()
		{
			/**
			 * @description Starts the downloads of .csv files based on the contents of
			 * symbolList via multiple threads (Callable). Disables itself when complete
			 * and enables the "Rate" button.
			 */
			public void actionPerformed(ActionEvent e)
			{
				ExecutorService executor = Executors.newFixedThreadPool(2 * symbolList.size());
				
				for(final String item:symbolList)
				{  
					Callable<String> worker1 = new Callable<String>()
					{
						public String call()
						{
							return ("Downloading " + item + " ...\n");
						}
					};
					Callable<Boolean> worker2 = new Callable<Boolean>()
						{
							public Boolean call()
							{
								Downloader.doDownload(item);
								return new Boolean(true);
							}
						};
					try
					{
						Future<String> submitMessage = executor.submit(worker1);
						Future<Boolean> submitDownloader = executor.submit(worker2);
						console.append(submitMessage.get());
					}
					catch(Exception except)
					{
						System.out.println(except.getMessage());
					}
				}
				executor.shutdown();
				console.append("Downloading is COMPLETE\n");
				btnRate.setEnabled(true);
				btnDownload.setEnabled(false);
			}
		});
		btnDownload.setEnabled(false);
		btnDownload.setBounds(193, 11, 173, 23);
		frmNextBillionareV.getContentPane().add(btnDownload);
		
		btnRate = new JButton("Rate");
		btnRate.addActionListener(new ActionListener()
		{
			/**
			 * @description Starts rating the stocks from the downloaded .csv files via multiple threading
			 * (Callable). The ratings are displayed on the console TextArea and are saved for
			 * later for possible sorting in ratingList. If there was an exception thrown while
			 * rating was being performed then -100, an impossible rating, will be displayed
			 * as a placeholder. Disables itself when complete and enables the "Sort Ratings" button.
			 */
			public void actionPerformed(ActionEvent arg0)
			{
				ExecutorService executor = Executors.newFixedThreadPool(symbolList.size());
				ratingList = new ArrayList<FiveDayRating>();
				console.append("SYMBOL\t\tPREV5\n");
				
				for(final String item:symbolList)
				{  
					Callable<Integer> ratingWorker = new Callable<Integer>()
					{
						public Integer call() throws Exception
						{
							try
							{
								FiveDayRater currentFiveDayRating = new FiveDayRater(item);
								return (new Integer(currentFiveDayRating.getRating()));
							}
							catch(Exception except)
							{
								System.out.println(except.getMessage());
								return new Integer(-100);
							}
						}
					};
					try
					{
						Future<Integer> submitMessage = executor.submit(ratingWorker);
						Integer rating = submitMessage.get();
						FiveDayRating wholeRating = new FiveDayRating(rating, item);
						ratingList.add(wholeRating);
						console.append(item + "\t\t" + rating + "\n");
					}
					catch(Exception except)
					{
						System.out.println(except.getMessage());
					}
				}
				executor.shutdown();
				console.append("Rating is COMPLETE\n");
				btnSortRatings.setEnabled(true);
				btnRate.setEnabled(false);
			}
		});
		btnRate.setEnabled(false);
		btnRate.setBounds(376, 11, 174, 23);
		frmNextBillionareV.getContentPane().add(btnRate);
		
		btnSortRatings = new JButton("Sort Ratings");
		btnSortRatings.addActionListener(new ActionListener()
		{
			/**
			 * @description Sorts the ratings in ratingList in descending order and displays them on
			 * the console TextArea. Disables itself when finished
			 */
			public void actionPerformed(ActionEvent arg0)
			{
				console.append("\nSYMBOL\t\tPREV5\n");
				Collections.sort(ratingList);
				for(FiveDayRating item:ratingList)
				{
					console.append(item.getSymbol() + "\t\t" + item.getRating() + "\n");
				}
				console.append("Sorting is COMPLETE\n");
				btnSortRatings.setEnabled(false);
			}
		});
		btnSortRatings.setEnabled(false);
		btnSortRatings.setBounds(560, 11, 174, 23);
		frmNextBillionareV.getContentPane().add(btnSortRatings);
		
		symbolListModel = new DefaultListModel<String>();
		symbolJList = new JList<String>(symbolListModel);
		symbolJList.addListSelectionListener(new ListSelectionListener()
		{
			/**
			 * @description Opens the file associated with the symbol selected and displays its
			 * contents on the GUI's JTable. If the file doesn't exist, nothing will be displayed
			 * on the table.
			 */
			public void valueChanged(ListSelectionEvent arg0)
			{
				if(!arg0.getValueIsAdjusting())
				{
					try
					{
						stockTableModel.setRowCount(0);
						URL quoteToRate = FiveDayRater.class.getResource(symbolJList.getSelectedValue() + ".csv");
						if(quoteToRate == null)
							throw new Exception(symbolJList.getSelectedValue() + ".csv not found");
						String filesPathAndName = quoteToRate.getPath();
						CSVReader reader;
						reader = new CSVReader(new FileReader(filesPathAndName), ',', '"', 1);
						String[] nextLine;
						String[] nextLineToDisplay = new String[6];
						while((nextLine = reader.readNext()) != null)
						{
					        for(int i = 0; i < 6; i++)
					        {
					        	nextLineToDisplay[i] = nextLine[i];
					        }
							stockTableModel.addRow(nextLineToDisplay);
					    }
						reader.close();
					}
					catch(IOException e)
					{  
						e.printStackTrace();
					}
					catch(Exception e)
					{  
						System.out.println(e.getMessage());
					}
				}
			}
		});
		symbolJList.setBackground(Color.WHITE);
		symbolJList.setFont(new Font("Courier New", Font.BOLD, 12));
		symbolJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane_1 = new JScrollPane(symbolJList);
		scrollPane_1.setBounds(10, 55, 96, 330);
		frmNextBillionareV.getContentPane().add(scrollPane_1);
		
		console = new JTextArea();
		console.setBackground(SystemColor.info);
		console.setFont(new Font("Courier New", Font.BOLD, 12));
		console.setEditable(false);
		
		JScrollPane scrollPane_2 = new JScrollPane(console);
		scrollPane_2.setBounds(0, 400, 744, 219);
		frmNextBillionareV.getContentPane().add(scrollPane_2);
		
		stockTableModel = new DefaultTableModel();
		table = new JTable(stockTableModel);
		table.setFont(new Font("Courier New", Font.BOLD, 12));
		stockTableModel.addColumn("Date");
		stockTableModel.addColumn("Open");
		stockTableModel.addColumn("High");
		stockTableModel.addColumn("Low");
		stockTableModel.addColumn("Close");
		stockTableModel.addColumn("Volume");
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(125, 55, 512, 330);
		frmNextBillionareV.getContentPane().add(scrollPane);
		
		JLabel lblConsule = new JLabel("Console");
		lblConsule.setFont(new Font("Courier New", Font.BOLD, 12));
		lblConsule.setBounds(26, 386, 52, 14);
		frmNextBillionareV.getContentPane().add(lblConsule);
		
		JLabel lblSymbol = new JLabel("Symbols");
		lblSymbol.setFont(new Font("Courier New", Font.BOLD, 12));
		lblSymbol.setBounds(20, 38, 52, 14);
		frmNextBillionareV.getContentPane().add(lblSymbol);
		
		JLabel lblHistoryData = new JLabel("History Data");
		lblHistoryData.setFont(new Font("Courier New", Font.BOLD, 12));
		lblHistoryData.setBounds(135, 38, 90, 14);
		frmNextBillionareV.getContentPane().add(lblHistoryData);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(115, 55, 2, 330);
		frmNextBillionareV.getContentPane().add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(78, 392, 666, 2);
		frmNextBillionareV.getContentPane().add(separator_1);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(0, 392, 24, 2);
		frmNextBillionareV.getContentPane().add(separator_2);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(222, 44, 522, 2);
		frmNextBillionareV.getContentPane().add(separator_3);
		
		JSeparator separator_4 = new JSeparator();
		separator_4.setBounds(71, 44, 62, 2);
		frmNextBillionareV.getContentPane().add(separator_4);
		
		JSeparator separator_5 = new JSeparator();
		separator_5.setBounds(0, 44, 18, 2);
		frmNextBillionareV.getContentPane().add(separator_5);
		
		JLabel IconLabel = new JLabel("");
		IconLabel.setBounds(647, 56, 87, 329);
		frmNextBillionareV.getContentPane().add(IconLabel);
		URL myIcon = theWindow.class.getResource("Icon1.png");
		if(myIcon != null)
			IconLabel.setIcon(new ImageIcon(myIcon));
	}
}
