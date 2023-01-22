package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database

		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
           Customer customer = customerRepository2.findById(customerId).get();

		   customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver> driverList = driverRepository2.findAll();
//		Collections.sort(driverList, new Comparator<Driver>() {
//			@Override
//			public int compare(Driver o1, Driver o2) {
//				return o1.getDriverId()-o2.getDriverId();
//			}
//		});

		TripBooking tripBooking=null;

		boolean isdriveravailable=false;

		for(Driver driver: driverList){
			if(driver.getCab().getAvailable()){
				isdriveravailable=true;

				int bill = driver.getCab().getPerKmRate()*distanceInKm;
				 tripBooking = new TripBooking(fromLocation,toLocation,distanceInKm,TripStatus.CONFIRMED,bill);

				Customer customer = customerRepository2.findById(customerId).get();
				tripBooking.setCustomer(customer);
				tripBooking.setDriver(driver);

				driver.getCab().setAvailable(false);

				List<TripBooking> tripBookingList = customer.getTripBookings();
				if(tripBookingList==null) tripBookingList = new ArrayList<>();
				tripBookingList.add(tripBooking);
				customer.setTripBookings(tripBookingList);


				List<TripBooking> tripBookingList1 = driver.getTripBookings();
				if(tripBookingList1==null) tripBookingList1 = new ArrayList<>();
				tripBookingList1.add(tripBooking);
				driver.setTripBookings(tripBookingList1);


				tripBookingRepository2.save(tripBooking);


				break;
			}
		}

		if(isdriveravailable==false){
			throw new Exception("No cab available!");
		}


//		customerRepository2.save(customer);
//		driverRepository2.save(availabledriver);


     return tripBooking;

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly



		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();

		tripBooking.setStatus(TripStatus.CANCELED);

		Driver driver = tripBooking.getDriver();
		driver.getCab().setAvailable(true);
		tripBooking.setBill(0);
		tripBooking.getCustomer().getTripBookings().remove(tripBooking);

//		driverRepository2.save(driver);

//		List<TripBooking> tripBookingList = driver.getTripBookings();
//		for(TripBooking trip: tripBookingList){
//			if(trip.getTripBookingId()==tripBooking.getTripBookingId()){
//				trip.setStatus(TripStatus.COMPLETED);
//				break;
//			}
//		}
//
//
//		List<TripBooking> tripBookingList1 = tripBooking.getCustomer().getTripBookings();
//		for(TripBooking trip: tripBookingList1){
//			if(trip.getTripBookingId()==tripBooking.getTripBookingId()){
//				trip.setStatus(TripStatus.COMPLETED);
//				break;
//			}
//		}


		tripBookingRepository2.save(tripBooking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly


          TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();

		tripBooking.setStatus(TripStatus.COMPLETED);
          Driver driver = tripBooking.getDriver();
		  driver.getCab().setAvailable(true);

//         List<TripBooking> tripBookingList = driver.getTripBookings();
//		 for(TripBooking trip: tripBookingList){
//			 if(trip.getTripBookingId()==tripBooking.getTripBookingId()){
//				 trip.setStatus(TripStatus.COMPLETED);
//				 break;
//			 }
//		 }
//
//
//		List<TripBooking> tripBookingList1 = tripBooking.getCustomer().getTripBookings();
//		for(TripBooking trip: tripBookingList1){
//			if(trip.getTripBookingId()==tripBooking.getTripBookingId()){
//				trip.setStatus(TripStatus.COMPLETED);
//				break;
//			}
//		}




//		  driverRepository2.save(driver);

		  tripBookingRepository2.save(tripBooking);
	}
}
