package tsp;

/**
 *  tsp.java
 *
 *	Programa que demonstra o uso de "simulated annealing" para resolver problemas de otimização combinatória.
 *	TSP
 */
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * "tsp" manages user input and output for the simulated annealing program. 
 * 
 * @author  Stephen Schmitt
 * @version 1.0
 * @since   01/12/00
 */
public class TSP extends JFrame 
{
	//frequently used program strings
	String app_name         = new String( "Simulated Annealing" );
	String route_file_name  = new String( "unnamed.txt" );

	// declarations for the command menu
	MenuBar  main_menu      = new MenuBar();
	Menu     file_menu      = new Menu();
	MenuItem file_new       = new MenuItem();
	MenuItem file_open      = new MenuItem();
	MenuItem file_save      = new MenuItem();
	MenuItem file_save_as   = new MenuItem();
	MenuItem file_separator = new MenuItem();
	MenuItem file_exit      = new MenuItem();
	Menu     comp_menu      = new Menu();
	MenuItem comp_run       = new MenuItem();
	MenuItem comp_step      = new MenuItem();
	MenuItem comp_reset     = new MenuItem();
	MenuItem comp_settings  = new MenuItem();
	MenuItem help_about     = new MenuItem();
	
    plot_area plot;                                 // area Para plotar os pontos
    static int mouse_x = 0, mouse_y = 0;            // Posicao do mouse
    
    Label label;                                    // Status bar
    
    llist  route             = new llist();         // Pontos para plotar
    double route_length      = 0;                   // Comprimento do Caminho plotado
    llist  best_route        = null;                // Melhor rota
    double best_route_length = 0;                   // Tamanho da melhor solucao
    llist  old_route         = null;                

    double factor          = 0.9;                   // Fator de reduçao
    int    anneal_steps    = 20;                    // Quantidade maxima de passos
    int    trials_per_step = 100;                   // total trial swaps de cada passo
    int    swaps_per_step  = 20;                    // maximo good swaps de cada passo
    double Temperature     = 0;                     // Media do tamanho entre pontos
    
    int     node_index     = 0;                     // Contador de nós
    boolean single_step    = false;                 // modo de calculo
    boolean fixed_end_pts  = false;                 // pode mudar os end points?

    /**
     * "main( String [] )" entry point for application.
     *
     * @return  nothing
     */
    public static void main( String args[] )
    {
    	TSP app = new TSP();                        
       
    	//posicionando
        app.setLocation( 200, 100 );                 
        app.setSize( 500, 400 );                    
        app.setVisible( true );                     
    }
    
    /**
     * Construtor
     *
     * @return  nothing
     */
    TSP() 
    {
        setTitle( app_name + " - " +                 
                  route_file_name );                
                
        set_menus();                                // colocando menus
       
        GridBagLayout gb = new GridBagLayout();            
        setLayout( gb );

        /*
         *  área para mostrar os pontos
         */
        plot = new plot_area( this );         
        GridBagConstraints c  = new GridBagConstraints();        
        
        c.fill      = GridBagConstraints.BOTH;
        c.weighty   = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints( plot, c );
        add( plot );

        /*
         *  informacoes
         */        
        label = new Label( "Click within the coordinate area." );
        label.setBackground( Color.lightGray );
        
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.0;
        gb.setConstraints( label, c );
        add( label );
        
        /*
         *  terminar a app
         */
        addWindowListener( new WindowAdapter()
        { 
            public void windowClosing( WindowEvent e )
            {
                file_exit_action();
            }
        } );
    }
        
    /**
     * "mouse_location( Point )" displays the mouse location on the status bar.
     *
     * @return  nothing
     */
    public void mouse_location( Point pt ) 
    {
        mouse_x = pt.x;                             // save current mouse location
        mouse_y = pt.y;
                
        current_status();                           // show its location
    }
    
    /**
     * "current_status()" displays current status on the status bar.
     *
     * @return  nothing
     */
    public void current_status()
    {
        label.setText( "Tamanho = " + route_length );
    }
    
