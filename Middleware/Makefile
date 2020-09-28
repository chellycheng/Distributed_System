all: java.policy compile-server-rmi

java.policy: FORCE
	@echo "Creating server java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

compile-server-rmi: CarServer/*.java FlightServer/*.java RoomServer/*.java Common/*.java ResourceManager/*.java
	javac CarServer/*.java FlightServer/*.java RoomServer/*.java Common/*.java ResourceManager/*.java
