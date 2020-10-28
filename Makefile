install:
	mvn install

run_find_requires_capture:
	mvn compile && mvn exec:java -Dexec.mainClass="com.stripe.sample.RequiresCapture"
