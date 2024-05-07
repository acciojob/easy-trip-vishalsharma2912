package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AirportService {
    AirportRepository airportRepository = new AirportRepository();
    public String addAirport(Airport airport) {
        //Simply add airport details to your database
        //Return a String message "SUCCESS"

        List<Airport> allAirport = airportRepository.getAllAirport();
        if(allAirport.contains(airport)){
            return "Airport already exist in database";
        }
        else{
            airportRepository.saveAirport(airport);
            return "SUCCESS";
        }
    }

    public String getLargestAirportName() {

        //Largest airport is in terms of terminals. 3 terminal airport is larger than 2 terminal airport
        //In case of a tie return the Lexicographically smallest airportName

        List<Airport> allAirport = airportRepository.getAllAirport();
        int maxNoOfTerminal = 0;
        for(Airport airport : allAirport){
            int numberOfTerminal = airport.getNoOfTerminals();
            maxNoOfTerminal = Math.max(numberOfTerminal,maxNoOfTerminal);
        }
        List<Airport> airportsHaveMaxTerminal = airportRepository.getAirportByTerminal(maxNoOfTerminal);

        int size = airportsHaveMaxTerminal.size();
        if(maxNoOfTerminal == 0){
            return "Max number of terminal is  zero";
        }
        if (size == 1){
            return airportsHaveMaxTerminal.get(0).getAirportName();
        }

        return getLexicographicallySmallestAirportName(airportsHaveMaxTerminal);
    }

    private String getLexicographicallySmallestAirportName(List<Airport> airportsHaveMaxTerminal) {
        int n = airportsHaveMaxTerminal.size();
        String smallestAirport = airportsHaveMaxTerminal.get(0).getAirportName();
        for (int i = 0 ; i < n - 1; i++){
            for(int j = i + 1 ; j < n ; j++){
                if(airportsHaveMaxTerminal.get(i).getAirportName().compareTo(airportsHaveMaxTerminal.get(j).getAirportName()) > 0){
                    smallestAirport = airportsHaveMaxTerminal.get(j).getAirportName();
                }
            }
        }
        return smallestAirport;
    }

    public String addFlight(Flight flight) {
        //Return a "SUCCESS" message string after adding a flight.
        airportRepository.saveFlight(flight);
        return "SUCCESS";
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        //Find the duration by finding the shortest flight that connects these 2 cities directly
        //If there is no direct flight between 2 cities return -1.

        double shortestDuration = Integer.MAX_VALUE;

        List<Flight> allFlights = airportRepository.getAllFlight();

        for(Flight flight : allFlights){
            if(flight.getFromCity().equals(fromCity) && flight.getToCity().equals(toCity)){
                shortestDuration = Math.min(shortestDuration, flight.getDuration());
            }
        }

        if(shortestDuration == Integer.MAX_VALUE){
            return -1;
        }
        return shortestDuration;
    }

    public String getAirportNameFromFlightId(Integer flightId) {

        //We need to get the starting airportName from where the flight will be taking off (Hint think of City variable if that can be of some use)
        //return null in case the flightId is invalid, or you are not able to find the airportName
        Flight flight = airportRepository.getFlightById(flightId);
        if(flight == null) return null;

        City fromCity = flight.getFromCity();
        List<Airport> airportList = airportRepository.getAllAirport();

        for(Airport airport : airportList){
            if(airport.getCity().equals(fromCity)){
                return airport.getAirportName();
            }
        }
        return  null;
    }

    public String addPassenger(Passenger passenger) {
        //Add a passenger to the database
        //And return a "SUCCESS" message if the passenger has been added successfully.

        airportRepository.savePassenger(passenger);
        return "SUCCESS";

    }


    public String bookATicket(Integer flightId, Integer passengerId) {

        //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
        //return a String "FAILURE"
        //Also if the passenger has already booked a flight then also return "FAILURE".
        //else if you are able to book a ticket then return "SUCCESS"

        Flight flight = airportRepository.getFlightById(flightId);
        Passenger passenger = airportRepository.getPassengerById(passengerId);
        if(flight == null || passenger == null) return "flight or passenger does not exist";

        int numberOfPassengersBookedFlight = airportRepository.numberOfTicketForFlight(flightId);

        if(numberOfPassengersBookedFlight >= flight.getMaxCapacity()){
            return "FAILURE";
        }

        return  airportRepository.bookATicket(flightId,passengerId);
    }


    public int calculateFlightFare(Integer flightId) {

        //Calculation of flight prices is a function of number of people who have booked the flight already.
        //Price for any flight will be : 3000 + noOfPeopleWhoHaveAlreadyBooked*50
        //Suppose if 2 people have booked the flight already : the price of flight for the third person will be 3000 + 2*50 = 3100
        //This will not include the current person who is trying to book, he might also be just checking price

        int numberOfPassengersBookedFlight = airportRepository.numberOfTicketForFlight(flightId);
        int price = 3000 + (numberOfPassengersBookedFlight*50);
        return price;

    }


    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        //Tell the count of flight bookings done by a passenger: This will tell the total count of flight bookings done by a passenger
        return airportRepository.countOfBookingsDoneByPassengerAllCombined(passengerId);
    }



    public int getNumberOfPeopleOn(Date date, String airportName) {

        //Calculate the total number of people who have flights on that day on a particular airport
        //This includes both the people who have come for a flight and who have landed on an airport after their flight
        Airport airport = airportRepository.getAirportByName(airportName);
        if(airport == null) return 0;
        City city = airport.getCity();

        //this was the best thing what I learnt today====================================
        String givenDateString = new SimpleDateFormat("yyyy-MM-dd").format(date);

        List<Flight> flightList = airportRepository.getAllFlight();
        if(flightList.size() == 0) return 0;//NumberOfPeopleOnWithNoFlight

        List<Flight> flights = new ArrayList<>();

        for (Flight flight : flightList){

            // this was the best thing what I learnt today====================================
            String flightDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

            if(( (givenDateString.equals(flightDate) )&& (flight.getFromCity().equals(city)||flight.getToCity().equals(city)))){
                flights.add(flight);
            }
        }
        int count = 0;//the total number of people
        for(Flight flight : flights){
            count += airportRepository.numberOfTicketForFlight(flight.getFlightId());
        }
        return count;
    }


    public String cancelATicket(Integer flightId, Integer passengerId) {
        //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId

        Flight flight = airportRepository.getFlightById(flightId);
        Passenger passenger = airportRepository.getPassengerById(passengerId);
        if(flight == null && passenger == null) return "FAILURE";

        List<Integer> flightIds = airportRepository.getAllBookingsWithFlightId();

        for(Integer fId : flightIds){
            List<Integer> passengers = airportRepository.getBookingPassengersByFlightIds(flightId);
            if(passengers.contains(passengerId)){
                return  airportRepository.cancelATicket(flightId,passengerId);

            }
            else {
                return "FAILURE";
            }
        }

        return "FAILURE";
    }


    //Error: TestCases.testCalculateRevenueOfAFlight:205 expected: <3000> but was: <3050
    public int calculateRevenueOfAFlight(Integer flightId) {

        //Calculate the total revenue that a flight could have
        //That is of all the passengers that have booked a flight till now and then calculate the revenue
        //Revenue will also decrease if some passenger cancels the flight

        int price = calculateFlightFare(flightId);
        int cancelBooking = airportRepository.getCancelBookings(flightId); // flight id se number of cancel booking correct nhi pta rha

        int cancelPrice = (cancelBooking * 50);
        return price - cancelPrice;
    }
}