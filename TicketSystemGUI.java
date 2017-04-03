package com.PamFields;

import jdk.internal.util.xml.impl.Input;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Pam on 4/2/2017.
 */
public class TicketSystemGUI extends JFrame{
    private JButton displayAllOpenTicketsButton;
    private JButton createTicketButton;
    private JButton searchTicketsButton;
    private JButton resolveTicketButton;
    private JButton quitButton;
    private JTextField resolveTicketField;
    private JTextField searchTicketField;
    private JButton displayAllClosedTicketsButton;
    private JLabel resultsLabel;
    private JScrollPane resultsScrollPane;
    private JPanel rootPanel;
    Scanner scan = new Scanner(System.in);
    LinkedList<Ticket> ticketQueue = new LinkedList<Ticket>();
    LinkedList<Ticket> resolvedTickets = new LinkedList<Ticket>();

    public TicketSystemGUI(){
        setContentPane(rootPanel);
        pack();
        setPreferredSize(new Dimension(800,500));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        createTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addTickets();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });


        displayAllOpenTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    printAllTickets();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        displayAllClosedTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    printAllResolvedTickets();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        searchTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phrase = resolveTicketField.getText();
                ArrayList<Ticket> results= search(phrase,ticketQueue);
                for(Ticket n:results){
                    JOptionPane.showMessageDialog(TicketSystemGUI.this, results);
                }
            }
        });

        resolveTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phrase = resolveTicketField.getText();
                int ticketNumber;
                ticketNumber= Integer.parseInt(phrase);
                try {
                    deleteTicketById();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    protected void addTickets() throws IOException {

        while (true) {

            Date dateReported = new Date(); //Default constructor creates Date with current date/time
            Date dateResolved = null;// entering in a ticket doesn't mean it is resolved right away
            String resolution = null;
            String description = Input.getStringInput("Enter problem");
            String reporter = Input.getStringInput("Who reported this issue?");
            int priority = Input.getPositiveIntInput("Enter priority of " + description);

            Ticket t = new Ticket(description, priority, reporter, dateReported, dateResolved, resolution);
            //ticketQueue.add(t);
            addTicketInPriorityOrder(t);

            try {
                printAllTickets();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected void searchTickets() {
        // TODO problem 4 implement this method. Return a list of matching tickets.
        // Ask user for search term
        // Use searchDescription() method to get list of matching Tickets
        // display list
        String phrase="";
        ArrayList<Ticket>results=search(phrase, ticketQueue);
        for (Ticket n:results){
            System.out.println(n);
        }
        System.out.println("Which ticket would you like to review?");
    }
    protected void addTicketInPriorityOrder(Ticket newTicket){

        //Logic: assume the list is either empty or sorted

        if (ticketQueue.size() == 0 ) {//Special case - if list is empty, add ticket and return
            ticketQueue.add(newTicket);
            return;
        }

        //Tickets with the HIGHEST priority number go at the front of the list. (e.g. 5=server on fire)
        //Tickets with the LOWEST value of their priority number (so the lowest priority) go at the end

        int newTicketPriority = newTicket.getPriority();

        for (int x = 0; x < ticketQueue.size() ; x++) {    //use a regular for loop so we know which element we are looking at

            //if newTicket is higher or equal priority than the this element, add it in front of this one, and return
            if (newTicketPriority >= ticketQueue.get(x).getPriority()) {
                ticketQueue.add(x, newTicket);
                return;
            }
        }

        //Will only get here if the ticket is not added in the loop
        //If that happens, it must be lower priority than all other tickets. So, add to the end.
        ticketQueue.addLast(newTicket);
    }
    protected void printAllTickets() throws IOException{
        System.out.println(" ------- All open tickets ----------");
        FileWriter writer = new FileWriter("C:/Users/Pam/IdeaProjects/TicketingSystem/open_tickets.txt");
        BufferedWriter bufWriter = new BufferedWriter(writer);
        for (Ticket t : ticketQueue ) {

            bufWriter.write(t+"/n");
            System.out.println(t); // This calls the  toString method for the Ticket object.
        }
        System.out.println(" ------- End of ticket list ----------");
    }

    protected void printAllResolvedTickets() throws IOException{
        Date cd=new Date();
        System.out.println(" ------- All resolved tickets ----------");
        FileWriter writer = new FileWriter("C:/Users/Pam/IdeaProjects/TicketingSystem/resolved_Tickets_as_of"+cd+".txt");
        BufferedWriter bufWriter = new BufferedWriter(writer);
        for (Ticket r : resolvedTickets ) {
            bufWriter.write(r+"/n");
            System.out.println(r); // This calls the  toString method for the Ticket object.
        }
        System.out.println(" ------- End of ticket list ----------");
    }
    public static ArrayList<Ticket>search( String phrase, LinkedList<Ticket> ticketQueue ){
        ArrayList<Ticket>results=new ArrayList<>();
        phrase=phrase.toLowerCase();
        for (Ticket t:ticketQueue){
            try{
                if(t.getDescription().contains(phrase)){
                    results.add(t);
                }else if (t.getReporter().contains(phrase)){
                    results.add(t);
                }
                else if (t.getTicketID()==Integer.parseInt(phrase)) {
                    results.add(t);
                }
            }catch(NumberFormatException ne){
                if(results.size()==0) {
                    System.out.println("There is no ticket containing this criteria.");
                }
            }
        }

        return results;
    }
    protected void deleteTicketById() throws IOException {

        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }

        int deleteID = Integer.parseInt(resolveTicketField.getText());
        try {
            if (deleteID >= 0) {
            } else {
                System.out.println("Please enter the ticket ID");
            }
        } catch (NumberFormatException fne) {
            System.out.println("Please enter the ticket ID");
        }

        //Loop over all tickets. Delete the one with this ticket ID
        boolean found = false;
        for (Ticket ticket : ticketQueue) {
            if (ticket.getTicketID() == deleteID) {
                found = true;
                ticket.dateResolved = new Date();//This means the ticket is resolved and will automatically enter the date it was removed from the open tickets list
                System.out.println("What was the resolution of the ticket?");
                ticket.resolution = scan.nextLine();
                ticketQueue.remove(ticket);
                resolvedTickets.add(ticket);
                System.out.println(String.format("Ticket %d deleted", deleteID));
                break; //don't need the loop any more.
            }
        }
        if (!found) {
            System.out.println("Ticket ID not found, no ticket deleted, please try entering the ID again");
            //TODO Problem 2 re-write this method to ask for ID again if not found
        }
        printAllTickets();  //print updated list

    }
}
