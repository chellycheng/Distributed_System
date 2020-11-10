all: java.policy compile-client

java.policy: FORCE
	@echo "Creating client java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

../Middleware/MwInterface.jar: ../Middleware/MwServer/MwInterface.java ../Middleware/Exceptions/*.java
	make -C ../Middleware/ MwInterface.jar


compile-client: ../Middleware/MwInterface.jar
	javac -cp ../Middleware/MwInterface.jar Client/*.java

clean:
	rm -f Client/*.class
	rm -f ../Server/MwInterface.jar
	rm -f java.policy