    /**
     * "set_menus()" creates the main menu bar and sub-menus.
     *
     * @return  pointer to list item
     */
    void set_menus()
    {
		// File menu
		file_menu.setLabel("Arquivo");		
		
		file_menu.add( file_open );
		file_open.setLabel( "Abrir..." );
		file_open.setShortcut( new MenuShortcut( KeyEvent.VK_O, false ) );			
		
		file_menu.add( file_separator );
		file_separator.setLabel( "-" );
		
		file_menu.add( file_exit );
		file_exit.setLabel( "Exit" );
    
		main_menu.add( file_menu );
		
		// Compute menu
		comp_menu.setLabel( "Executar" );
		
		comp_menu.add( comp_run );
		comp_run.setLabel( "Executar" );
		comp_run.setShortcut( new MenuShortcut( KeyEvent.VK_V, false ) );
		
		comp_menu.add( comp_step );
		comp_step.setLabel( "Passo" );
		comp_step.setShortcut( new MenuShortcut( KeyEvent.VK_X, false ) );
				
		comp_menu.add( comp_reset );
		comp_reset.setLabel( "Reset" );
		comp_reset.setShortcut( new MenuShortcut( KeyEvent.VK_Z, false ) );
		
		comp_menu.add( comp_settings );
		comp_settings.setLabel( "Configuracoes" );
		
		main_menu.add( comp_menu );
				
		setMenuBar( main_menu );
		
		action_interface action = new action_interface();
		
		file_new.addActionListener( action );
		file_open.addActionListener( action );
		file_save.addActionListener( action );
		file_save_as.addActionListener( action );
		file_exit.addActionListener( action );
		
		comp_run.addActionListener( action );
		comp_reset.addActionListener( action );
		comp_step.addActionListener( action );
		comp_settings.addActionListener( action );
				
	}

    /**
     * "action_interface" links menu command actions to application methods.
     *
     * @return  nothing
     */
	class action_interface implements ActionListener
	{
        /**
         * "actionPerformed( ActionEvent )" selects a method corresponding to
         * a user selected menu command.
         *
         * @return  nothing
         */
		public void actionPerformed( ActionEvent event )
		{
			Object object = event.getSource();						
			
			// select menu command response
			if( object == file_open )
				file_open_action();
			else if( object == file_exit )
				file_exit_action();
			else if( object == comp_run )
				comp_run_action();
			else if( object == comp_reset )
				comp_reset_action();
			else if( object == comp_step )
				comp_step_action();
			else if( object == comp_settings )
				comp_settings_action();			
		}
	}	   

    /**
     * "file_open_action()" enables user to load a previously saved point list.
     *
     * @return  nothing
     */
	void file_open_action()
	{
		FileDialog open;                            // file open dialog class
        FileInputStream fs;                         // file stream
        BufferedReader br;                          // class for reading data
        
        String file_name;                           // file to open

		try 
		{
		    // create a file open dialog
		    open = new FileDialog( this, "Carregar Arquivo", FileDialog.LOAD );
		    open.setFilenameFilter( new dot_extension() );
		    open.setVisible( true );
		    		    
		    // open the selected file, if any
		    if( ( file_name = open.getDirectory()+open.getFile() ) != null )
		    {
                route = new llist();		        // create a new list, on failure
                if( route == null )                 // return without changing file name
                    return;
                        
                // now read in the file
                fs = new FileInputStream( file_name );
                br = new BufferedReader( new InputStreamReader( fs ) );
                parse_file( br );                   // get data
                br.close();                         // the input file

                // initialize parameters
                route_file_name = new String( file_name );
                route_length    = route.get_route_length();
		        single_step     = false;	 
                
                // update display
                current_status();
                setTitle( app_name + " - " + route_file_name );
                plot.repaint();			 
            }
		} 
		catch( Exception e ) 
		{
		}
	}
	
    /**
     *  Faz a leitura do arquivo
     *
     *      index : x_coord , y_coord '\n'
     *
     *  Loading will stop silently on format error.
     *
     *  @return  nothing
     */
	void parse_file( BufferedReader br ) throws IOException
	{
        String text;                                // input line of text
        int n, x, y;                                // point parameters
        token tkn = new token();                    // class for parsing file

        node_index = 0;                             // init node counter
        
        if( (text = br.readLine() ) != null){//Ignora a primeira linha
	        // get text line from file one at a time                
	        while( ( text = br.readLine() ) != null )
	        {
	            tkn.next_line( text );                  // initialize parser 
	                    
	            if( tkn.NUMBER == tkn.get_token() )
	                n = tkn.value;
	            else
	                break;                                    
		    
	            if( tkn.NUMBER == tkn.get_token() )
	                x = tkn.value;
	            else
	                break;                              
	                       
	            if( tkn.NUMBER == tkn.get_token() )
	                y = tkn.value;
	            else
	                break;
	             
	            if( node_index < n )                    // set next node counter to 
	                node_index = n;                     // largest value seen
	                    
	            route.insert( x, y, n );                // insert node into list
	        }
        }
	}

    /**
     * "file_exit_action()" displays a dialog to confirm the exit command.
     *
     * @return  nothing
     */
	void file_exit_action()
	{
		try 
		{
		    exit_dlg qt = new exit_dlg( this, true );
		    qt.setVisible( true );
		} 
		catch( Exception e ) 
		{
		}
	}

