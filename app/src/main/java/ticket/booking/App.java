package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;

public class App {

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner sc = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;
        Train trainSelectedForBooking = null; // Ensure train is declared

        try {
            userBookingService = new UserBookingService();
        } catch (IOException e) {
            System.out.println("Error initializing UserBookingService: " + e.getMessage());
            e.printStackTrace();  // Print full error details
            return;
        }

        while (option != 7) {
            try {
                System.out.println("\nSelect your Option:");
                System.out.println("1. Sign-Up");
                System.out.println("2. Login");
                System.out.println("3. Fetch Bookings");
                System.out.println("4. Search Trains");
                System.out.println("5. Book a Seat");
                System.out.println("6. Cancel my Booking");
                System.out.println("7. Exit");
                System.out.print("Your choice: ");
                option = sc.nextInt();

                switch (option) {
                    case 1:
                        System.out.print("Enter your Username: ");
                        String username = sc.next();
                        System.out.print("Enter your Password: ");
                        String password = sc.next();
                        User userToSignUp = new User(username, password,
                                UserServiceUtil.hashPassword(password), new ArrayList<>(), UUID.randomUUID().toString());
                        if (userBookingService.signUp(userToSignUp)) {
                            System.out.println("Sign-up successful!");
                        } else {
                            System.out.println("Sign-up failed!");
                        }
                        break;

                    case 2:
                        System.out.print("Enter your Username: ");
                        username = sc.next();
                        System.out.print("Enter your Password: ");
                        password = sc.next();
                        User userToLogin = new User(username, password,
                                UserServiceUtil.hashPassword(password), new ArrayList<>(), UUID.randomUUID().toString());
                        try {
                            userBookingService = new UserBookingService(userToLogin);
                            System.out.println("Login successful!");
                        } catch (IOException e) {
                            System.out.println("Login failed. Error: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;

                    case 3:
                        System.out.println("Fetching your Bookings...");
                        userBookingService.fetchBookings();
                        break;

                    case 4:
                        System.out.print("Enter your Source Station: ");
                        String source = sc.next();
                        System.out.print("Enter your Destination Station: ");
                        String destination = sc.next();
                        List<Train> trains = userBookingService.getTrains(source, destination);
                        if (trains.isEmpty()) {
                            System.out.println("No trains found for this route.");
                            break;
                        }
                        int index = 1;
                        for (Train t : trains) {
                            System.out.println(index + ". Train ID: " + t.getTrainId());
                            index++;
                            for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                                System.out.println("Station: " + entry.getKey() + ", Time: " + entry.getValue());
                            }
                        }
                        System.out.print("Select a Train by typing 1,2,3...: ");
                        int trainChoice = sc.nextInt();
                        if (trainChoice > 0 && trainChoice <= trains.size()) {
                            trainSelectedForBooking = trains.get(trainChoice - 1);
                            System.out.println("Train selected successfully.");
                        } else {
                            System.out.println("Invalid train selection.");
                        }
                        break;

                    case 5:
                        if (trainSelectedForBooking == null) {
                            System.out.println("No train selected. Search for trains first.");
                            break;
                        }
                        System.out.println("Available Seats:");
                        List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                        for (int i = 0; i < seats.size(); i++) {
                            for (int j = 0; j < seats.get(i).size(); j++) {
                                System.out.print(seats.get(i).get(j) + " ");
                            }
                            System.out.println();
                        }
                        System.out.print("Enter the row: ");
                        int row = sc.nextInt();
                        System.out.print("Enter the column: ");
                        int col = sc.nextInt();
                        System.out.println("Booking your seat....");
                        if (userBookingService.bookTrainSeat(trainSelectedForBooking, row, col)) {
                            System.out.println("Seat booked! Enjoy your journey.");
                        } else {
                            System.out.println("Seat booking failed. It might already be booked.");
                        }
                        break;

                    case 6:
                        System.out.print("Enter the Ticket ID to cancel: ");
                        String ticketId = sc.next();
                        if (userBookingService.cancelBooking(ticketId)) {
                            System.out.println("Ticket cancelled successfully.");
                        } else {
                            System.out.println("Failed to cancel ticket. Please check the ID.");
                        }
                        break;

                    case 7:
                        System.out.println("Exiting the system. Thank you!");
                        break;

                    default:
                        System.out.println("Invalid option. Please select a number between 1 and 7.");
                        break;
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();  // Clear the invalid input
            }
        }
        sc.close();
    }
}
