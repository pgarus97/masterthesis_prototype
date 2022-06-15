package prototype;

public class Main {

	public static void main(String[] args) {
		WPCalculator mainCalculator = new WPCalculator();
		WPCalculatorView mainView = new WPCalculatorView();

		mainCalculator.linkView(mainView);
		mainView.linkCalculator(mainCalculator);
	}

}