    /**
     * "comp_run_action()" Roda até terminar.
     *
     * @return  nothing
     */
	void comp_run_action()
	{
		int    n = route.get_length();
		Cursor c = plot.getCursor();
		
        plot.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );

		if( n > 1 )                                 // diviao por zero
		{
		    Graphics g = plot.getGraphics();
		        
		    old_route         = route.duplicate();  
 	        best_route        = route.duplicate(); 
            best_route_length = route.get_route_length();
            
            /*
             * Parametro inicial d etemperatura é a distancia media entre os pontos no caminho inicial.
             */
            Temperature = best_route_length / n - 1;    
		    
		    for( int i = 0; i < anneal_steps; i++ )
		    {
	            // se melhorou reduz a temperatura e repete
	            if( route.anneal( Temperature, trials_per_step, swaps_per_step, fixed_end_pts ) )
                    Temperature *= factor;
                else      
                    break;                          // nao melhorou            
		        
                // procura uma nova rota melhor
		        route_length = route.get_route_length();
                
                if( route_length < best_route_length )
                {
                    // encontrou rota melhor
                    best_route        = route.duplicate();
                    best_route_length = best_route.get_route_length();
                }
		        
		        current_status();
		        plot.update( g );
		    }
        
            if( best_route != null )                 // melhor lista encontrada durante a busca
                route.replace( best_route ); 
		}
					
		route_length = route.get_route_length();     // update windows
		current_status();
		plot.repaint();
		plot.setCursor( c );
	}

    /**
     * "comp_step_action()" runs the algorithm for a single temperature step. Repeated step
     * commands will reduce the temperature parameter at each step. The previous route is
     * saved at each step so that it can be restored using the reset command.
     *
     * @return  nothing
     */
	void comp_step_action()
	{
		// set wait cursor
		Cursor c = plot.getCursor();
        plot.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );

		if( single_step == false )                  // initialize
		{		
		    int n = route.get_length();
		    
		    if( n > 1 )                             // avoid divide by zero below
		    {
                route_length = route.get_route_length();
                Temperature  = route_length / n - 1;
		    		    
		        old_route   = route.duplicate();
		        single_step = true;	
		    }
		}
		
		// now do annealing algorithm
		if( single_step && 
		    route.anneal( Temperature, trials_per_step, swaps_per_step, fixed_end_pts ) )
		    Temperature *= factor; 
		        

		route_length = route.get_route_length();    // update windows
		current_status();
		plot.repaint();      
		plot.setCursor( c );
    }

    /**
     * "comp_reset_action()" restores the linked list to the order it was in before
     * run or step commands altered it.
     *
     * @return  nothing
     */
	void comp_reset_action()
	{
		if( old_route != null )                     // restore from saved route
		    route = old_route.duplicate();
		
		single_step = false;	 
		
		route_length = route.get_route_length();    // update windows
		current_status();
		plot.repaint();	
	}

    /**
     * "comp_settings_action()" calls a dialog box that allows user to adjust 
     * parameters of the annealing algorithm.
     *
     * @return  nothing
     */
	void comp_settings_action()
	{
		try 
		{
		    settings_dlg st = new settings_dlg( this, true );

		    st.set_factor( factor );		        // enter current settings
		    st.set_ntemps( anneal_steps );
		    st.set_nlimit( trials_per_step );
		    st.set_glimit( swaps_per_step );
		    st.set_endpts( fixed_end_pts );

		    st.setVisible( true );		            // display dialog
		    
		    factor          = st.get_factor();		// get new values if any
		    anneal_steps    = st.get_ntemps();
		    trials_per_step = st.get_nlimit();
		    swaps_per_step  = st.get_glimit();
		    fixed_end_pts   = st.get_endpts();
		} 
		catch( Exception e ) 
		{
		}
	}
}

class dot_extension implements FilenameFilter
{
    public boolean accept( File dir, String name )
    {
        return true;
    }
}


class node
{
    int  index;                                     // of this node
    int  x_loc;                                     // units right
    int  y_loc;                                     // units down
    node next;                                      // in list
}

class llist
{
    node start = null;                          // top of list
    int length = 0;                             // items in list
    node [] array = null;                           // array for fast lookup
      
    /**
     * "get_item( int )" finds the indexed item in the list.
     *
     * @return  pointer to list item
     */
    node get_item( int i )
    {
        node c = start;                             // start at top

        while( i > 0 && c != null )                 // traverse
        {
            c = c.next;
            i--;
        }

        return c;                                   // ptr to item i
    }
    
    /**
     * "init_llist()" creates an array for fast lookup of nodes.
     *
     * @return  nothing
     */
    void init_llist()
    {
        array = new node[ length ];                 // create the array
        
        for( int i = 0; i < length; i++ )           // copy pointers to array
            array[i] = get_item( i );
    }

