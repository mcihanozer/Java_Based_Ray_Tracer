package comp557.a5;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

@SuppressWarnings("serial")
public class A5App extends JFrame implements Runnable {	
	
	/**
	 * Entry point for the application, either renders the file specified as
	 * the first argument, or opens a simple interface listing all scene files
	 * in the a5data directory.
	 * @param args The first argument should be the filename of the scene XML file.
	 */
	public static void main(String[] args)
	{		
		Locale.setDefault(Locale.ENGLISH);
		
		
		A5App app = new A5App();
		
		if (args.length == 0)
		{
			app.openSceneList();	// Load XML files list, and create the GUI
		}
		else
		{
			app.renderFile(args[0]);	// Start a thread to render the file directly (From command prompt)
										// Also, when CLICK EVENT is triggered by the GUI,
										// this method is called for starting a threat that renders the scene
			
										// The tread reads the file (.xml), creates the scene, and starts rendering process
		}
	}

	private JList<File> listbox;	// Scene list (.xml)
	private JPanel topPanel;	// GUI

	public A5App() {}	// Constructor
	
	// Load XML files list, and create the GUI
	public void openSceneList()
	{
		File folder = new File("a5data");
		
		// Load all files in "a5data" folder with ".xml" extension
		final File[] listOfFiles = folder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File arg0, String arg1)
			{
				return arg1.endsWith(".xml");
			}
		}
		);
		
		// Create "little" user interface for selecting the scenes
		setTitle("Scenes");
		setSize(200, 500);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		
		getContentPane().add(topPanel);
		
		listbox = new JList<File>(listOfFiles);
		topPanel.add(listbox, BorderLayout.CENTER);
		
		setVisible(true);	// Show the GUI
		
		// Add mouse listener (CLICK EVENT)
		listbox.addMouseListener(new MouseAdapter()
		{
		    public void mouseClicked(MouseEvent evt)
		    {
		        JList<?> list = (JList<?>)evt.getSource();
		        
		        if (evt.getClickCount() == 2)
		        {
		            int index = list.locationToIndex(evt.getPoint());
		            
		            if (listOfFiles[index].getName().endsWith(".xml"))
		            {
		            	renderFile(listOfFiles[index].getAbsolutePath());
		            }
		        }
		    }
		}
		);
	}	// End of openSceneList()
	
	private String currentFile;
	
	/**
	 * Renders the scene in a separate thread
	 * @param path
	 */
	public void renderFile(String path)	// Start a thread to render the selected file
	{
		currentFile = path;
		
		new Thread(this).start();
	}
	
	@Override
	public void run()	// The tread reading the file (.xml), creating the scene, and starting rendering process
	{
		try
		{
			// JAVA XML READING PART + Our code	//
			
			// SceneNode, Scene, Parser, Scene.render() main related part with us (Marked as "US")	//
			
			SceneNode.nodeMap.clear();	// US
			
			InputStream inputStream = new FileInputStream( new File(currentFile) );
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse( inputStream );
			
			long pstart = System.nanoTime();

			Scene scene = Parser.createScene( document.getDocumentElement() );	// US: Read the XML file and generate related data structures
			
			long rstart = System.nanoTime();
			
			scene.render(true);	// US: All rendering process is in here
								// Generating the rays, shooting them, shading, and rsaving the pixel color
			
			// JAVA XML READING PART + Our code	//
			
			// Calculate and print parsing, and rendering time
			double ptime = (double)((rstart-pstart)/1E9);
			double rtime = (double)((System.nanoTime()-rstart)/1E9);
			
			String filename = currentFile.substring(currentFile.lastIndexOf(File.separator)+1);
			System.out.println(filename + ": Parse=" + ptime + " | Render=" + rtime + " | Total=" + (ptime+rtime));
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Failed to load simulation input file.", e);
		}
	}
}