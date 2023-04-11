package org.paumard.loom.B_structuredconcurrency.B_model;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jdk.incubator.concurrent.StructuredTaskScope;

public record Weather(String agency, String weather) {

	static Random random = new Random();
	
    public static Weather readWeather() throws InterruptedException, ExecutionException {
    	try(var scope = new StructuredTaskScope.ShutdownOnSuccess<Weather>()){
    		Future<Weather> futureA = scope.fork(Weather::readWeatherFromA);
			Future<Weather> futureB = scope.fork(Weather::readWeatherFromB);
			Future<Weather> futureC = scope.fork(Weather::readWeatherFromC);

			scope.join();

			System.out.println("Future A state = " + futureA.state());
			System.out.println("Future B state = " + futureB.state());
			System.out.println("Future C state = " + futureC.state());

			return scope.result();
		}
    	
        //return readWeatherFromA();
    }

	private static Weather readWeatherFromA() {
		try {
			Thread.sleep(random.nextInt(30,90));
			return new Weather("A", "Cloudy");
		}catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private static Weather readWeatherFromB() {
		try {
			Thread.sleep(random.nextInt(30,90));
			return new Weather("B", "Sunny");
		}catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private static Weather readWeatherFromC() {
		try {
			Thread.sleep(random.nextInt(30,90));
			return new Weather("C", "JEMAPPELLEBASTIENAAAAAAAAAH");
		}catch (Exception e) {
			throw new RuntimeException();
		}
	}
}