    /**
     * "get_length" is for access to length of list.
     *
     * @return  the length of the list
     */
    int get_length() 
    { 
        return length; 
    }
    
    /**
     * "insert( int, int, int )" appends a new node to the list.  
     *
     * @return  nothing
     */
    void insert( int x, int y, int i )          // point to add
    {
        node curr = start;                      // start at top
        node ptr = new node();                  // allocate a new node item for the list

        ptr.index = i;                          // id of node
        ptr.x_loc = x;                          // coordinate
        ptr.y_loc = y;                          //     "
        ptr.next = null;

        if( start == null )                     // is first item
        {
            start = ptr;
            length++;                           // update list length
            return;
        }

        while( curr.next != null )              // find end
            curr = curr.next;

        curr.next = ptr;                        // must add to end
        length++;                               // update list length
        
        init_llist();                           // update lookup array
    }

    /**
     * "duplicate()" creates a copy of this linked list
     *
     * @return  pointer to new list
     */
    public llist duplicate()
    {
        llist copy = new llist();               // create empty list
        node srce = start;                      // this list
        
        while( srce != null )
        {
            // add entry to copy from original source
            copy.insert( srce.x_loc, srce.y_loc, srce.index );           

            srce = srce.next;                   // advance to next entry
        }
  
        return copy;                            // of a new linked list
    }

    /**
     * "replace( llist )" copies another list into this linked list
     *
     * @return  pointer to new list
     */
    public void replace( llist from )
    {
        node srce  = from.start;                // source list
        start  = null;                          // free current list
        length = 0;
        
        while( srce != null )                   // add entry from source
        {
            insert( srce.x_loc, srce.y_loc, srce.index );           

            srce = srce.next;                   // advance to next entry
        }
    }

    /**
     * "swap( int, int )" exchanges two items in the list by swapping values. 
     * This requires no complex decision logic.
     *
     * @return  nothing
     */
    public void swap( int i1, int i2 )
    {
        int x, y, i;
        
        // save first set of values
        x = array[i1].x_loc;
        y = array[i1].y_loc;
        i = array[i1].index;
        
        // replace with second set of values
        array[i1].x_loc = array[i2].x_loc;
        array[i1].y_loc = array[i2].y_loc;
        array[i1].index = array[i2].index;
        
        // put first set into second
        array[i2].x_loc = x;
        array[i2].y_loc = y;
        array[i2].index = i;
    }

    /**
     * "randint( int, int )" generate a random integer in the range [beg...end]
     *
     * @return  nothing
     */
    static int randint( int beg, int end )
    {
        int range = end - beg + 1;                  // of random int's 
        if( range < 1 )
            return 0;

        return beg + (int)( range * Math.random() );
    }

    /**
     * "write_to_file( BufferedWriter )" writes the contents of the list to a file.
     *
     * @return  nothing
     */
    void write_to_file( BufferedWriter bw )
    {
        node nd = start;                            // top of list

        try
        {
            while( nd != null )                     // another node
            {     
                // write each node's contents on a separate line
                bw.write( nd.index + ": " + nd.x_loc + ", " + nd.y_loc );
                bw.newLine();

                nd = nd.next;                       // advance
            }
        }
		catch( Exception e ) 
		{
		}
    }
    
    /** 
     * "anneal( double, int, int, boolean )" executa o algoritmo de simulated annealing.
     *
     * @return  true if improvement made, else false
     */
    //route.anneal( Temperature, trials_per_step, swaps_per_step, fixed_end_pts )
    boolean anneal( double temp, int nlimit, int glimit, boolean fix_ends ) 
    {
        int first, second, swaps = 0;
        double delta, current_cost, trial_cost;
        boolean improvement = false;
        double p, m;
        int length = get_length();
        
        if( length < 4 )                            // loop infinito? 
            return false;                           
	        
        for( int j = 1; j <= nlimit; j++ )
        {
            do                                      // Escolhe dois pontos para trocas
            {
                if( fix_ends )                      // Pode mudar as terminacoes?
                {
                    first  = randint( 1, length - 2 );
                    second = randint( 1, length - 2 );
                }
                else                               
                {
                    first  = randint( 0, length - 1 );
                    second = randint( 0, length - 1 );
                }
                    
            }
            while( first == second );               // igual, tente de novo
       
            current_cost = get_route_length();
            swap( first, second );
            trial_cost = get_route_length();
            
            delta = current_cost - trial_cost;
            
            if( delta > 0.0 )                       // improved solution
            {
                improvement = true;                 // mantém a troca
                swaps++;
            }
            else                                    // try Metropolis criterion
            {
                p = Math.random();
                m = Math.exp( delta / temp );
                
                if( p < m )
                {
	                improvement = true;             // mantém a troca
	                swaps++;
	            }
	            else
                    swap( second, first );          // Volta
            }
            
            if( swaps > glimit )                    // fez trocas suficientes
                break;
        }
        
        return improvement;                         // falso se nao trocou nada
    }

