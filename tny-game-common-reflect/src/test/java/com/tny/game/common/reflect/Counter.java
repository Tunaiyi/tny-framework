package com.tny.game.common.reflect;

import com.tny.game.common.event.bus.annotation.*;

@EventListener
public class Counter {

	private double number;

	private double num1;

	private double num2;

	public Counter(double number, double num1, double num2) {
		super();
		this.number = number;
		this.num1 = num1;
		this.num2 = num2;
	}

	public double count(int time, long size) {
		return (this.number - this.num1 + this.num2) * time / size;
	}

}
