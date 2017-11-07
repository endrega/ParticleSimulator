package net.starvec;

import java.awt.Color;
import java.util.Random;

public class Particle 
{
	private double xLoc, yLoc; //Meters
	private double size, radius; //Meters
	private double speed, direction; //m/s, radians
	private double surfaceArea, volume, density, mass; //m^2, m^3, kg/m^3, kg
	private double thermalEnergy, temperature, thermalMass, emissivity; //Joules, C°, J/g/C°, W/m^2*K^4 
	private double strength; //
	private Random rand = new Random();
	
	public Particle(int xStart, int yStart, int xEnd, int yEnd, double sizeLower, double sizeUpper, double density, double temperatureLower, double temperatureUpper, double thermalMass, double emissivity, double strengthLower, double strengthUpper)
	{
		//position
		xLoc = rand.nextInt((xEnd-10)-(xStart+10)) + (xStart+10);
		yLoc = rand.nextInt((yEnd-10)-(yStart+10)) + (yStart+10);
		//size
		size = (sizeUpper-sizeLower)*rand.nextDouble() + sizeLower;
		radius = size/2.0;
		//speed and direction
		speed = 0; //rand.nextDouble()/10;
		direction = Math.PI*2*rand.nextDouble();
		//density and mass
		this.density = density;
		surfaceArea = 128*4*Math.PI*Math.pow(radius, 2);
		volume = (4/3.0)*Math.PI*Math.pow(radius, 3);
		mass = density*volume;
		//thermal energy, mass, and emissivity
		temperature = (temperatureUpper-temperatureLower)*rand.nextDouble() + temperatureLower;
		thermalEnergy = mass*thermalMass*temperature;
		this.thermalMass = thermalMass;
		this.emissivity = emissivity;
		//strength
		strength = (strengthUpper-strengthLower)*rand.nextDouble() + strengthLower;
	}
	
	public Particle(double xLoc, double yLoc, double radius, double speed, double direction, double density, double thermalEnergy, double thermalMass, double emissivity, double strength)
	{
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		this.radius = radius;
		size = radius*2;
		this.speed = speed;
		this.direction = direction;
		this.density = density;
		surfaceArea = 128*4*Math.PI*Math.pow(radius, 2);
		volume = (4/3.0)*Math.PI*Math.pow(radius, 3);
		mass = density*volume;
		this.thermalEnergy = thermalEnergy;
		this.thermalMass = thermalMass;
		this.emissivity = emissivity;
		this.strength = strength;
	}

	public double getxLoc() {
		return xLoc;
	}

	public void setxLoc(double xLoc) {
		this.xLoc = xLoc;
	}

	public double getyLoc() {
		return yLoc;
	}

	public void setyLoc(double yLoc) {
		this.yLoc = yLoc;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) 
	{
		if (speed > 10)
			this.speed = 10;
		else
			this.speed = speed;
	}

	public double getDirection() {
		return direction;
	}

	public void setDirection(double direction) {
		this.direction = direction;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getThermalEnergy() {
		return thermalEnergy;
	}

	public void setThermalEnergy(double thermalEnergy) 
	{
		if (thermalEnergy < 0)
			this.thermalEnergy = 0;
		else
			this.thermalEnergy = thermalEnergy;
	}

	public double getStrength() {
		return strength;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}
	
	public double getSurfaceArea() {
		return surfaceArea;
	}

	public void setSurfaceArea(double surfaceArea) {
		this.surfaceArea = surfaceArea;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getDensity() {
		return density;
	}

	public void setDensity(double density) {
		this.density = density;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getThermalMass() {
		return thermalMass;
	}

	public void setThermalMass(double thermalMass) {
		this.thermalMass = thermalMass;
	}
	

	public double getEmissivity() {
		return emissivity;
	}

	public void setEmissivity(double emissivity) {
		this.emissivity = emissivity;
	}

	public Color getColor()
	{
		int red = 45, green = 45, blue = 45;
		//t = q/mc
		temperature = thermalEnergy/(mass*thermalMass);
		red = restrainRGB((int)map(temperature, 480, 1000, 50, 255));
		green = restrainRGB((int)map(temperature, 700, 1300, 50, 255));
		blue = restrainRGB((int)map(temperature, 1200, 1500, 45, 255));
		Color color = new Color(red, green, blue);
		return color;
	}
	
	private int restrainRGB(int color)
	{
		if (color > 255)
			return 255;
		else
			return color;
	}
	
	private double map(double value, double inMin, double inMax, double outMin, double outMax) 
	{
		if (value >= inMin && value <= inMax)
			return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
		else if (value > inMax)
			return (outMax);
		else
			return (outMin);
	}
			
}