    /** 
     * "get_route_length()" computes the total path length for the list
     * 
     * @return  path length
     */
    double get_route_length()
    {	
        double dx, dy, path = 0.0;                  // init path length
        int i, n = get_length();                    // point count
        
        for( i = 0; i < n - 1; i++ )                // sum segments
        {
            dx = array[i].x_loc - array[i + 1].x_loc;
            dy = array[i].y_loc - array[i + 1].y_loc;
            double r= Math.sqrt( dx * dx + dy * dy ) + 0.5;
	        
            path+=(long) r;
	    }

        return path;                                // total path length
    }
}	

/**
 * "plot_area" Metodos para plotagem
 *
 * 
 */
class plot_area extends Canvas 
{
    static Point mouse = null;                      // mouse location in plot area
    TSP parent;                                     // of plot area

    /**
     * "plot_area( tsp )"
     *
     * @return  nothing
     */
    public plot_area( TSP ctrl ) 
    {
        parent = ctrl;
        
        //setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );	    			
    }
    
    /**
     * "show_mouse_location()" updates display of mouse location on status bar. 
     *
     * @return  nothing
     */
    public void show_mouse_location()
    {
        if( mouse != null )
            parent.mouse_location( mouse );
    }

    /**
     * "paint( Graphics )" updates plot area display.
     *
     * @return  pointer to list item
     */
    public void paint( Graphics g ) 
    {
	    node pt;
	    int n, i = 0;
        int x1, x2 = 0;
	    int y1, y2 = 0;	 
	    
	    int xini, yini = 0;
	    
	    Color fg = getForeground();
	    
	    g.setColor( fg );                           // set default foreground color
	    g.setPaintMode();
	    
	    n = parent.route.get_length();              // number of points in list
	    
	    if( n == 0 )                                // empty list
	        return;
	        
	    pt = parent.route.get_item( 0 );            // first item
        x1 = pt.x_loc;
        y1 = pt.y_loc;
        
        xini=x1;
        yini=y1;
        
	    //g.setColor( Color.blue );                   // paint the first point
	    g.setPaintMode();                           // as blue square
        g.fillOval( x1 - 2, y1 - 2, 5, 5 );
	    
	    g.setColor( fg );                           // paint intermediate points 
	    g.setPaintMode();                           // the default foreground color
	    
	    for( i = 0; i < n - 1; i++ )                // do intermediate points              
	    {
            x2 = x1;                                // save last point
		    y2 = y1;
	        
	        pt = parent.route.get_item( i + 1 );	// get current point
            x1 = pt.x_loc;
            y1 = pt.y_loc;
		    
            g.drawLine( x1, y1, x2, y2 );           // line from last to current point
            g.fillOval( x1 - 2, y1 - 2, 5, 5 );     // draw current point as circle	
	    } 
        
        g.drawLine( x1, y1, xini, yini );
	    
        //g.setColor( Color.red );                    // paint the last point 
        g.setPaintMode();                           // as red square
        g.fillOval( x1 - 2, y1 - 2, 5, 5 );
    }
}

class settings_dlg extends JDialog
{
	Button ok       = new Button();
	Button defaults = new Button();
	Button cancel   = new Button();

	Label label1    = new Label( "Fator de reduçao de temperatura.", Label.LEFT );
	TextField text1 = new TextField( 60 );

	Label label2    = new Label( "numero de reduçoes.", Label.LEFT );
	TextField text2 = new TextField( 60 );

	Label label3    = new Label( "numero de trials para cada temperatura.", Label.LEFT );
	TextField text3 = new TextField( 60 );

	//Label label4    = new Label( "Number of good swaps at each temperature.", Label.LEFT );
	TextField text4 = new TextField( 60 );
	
	//Checkbox cb     = new Checkbox( " End points fixed", true );

    double  factor;                                 // settings for application
    int     ntemps, nlimit, glimit;
    boolean endpts;

	boolean notified = false;                       // reset addNotify flag
	JFrame   frame    = null;	                    // Invoking frame

