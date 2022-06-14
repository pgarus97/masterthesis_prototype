package prototype;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;

class MainTest {
	
	//TODO make systematic border tests etc.
	
    HashMap<String, String> variables = new HashMap<String,String>();
	WPCalculator mainCalculator = new WPCalculator();
	WPCalculatorAllSigma allSigmaCalculator = new WPCalculatorAllSigma();
	WPCalculatorView mainView = new WPCalculatorView();

	
	MainTest(){
		mainCalculator.linkView(mainView);
		mainView.linkCalculator(mainCalculator);
	}
	
	@Test
	void calcTest() {
		System.out.println(NumberUtils.isCreatable("-1"));
		System.out.println(mainCalculator.calculation("(5-6)"));
		System.out.println(mainCalculator.calculation("min(2,4)"));
		System.out.println(mainCalculator.calculation("if(1=1,x+1,x)"));

	}

	
	@Test
	void truncateTest() {
		mainView.setRestriction(10); //TODO change restriction and iterationCount to model and pass/update on click?
		
		assert mainCalculator.truncate("#{1}").equals("1.0");
		assert mainCalculator.truncate("#{-1}").equals("0");
		assert mainCalculator.truncate("#{11}").equals("10.0");
		
		assert mainCalculator.truncate("#{x+1}").equals("#{x+1}");
		assert mainCalculator.truncate("#{1+1}").equals("2.0");
		assert mainCalculator.truncate("#{x+#{1+3}}").equals("#{x+4.0}");
		assert mainCalculator.truncate("#{2+#{1+3}}").equals("6.0");
		assert mainCalculator.truncate("#{x+#{y+3}}").equals("#{x+#{y+3}}");


	}
	
	@Test
	void testAssignments() {
		variables.put("x", "0");
		variables.put("y", "0");
		mainCalculator.linkView(mainView);
		mainView.linkCalculator(mainCalculator);
		mainCalculator.setVariables(variables);
		mainView.setRestriction(10);

		
		//assignments
		assert mainCalculator.calculation(mainCalculator.wp("x=5", "x^2")).equals("25.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; x=10", "x^2")).equals("100.0");	
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; x=10 ; y=2", "x^2")).equals("100.0");	
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; y=10", "x^2")).equals("25.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; y=10", "y^2")).equals("100.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; y=10", "x+y")).equals("15.0");



	}
	
	@Test
	void testProbability() {
		variables.put("x", "0");
		variables.put("y", "0");
		mainCalculator.linkView(mainView);
		mainView.linkCalculator(mainCalculator);
		mainCalculator.setVariables(variables);
		mainView.setRestriction(10);

		
		//probability with initial assignment x=5
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[4/5]{x=10}","x^2")).equals("40.0");
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[1/2]{x=10};{x=3}[1/2]{x=4}","x")).equals("3.5");
		assert mainCalculator.calculation(mainCalculator.wp("x=0;y=0;{skip}[1/2]{x=x+2}","x")).equals("1.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;{skip}[1/2]{x=x+2}","x")).equals("6.0");
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[1/2]{x=10};{x=3}[1/2]{x=4};x=6","x")).equals("6.0");
		

		
	}
	
	@Test
	void testConditional() {
		variables.put("x", "0");
		variables.put("y", "0");
		mainCalculator.linkView(mainView);
		mainView.linkCalculator(mainCalculator);
		mainCalculator.setVariables(variables);
		mainView.setRestriction(15);

		
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if {x<5} {x=x+1} else {x=x-1}", "x^2")).equals("16.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if {x<5} {x=x+1} else {x=x-1};x=8", "x^2")).equals("64.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;{x=3}[1/2]{x=10};if {x<5} {x=x+1} else {x=x-1}", "x")).equals("6.5");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if {x<5} {x=x+1} else {if{x=5}{x=3}else{x=8}}", "x")).equals("3.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if {x<5} {x=x+1} else {min{x=x+1}{x=3}", "x")).equals("3.0");



	}
	
	@Test
	void testDemonicChoice() {
		variables.put("x", "0");
		variables.put("y", "0");
		mainCalculator.linkView(mainView);
		mainView.linkCalculator(mainCalculator);
		mainCalculator.setVariables(variables);
		mainView.setRestriction(10);

		
		assert mainCalculator.calculation(mainCalculator.wp("x=1;min{x=x+1}{x=3}","x")).equals("2.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=3;min{x=x+1}{x=3}","x")).equals("3.0");
	}
	
	@Test
	void testWhile() {	
		variables.put("x", "1");
		variables.put("c", "0");
		mainCalculator.linkView(mainView);
		mainView.linkCalculator(mainCalculator);
		mainCalculator.setVariables(variables);
		mainView.setRestriction(100);
		mainView.setIterationCount(10);

		
		assert mainCalculator.calculation(mainCalculator.wp("c=0;x=1; while(c=1){{x=x+1}[1/2]{c=0}}", "x")).equals("1.0");
		assert mainCalculator.calculation(mainCalculator.wp("c=1;x=1; while(c=1){{x=x+1}[1/2]{c=0}}", "x")).equals("1.978515625");
	}
	
	@Test
	void testfillAllSigma() {
		mainCalculator.linkView(mainView);
		mainView.linkCalculator(mainCalculator);
		mainView.linkAllSigmaCalculator(allSigmaCalculator);
		allSigmaCalculator.linkView(mainView);
		mainCalculator.setVariables(variables);

		mainView.setRestriction(1);
		mainView.setIterationCount(10);
		
		ArrayList<HashMap<String,String>> testAllSigma = allSigmaCalculator.fillAllSigma("xy", mainView.getRestriction());
		
		assert testAllSigma.get(0).get("x").equals("0");
		assert testAllSigma.get(0).get("y").equals("0");
		
		assert testAllSigma.get(1).get("x").equals("0");
		assert testAllSigma.get(1).get("y").equals("1");
		
		assert testAllSigma.get(2).get("x").equals("1");
		assert testAllSigma.get(2).get("y").equals("0");
		
		assert testAllSigma.get(3).get("x").equals("1");
		assert testAllSigma.get(3).get("y").equals("1");
		

	}
}
