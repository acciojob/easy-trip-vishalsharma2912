package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AirportService {
    AirportRepositry obj = new AirportRepositry();
    public String addAirportinfo(Airport airport){
        String str = obj.addAirport(airport);
        return str;
    }
    public String getAirportLargest(){
        List<Airport> list  = obj.getAllAirports();
        int maxTerminal=0;
        for(Airport apr : list){
            int temp = apr.getNoOfTerminals();
            maxTerminal=Math.max(temp,maxTerminal);

        }
        List<Airport> listOfMaxTerminal = obj.getAirportByTerminal(maxTerminal);
        int size = listOfMaxTerminal.size();
        if(maxTerminal==0) {
            return "Max number of terminal is  zero";
        }
       if(size==1){
           return listOfMaxTerminal.get(0).getAirportName();
       }
       return lexiSmallest(listOfMaxTerminal);
    }


    public String lexiSmallest(List<Airport> listOfMaxTerminal){
        String smallest = listOfMaxTerminal.get(0).getAirportName();
        int n = listOfMaxTerminal.size();
        for(int i=0;i<n-1;i++){
            for(int j=i+1;j<n;j++){
                if(listOfMaxTerminal.get(i).getAirportName().compareTo(listOfMaxTerminal.get(j).getAirportName())>0){
                    smallest=listOfMaxTerminal.get(j).getAirportName();
                }
            }
        }

        return smallest;
    }
    public double getShortestDuration(City fromCity , City toCity){
        List<Flight> list = obj.getFlightLists();
        double duration = Integer.MAX_VALUE;

        for(Flight temp : list){
            if(temp.getFromCity().equals(fromCity) && temp.getToCity().equals(toCity)){
                duration=Math.min(duration,temp.getDuration());
            }
        }
        if(duration==Integer.MAX_VALUE){
            return -1;
        }
        return duration;
    }
   public int getFlightFare(int flightId){
        int temp = obj.noOfPassOnFlightId(flightId);
        return 3000 + temp*50;
    }
    public String bookTicket(int flightId,int passengerId){
        Flight flight = obj.getFlightByFlightId(flightId);
        Passenger passenger = obj.getPassengerByPassengerId(passengerId);

        if(flight == null || passenger ==null) {
            return "Don't exist";

        }
        int noOfPassBookedFlight = obj.noOfPassOnFlightId(flightId);
        if(noOfPassBookedFlight>= flight.getMaxCapacity()){
            return "Failure";
        }
        return obj.bookATicket(flightId,passengerId);
    }
    public String addPassenger(Passenger passenger){
        obj.savePassenger(passenger);
       return "SUCCESS";

    }
    public String addFlight(Flight flight){
        obj.saveFlight(flight);
        return "SUCCESS";
    }
    public String cancelTicket(int flightId , int passengerId){
        Flight flight = obj.getFlightByFlightId(flightId);
        Passenger passenger = obj.getPassengerByPassengerId(passengerId);
        if(flight==null || passenger==null ){
            return "FAILURE";
        }
        List<Integer> listOfBooksFlightId = obj.getListOfBookFlightId();
        for(Integer fid : listOfBooksFlightId){
            List<Integer> passengers = obj.getBookingPassengersByFlightIds(flightId);
            if(passengers.contains(passengerId)){
                return  obj.cancelATicket(flightId,passengerId);

            }
            else {
                return "FAILURE";
            }

        }
        return "FAILURE";

    }
    public int countOfBooking(int passengerId){
        return obj.countOfBooking(passengerId);
    }
    public String getAirportNameByFlightId(int flightId){
        Flight flight = obj.getFlightByFlightId(flightId);
        if(flight == null) return null;

        City fromCity = flight.getFromCity();
        List<Airport> airportList = obj.getAllAirport();

        for(Airport airport : airportList){
            if(airport.getCity().equals(fromCity)){
                return airport.getAirportName();
            }
        }
        return  null;
    }
    public int calculateRevenue(int flightId){
        int price = getFlightFare(flightId);
        int cancelBooking = obj.getCancelBookings(flightId); // flight id se number of cancel booking correct nhi pta rha

        int cancelPrice = (cancelBooking * 50);
        return price - cancelPrice;
    }
    public int getNumberOfPeopleOn(Date date, String airportName) {

        //Calculate the total number of people who have flights on that day on a particular airport
        //This includes both the people who have come for a flight and who have landed on an airport after their flight
        Airport airport = obj.getAirportByName(airportName);
        if(airport == null) return 0;
        City city = airport.getCity();

        //this was the best thing what I learnt today====================================
        String givenDateString = new SimpleDateFormat("yyyy-MM-dd").format(date);

        List<Flight> flightList = obj.getAllFlight();
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
            count += obj.numberOfTicketForFlight(flight.getFlightId());
        }
        return count;
    }
}