    /**
     * "settings_dlg( Frame, boolean )"
     *
     * @return  nothing
     */
	public settings_dlg( JFrame parent, boolean modal )
	{
		super( parent, modal );

        //Keep a local reference to the invoking frame
        frame = parent;
                
		setTitle( "Simulated Annealing - parameters" );
		setLayout( null );
		setSize( 360, 240 );
		setVisible( false );
		
		ok.setLabel( " OK " );
		add( ok );
		ok.setFont( new Font( "Dialog", Font.BOLD, 12 ) );
		ok.setBounds( 20, 210, 80, 22 );
		
		defaults.setLabel( " Defaults " );
		add( defaults );
		defaults.setFont( new Font( "Dialog", Font.BOLD, 12 ) );
		defaults.setBounds( 140, 210, 80, 22 );
		
		cancel.setLabel( " Cancel " );
		add( cancel );
		cancel.setFont( new Font( "Dialog", Font.BOLD, 12 ) );
		cancel.setBounds( 260, 210, 80, 22);
		
		// parameter 1
		add( text1 );
		text1.setBounds( 20, 10, 60, 22);
		add( label1 );
		label1.setBounds( 90, 10, 260, 22 );
			
		// parameter 2
		add( text2 );
		text2.setBounds( 20, 50, 60, 22);
		add( label2 );
		label2.setBounds( 90, 50, 260, 22 );
			
		// parameter 3
		add( text3 );
		text3.setBounds( 20, 90, 60, 22 );
		add( label3 );
		label3.setBounds( 90, 90, 260, 22 );
			
		// parameter 4
		//add( text4 );
		//text4.setBounds( 20, 130, 60, 22 );
		//add( label4 );
		//label4.setBounds( 90, 130, 260, 22 );
		
		//add( cb );
		//cb.setBounds( 20, 170, 260, 22 );
			
		window_interface wi = new window_interface();
		this.addWindowListener( wi );
		
		action_interface action = new action_interface();
		cancel.addActionListener( action );
		defaults.addActionListener( action );
		ok.addActionListener( action );
	}
	
    /**
     * "set_factor( double )" sets the temperature reduction factor parameter 
     * in the dialog window.
     *
     * @return  nothing
     */
	void set_factor( double f )
	{
	    factor = f;
	    text1.setText( Double.toString( f ) );
	}
	
    /**
     * "set_ntemps( int )" sets the number of temperatures parameter 
     * in the dialog window.
     *
     * @return  nothing
     */
	void set_ntemps( int n )
	{
	    ntemps = n;
	    text2.setText( Integer.toString( n ) );
	}
	
    /**
     * "set_nlimit( int )" sets the number of trials parameter 
     * in the dialog window.
     *
     * @return  nothing
     */
	void set_nlimit( int n )
	{
	    nlimit = n;
	    text3.setText( Integer.toString( n ) );
	}
	
    /**
     * "set_glimit( int )" sets the number of good swaps parameter 
     * in the dialog window.
     *
     * @return  nothing
     */
	void set_glimit( int g )
	{
	    glimit = g;
	    text4.setText( Integer.toString( g ) );
	}
	
    /**
     * "set_endpts( boolean )" sets the fixed endpoints checkbox state 
     * in the dialog window.
     *
     * @return  nothing
     */
	void set_endpts( boolean b )
	{
	    endpts = b;
	    //cb.setState( b );
	}
	
    /**
     * "get_factor()" returns updated setting to parent.
     *
     * @return  nothing
     */
	double get_factor()
	{
	    return factor;
	}
	
    /**
     * "get_ntemps()" returns updated setting to parent.
     *
     * @return  nothing
     */
	int get_ntemps()
	{
	    return ntemps;
	}
	
    /**
     * "get_nlimit()" returns updated setting to parent.
     *
     * @return  nothing
     */
	int get_nlimit()
	{
	    return nlimit;
	}
	
    /**
     * "get_glimit()" returns updated setting to parent.
     *
     * @return  nothing
     */
	int get_glimit()
	{
	    return glimit;
	}
	
    /**
     * "get_endpts()" returns updated setting to parent.
     *
     * @return  nothing
     */
	boolean get_endpts()
	{
	    return endpts;
	}

    /**
     * "addNotify()" notifies a Component that it has been added to a container. 
     *
     * @return  nothing
     */
	public void addNotify()
	{
	    Dimension d = getSize();	                // get window size prior to 
		super.addNotify();	                        // calling parents addNotify.

		if( notified )		                        // only do this once.
			return;

		Insets in = getInsets();		            // adjust according to insets
		setSize( in.left + in.right + d.width, 
		         in.top + in.bottom + d.height );
		
		Component components[] = getComponents();   // list of components
		
		for( int i = 0; i < components.length; i++ )
		{
			Point p = components[i].getLocation();
			p.translate( in.left, in.top );
			components[i].setLocation( p );
		}
		
		notified = true;		                    // set flag
	}

