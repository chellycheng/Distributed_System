all: java.policy clean

java.policy: FORCE
	@echo "Creating server java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

MwInterface.jar: MwServer/MwInterface.java
	@echo "Compiling RMI server interface"
	javac MwServer/MwInterface.java
	jar cvf MwInterface.jar MwServer/MwInterface.class

compile-server-rmi: CarServer/*.java FlightServer/*.java RoomServer/*.java CustomerServer/*.java MwServer/*.java Common/*.java ResourceManager/*.java
	javac CarServer/*.java FlightServer/*.java RoomServer/*.java CustomerServer/*.java MwServer/*.java Common/*.java ResourceManager/*.java

clean:
	rm -f CarServer/*.class FlightServer/*.class RoomServer/*.class CustomerServer/*.class MwServer/*.class Common/*.class ResourceManager/*.class
	rm -f MwInterface.jar
	rm -f java.policy
