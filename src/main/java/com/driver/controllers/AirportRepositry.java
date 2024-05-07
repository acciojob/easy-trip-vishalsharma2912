package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class AirportRepositry {
    HashMap<String, Airport> AirportDb = new HashMap<>();
    HashMap<Integer, Passenger> PassengerDb = new HashMap<>();
    HashMap<Integer, Flight> flightDb = new HashMap<>();
    // now we will make a map flightid,listofpassengerid
    HashMap<Integer,List<Integer>> flightPassengerDb = new HashMap<>();


    HashMap<Integer,Integer> flightCancelMap = new HashMap<>();// flight ids , number of passenger cancel the flight

    public String addAirport(Airport airport){
        String temp = airport.getAirportName();
        AirportDb.put(temp,airport);
        return "SUCCESS";

    }

    public List<Airport> getAllAirports(){
    return new ArrayList<Airport>(AirportDb.values());
    }

    public List<Airport> getAirportByTerminal(int noOfTerminals){
        List<Airport> ans = new ArrayList<>();

        for(String airportName : AirportDb.keySet()){
            Airport airport = AirportDb.get(airportName);
            if(airport.getNoOfTerminals() == noOfTerminals){
                ans.add(airport);
            }
        }
        return ans;
    }
    public List<Flight> getFlightLists(){
        if(flightDb.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(flightDb.values());
    }
    public int noOfPassOnFlightId(int flightId){
        if(flightPassengerDb.containsKey(flightId)){
            return flightPassengerDb.get(flightId).size();
        }
        return 0;
    }
    public Flight getFlightByFlightId(int flightId){
        return flightDb.get(flightId);
    }
    public Passenger getPassengerByPassengerId(int passengerId){
        return PassengerDb.get(passengerId);
    }
    public String bookATicket(int flightId , int passengerId){
        if(flightPassengerDb.containsKey(flightId)) {


            List<Integer> list = flightPassengerDb.get(flightId);
            for (int a : list) {
                if (a == passengerId) {
                    return "FAILURE";
                }
                list.add(passengerId);
                flightPassengerDb.put(flightId, list);
            }
        }
        else{
            List<Integer> temp = new ArrayList<>();
            temp.add(passengerId);
            flightPassengerDb.put(flightId,temp);

        }
        return "SUCCESS";
    }
    public void  savePassenger(Passenger passenger){
         PassengerDb.put(passenger.getPassengerId(),passenger);

    }
    public void saveFlight(Flight flight){
        flightDb.put(flight.getFlightId(),flight);
    }
    public List<Integer> getListOfBookFlightId(){
        return new ArrayList<>(flightDb.keySet());
    }
    public List<Integer> getBookingPassengersByFlightIds(int flightId){
         return flightPassengerDb.get(flightId);
    }
    public String cancelATicket(int flightId , int passengerId){
        List<Integer> pIds = flightPassengerDb.get(flightId);
        if(!pIds.contains(passengerId)) return "FAILURE";
        List<Integer> newList = new ArrayList<>();
        int n = pIds.size();
        for(int i = 0 ; i < n ; i++ ){
            if(pIds.get(i) == passengerId){
                newList.add(i);
            }
        }
        for(int i = 0 ; i < newList.size() ; i++){
            int idx = newList.get(i);
            pIds.remove(idx);

        }

        flightCancelMap.put(flightId,flightCancelMap.getOrDefault(0,1)+1);
        return "SUCCESS";
    }
    public int countOfBooking(int passengerId){
        int count=0;
        for(Integer fid : flightPassengerDb.keySet()){

            List<Integer> passengers = flightPassengerDb.get(fid);
            for(Integer a : passengers){
                if(a==passengerId){
                    count++;
                }
            }
        }
        return count;
    }
    public List<Airport> getAllAirport(){
        return new ArrayList<>(AirportDb.values());
    }
    public int getCancelBookings(int flightId){
        if(flightCancelMap.containsKey(flightId)){
            return flightCancelMap.get(flightId);
            //  return 1;
        }
        else return  1;
    }
    public Airport getAirportByName(String airportName) {
        if(AirportDb.containsKey(airportName)){
            return AirportDb.get(airportName);
        }
        else return null;
    }
    public List<Flight> getAllFlight() {
        if(flightDb.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(flightDb.values());
    }
    public int numberOfTicketForFlight(Integer flightId) {
        if(flightPassengerDb.containsKey(flightId)){
            return flightPassengerDb.get(flightId).size();
        }
        else return  0;
    }
}