    /**
     * "setVisible( boolean )" shows or hides the component depending on the boolean flag b.
     *
     * @return  nothing
     */
    public void setVisible( boolean b )
	{
		if( b )
		{
			Rectangle parent = getParent().getBounds();
			Rectangle child  = getBounds();
	
			setLocation( parent.x + ( parent.width  - child.width   )/2,
				         parent.y + ( parent.height - child.height )/2 );
			
			Toolkit.getDefaultToolkit().beep();
		}
		super.setVisible( b );
	}

    /**
     * "action_interface" class for handling user input actions.
     *
     * @return  nothing
     */
	class action_interface implements ActionListener
	{
        /**
         * "actionPerformed( ActionEvent )" selects response to user input actions.
         *
         * @return  nothing
         */
		public void actionPerformed( ActionEvent event )
		{
		    Double  d;
		    Integer n;
		    Boolean b;
			Object  object = event.getSource();
			
			if( object == ok )
		    {
                d      = new Double( text1.getText() );
		        factor = d.doubleValue();
		        
		        n      = new Integer( text2.getText() );
		        ntemps = n.intValue();
		        
                n      = new Integer( text3.getText() ); 
		        nlimit = n.intValue();
		        
                n      = new Integer( text4.getText() ); 
		        glimit = n.intValue();
		        
		        //b      = new Boolean( cb.getState() );
		        b      = new Boolean(false);
		        endpts = b.booleanValue();
		        
	            dispose(); 
		    } 
			else if( object == defaults )
		    {
		        // strings should agree with initial values in tsp
		        text1.setText( "0.9" );
		        text2.setText( "20" );
		        text3.setText( "100" );
		        text4.setText( "20" );
		        //cb.setState( false );
		    } 
			else if( object == cancel )
			    dispose();
		}
	}

    /**
     * "window_interface" class for handling system commands.
     *
     * @since   01/12/00
     */
	class window_interface extends WindowAdapter
	{
        /**
         * "windowClosing( WindowEvent )" processes a window closing system command.
         *
         * @return  nothing
         */
		public void windowClosing( WindowEvent event )
		{
			Object object = event.getSource();
			
			if( object == settings_dlg.this )
			    dispose();
		}
	}
}

class exit_dlg extends JDialog
{
	Button yesButton = new Button();
	Button noButton  = new Button();
	Label label1     = new Label();

	JFrame   frame    = null;	                    // Invoking frame
	boolean notified = false;                       // reset addNotify flag

    /**
     * "exit_dlg( Frame, boolean )"
     *
     * @return  nothing
     */
	public exit_dlg( JFrame parent, boolean modal )
	{
		super( parent, modal );
        frame = parent;                             // local reference to invoking frame
        
		setLayout( null );                          // set dialog size
		setSize( 300,120 );
		setVisible( false );
		
		yesButton.setLabel( " Sim " );              
		add( yesButton );
		yesButton.setFont( new Font( "Dialog", Font.BOLD, 12 ) );
		yesButton.setBounds( 52, 80, 78, 22 );
		
		noButton.setLabel( "  Não  " );
		add( noButton );
		noButton.setFont( new Font( "Dialog", Font.BOLD, 12 ) );
		noButton.setBounds( 170, 80, 78, 22);
		
		label1.setText( "Sair?" );
		label1.setAlignment( Label.CENTER );
		add( label1 );
		label1.setBounds( 60, 32, 180, 22 );
		
		setTitle( "Simulated Annealing - Sair" );

		window_interface wi = new window_interface();
		this.addWindowListener( wi );
		action_interface action = new action_interface();
		noButton.addActionListener( action );
		yesButton.addActionListener( action );
	}

    /**
     * "addNotify()" notifies a Component that it has been added to a container. 
     *
     * @return  nothing
     */
	public void addNotify()
	{
	    Dimension d = getSize();	                // get window size prior to 
	    super.addNotify();                          // calling parents addNotify.
	    
		if( notified )		                        // only do this once.
			return;

		// Adjust components according to the insets
		setSize( getInsets().left + getInsets().right  + d.width, 
		         getInsets().top  + getInsets().bottom + d.height );
		         
		Component components[] = getComponents();   // list of components
		
		for( int i = 0; i < components.length; i++ )
		{
			Point p = components[i].getLocation();
			p.translate( getInsets().left, getInsets().top );
			components[i].setLocation(p);
		}
		
		notified = true;		                    // set flag
	}

    /**
     * "setVisible( boolean )" shows or hides the component depending on the boolean flag b.
     *
     * @return  nothing
     */
    public void setVisible( boolean b )
	{
		if( b )
		{
			Rectangle parent = getParent().getBounds();
			Rectangle child  = getBounds();
	
			setLocation( parent.x + ( parent.width  - child.width  ) / 2,
				         parent.y + ( parent.height - child.height ) / 2 );
			
			Toolkit.getDefaultToolkit().beep();
		}
		super.setVisible( b );
	}

