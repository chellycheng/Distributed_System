all: java.policy compile-server-rmi MwInterface.jar

java.policy: FORCE
	@echo "Creating server java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

MwInterface.jar: MwServer/MwInterface.java
	@echo "Compiling RMI server interface"
	javac MwServer/MwInterface.java Exceptions/*.java
	jar cvf MwInterface.jar MwServer/MwInterface.class Exceptions/*.class


compile-server-rmi: CarServer/*.java FlightServer/*.java RoomServer/*.java CustomerServer/*.java MwServer/*.java Common/*.java ResourceManager/*.java TransactionManager/*.java Exceptions/*.java
	javac CarServer/*.java FlightServer/*.java RoomServer/*.java CustomerServer/*.java MwServer/*.java Common/*.java ResourceManager/*.java TransactionManager/*.java Exceptions/*.java


clean:
	rm -f CarServer/*.class FlightServer/*.class RoomServer/*.class CustomerServer/*.class MwServer/*.class Common/*.class ResourceManager/*.class TransactionManager/*.class Exceptions/*.class
	rm -f MwInterface.jar
	rm -f java.policy