    /**
     * "action_interface" class for handling user input actions.
     *
     * @return  nothing
     */
	class action_interface implements ActionListener
	{
        /**
         * "actionPerformed( ActionEvent )" selects response to user input actions.
         *
         * @return  nothing
         */
		public void actionPerformed( ActionEvent event )
		{
			Object object = event.getSource();
			
			if( object == yesButton )
		    {
	            frame.setVisible( false );          // hide the invoking frame
	            frame.dispose();                    // of application
	            dispose();                          // of dialog
		        System.exit( 0 );                   // close the application
		    } 
			else if( object == noButton )
			    dispose();                          // of dialog
		}
	}


	class window_interface extends WindowAdapter
	{
        /**
         * "windowClosing( WindowEvent )" processes a window closing system command.
         *
         * @return  nothing
         */
		public void windowClosing( WindowEvent event )
		{
			Object object = event.getSource();
			
			if( object == exit_dlg.this )
			    dispose();
		}
	}
}

class token
{
    static String line;                             // source text parameters
    static int line_ptr;
    static int line_length;

    static int  code;                               // token from file
    static int  value;
    static char symbol;
    static char text[] = new char[256];

    static final int EOF    = 0;                    // token codes
    static final int WORD   = 1;
    static final int NUMBER = 2;
    static final int COLON  = 3;
    static final int COMMA  = 4;
    static final int UNK    = 9;

    static final int white_space = 1;               // machine states
    static final int in_word     = 2;
    static final int in_number   = 3;    

    /**
     * "next_line( String )" gets a line for processing.  It initializes 
     * global parameters used in parsing the input line.  
     * 
     * @return  nothing
     */
    static void next_line( String s )
    {
        line = new String( s );
        line_length = line.length();
        line_ptr = 0;
    }
    

    static int get_token()
    {
        // initialize for new token
        int i        = 0;                           // for word buffer
        int token    = EOF;
        boolean done = false;
        int state    = white_space;
        
        text[0] = 0;                                // init token values
        value   = 0;
        symbol  = 0;
    
        while( !done )
        {
            // get next char
            char ch = line_ptr < line_length ? line.charAt( line_ptr ) : 0;
            line_ptr++;         
            
            switch( state )                         // of machine
            {
            case white_space:
                if( ch == 0 || ch == ' ' || ch == '\t' )
                {
                    if( ch == 0 )                   // at end of line
                    {
                        token = EOF;
                        done = true;
                    }
                }
                else if( is_alpha( ch ) )           // initialize word
                {                    
                    i = 0;
                    text[i] = ch;
                    i++;
                    state = in_word;
                }
                else if( is_digit( ch ) )           // initialize number
                {
                    value = (int)( ch - '0' );
                    state = in_number;
                }
                else                                // must be a symbol
                {
                    symbol = ch;
                    
                    if( ch == ':' )
                        token = COLON;
                    else if( ch == ',' )
                        token = COMMA;
                    else
                        token = UNK;
                    
                    done = true;
                }
                break;

            case in_word:
                if( is_alphanum( ch ) )             // add character
                {
                    text[i] = ch;
                    i++;
                }
                else                                // done
                {
                    text[i] = 0;                    // terminate string
                    token = WORD;
                    line_ptr--;                     // since non-alphanum may be symbol
                    done = true;
                }
                break;

            case in_number:
                if( is_digit( ch ) )                // update value
                {
                    value = 10 * value + (int)( ch - '0' );
                }
                else                                // done
                {
                    token = NUMBER;
                    line_ptr--;                     // since non-digit may be symbol
                    done = true;
                }
                break;

            default:
                return EOF;
            }
        }

        return token;
    }

    /**
     * "is_alpha( char )" checks that character is in the alphabet.
     *
     * @return  true if argument is in alphabet range, otherwise false
     */
    static boolean is_alpha( char c )
    {
        return ( ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' ) );
    }

    /**
     * "is_alphanum( char )" checks that character is a digit or a letter.
     *
     * @return  true if argument is a digit or a letter, otherwise false
     */
    static boolean is_alphanum( char c )
    {
        return ( ( c >= 'a' && c <= 'z' ) ||
                 ( c >= 'A' && c <= 'Z' ) ||
                 ( c >= '0' && c <= '9' ) );
    }

    /**
     * "is_digit( char )" checks that character is a digit.
     *
     * @return  true if argument is a digit, otherwise false
     */
    static boolean is_digit( char c )
    {
        return ( c >= '0' && c <= '9' );
    }
}

